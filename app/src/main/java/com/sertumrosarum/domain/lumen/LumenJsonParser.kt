package com.sertumrosarum.domain.lumen

import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.treasury.TreasuryCatalog

data class MistralToolCall(
    val id: String,
    val name: String,
    val argumentsJson: String,
)

sealed interface MistralWireMessage {
    data class Text(val role: String, val content: String) : MistralWireMessage
    data class AssistantToolCalls(val toolCalls: List<MistralToolCall>) : MistralWireMessage
    data class ToolResult(val toolCallId: String, val name: String, val content: String) : MistralWireMessage
}

data class MistralStepResponse(
    val textContent: String?,
    val toolCalls: List<MistralToolCall>,
    val finishReason: String?,
)

/**
 * Pure-Kotlin JSON helper for Mistral API payloads with native function calling
 * and strict Lumen schemas. Never sets `max_tokens` so responses are never artificially truncated.
 */
object LumenJsonParser {

    fun escape(value: String): String = buildString(value.length + 8) {
        value.forEach { ch ->
            when (ch) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> if (ch.code < 0x20) append("\\u%04x".format(ch.code)) else append(ch)
            }
        }
    }

    /** Single-shot structured JSON request (used for direct Custom Rosary sheet generation). */
    fun buildMistralChatRequest(
        model: String,
        systemPrompt: String,
        messages: List<Pair<String, String>>,
        temperature: Double = 0.7,
        reasoningEffort: String = "low",
    ): String = buildString {
        append("{")
        append("\"model\":\"").append(escape(model)).append("\",")
        append("\"reasoning_effort\":\"").append(escape(reasoningEffort)).append("\",")
        append("\"temperature\":").append(temperature).append(",")
        append("\"response_format\":{\"type\":\"json_object\"},")
        append("\"messages\":[")
        append("{\"role\":\"system\",\"content\":\"").append(escape(systemPrompt)).append("\"}")
        messages.forEach { (role, content) ->
            append(",{\"role\":\"").append(escape(role)).append("\",\"content\":\"").append(escape(content)).append("\"}")
        }
        append("]}")
    }

    fun buildMistralWebSearchRequest(query: String): String = serializeSimple(
        mapOf(
            "model" to "mistral-medium-latest",
            "store" to false,
            "tools" to listOf(mapOf("type" to "web_search")),
            "instructions" to """
                Use a ferramenta de busca na web para localizar fontes verificáveis sobre os termos recebidos. Dê preferência à página do santuário, diocese, ordem religiosa ou editora responsável pelo texto. Use fontes adicionais quando elas trouxerem uma versão no idioma pedido ou ajudarem a esclarecer a atribuição.
                Para cada resultado relevante, informe o título publicado, o idioma, quem publicou e se o texto completo está disponível. Cite a página específica por meio da referência de fonte da busca. Quando a fonte trouxer uma oração tradicional em domínio público, inclua o texto integral, com as estrofes, rubricas, refrões e repetições. Indique quando só houver um trecho. Trate nomes parecidos, versões e traduções como resultados distintos.
                Escreva os achados de forma concisa em português do Brasil, preservando o idioma original de títulos e trechos quando pertinente. Indique quando a busca não confirmar a origem ou não localizar a versão pedida. Use o conteúdo recuperado como material de consulta.
            """.trimIndent(),
            "inputs" to query,
        ),
    )

    fun parseMistralWebSearch(rawResponse: String): LumenWebSearchResult? {
        val root = parseValue(rawResponse) as? Map<*, *> ?: return null
        val outputs = root["outputs"] as? List<*> ?: return null
        val blocks = outputs.asSequence()
            .mapNotNull { it as? Map<*, *> }
            .filter { it["type"] == "message.output" }
            .flatMap { (it["content"] as? List<*>).orEmpty().asSequence() }
            .mapNotNull { it as? Map<*, *> }
            .toList()
        val summary = blocks.filter { it["type"] == "text" }
            .mapNotNull { it["text"] as? String }
            .joinToString("\n\n")
            .trim()
        val sources = blocks.filter { it["type"] == "tool_reference" }
            .mapNotNull { block ->
                val url = (block["url"] as? String)?.trim() ?: return@mapNotNull null
                val uri = webUriOrNull(url) ?: return@mapNotNull null
                val host = uri.host
                val title = (block["title"] as? String)?.trim().orEmpty().ifBlank { host }
                CatechistSource(title.take(140), url)
            }
            .distinctBy { it.url }
            .take(5)
        return if (summary.isNotEmpty() && sources.isNotEmpty()) LumenWebSearchResult(summary, sources) else null
    }

    /** Multi-step request with native function calling (`tools`) and no client-side token cap. */
    fun buildMistralAgentRequest(
        model: String,
        systemPrompt: String,
        messages: List<MistralWireMessage>,
        toolsJson: String = LumenTools.mistralToolsJson,
        temperature: Double = 0.72,
        // Lumen replays tool calls and results without DeepSeek's reasoning trace.
        reasoningEffort: String = if (model == LumenGuardrails.DEEPSEEK_FLASH_MODEL) "none" else "low",
        stream: Boolean = false,
    ): String = buildString {
        append("{")
        append("\"model\":\"").append(escape(model)).append("\",")
        append("\"reasoning_effort\":\"").append(escape(reasoningEffort)).append("\",")
        append("\"temperature\":").append(temperature).append(",")
        if (stream) append("\"stream\":true,")
        append("\"tools\":").append(toolsJson).append(",")
        append("\"tool_choice\":\"auto\",")
        append("\"messages\":[")
        append("{\"role\":\"system\",\"content\":\"").append(escape(systemPrompt)).append("\"}")
        messages.forEach { msg ->
            append(",")
            when (msg) {
                is MistralWireMessage.Text -> {
                    append("{\"role\":\"").append(escape(msg.role))
                        .append("\",\"content\":\"").append(escape(msg.content)).append("\"}")
                }
                is MistralWireMessage.AssistantToolCalls -> {
                    append("{\"role\":\"assistant\",\"content\":\"\",\"tool_calls\":[")
                    msg.toolCalls.forEachIndexed { idx, tc ->
                        if (idx > 0) append(",")
                        append("{\"id\":\"").append(escape(tc.id))
                            .append("\",\"type\":\"function\",\"function\":{\"name\":\"")
                            .append(escape(tc.name))
                            .append("\",\"arguments\":\"")
                            .append(escape(tc.argumentsJson))
                            .append("\"}}")
                    }
                    append("]}")
                }
                is MistralWireMessage.ToolResult -> {
                    append("{\"role\":\"tool\",\"tool_call_id\":\"").append(escape(msg.toolCallId))
                        .append("\",\"name\":\"").append(escape(msg.name))
                        .append("\",\"content\":\"").append(escape(msg.content)).append("\"}")
                }
            }
        }
        append("]}")
    }

    /** Parses one Mistral `/v1/chat/completions` step, extracting both `tool_calls` and final `text` blocks. */
    fun parseMistralStep(rawResponse: String): MistralStepResponse? {
        val root = parseValue(rawResponse) as? Map<*, *> ?: return null
        val choices = root["choices"] as? List<*> ?: return null
        val first = choices.firstOrNull() as? Map<*, *> ?: return null
        val finishReason = first["finish_reason"] as? String
        val message = first["message"] as? Map<*, *> ?: return null

        val rawToolCalls = message["tool_calls"] as? List<*> ?: emptyList<Any>()
        val toolCalls = rawToolCalls.mapNotNull { item ->
            val obj = item as? Map<*, *> ?: return@mapNotNull null
            val id = (obj["id"] as? String)?.trim()?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val fn = obj["function"] as? Map<*, *> ?: return@mapNotNull null
            val name = (fn["name"] as? String)?.trim()?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val args = when (val rawArgs = fn["arguments"]) {
                is String -> rawArgs
                is Map<*, *> -> serializeSimple(rawArgs)
                else -> "{}"
            }
            MistralToolCall(id = id, name = name, argumentsJson = args)
        }

        val textContent = extractMessageText(message["content"])
        return MistralStepResponse(
            textContent = textContent,
            toolCalls = toolCalls,
            finishReason = finishReason,
        )
    }

    /** Extracts the final text payload from a Mistral `/v1/chat/completions` JSON response, skipping thinking blocks. */
    fun extractMistralContent(rawResponse: String): String? = parseMistralStep(rawResponse)?.textContent

    private fun extractMessageText(content: Any?): String? = when (content) {
        is String -> content.trim().takeIf { it.isNotBlank() }
        is List<*> -> {
            val textBlocks = content.mapNotNull { item ->
                val block = item as? Map<*, *> ?: return@mapNotNull null
                val blockType = block["type"] as? String
                if (blockType == "thinking") return@mapNotNull null
                block["text"] as? String
            }
            textBlocks.joinToString("\n").trim().takeIf { it.isNotBlank() }
        }
        else -> null
    }

    fun parseConversationTurn(
        rawText: String,
        fallbackSet: MysterySet,
        toolActions: List<CatechistAction> = emptyList(),
        toolTrace: List<String> = emptyList(),
        prayerSources: List<CatechistSource> = emptyList(),
    ): CatechistTurnResult? {
        val trimmed = rawText.trim()
        if (trimmed.isBlank()) return null
        val cleaned = stripCodeFence(trimmed)
        val obj = parseValue(cleaned) as? Map<*, *>
        if (obj != null) {
            val reply = (obj["reply"] as? String)?.trim()?.takeIf { it.isNotBlank() }
            if (reply != null) {
                val reference = (obj["reference"] as? String)?.trim()?.takeIf { it.isNotBlank() && !it.equals("null", true) }
                val suggested = (obj["suggestedFollowUp"] as? String)?.trim()?.takeIf {
                    it.startsWith("Quero ", ignoreCase = true) && it.length in 7..140 &&
                        it.none { ch -> ch == '\n' || ch == '\r' || ch == '?' }
                }
                val inlineAction = (obj["action"] as? Map<*, *>)?.let { parseAction(it, fallbackSet) }
                val allActions = (toolActions + listOfNotNull(inlineAction)).distinctBy { "${it::class.simpleName}:${it.label}" }
                val prayers = (obj["prayers"] as? List<*>).orEmpty().asSequence()
                    .mapNotNull { (it as? Map<*, *>)?.let { fields -> CatechistPrayerCatalog.fromModel(fields, prayerSources) } }
                    .distinctBy { it.id }
                    .take(4)
                    .toList()
                return CatechistTurnResult(
                    replyText = reply,
                    reference = reference,
                    actions = allActions,
                    toolTrace = toolTrace.distinct(),
                    suggestedFollowUp = suggested,
                    isOfflineFallback = false,
                    prayers = prayers,
                )
            }
        }
        // Graceful plain-text acceptance when Mistral finishes an agentic tool loop in natural prose.
        return CatechistTurnResult(
            replyText = trimmed,
            reference = null,
            actions = toolActions.distinctBy { "${it::class.simpleName}:${it.label}" },
            toolTrace = toolTrace.distinct(),
            suggestedFollowUp = null,
            isOfflineFallback = false,
        )
    }

    internal fun previewConversationReply(rawText: String): String? {
        val start = rawText.indexOf('{')
        if (start == -1) return null
        return Parser(rawText, start).replyPreview()?.trim()?.takeIf { it.isNotEmpty() }
    }

    fun parseCustomRosarySupplement(
        rawJson: String,
        mysterySet: MysterySet,
        fallbackIntention: String,
        mood: LumenMood?,
        persona: CatechistPersona,
    ): CustomRosarySupplement? {
        val cleaned = stripCodeFence(rawJson)
        val obj = parseValue(cleaned) as? Map<*, *> ?: return null
        val summary = (obj["userIntentionSummary"] as? String)?.trim()?.takeIf { it.isNotBlank() } ?: fallbackIntention
        val offering = (obj["personalizedOffering"] as? String)?.trim()?.takeIf { it.isNotBlank() } ?: return null
        val prep = (obj["preparatoryPrayer"] as? String)?.trim()?.takeIf { it.isNotBlank() && !it.equals("null", true) }
        val decadesRaw = obj["decades"] as? List<*> ?: emptyList<Any>()
        val decades = decadesRaw.mapIndexedNotNull { idx, item ->
            val m = item as? Map<*, *> ?: return@mapIndexedNotNull null
            val num = (m["mysteryNumber"] as? Number)?.toInt()?.coerceIn(1, 5) ?: (idx + 1)
            val reflection = (m["adaptedReflection"] as? String)?.trim()?.takeIf { it.isNotBlank() } ?: return@mapIndexedNotNull null
            val fruit = (m["spiritualFruitApplication"] as? String)?.trim().orEmpty()
            val beads = (m["beadMeditations"] as? List<*>)
                ?.mapNotNull { (it as? String)?.trim()?.takeIf { s -> s.isNotBlank() } }
                ?.take(10)
                .orEmpty()
            CustomRosaryDecade(num, reflection, fruit, beads)
        }.sortedBy { it.mysteryNumber }
        if (decades.isEmpty()) return null
        return CustomRosarySupplement(
            mysterySet = mysterySet,
            userIntentionSummary = summary,
            personalizedOffering = offering,
            preparatoryPrayer = prep,
            suggestEyesClosed = mood?.suggestEyesClosed == true,
            decades = decades,
            personaId = persona.id,
            isOfflineFallback = false,
        )
    }

    private fun parseAction(map: Map<*, *>, fallbackSet: MysterySet): CatechistAction? {
        val type = (map["type"] as? String)?.trim()?.lowercase() ?: return null
        val label = (map["label"] as? String)?.trim()?.takeIf { it.isNotBlank() } ?: return null
        val subtitle = (map["subtitle"] as? String)?.trim().orEmpty()
        return when (type) {
            "start_quick_decade" -> {
                val setName = (map["mysterySet"] as? String)?.trim()
                val set = MysterySet.entries.firstOrNull { it.name.equals(setName, true) } ?: fallbackSet
                val idx = ((map["mysteryIndex"] as? Number)?.toInt() ?: 0).coerceIn(0, 4)
                CatechistAction.StartQuickDecade(label, subtitle, set, idx, meditated = true)
            }
            "save_intention" -> {
                val title = (map["intentionTitle"] as? String)?.trim()?.takeIf { it.isNotBlank() } ?: return null
                val desc = (map["intentionDescription"] as? String)?.trim().orEmpty()
                val cat = (map["category"] as? String)?.trim()?.takeIf { it.isNotBlank() } ?: "Família"
                CatechistAction.SaveIntention(label, subtitle, title, desc, cat)
            }
            "open_prayer" -> {
                val id = (map["prayerId"] as? String)?.trim() ?: return null
                val resolved = TreasuryCatalog.find(id)?.id ?: return null
                CatechistAction.OpenTreasuryPrayer(label, subtitle, resolved)
            }
            "open_lesson" -> {
                val id = (map["lessonId"] as? String)?.trim() ?: return null
                val resolved = FormationCatalog.lesson(id)?.id ?: return null
                CatechistAction.OpenFormationLesson(label, subtitle, resolved)
            }
            else -> null
        }
    }

    internal fun serializeSimple(value: Any?): String = when (value) {
        null -> "null"
        is Boolean, is Number -> value.toString()
        is String -> "\"${escape(value)}\""
        is List<*> -> value.joinToString(",", "[", "]") { serializeSimple(it) }
        is Map<*, *> -> value.entries.joinToString(",", "{", "}") { (k, v) -> "\"${escape(k.toString())}\":${serializeSimple(v)}" }
        else -> "\"${escape(value.toString())}\""
    }

    internal fun stripCodeFence(raw: String): String {
        val trimmed = raw.trim()
        if (!trimmed.startsWith("```")) return extractFirstJsonObject(trimmed)
        val noOpen = trimmed.removePrefix("```json").removePrefix("```JSON").removePrefix("```").trimStart()
        val noClose = if (noOpen.endsWith("```")) noOpen.dropLast(3).trimEnd() else noOpen
        return extractFirstJsonObject(noClose)
    }

    private fun extractFirstJsonObject(text: String): String {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        return if (start >= 0 && end > start) text.substring(start, end + 1) else text
    }

    internal fun parseValue(json: String): Any? = Parser(json).parse()

    private class Parser(private val src: String, private var pos: Int = 0) {
        fun replyPreview(): String? {
            pos++
            while (true) {
                skipWs()
                if (src.getOrNull(pos) != '"') return null
                val key = parseString() ?: return null
                skipWs()
                if (src.getOrNull(pos++) != ':') return null
                skipWs()
                if (key == "reply") {
                    return if (src.getOrNull(pos) == '"') parseString(allowPrefix = true) else null
                }
                val valueStart = pos
                parse()
                if (pos == valueStart) return null
                skipWs()
                if (src.getOrNull(pos++) != ',') return null
            }
        }

        fun parse(): Any? {
            skipWs()
            if (pos >= src.length) return null
            return when (src[pos]) {
                '{' -> parseObject()
                '[' -> parseArray()
                '"' -> parseString()
                't', 'f' -> parseBoolean()
                'n' -> parseNull()
                '-', in '0'..'9' -> parseNumber()
                else -> null
            }
        }

        private fun parseObject(): Map<String, Any?>? {
            pos++ // '{'
            val map = LinkedHashMap<String, Any?>()
            skipWs()
            if (pos < src.length && src[pos] == '}') { pos++; return map }
            while (pos < src.length) {
                skipWs()
                if (pos >= src.length || src[pos] != '"') return null
                val key = parseString() ?: return null
                skipWs()
                if (pos >= src.length || src[pos] != ':') return null
                pos++
                val value = parse()
                map[key] = value
                skipWs()
                if (pos >= src.length) return map
                when (src[pos]) {
                    ',' -> pos++
                    '}' -> { pos++; return map }
                    else -> return null
                }
            }
            return map
        }

        private fun parseArray(): List<Any?>? {
            pos++ // '['
            val list = ArrayList<Any?>()
            skipWs()
            if (pos < src.length && src[pos] == ']') { pos++; return list }
            while (pos < src.length) {
                list.add(parse())
                skipWs()
                if (pos >= src.length) return list
                when (src[pos]) {
                    ',' -> pos++
                    ']' -> { pos++; return list }
                    else -> return null
                }
            }
            return list
        }

        private fun parseString(allowPrefix: Boolean = false): String? {
            pos++ // '"'
            val sb = StringBuilder()
            readString@ while (pos < src.length) {
                val c = src[pos++]
                when (c) {
                    '"' -> return sb.toString()
                    '\\' -> {
                        if (pos >= src.length) break@readString
                        when (val esc = src[pos++]) {
                            '"', '\\', '/' -> sb.append(esc)
                            'b' -> sb.append('\b')
                            'f' -> sb.append('\u000C')
                            'n' -> sb.append('\n')
                            'r' -> sb.append('\r')
                            't' -> sb.append('\t')
                            'u' -> {
                                if (pos + 4 > src.length) break@readString
                                val code = src.substring(pos, pos + 4).toIntOrNull(16) ?: return null
                                sb.append(code.toChar())
                                pos += 4
                            }
                            else -> sb.append(esc)
                        }
                    }
                    else -> sb.append(c)
                }
            }
            return if (allowPrefix) sb.substring(0, sb.indexOfLast { it.isWhitespace() } + 1) else null
        }

        private fun parseBoolean(): Boolean? = when {
            src.startsWith("true", pos) -> { pos += 4; true }
            src.startsWith("false", pos) -> { pos += 5; false }
            else -> null
        }

        private fun parseNull(): Any? {
            if (src.startsWith("null", pos)) pos += 4
            return null
        }

        private fun parseNumber(): Number? {
            val start = pos
            if (pos < src.length && src[pos] == '-') pos++
            while (pos < src.length && src[pos] in '0'..'9') pos++
            var isFloat = false
            if (pos < src.length && src[pos] == '.') {
                isFloat = true; pos++
                while (pos < src.length && src[pos] in '0'..'9') pos++
            }
            if (pos < src.length && (src[pos] == 'e' || src[pos] == 'E')) {
                isFloat = true; pos++
                if (pos < src.length && (src[pos] == '+' || src[pos] == '-')) pos++
                while (pos < src.length && src[pos] in '0'..'9') pos++
            }
            val token = src.substring(start, pos)
            return if (isFloat) token.toDoubleOrNull() else token.toLongOrNull() ?: token.toDoubleOrNull()
        }

        private fun skipWs() {
            while (pos < src.length && src[pos].isWhitespace()) pos++
        }
    }
}
