package com.sertumrosarum.screenshots

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import com.github.takahirom.roborazzi.captureRoboImage
import com.sertumrosarum.SertumApp
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import com.sertumrosarum.feature.prayer.ThemeMode
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Sample real Compose animation frames, rather than replacing motion for screenshots. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class PrayerMotionScreenshotTest {
    @get:Rule val compose = createComposeRule()

    private fun frames(name: String, times: List<Long>) {
        var elapsed = 0L
        times.forEachIndexed { index, time ->
            if (time > elapsed) compose.mainClock.advanceTimeBy(time - elapsed)
            elapsed = time
            compose.onRoot().captureRoboImage("build/outputs/roborazzi/motion/$name-${index.toString().padStart(2, '0')}.png")
        }
    }

    @Test fun dimensionTransitionAndBloomingBead() {
        val model = PrayerSessionViewModel(SavedStateHandle(), Clock.fixed(Instant.parse("2026-11-12T12:00:00Z"), ZoneOffset.UTC))
        model.selectMystery(MysterySet.LUMINOUS)
        model.setTheme(ThemeMode.VIGIL)
        model.start()
        model.jumpToDecade(1)
        compose.setContent { SertumApp(model) }
        compose.waitForIdle()
        compose.mainClock.autoAdvance = false
        compose.onNodeWithTag("dimension-1").performClick()
        frames("dimension", listOf(0, 48, 96, 144, 192, 240, 320, 480))
        compose.mainClock.autoAdvance = true
        compose.waitForIdle()
        compose.onNodeWithTag("dimension-1").assertIsSelected()
        compose.mainClock.autoAdvance = false
        compose.onNodeWithTag("bead-0").performClick()
        frames("bloom", listOf(0, 48, 96, 144, 192, 240, 320, 480, 640, 800))
        compose.mainClock.autoAdvance = true
        compose.waitForIdle()
        compose.onNodeWithTag("bead-0").assertIsSelected()
    }
}
