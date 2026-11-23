package com.sertumrosarum.data.lumen

import com.sertumrosarum.BuildConfig
import com.sertumrosarum.domain.lumen.CatechistAction
import com.sertumrosarum.domain.lumen.CatechistContext
import com.sertumrosarum.domain.lumen.CatechistMessage
import com.sertumrosarum.domain.lumen.CatechistPersona
import com.sertumrosarum.domain.lumen.CatechistPrayerCatalog
import com.sertumrosarum.domain.lumen.CatechistProgress
import com.sertumrosarum.domain.lumen.CatechistSource
import com.sertumrosarum.domain.lumen.CatechistTurnResult
import com.sertumrosarum.domain.lumen.CustomRosarySupplement
import com.sertumrosarum.domain.lumen.LumenGuardrails
import com.sertumrosarum.domain.lumen.LumenJsonParser
import com.sertumrosarum.domain.lumen.LumenMood
import com.sertumrosarum.domain.lumen.LumenToolBridge
import com.sertumrosarum.domain.lumen.LumenTools
import com.sertumrosarum.domain.lumen.MistralStepUpdate
import com.sertumrosarum.domain.lumen.MistralWireMessage
import com.sertumrosarum.domain.lumen.OfflineCatechistEngine
import com.sertumrosarum.domain.lumen.ToolExecutionOutput
import com.sertumrosarum.domain.prayer.MysterySet
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Coordinates Lumen's multi-step function calling loop with
 * automatic, reverent fallback to [OfflineCatechistEngine] whenever offline or unconfigured.
 */
class LumenRepository(
    private val client: LumenApiClient = MistralLumenClient(),
    private val defaultApiKeyProvider: () -> String = { BuildConfig.MISTRAL_API_KEY },
    private val modelName: String = LumenGuardrails.DEEPSEEK_FLASH_MODEL,
) {
    fun resolveApiKey(userOverrideKey: String): String =
        userOverrideKey.trim().ifBlank { defaultApiKeyProvider().trim() }

    fun hasApiKey(userOverrideKey: String = ""): Boolean = resolveApiKey(userOverrideKey).isNotBlank()

    suspend fun sendConversationTurn(
        userMessage: String,
        history: List<CatechistMessage>,
        persona: CatechistPersona,
        context: CatechistContext,
        toolBridge: LumenToolBridge = LumenToolBridge.EMPTY,
        onlineEnabled: Boolean = true,
        userApiKeyOverride: String = "",
        onInterimReply: (String) -> Unit = {},
        onProgress: (CatechistProgress) -> Unit = {},
    ): CatechistTurnResult {
        val clean = LumenGuardrails.sanitizeInput(userMessage)
        val key = resolveApiKey(userApiKeyOverride)
        if (!onlineEnabled || key.isBlank()) {
            return OfflineCatechistEngine.reply(clean, persona, context)
        }
        CatechistPrayerCatalog.requestedPrayer(clean)?.let {
            return OfflineCatechistEngine.prayerReply(listOf(it), offline = false)
        }

        val systemPrompt = LumenGuardrails.conversationSystemPrompt(persona, context)
        val wireMessages = mutableListOf<MistralWireMessage>()
        val recentHistory = history.filterNot { it.isInterim }.takeLast(12)
        recentHistory.forEach { msg ->
            val content = if (msg.prayers.isEmpty()) msg.text else LumenJsonParser.serializeSimple(
                mapOf(
                    "reply" to msg.text,
                    "prayers" to msg.prayers.map {
                        mapOf("title" to it.title, "id" to it.id, "attribution" to it.attribution, "sourceUrl" to it.sourceUrl)
                    },
                ),
            )
            wireMessages += MistralWireMessage.Text(if (msg.fromUser) "user" else "assistant", content)
        }
        wireMessages += MistralWireMessage.Text("user", clean)

        val collectedActions = mutableListOf<CatechistAction>()
        val collectedTrace = mutableListOf<String>()
        val collectedSources = mutableListOf<CatechistSource>()
        val interimReplies = mutableSetOf<String>()
        val writingStatus = "${persona.displayName} está escrevendo a resposta…"

        fun report(status: String, preview: String? = null) {
            onProgress(CatechistProgress(persona, status, collectedTrace.distinct(), preview))
        }

        fun reportTool(name: String) {
            if (name != LumenTools.SEND_INTERIM_REPLY) report(LumenTools.liveProgressLabel(name, persona))
        }

        fun publishInterimReply(text: String) {
            // The streamed message can arrive before the same tool is executed at the end of the step.
            if (interimReplies.add(text)) onInterimReply(text)
        }

        fun offlineReply(): CatechistTurnResult {
            val turn = OfflineCatechistEngine.reply(clean, persona, context)
            return turn.copy(
                actions = (collectedActions + turn.actions).distinct(),
                toolTrace = collectedTrace.distinct(),
                webSources = collectedSources.distinctBy { it.url },
            )
        }

        fun finalReply(rawText: String?): CatechistTurnResult {
            val prayerSources = collectedSources + recentHistory.flatMap { it.webSources }
            val turn = rawText?.let {
                LumenJsonParser.parseConversationTurn(it, context.activeMysterySet, collectedActions, collectedTrace, prayerSources)
            } ?: return offlineReply()
            if (turn.prayers.isEmpty()) {
                val options = CatechistPrayerCatalog.curatedOptions(clean)
                if (options.isNotEmpty()) {
                    return OfflineCatechistEngine.prayerReply(options, offline = false).copy(toolTrace = turn.toolTrace)
                }
            }
            return turn.copy(webSources = collectedSources.distinctBy { it.url })
        }

        val onStepUpdate: (MistralStepUpdate) -> Unit = { update ->
            when (update) {
                MistralStepUpdate.Thinking -> report(persona.thinkingLine)
                is MistralStepUpdate.ToolCall -> reportTool(update.name)
                is MistralStepUpdate.InterimReply -> publishInterimReply(update.text)
                is MistralStepUpdate.Reply -> report(writingStatus, update.text)
            }
        }
        repeat(MAX_TOOL_ROUNDS) {
            val step = client.completeAgentStep(
                apiKey = key,
                systemPrompt = systemPrompt,
                messages = wireMessages,
                toolsJson = LumenTools.mistralToolsJson,
                model = modelName,
                temperature = 0.72,
                onUpdate = onStepUpdate,
            ).getOrNull() ?: return offlineReply()

            if (step.toolCalls.isEmpty()) {
                return finalReply(step.textContent)
            }

            wireMessages += MistralWireMessage.AssistantToolCalls(step.toolCalls)
            coroutineScope {
                // Web queries in a batch are independent. Process local tools and return results in call order.
                val webSearches = step.toolCalls.filter { it.name == LumenTools.SEARCH_WEB_PRAYERS }
                    .associateWith { call -> async { searchWebPrayers(key, call.argumentsJson) } }
                for (tc in step.toolCalls) {
                    reportTool(tc.name)
                    val out = webSearches[tc]?.await() ?: LumenTools.execute(
                        toolName = tc.name,
                        argumentsJson = tc.argumentsJson,
                        bridge = toolBridge,
                        persona = persona,
                        fallbackMysterySet = context.activeMysterySet,
                    )
                    out.emittedAction?.let { collectedActions += it }
                    out.traceLabel?.let { collectedTrace += it }
                    collectedSources += out.webSources
                    out.interimReply?.let(::publishInterimReply)
                    wireMessages += MistralWireMessage.ToolResult(
                        toolCallId = tc.id,
                        name = tc.name,
                        content = out.resultJson,
                    )
                }
            }
            if (step.toolCalls.any { it.name != LumenTools.SEND_INTERIM_REPLY }) {
                report("${persona.displayName} está reunindo o que encontrou…")
            }
        }

        // If the model exhausted tool rounds without a final text turn, ask once for the closing reply.
        report("${persona.displayName} está preparando a resposta final…")
        val closing = client.completeAgentStep(
            apiKey = key,
            systemPrompt = systemPrompt,
            messages = wireMessages + MistralWireMessage.Text(
                "user",
                "Finalize com um único objeto JSON, com reply como primeiro campo. Use os resultados que já recebeu e não chame outras ferramentas.",
            ),
            toolsJson = "[]",
            model = modelName,
            temperature = 0.7,
            onUpdate = onStepUpdate,
        ).getOrNull()
        return finalReply(closing?.textContent)
    }

    private suspend fun searchWebPrayers(apiKey: String, argumentsJson: String): ToolExecutionOutput {
        val args = LumenJsonParser.parseValue(argumentsJson) as? Map<*, *>
        val query = (args?.get("query") as? String)?.let(LumenGuardrails::sanitizeInput)?.take(300)
        val result = query?.takeIf { it.isNotBlank() }?.let { client.searchWebPrayers(apiKey, it).getOrNull() }
        if (result == null) {
            return ToolExecutionOutput("""{"error":"A busca não encontrou fontes disponíveis. Informe isso ao fiel e ofereça uma alternativa do Tesouro quando apropriado."}""")
        }
        return ToolExecutionOutput(
            resultJson = LumenJsonParser.serializeSimple(
                mapOf(
                    "summary" to result.summary,
                    "sources" to result.sources.map { mapOf("title" to it.title, "url" to it.url) },
                ),
            ),
            traceLabel = "Consultou orações na web",
            webSources = result.sources,
        )
    }

    suspend fun generateCustomRosary(
        intention: String,
        mysterySet: MysterySet,
        mood: LumenMood?,
        persona: CatechistPersona,
        onlineEnabled: Boolean = true,
        userApiKeyOverride: String = "",
    ): CustomRosarySupplement {
        val clean = LumenGuardrails.sanitizeInput(intention)
        val key = resolveApiKey(userApiKeyOverride)
        if (!onlineEnabled || key.isBlank()) {
            return OfflineCatechistEngine.generateCustomRosary(clean, mysterySet, mood, persona)
        }
        val systemPrompt = LumenGuardrails.customRosarySystemPrompt(persona, mysterySet, mood)
        val userPrompt = buildString {
            append("Minha intenção para este terço: ")
            append(clean.ifBlank { "Paz no coração e fidelidade no dia de hoje." })
            if (mood != null) {
                append("\nComo estou me sentindo agora: ${mood.label} (${mood.hint}).")
            }
        }
        val result = client.completeJson(
            apiKey = key,
            systemPrompt = systemPrompt,
            messages = listOf("user" to userPrompt),
            model = modelName,
            temperature = 0.65,
        )
        val raw = result.getOrNull()
        val parsed = raw?.let {
            LumenJsonParser.parseCustomRosarySupplement(
                rawJson = it,
                mysterySet = mysterySet,
                fallbackIntention = clean,
                mood = mood,
                persona = persona,
            )
        }
        return parsed ?: OfflineCatechistEngine.generateCustomRosary(clean, mysterySet, mood, persona)
    }

    companion object {
        private const val MAX_TOOL_ROUNDS = 6
    }
}
