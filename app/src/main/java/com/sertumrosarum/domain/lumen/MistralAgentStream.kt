package com.sertumrosarum.domain.lumen

import java.io.BufferedReader

sealed interface MistralStepUpdate {
    data object Thinking : MistralStepUpdate
    data class ToolCall(val name: String) : MistralStepUpdate
    data class InterimReply(val text: String) : MistralStepUpdate
    data class Reply(val text: String) : MistralStepUpdate
}

/** Reassembles a single Mistral completion from its server-sent events. */
internal class MistralAgentStream(private val onUpdate: (MistralStepUpdate) -> Unit) {
    private class ToolCall {
        var id = ""
        var name = ""
        val arguments = StringBuilder()
        var interimReplySent = false
    }

    private val content = StringBuilder()
    private val toolCalls = sortedMapOf<Int, ToolCall>()
    private var finishReason: String? = null
    private var reportedThinking = false
    private var replyPreview: String? = null

    fun read(reader: BufferedReader): MistralStepResponse {
        val event = StringBuilder()
        var line = reader.readLine()?.removePrefix("\uFEFF")
        while (line != null) {
            if (line.isEmpty() && event.isNotEmpty()) {
                if (event.toString() == "[DONE]") break
                consume(event.toString())
                event.clear()
                if (finishReason != null) break
            } else if (line.startsWith("data:")) {
                if (event.isNotEmpty()) event.append('\n')
                event.append(line.removePrefix("data:").removePrefix(" "))
            }
            line = reader.readLine()
        }
        check(finishReason == "stop" || finishReason == "tool_calls") { "Resposta Mistral interrompida." }
        return MistralStepResponse(
            textContent = content.toString().takeIf { it.isNotBlank() },
            toolCalls = toolCalls.values.map { call ->
                check(call.id.isNotBlank() && call.name.isNotBlank()) { "Chamada de ferramenta Mistral incompleta." }
                MistralToolCall(call.id, call.name, call.arguments.toString().ifBlank { "{}" })
            },
            finishReason = finishReason,
        )
    }

    private fun consume(event: String) {
        val root = LumenJsonParser.parseValue(event) as? Map<*, *>
            ?: error("Evento Mistral inválido.")
        check(root["error"] == null) { "Erro no streaming Mistral." }
        val choice = (root["choices"] as? List<*>)?.firstOrNull() as? Map<*, *> ?: return
        finishReason = (choice["finish_reason"] as? String) ?: finishReason
        val delta = choice["delta"] as? Map<*, *> ?: return
        (delta["tool_calls"] as? List<*>)?.forEachIndexed { index, call ->
            addToolCall(call as? Map<*, *> ?: return@forEachIndexed, index)
        }
        val blocks = delta["content"]
        if (!reportedThinking && (delta["reasoning_content"] is String ||
                (blocks as? List<*>)?.any { (it as? Map<*, *>)?.get("type") == "thinking" } == true)
        ) {
            reportedThinking = true
            if (toolCalls.isEmpty() && replyPreview == null) onUpdate(MistralStepUpdate.Thinking)
        }

        val previousLength = content.length
        when (blocks) {
            is String -> content.append(blocks)
            is List<*> -> blocks.forEach { block ->
                val part = block as? Map<*, *> ?: return@forEach
                if (part["type"] == "text") (part["text"] as? String)?.let(content::append)
            }
        }
        if (toolCalls.isNotEmpty() || content.length == previousLength) return
        val preview = LumenJsonParser.previewConversationReply(content.toString())
        if (preview != null && preview != replyPreview) {
            replyPreview = preview
            onUpdate(MistralStepUpdate.Reply(preview))
        }
    }

    private fun addToolCall(delta: Map<*, *>, position: Int) {
        val index = (delta["index"] as? Number)?.toInt() ?: position
        val call = toolCalls.getOrPut(index) { ToolCall() }
        (delta["id"] as? String)?.takeIf { it.isNotBlank() && it != "null" }?.let { call.id = it }
        val function = delta["function"] as? Map<*, *> ?: return
        (function["name"] as? String)?.takeIf { it.isNotBlank() && it != call.name }?.let { name ->
            call.name += name
            onUpdate(MistralStepUpdate.ToolCall(call.name))
        }
        when (val args = function["arguments"]) {
            is String -> call.arguments.append(args)
            is Map<*, *> -> call.arguments.append(LumenJsonParser.serializeSimple(args))
        }
        if (call.name == LumenTools.SEND_INTERIM_REPLY && !call.interimReplySent && call.arguments.endsWith('}')) {
            LumenTools.parseInterimReply(call.arguments.toString())?.let { reply ->
                call.interimReplySent = true
                onUpdate(MistralStepUpdate.InterimReply(reply))
            }
        }
    }
}
