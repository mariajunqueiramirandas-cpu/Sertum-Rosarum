package com.sertumrosarum

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import com.github.takahirom.roborazzi.captureRoboImage
import com.sertumrosarum.data.lumen.LumenApiClient
import com.sertumrosarum.data.lumen.LumenRepository
import com.sertumrosarum.domain.lumen.CatechistSource
import com.sertumrosarum.domain.lumen.LumenWebSearchResult
import com.sertumrosarum.domain.lumen.MistralStepResponse
import com.sertumrosarum.domain.lumen.MistralStepUpdate
import com.sertumrosarum.domain.lumen.MistralToolCall
import com.sertumrosarum.domain.lumen.MistralWireMessage
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class CatechistFlowTest {

    @get:Rule
    val compose = createComposeRule()

    private fun fakeLumen(): LumenRepository {
        val fakeClient = object : LumenApiClient {
            override suspend fun completeJson(
                apiKey: String,
                systemPrompt: String,
                messages: List<Pair<String, String>>,
                model: String,
                temperature: Double,
            ): Result<String> = Result.failure(IllegalStateException("Offline test mode"))
        }
        return LumenRepository(client = fakeClient, defaultApiKeyProvider = { "" })
    }

    private fun shot(name: String) {
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    @Test
    fun openCatechistSwitchPersonaSendMessageAndLaunchPrayer() {
        val clock = Clock.fixed(Instant.parse("2026-11-24T12:00:00Z"), ZoneOffset.UTC)
        val model = PrayerSessionViewModel(SavedStateHandle(), clock)
        compose.setContent {
            SertumApp(model = model, repository = null, lumenRepository = fakeLumen())
        }

        // Open Catechist from HomeDashboard
        compose.onNodeWithTag("home-open-catechist").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("catechist-screen").assertIsDisplayed()
        compose.onNodeWithText("Mateus").assertIsDisplayed()
        shot("catechist-welcome-mateus")

        // Switch persona to Clara
        compose.onNodeWithTag("catechist-persona-trigger").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("persona-sheet").assertIsDisplayed()
        shot("catechist-persona-picker")
        compose.onNodeWithTag("persona-option-clara").performScrollTo().performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
        compose.onNodeWithText("Clara").assertIsDisplayed()

        // Send a message about distraction and receive an actionable card
        compose.onNodeWithTag("catechist-input").performTextInput("Tô muito distraído hoje pra rezar")
        compose.onNodeWithTag("catechist-send").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("catechist-reply").assertIsDisplayed()
        shot("catechist-conversation-clara")
        compose.onNodeWithTag("catechist-action-cta").performScrollTo().performClick()
        compose.waitForIdle()

        // Action launches straight into ImmersivePrayerScreen
        compose.onNodeWithTag("prayer-screen").assertIsDisplayed()
    }

    @Test
    fun showsInterimReplyDuringWebSearchAndKeepsItWithTheCitedFinalReply() {
        val toolReady = CompletableDeferred<Unit>()
        val replyReady = CompletableDeferred<Unit>()
        val secondSearchStarted = CompletableDeferred<Unit>()
        val interimReply = "Vou procurar uma oração de hoje e conferir a fonte."
        val suggestedReply = "Quero um terço personalizado para hoje."
        val webSource = CatechistSource("Angelus · Vatican News", "https://www.vaticannews.va/pt/utility/prayers/angelus.html")
        var openedUrl: String? = null
        var step = 0
        val client = object : LumenApiClient {
            override suspend fun completeJson(
                apiKey: String,
                systemPrompt: String,
                messages: List<Pair<String, String>>,
                model: String,
                temperature: Double,
            ): Result<String> = error("Unexpected rosary request")

            override suspend fun searchWebPrayers(apiKey: String, query: String): Result<LumenWebSearchResult> {
                if (query == "Angelus em português") {
                    withTimeout(5_000) { secondSearchStarted.await() }
                } else {
                    assertEquals("Angelus em inglês", query)
                    secondSearchStarted.complete(Unit)
                }
                return Result.success(LumenWebSearchResult("A oração do Angelus em português.", listOf(webSource)))
            }

            override suspend fun completeAgentStep(
                apiKey: String,
                systemPrompt: String,
                messages: List<MistralWireMessage>,
                toolsJson: String,
                model: String,
                temperature: Double,
                onUpdate: (MistralStepUpdate) -> Unit,
            ): Result<MistralStepResponse> {
                return Result.success(
                    when (++step) {
                        1 -> {
                            onUpdate(MistralStepUpdate.InterimReply(interimReply))
                            onUpdate(MistralStepUpdate.ToolCall("search_web_prayers"))
                            toolReady.await()
                            MistralStepResponse(
                                null,
                                listOf(
                                    MistralToolCall("update", "send_interim_reply", """{"message":"$interimReply"}"""),
                                    MistralToolCall("web", "search_web_prayers", """{"query":"Angelus em português"}"""),
                                    MistralToolCall("web-en", "search_web_prayers", """{"query":"Angelus em inglês"}"""),
                                ),
                                "tool_calls",
                            )
                        }
                        2 -> {
                            onUpdate(MistralStepUpdate.Reply("Podemos rezar juntos."))
                            replyReady.await()
                            MistralStepResponse(
                                """{"reply":"Podemos rezar juntos. Quer um terço personalizado?","suggestedFollowUp":"$suggestedReply"}""",
                                emptyList(),
                                "stop",
                            )
                        }
                        else -> MistralStepResponse("""{"reply":"Por quem você quer rezar?"}""", emptyList(), "stop")
                    },
                )
            }
        }
        val clock = Clock.fixed(Instant.parse("2026-11-24T12:00:00Z"), ZoneOffset.UTC)
        val model = PrayerSessionViewModel(SavedStateHandle(), clock)
        val lumen = LumenRepository(client = client, defaultApiKeyProvider = { "test" })
        val uriHandler = object : UriHandler {
            override fun openUri(uri: String) {
                openedUrl = uri
            }
        }
        compose.setContent {
            CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                SertumApp(model = model, repository = null, lumenRepository = lumen)
            }
        }

        compose.onNodeWithTag("home-open-catechist").performScrollTo().performClick()
        compose.onNodeWithTag("catechist-input").performTextInput("Busca o Angelus na web?")
        compose.onNodeWithTag("catechist-send").performClick()
        compose.onNodeWithText(interimReply).assertIsDisplayed()
        compose.onNodeWithTag("catechist-interim-reply").assertIsDisplayed()
        compose.onNodeWithText("Mateus está procurando orações na web…").assertIsDisplayed()
        compose.onNodeWithTag("catechist-reply").assertDoesNotExist()

        toolReady.complete(Unit)
        compose.onNodeWithText("Consultou orações na web").assertIsDisplayed()
        compose.onNodeWithText("Podemos rezar juntos.").assertIsDisplayed()
        compose.onNodeWithTag("catechist-suggested-reply").assertDoesNotExist()

        replyReady.complete(Unit)
        compose.onNodeWithTag("catechist-interim-reply").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("catechist-web-source-1").assertDoesNotExist()
        compose.onNodeWithTag("catechist-web-source-0").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(webSource.url, openedUrl) }
        compose.onNodeWithTag("catechist-suggested-reply").performScrollTo().performClick()
        compose.onNode(hasText(suggestedReply) and hasAnyAncestor(hasTestTag("user-message")))
            .assertIsDisplayed()
        compose.onNodeWithText("Por quem você quer rezar?").assertIsDisplayed()
    }

    @Test
    fun prepareCustomRosaryWeavesPersonalizedOfferingWithoutAlteringFixedPrayers() {
        val clock = Clock.fixed(Instant.parse("2026-11-24T12:00:00Z"), ZoneOffset.UTC)
        val model = PrayerSessionViewModel(SavedStateHandle(), clock)
        compose.setContent {
            SertumApp(model = model, repository = null, lumenRepository = fakeLumen())
        }

        // Open custom Rosary preparation directly from HomeDashboard
        compose.onNodeWithTag("home-prepare-custom-rosary").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("prepare-rosary-sheet").assertIsDisplayed()
        compose.onNodeWithTag("custom-rosary-intention").performTextInput("Pela cirurgia da minha mãe")
        compose.onNodeWithTag("mood-cansado").performScrollTo().performClick()
        compose.onNodeWithTag("generate-custom-rosary").performScrollTo().performClick()
        compose.waitForIdle()

        // Launches prayer screen on Sign of the Cross; next step is Offering with personalized card
        compose.onNodeWithTag("prayer-screen").assertIsDisplayed()
        compose.onNodeWithTag("next-prayer").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("lumen-custom-reflection").performScrollTo().assertIsDisplayed()
    }
}
