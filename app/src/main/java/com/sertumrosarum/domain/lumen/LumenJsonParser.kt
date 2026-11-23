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
 * Pure-Kotlin JSON helper for Mistral Large 4 agentic payloads (with native function calling)
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

    /** Multi-step agentic request with Mistral Large 4 native Function Calling (`tools`) and NO token limit. */
    fun buildMistralAgentRequest(
        model: String,
        systemPrompt: String,
        messages: List<MistralWireMessage>,
        toolsJson: String = LumenTools.mistralToolsJson,
        temperature: Double = 0.72,
        reasoningEffort: String = "low",
    ): String = buildString {
        append("{")
        append("\"model\":\"").append(escape(model)).append("\",")
        append("\"reasoning_effort\":\"").append(escape(reasoningEffort)).append("\",")
        append("\"temperature\":").append(temperature).append(",")
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
    ): CatechistTurnResult? {
        val trimmed = rawText.trim()
        if (trimmed.isBlank()) return null
        val cleaned = stripCodeFence(trimmed)
        val obj = parseValue(cleaned) as? Map<*, *>
        if (obj != null) {
            val reply = (obj["reply"] as? String)?.trim()?.takeIf { it.isNotBlank() }
            if (reply != null) {
                val reference = (obj["reference"] as? String)?.trim()?.takeIf { it.isNotBlank() && !it.equals("null", true) }
                val suggested = (obj["suggestedFollowUp"] as? String)?.trim()?.takeIf { it.isNotBlank() && !it.equals("null", true) }
                val inlineAction = (obj["action"] as? Map<*, *>)?.let { parseAction(it, fallbackSet) }
                val allActions = (toolActions + listOfNotNull(inlineAction)).distinctBy { "${it::class.simpleName}:${it.label}" }
                return CatechistTurnResult(
                    replyText = reply,
                    reference = reference,
                    actions = allActions,
                    toolTrace = toolTrace.distinct(),
                    suggestedFollowUp = suggested,
                    isOfflineFallback = false,
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

    private fun serializeSimple(value: Any?): String = when (value) {
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

    private class Parser(private val src: String) {
        private var pos = 0

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

        private fun parseString(): String? {
            pos++ // '"'
            val sb = StringBuilder()
            while (pos < src.length) {
                val c = src[pos++]
                when (c) {
                    '"' -> return sb.toString()
                    '\\' -> {
                        if (pos >= src.length) return null
                        when (val esc = src[pos++]) {
                            '"', '\\', '/' -> sb.append(esc)
                            'b' -> sb.append('\b')
                            'f' -> sb.append('\u000C')
                            'n' -> sb.append('\n')
                            'r' -> sb.append('\r')
                            't' -> sb.append('\t')
                            'u' -> {
                                if (pos + 4 > src.length) return null
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
            return null
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
