package com.sertumrosarum.domain.lumen

import com.sertumrosarum.domain.prayer.MysterySet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MistralAgentStreamTest {
    @Test
    fun streamEmitsOnlyFinishedWordsFromVisibleReplyField() {
        val events = listOf(
            event("""{"content":[{"type":"thinking","thinking":[{"type":"text","text":"internal thought"}]}]}"""),
            event(content("Segue a resposta:\n```json\n")),
            event(content("""{"reply":"Olá! Vamos rez""")),
            event(content("""ar juntos.\n\nT""")),
            event(content("""em fé \uD8""")),
            event(content("""3D\uDE0A","reference":"CIC","suggestedFollowUp":"Quero rezar."}"""), "stop"),
        )
        val updates = mutableListOf<MistralStepUpdate>()
        val step = MistralAgentStream { updates += it }.read(events.joinToString("").reader().buffered())
        assertEquals(
            listOf(
                MistralStepUpdate.Thinking,
                MistralStepUpdate.Reply("Olá! Vamos"),
                MistralStepUpdate.Reply("Olá! Vamos rezar juntos."),
                MistralStepUpdate.Reply("Olá! Vamos rezar juntos.\n\nTem fé"),
                MistralStepUpdate.Reply("Olá! Vamos rezar juntos.\n\nTem fé 😊"),
            ),
            updates,
        )
        val turn = requireNotNull(LumenJsonParser.parseConversationTurn(requireNotNull(step.textContent), MysterySet.JOYFUL))
        assertEquals("Olá! Vamos rezar juntos.\n\nTem fé 😊", turn.replyText)
        assertEquals("CIC", turn.reference)
        assertEquals("Quero rezar.", turn.suggestedFollowUp)
    }

    @Test
    fun streamAssemblesInterleavedToolCallsWithoutShowingArguments() {
        val events = listOf(
            event("""{"tool_calls":[{"index":0,"id":"garden","function":{"name":"read_my_garden","arguments":"{"}}]}"""),
            event("""{"tool_calls":[{"index":1,"id":"search","function":{"name":"search_treasury","arguments":"{\"query\":"}}]}"""),
            event("""{"tool_calls":[{"index":0,"function":{"arguments":"}"}},{"index":1,"function":{"arguments":"\"paz\"}"}}]}"""),
            event("{}", "tool_calls"),
        )
        val updates = mutableListOf<MistralStepUpdate>()
        val step = MistralAgentStream { updates += it }.read(events.joinToString("").reader().buffered())
        assertEquals(
            listOf(MistralStepUpdate.ToolCall("read_my_garden"), MistralStepUpdate.ToolCall("search_treasury")),
            updates,
        )
        assertEquals(
            listOf(
                MistralToolCall("garden", "read_my_garden", "{}"),
                MistralToolCall("search", "search_treasury", """{"query":"paz"}"""),
            ),
            step.toolCalls,
        )
        assertTrue(step.textContent.isNullOrBlank())
    }

    @Test
    fun interimReplyArrivesBeforeTheRestOfTheToolBatch() {
        val interim = "Vou buscar uma oração para este momento."
        val events = listOf(
            event("""{"tool_calls":[{"index":0,"id":"update","function":{"name":"send_interim_reply","arguments":"{\"message\":\"Vou buscar "}}]}"""),
            event("""{"tool_calls":[{"index":0,"function":{"arguments":"uma oração para este momento.\""}}]}"""),
            event("""{"tool_calls":[{"index":0,"function":{"arguments":"}"}}]}"""),
            event("""{"tool_calls":[{"index":1,"id":"search","function":{"name":"search_treasury","arguments":"{}"}}]}"""),
            event("{}", "tool_calls"),
        )
        val updates = mutableListOf<MistralStepUpdate>()
        val step = MistralAgentStream { updates += it }.read(events.joinToString("").reader().buffered())

        assertEquals(
            listOf(
                MistralStepUpdate.ToolCall("send_interim_reply"),
                MistralStepUpdate.InterimReply(interim),
                MistralStepUpdate.ToolCall("search_treasury"),
            ),
            updates,
        )
        assertEquals(2, step.toolCalls.size)
        assertEquals(interim, LumenTools.parseInterimReply(step.toolCalls.first().argumentsJson))
    }

    private fun content(text: String): String = """{"content":"${LumenJsonParser.escape(text)}"}"""

    private fun event(delta: String, finishReason: String? = null): String {
        val finish = finishReason?.let { "\"$it\"" } ?: "null"
        return "data: {\"choices\":[{\"delta\":$delta,\"finish_reason\":$finish}]}\n\n"
    }
}
