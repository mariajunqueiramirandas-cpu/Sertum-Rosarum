package com.sertumrosarum

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.domain.prayer.PrayerMode
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class PrayerModeFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun tap(tag: String) {
        val node = compose.onNodeWithTag(tag)
        if (tag.startsWith("prayer-mode-")) node.performScrollTo()
        node.performClick()
    }

    private fun checkScrollingModeChoice() {
        compose.onNodeWithTag("prayer-mode-SOLO").assertIsSelected().assertIsNotDisplayed()
        compose.onNodeWithTag("start-rosary").assertIsDisplayed()
        compose.onNodeWithTag("sanctuary-scroll-cue").assertIsDisplayed().performClick()
        compose.waitForIdle()
        val scroll = compose.onNodeWithTag("sanctuary-scroll")
        assertTrue(scroll.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value() > 0f)
        scroll.performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 100_000f) }
        compose.waitForIdle()
        compose.onNodeWithTag("sanctuary-scroll-cue").assertDoesNotExist()
        compose.onNodeWithTag("prayer-mode-GROUP").performScrollTo().assertIsDisplayed().performClick().assertIsSelected()
        tap("start-rosary")
        compose.onNodeWithTag("session-mode").assertTextEquals("Oração em grupo")
    }

    @Test fun `solo is the default and mode choices appear after scrolling with a working cue`() = checkScrollingModeChoice()

    @Test
    @Config(qualifiers = "w320dp-h640dp-xxhdpi")
    fun `a small phone can scroll to mode choices and the cue disappears at the end`() = checkScrollingModeChoice()

    @Test fun `paused group prayer keeps its mode while a new solo rosary uses personal texts`() {
        compose.onNodeWithTag("prayer-mode-SOLO").assertIsSelected()
        tap("prayer-mode-GROUP")
        tap("start-rosary")
        tap("next-prayer")
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("session-mode").assertTextEquals("Oração em grupo")
        compose.onNodeWithTag("prayer-text").assertTextEquals(FixedPrayer.OFFERING.text)
        tap("pause-prayer")
        tap("prayer-mode-SOLO")
        tap("resume")
        compose.onNodeWithTag("session-mode").assertTextEquals("Oração em grupo")
        compose.onNodeWithTag("prayer-text").assertTextEquals(FixedPrayer.OFFERING.text)
        tap("pause-prayer")
        tap("start-rosary")
        compose.onNodeWithTag("session-mode").assertTextEquals("Oração a sós")
        tap("next-prayer")
        compose.onNodeWithTag("prayer-text").assertTextEquals(FixedPrayer.OFFERING.textFor(PrayerMode.SOLO))
        repeat(77) { tap("next-prayer") }
        compose.onNodeWithTag("prayer-text").assertTextEquals(FixedPrayer.THANKSGIVING.textFor(PrayerMode.SOLO))
        repeat(2) { tap("next-prayer") }
        compose.onNodeWithTag("crown-delivered").assertIsDisplayed()
    }

    @Test fun `a guide can choose solo prayer and its litany retains both invocation and response`() {
        tap("tab-GUIDES")
        tap("guide-COMPLETE")
        compose.onNodeWithTag("prayer-mode-GROUP").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithText("Uma voz chama, e todos respondem.").assertExists()
        tap("prayer-mode-SOLO")
        compose.onNodeWithText("Reze a invocação e a resposta, no seu ritmo.").assertExists()
        tap("guide-start")
        compose.onNodeWithTag("session-mode").assertTextEquals("Oração a sós")
        tap("decade-4")
        tap("bead-9")
        repeat(6) { tap("next-prayer") }
        compose.onNodeWithTag("litany-call", useUnmergedTree = true).assertTextEquals("Senhor, tende piedade de nós.")
        compose.onNodeWithTag("litany-response", useUnmergedTree = true).assertTextEquals("Senhor, tende piedade de nós.")
    }

    @Test
    @Config(qualifiers = "w851dp-h393dp-land-xxhdpi")
    fun `landscape keeps start and resume visible while modes and mysteries scroll`() {
        compose.onNodeWithTag("prayer-mode-SOLO").assertIsNotDisplayed()
        tap("prayer-mode-GROUP")
        compose.onNodeWithTag("start-rosary").assertIsDisplayed().performClick()
        tap("pause-prayer")
        compose.onNodeWithTag("resume").assertIsDisplayed()
        compose.onNodeWithTag("prayer-mode-SOLO").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("start-rosary").assertIsDisplayed()
        compose.onNodeWithTag("mystery-GLORIOUS").performScrollTo().assertIsDisplayed().performClick()
    }
}
