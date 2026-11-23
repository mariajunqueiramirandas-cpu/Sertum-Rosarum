package com.sertumrosarum.data.lumen

import com.sertumrosarum.BuildConfig
import com.sertumrosarum.domain.lumen.CatechistAction
import com.sertumrosarum.domain.lumen.CatechistContext
import com.sertumrosarum.domain.lumen.CatechistMessage
import com.sertumrosarum.domain.lumen.CatechistPersona
import com.sertumrosarum.domain.lumen.CatechistTurnResult
import com.sertumrosarum.domain.lumen.CustomRosarySupplement
import com.sertumrosarum.domain.lumen.LumenGuardrails
import com.sertumrosarum.domain.lumen.LumenJsonParser
import com.sertumrosarum.domain.lumen.LumenMood
import com.sertumrosarum.domain.lumen.LumenToolBridge
import com.sertumrosarum.domain.lumen.LumenTools
import com.sertumrosarum.domain.lumen.MistralWireMessage
import com.sertumrosarum.domain.lumen.OfflineCatechistEngine
import com.sertumrosarum.domain.prayer.MysterySet

/**
 * Coordinates Mistral Large 4's multi-step native Function Calling agent loop with
 * automatic, reverent fallback to [OfflineCatechistEngine] whenever offline or unconfigured.
 */
class LumenRepository(
    private val client: LumenApiClient = MistralLumenClient(),
    private val defaultApiKeyProvider: () -> String = { BuildConfig.MISTRAL_API_KEY },
    private val modelName: String = BuildConfig.MISTRAL_MODEL.ifBlank { LumenGuardrails.MISTRAL_MODEL },
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
        onToolStatus: (String) -> Unit = {},
    ): CatechistTurnResult {
        val clean = LumenGuardrails.sanitizeInput(userMessage)
        val key = resolveApiKey(userApiKeyOverride)
        if (!onlineEnabled || key.isBlank()) {
            return OfflineCatechistEngine.reply(clean, persona, context)
        }

        val systemPrompt = LumenGuardrails.conversationSystemPrompt(persona, context)
        val wireMessages = mutableListOf<MistralWireMessage>()
        history.takeLast(12).forEach { msg ->
            wireMessages += MistralWireMessage.Text(if (msg.fromUser) "user" else "assistant", msg.text)
        }
        wireMessages += MistralWireMessage.Text("user", clean)

        val collectedActions = mutableListOf<CatechistAction>()
        val collectedTrace = mutableListOf<String>()

        repeat(MAX_TOOL_ROUNDS) {
            val step = client.completeAgentStep(
                apiKey = key,
                systemPrompt = systemPrompt,
                messages = wireMessages,
                toolsJson = LumenTools.mistralToolsJson,
                model = modelName,
                temperature = 0.72,
            ).getOrNull() ?: return OfflineCatechistEngine.reply(clean, persona, context)

            if (step.toolCalls.isEmpty()) {
                val parsed = step.textContent?.let {
                    LumenJsonParser.parseConversationTurn(
                        rawText = it,
                        fallbackSet = context.activeMysterySet,
                        toolActions = collectedActions,
                        toolTrace = collectedTrace,
                    )
                }
                return parsed ?: OfflineCatechistEngine.reply(clean, persona, context)
            }

            // Execute EVERY parallel tool call returned by Mistral Large 4 and feed results back.
            wireMessages += MistralWireMessage.AssistantToolCalls(step.toolCalls)
            for (tc in step.toolCalls) {
                onToolStatus(LumenTools.liveProgressLabel(tc.name, persona))
                val out = LumenTools.execute(
                    toolName = tc.name,
                    argumentsJson = tc.argumentsJson,
                    bridge = toolBridge,
                    persona = persona,
                    fallbackMysterySet = context.activeMysterySet,
                )
                out.emittedAction?.let { collectedActions += it }
                out.traceLabel?.let { collectedTrace += it }
                wireMessages += MistralWireMessage.ToolResult(
                    toolCallId = tc.id,
                    name = tc.name,
                    content = out.resultJson,
                )
            }
            onToolStatus(persona.thinkingLine)
        }

        // If the model exhausted tool rounds without a final text turn, ask once for the closing reply.
        val closing = client.completeAgentStep(
            apiKey = key,
            systemPrompt = systemPrompt,
            messages = wireMessages + MistralWireMessage.Text("user", "Agora sintetize sua resposta final ao usuário sem chamar novas ferramentas."),
            toolsJson = "[]",
            model = modelName,
            temperature = 0.7,
        ).getOrNull()
        val parsed = closing?.textContent?.let {
            LumenJsonParser.parseConversationTurn(it, context.activeMysterySet, collectedActions, collectedTrace)
        }
        return parsed ?: OfflineCatechistEngine.reply(clean, persona, context)
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
