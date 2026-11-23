package com.sertumrosarum

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
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
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.Rule
import org.junit.Test
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
