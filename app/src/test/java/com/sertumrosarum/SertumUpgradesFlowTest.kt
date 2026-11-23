package com.sertumrosarum

import android.view.KeyEvent
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.ViewModelProvider
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import com.sertumrosarum.ui.theme.ReadingScale
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class SertumUpgradesFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val repo get() = (compose.activity.application as SertumApplication).garden

    @Before fun ready() { compose.awaitSertumReady() }

    private fun tap(tag: String, scroll: Boolean = false) {
        val node = compose.onNodeWithTag(tag, useUnmergedTree = true)
        if (scroll) node.performScrollTo()
        node.performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
    }

    @Test fun `home shows liturgical awareness and starts prayer in one tap with volume key advance`() {
        compose.onNodeWithTag("liturgical-day-badge").assertExists()
        compose.onNodeWithTag("home-liturgy-card", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        tap("home-pray-now", scroll = true)
        compose.onNodeWithTag("prayer-screen").assertIsDisplayed()
        compose.onNodeWithTag("prayer-title").assertTextEquals("Sinal da Cruz")

        // Hardware volume keys advance and step back beads while praying.
        compose.activityRule.scenario.onActivity { activity ->
            assertTrue(activity.onKeyDown(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_DOWN)))
        }
        compose.waitForIdle()
        compose.onNodeWithTag("prayer-title").assertTextEquals("Oferecimento do Terço")

        compose.activityRule.scenario.onActivity { activity ->
            assertTrue(activity.onKeyDown(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_UP)))
        }
        compose.waitForIdle()
        compose.onNodeWithTag("prayer-title").assertTextEquals("Sinal da Cruz")
    }

    @Test fun `mystery study shows deep biblical context, bookmarks to Garden, and launches single decade`() {
        tap("home-mysteries", scroll = true)
        compose.onNodeWithTag("treasury-mysteries").assertIsDisplayed()
        tap("study-mystery-0", scroll = true)
        compose.onNodeWithTag("study-gospel-card").assertExists()
        compose.onNodeWithTag("mystery-deep-study").assertExists()

        tap("bookmark-mystery", scroll = true)
        runBlocking { repo.flush() }

        tap("pray-single-decade", scroll = true)
        compose.onNodeWithTag("prayer-screen").assertIsDisplayed()
        compose.onNodeWithTag("prayer-guide").assertTextEquals("Uma dezena")

        tap("pause-prayer")
        tap("tab-GARDEN")
        tap("garden-Guardados")
        compose.onNodeWithText("Mistérios do Rosário").assertExists()
    }

    @Test fun `offered intentions appear during the Offering step and deep study opens inside prayer`() {
        tap("tab-GARDEN")
        tap("new-intention", scroll = true)
        compose.onNodeWithTag("intention-title").performTextInput("Pela paz na família")
        tap("save-intention", scroll = true)
        runBlocking { repo.flush() }

        tap("tab-GUIDES")
        tap("rosary-setup-open", scroll = true)
        tap("choose-intentions", scroll = true)
        compose.onNodeWithText("Pela paz na família").performSemanticsAction(SemanticsActions.OnClick)
        tap("picker-done", scroll = true)
        tap("start-rosary")
        tap("next-prayer")
        compose.onNodeWithTag("offered-intentions-card").assertExists()
        compose.onNodeWithText("Por Pela paz na família").assertExists()

        // Jump into 1st decade, advance to Pai-Nosso, open contemplation sheet and deep study.
        tap("decade-0")
        tap("next-prayer")
        tap("mystery-chip")
        compose.onNodeWithTag("deep-study-toggle", useUnmergedTree = true).performScrollTo().performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
        compose.onNodeWithTag("mystery-deep-study").assertExists()
    }

    @Test fun `unified search finds Brazilian devotions and reading scale updates preference`() {
        tap("home-search")
        compose.onNodeWithTag("unified-search-field").performTextInput("Via-Sacra")
        compose.waitForIdle()
        tap("search-guide-VIA_SACRA", scroll = true)
        compose.onNodeWithTag("guide-title").assertTextEquals("Via-Sacra")
        tap("guide-start")
        compose.onNodeWithTag("prayer-screen").assertIsDisplayed()
        tap("pause-prayer")

        tap("theme-menu")
        tap("reading-scale-LARGE")
        val vm = ViewModelProvider(compose.activity)[PrayerSessionViewModel::class.java]
        assertEquals(ReadingScale.LARGE, vm.state.value.readingScale)
    }
}
