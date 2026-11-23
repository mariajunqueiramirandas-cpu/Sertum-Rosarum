package com.sertumrosarum.domain.lumen

import androidx.lifecycle.SavedStateHandle
import com.sertumrosarum.data.lumen.LumenApiClient
import com.sertumrosarum.data.lumen.LumenRepository
import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LumenAgentTest {

    @Test
    fun defaultPersonaIsMateusBlendAndAllFourResolve() {
        assertEquals(CatechistPersona.MATEUS, CatechistPersona.DEFAULT)
        assertEquals(4, CatechistPersona.entries.size)
        assertEquals(CatechistPersona.MATEUS, CatechistPersona.byId("mateus"))
        assertEquals(CatechistPersona.LUCAS, CatechistPersona.byId("lucas"))
        assertEquals(CatechistPersona.CLARA, CatechistPersona.byId("clara"))
        assertEquals(CatechistPersona.BENTO, CatechistPersona.byId("bento"))
        assertEquals(CatechistPersona.MATEUS, CatechistPersona.byId("unknown"))
    }

    @Test
    fun mistralRequestTargetsMistralLarge4WithJsonFormat() {
        val req = LumenJsonParser.buildMistralChatRequest(
            model = LumenGuardrails.MISTRAL_MODEL,
            systemPrompt = "Sistema",
            messages = listOf("user" to "Olá, Mateus!"),
        )
        assertTrue(req.contains("\"model\":\"mistral-large-4\""))
        assertTrue(req.contains("\"reasoning_effort\":\"low\""))
        assertTrue(req.contains("\"response_format\":{\"type\":\"json_object\"}"))
        assertFalse(req.contains("max_tokens"))
        assertTrue(req.contains("Olá, Mateus!"))
    }

    @Test
    fun parsesMistralConversationTurnWithThinkingAndTextBlocks() {
        val apiResponse = """
            {
              "choices": [
                {
                  "message": {
                    "role": "assistant",
                    "content": [
                      {"type": "thinking", "thinking": [{"type": "text", "text": "internal thought"}]},
                      {"type": "text", "text": "{\"reply\":\"Fica em paz, até Santa Teresa lutava contra distrações.\\n\\nVamos rezar uma dezena calma agora?\",\"reference\":\"CIC §2729 · Santa Teresa\",\"suggestedFollowUp\":\"Quero rezar uma dezena com calma.\",\"action\":{\"type\":\"start_quick_decade\",\"label\":\"Rezar 1º Mistério Doloroso\",\"subtitle\":\"Ritmo leve de um fôlego\",\"mysterySet\":\"SORROWFUL\",\"mysteryIndex\":0}}"}
                    ]
                  }
                }
              ]
            }
        """.trimIndent()
        val content = LumenJsonParser.extractMistralContent(apiResponse)
        assertNotNull(content)
        val turn = LumenJsonParser.parseConversationTurn(requireNotNull(content), MysterySet.JOYFUL)
        assertNotNull(turn)
        assertTrue(turn!!.replyText.contains("Santa Teresa"))
        assertEquals("CIC §2729 · Santa Teresa", turn.reference)
        assertEquals("Quero rezar uma dezena com calma.", turn.suggestedFollowUp)
        assertFalse(turn.isOfflineFallback)
        val action = turn.action as CatechistAction.StartQuickDecade
        assertEquals(MysterySet.SORROWFUL, action.mysterySet)
        assertEquals(0, action.mysteryIndex)
    }

    @Test
    fun catechistOfferIsNeverSentAsASuggestedReply() {
        val offer = "Quer que eu monte um terço personalizado pra hoje?"
        val raw = """{"reply":"$offer","suggestedFollowUp":"$offer"}"""
        val turn = requireNotNull(LumenJsonParser.parseConversationTurn(raw, MysterySet.JOYFUL))
        assertEquals(offer, turn.replyText)
        assertNull(turn.suggestedFollowUp)
    }

    @Test
    fun parsesCustomRosarySupplementJson() {
        val raw = """
            ```json
            {
              "userIntentionSummary": "Pela cirurgia da minha mãe",
              "preparatoryPrayer": "Vinde, Espírito Santo, trazei paz ao nosso coração.",
              "personalizedOffering": "Senhor Jesus, oferecemos esta coroa pela saúde da nossa mãe.",
              "decades": [
                {
                  "mysteryNumber": 1,
                  "adaptedReflection": "No Horto das Oliveiras, Cristo acolhe toda angústia.",
                  "spiritualFruitApplication": "Confiar a cirurgia ao Pai.",
                  "beadMeditations": ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"]
                }
              ]
            }
            ```
        """.trimIndent()
        val sup = LumenJsonParser.parseCustomRosarySupplement(
            rawJson = raw,
            mysterySet = MysterySet.SORROWFUL,
            fallbackIntention = "Fallback",
            mood = LumenMood.TIRED,
            persona = CatechistPersona.MATEUS,
        )
        assertNotNull(sup)
        assertEquals("Pela cirurgia da minha mãe", sup!!.userIntentionSummary)
        assertTrue(sup.suggestEyesClosed)
        assertEquals("No Horto das Oliveiras, Cristo acolhe toda angústia.", sup.decadeFor(0)?.adaptedReflection)
        assertEquals("3", sup.beadMeditationFor(0, 2))
        assertFalse(sup.isOfflineFallback)
    }

    @Test
    fun customSupplementNeverAltersFixedChurchPrayers() {
        val clock = Clock.fixed(Instant.parse("2026-11-24T12:00:00Z"), ZoneOffset.UTC)
        val vm = PrayerSessionViewModel(SavedStateHandle(), clock)
        val supplement = OfflineCatechistEngine.generateCustomRosary(
            intentionRaw = "Pela recuperação cirúrgica da minha mãe",
            mysterySet = MysterySet.SORROWFUL,
            mood = LumenMood.TIRED,
            persona = CatechistPersona.MATEUS,
        )
        vm.startCustomRosary(supplement)

        // Step through every step in the Rosary and confirm every FixedPrayer matches the immutable binary catalog.
        val state0 = vm.state.value
        assertEquals(supplement, state0.customSupplement)
        assertTrue(state0.meditated)

        state0.sequence.forEachIndexed { idx, step ->
            if (step.prayer != null) {
                val expected = step.prayer.textFor(state0.sessionMode)
                val stepState = state0.copy(stepIndex = idx)
                assertEquals("Fixed prayer ${step.prayer} must remain immutable", expected, stepState.prayerText)
                if (step.prayer == FixedPrayer.HAIL_MARY && step.decadeIndex != null && step.beadIndex != null) {
                    assertEquals(
                        supplement.beadMeditationFor(step.decadeIndex, step.beadIndex),
                        stepState.beadMeditation,
                    )
                }
            }
        }
    }

    @Test
    fun repositoryFallsBackGracefullyWhenMistralFailsOrOffline() = runBlocking {
        val failingClient = object : LumenApiClient {
            override suspend fun completeJson(
                apiKey: String,
                systemPrompt: String,
                messages: List<Pair<String, String>>,
                model: String,
                temperature: Double,
            ): Result<String> = Result.failure(RuntimeException("Offline"))
        }
        val repo = LumenRepository(client = failingClient, defaultApiKeyProvider = { "test-key" })
        val ctx = CatechistContext(activeMysterySet = MysterySet.SORROWFUL)

        val turn = repo.sendConversationTurn(
            userMessage = "Estou muito distraído hoje pra rezar",
            history = emptyList(),
            persona = CatechistPersona.CLARA,
            context = ctx,
            onlineEnabled = true,
        )
        assertTrue(turn.isOfflineFallback)
        assertTrue(turn.replyText.contains("São Filipe Néri"))

        val sup = repo.generateCustomRosary(
            intention = "Pela saúde da minha mãe",
            mysterySet = MysterySet.SORROWFUL,
            mood = LumenMood.ANXIOUS,
            persona = CatechistPersona.BENTO,
            onlineEnabled = false,
        )
        assertTrue(sup.isOfflineFallback)
        assertEquals(5, sup.decades.size)
        assertEquals(10, sup.decades.first().beadMeditations.size)
    }

    @Test
    fun contemplativeMemoryRecallsIntentionsFiveToFourteenDaysOld() {
        val now = 1_795_000_000_000L
        val dayMs = 86_400_000L
        val tooRecent = Triple("1", "Hoje", now - 2 * dayMs)
        val ideal = Triple("2", "Cirurgia da mamãe", now - 7 * dayMs)
        val tooOld = Triple("3", "Mês passado", now - 25 * dayMs)

        assertNull(OfflineCatechistEngine.recallIntention(listOf(tooRecent, tooOld), now))
        val recalled = OfflineCatechistEngine.recallIntention(listOf(tooRecent, ideal, tooOld), now)
        assertNotNull(recalled)
        assertEquals("2", recalled!!.intentionId)
        assertEquals(7, recalled.daysAgo)
        assertTrue(recalled.heading.contains("Cirurgia da mamãe"))
    }

    @Test
    fun agentToolsHaveValidJsonSchemasAndNoTokenCap() {
        val parsed = LumenJsonParser.parseValue(LumenTools.mistralToolsJson) as? List<*>
        assertNotNull(parsed)
        assertEquals(16, parsed!!.size)

        val agentReq = LumenJsonParser.buildMistralAgentRequest(
            model = LumenGuardrails.MISTRAL_MODEL,
            systemPrompt = "Sistema",
            messages = listOf(MistralWireMessage.Text("user", "Quero rezar agora")),
        )
        assertTrue(agentReq.contains("\"model\":\"mistral-large-4\""))
        assertTrue(agentReq.contains("\"tool_choice\":\"auto\""))
        assertTrue(agentReq.contains("build_custom_rosary"))
        assertTrue(agentReq.contains("read_my_garden"))
        assertTrue(agentReq.contains("send_interim_reply"))
        assertTrue(agentReq.contains("search_web_prayers"))
        assertFalse(agentReq.contains("max_tokens"))
    }

    @Test
    fun mistralWebSearchUsesBuiltinSearchAndKeepsOnlyCitedWebLinks() {
        val query = "Angelus em \"português\""
        val request = LumenJsonParser.parseValue(LumenJsonParser.buildMistralWebSearchRequest(query)) as Map<*, *>
        assertEquals(query, request["inputs"])
        assertEquals(false, request["store"])
        assertEquals(listOf(mapOf("type" to "web_search")), request["tools"])

        val response = """
            {
              "outputs": [
                {"type":"tool.execution","name":"web_search","info":"ignored"},
                {"type":"message.output","content":[
                  {"type":"text","text":"O Angelus está disponível em português."},
                  {"type":"tool_reference","title":"Angelus · Vatican News","url":"https://www.vaticannews.va/angelus.html"},
                  {"type":"tool_reference","title":"Repetido","url":"https://www.vaticannews.va/angelus.html"},
                  {"type":"tool_reference","title":"Arquivo","url":"file:///secret"},
                  {"type":"tool_reference","title":"Endereço inválido","url":"https://not-a-source@valid.example"}
                ]}
              ]
            }
        """.trimIndent()
        val result = requireNotNull(LumenJsonParser.parseMistralWebSearch(response))
        assertEquals("O Angelus está disponível em português.", result.summary)
        assertEquals(listOf(CatechistSource("Angelus · Vatican News", "https://www.vaticannews.va/angelus.html")), result.sources)
    }

    @Test
    fun readAndWriteToolsGroundAnswersInRealCatalogsAndMutateGarden() {
        var savedIntentionTitle: String? = null
        var markedThanksgivingId: String? = null
        var savedJournal: String? = null
        var bookmarked: Pair<String, String>? = null

        val bridge = object : LumenToolBridge {
            override fun readGarden() = GardenSnapshot(
                intentions = listOf(
                    GardenIntentionSnapshot("int-1", "Cirurgia da mamãe", "Recuperação", "Saúde", false, "", false, System.currentTimeMillis() - 8 * 86_400_000L),
                ),
            )
            override fun createIntention(title: String, description: String, category: String, thanksgiving: Boolean, selectForNextRosary: Boolean): String {
                savedIntentionTitle = title
                return "int-new"
            }
            override fun markIntentionThanksgiving(intentionId: String, note: String): Boolean {
                markedThanksgivingId = intentionId
                return true
            }
            override fun saveJournalReflection(text: String): Boolean {
                savedJournal = text
                return true
            }
            override fun bookmarkContent(kind: String, itemId: String): Boolean {
                bookmarked = kind to itemId
                return true
            }
        }

        val treasuryRes = LumenTools.execute("search_treasury", "{\"query\":\"Salve Rainha\"}", bridge, CatechistPersona.MATEUS, MysterySet.JOYFUL)
        assertTrue(treasuryRes.resultJson.contains("Salve"))

        val catechismRes = LumenTools.execute("search_catechism", "{\"query\":\"confissao\"}", bridge, CatechistPersona.CLARA, MysterySet.SORROWFUL)
        assertTrue(catechismRes.resultJson.contains("confession"))

        val mysteryRes = LumenTools.execute("read_mystery_study", "{\"mysterySet\":\"SORROWFUL\",\"mysteryNumber\":1}", bridge, CatechistPersona.BENTO, MysterySet.SORROWFUL)
        assertTrue(mysteryRes.resultJson.contains("historicalContext"))
        assertTrue(mysteryRes.resultJson.contains("saintsVoices"))

        val liturgyRes = LumenTools.execute("read_liturgy_and_guides", "{}", bridge, CatechistPersona.MATEUS, MysterySet.JOYFUL)
        assertTrue(liturgyRes.resultJson.contains("DIVINE_MERCY"))

        val gardenRes = LumenTools.execute("read_my_garden", "{}", bridge, CatechistPersona.MATEUS, MysterySet.JOYFUL)
        assertTrue(gardenRes.resultJson.contains("Cirurgia da mamãe"))

        LumenTools.execute("manage_intention", "{\"operation\":\"create\",\"title\":\"Pela paz em casa\",\"category\":\"Família\"}", bridge, CatechistPersona.MATEUS, MysterySet.JOYFUL)
        assertEquals("Pela paz em casa", savedIntentionTitle)

        LumenTools.execute("manage_intention", "{\"operation\":\"mark_thanksgiving\",\"intentionId\":\"int-1\",\"thanksgivingNote\":\"Alta recebida!\"}", bridge, CatechistPersona.MATEUS, MysterySet.JOYFUL)
        assertEquals("int-1", markedThanksgivingId)

        LumenTools.execute("save_journal_reflection", "{\"text\":\"Hoje confiei minhas preocupações a Deus.\"}", bridge, CatechistPersona.BENTO, MysterySet.JOYFUL)
        assertEquals("Hoje confiei minhas preocupações a Deus.", savedJournal)

        LumenTools.execute("bookmark_content", "{\"kind\":\"lesson\",\"itemId\":\"reconciliation\"}", bridge, CatechistPersona.CLARA, MysterySet.JOYFUL)
        assertEquals("lesson" to "reconciliation", bookmarked)

        val guideOut = LumenTools.execute("suggest_prayer_guide", "{\"guideName\":\"DIVINE_MERCY\",\"label\":\"Terço da Divina Misericórdia\"}", bridge, CatechistPersona.LUCAS, MysterySet.JOYFUL)
        assertTrue(guideOut.emittedAction is CatechistAction.LaunchGuide)

        val journeyOut = LumenTools.execute("open_app_content", "{\"targetType\":\"journey\",\"itemId\":\"confession\",\"label\":\"Preparar Confissão\"}", bridge, CatechistPersona.CLARA, MysterySet.JOYFUL)
        assertTrue(journeyOut.emittedAction is CatechistAction.OpenPracticeJourney)
    }

    @Test
    fun multiStepParallelMistralToolLoopExecutesToolsAndReturnsGroundedReply() = runBlocking {
        var stepCount = 0
        var markedId: String? = null
        val progress = mutableListOf<CatechistProgress>()
        val interimReplies = mutableListOf<String>()
        val interimReply = "Vou separar uma oração de agradecimento pela recuperação da sua mãe."

        val multiStepClient = object : LumenApiClient {
            override suspend fun completeJson(
                apiKey: String,
                systemPrompt: String,
                messages: List<Pair<String, String>>,
                model: String,
                temperature: Double,
            ): Result<String> = Result.failure(UnsupportedOperationException())

            override suspend fun completeAgentStep(
                apiKey: String,
                systemPrompt: String,
                messages: List<MistralWireMessage>,
                toolsJson: String,
                model: String,
                temperature: Double,
                onUpdate: (MistralStepUpdate) -> Unit,
            ): Result<MistralStepResponse> {
                stepCount++
                return when (stepCount) {
                    1 -> Result.success(
                        MistralStepResponse(
                            textContent = null,
                            toolCalls = listOf(
                                MistralToolCall("tc_1", "read_my_garden", "{}"),
                                MistralToolCall("tc_2", "read_liturgy_and_guides", "{}"),
                            ),
                            finishReason = "tool_calls",
                        ),
                    )
                    2 -> {
                        // Verify both tool results were returned back to Mistral!
                        val toolResults = messages.filterIsInstance<MistralWireMessage.ToolResult>()
                        assertEquals(2, toolResults.size)
                        Result.success(
                            MistralStepResponse(
                                textContent = null,
                                toolCalls = listOf(
                                    MistralToolCall("tc_3", "manage_intention", "{\"operation\":\"mark_thanksgiving\",\"intentionId\":\"int-mom\",\"thanksgivingNote\":\"Alta recebida!\"}"),
                                    MistralToolCall("tc_4", "suggest_prayer_guide", "{\"guideName\":\"DIVINE_MERCY\",\"label\":\"Rezar Divina Misericórdia em gratidão\",\"subtitle\":\"Para celebrar a recuperação da sua mãe\"}"),
                                    MistralToolCall("tc_5", "send_interim_reply", """{"message":"$interimReply"}"""),
                                ),
                                finishReason = "tool_calls",
                            ),
                        )
                    }
                    else -> {
                        assertEquals(listOf(interimReply), interimReplies)
                        assertTrue(messages.filterIsInstance<MistralWireMessage.ToolResult>().any {
                            it.toolCallId == "tc_5" && it.content == """{"ok":true}"""
                        })
                        onUpdate(MistralStepUpdate.Reply("Que notícia abençoada!"))
                        Result.success(
                            MistralStepResponse(
                                textContent = "{\"reply\":\"Que notícia abençoada! Já marquei a intenção pela sua mãe em ação de graças no seu Jardim e separei o Terço da Divina Misericórdia pra gente agradecer agora.\",\"reference\":\"1Ts 5, 18 · CIC §2637\",\"suggestedFollowUp\":\"Quero anotar essa graça no Diário.\"}",
                                toolCalls = emptyList(),
                                finishReason = "stop",
                            ),
                        )
                    }
                }
            }
        }

        val bridge = object : LumenToolBridge {
            override fun readGarden() = GardenSnapshot(
                intentions = listOf(GardenIntentionSnapshot("int-mom", "Cirurgia da mamãe", "", "Saúde", false, "", false, System.currentTimeMillis() - 7 * 86_400_000L)),
            )
            override fun markIntentionThanksgiving(intentionId: String, note: String): Boolean {
                markedId = intentionId
                return true
            }
        }

        val repo = LumenRepository(client = multiStepClient, defaultApiKeyProvider = { "dev-key" })
        val turn = repo.sendConversationTurn(
            userMessage = "Minha mãe teve alta da cirurgia! Quero agradecer rezando.",
            history = emptyList(),
            persona = CatechistPersona.MATEUS,
            context = CatechistContext(activeMysterySet = MysterySet.GLORIOUS),
            toolBridge = bridge,
            onlineEnabled = true,
            onInterimReply = { interimReplies += it },
            onProgress = { progress += it },
        )

        assertEquals(3, stepCount)
        assertEquals("int-mom", markedId)
        assertFalse(turn.isOfflineFallback)
        assertTrue(turn.replyText.contains("ação de graças"))
        assertEquals(1, turn.actions.size)
        assertTrue(turn.actions.first() is CatechistAction.LaunchGuide)
        assertTrue(turn.toolTrace.contains("Consultou o Meu Jardim"))
        assertTrue(turn.toolTrace.contains("Marcou em ação de graças"))
        assertTrue(progress.any { it.status == "Mateus está olhando o seu Jardim…" })
        assertTrue(progress.any { "Consultou o Meu Jardim" in it.toolTrace && it.replyPreview == null })
        assertEquals("Que notícia abençoada!", progress.last().replyPreview)
        assertEquals(turn.toolTrace, progress.last().toolTrace)
    }
}
