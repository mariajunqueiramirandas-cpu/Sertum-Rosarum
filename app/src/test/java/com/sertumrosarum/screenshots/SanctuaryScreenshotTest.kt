package com.sertumrosarum.screenshots

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.github.takahirom.roborazzi.captureRoboImage
import com.sertumrosarum.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * JVM screenshots (no emulator/KVM needed). Record with `./gradlew recordRoborazziDebug`,
 * images land in app/build/outputs/roborazzi/.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class SanctuaryScreenshotTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun shot(name: String) = compose.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")

    @Test fun sanctuaryDay() {
        compose.onNodeWithTag("theme-menu").performClick()
        compose.onNodeWithTag("theme-DAY").performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
        shot("sanctuary-day")
    }

    @Test fun sanctuaryVigil() {
        compose.onNodeWithTag("theme-menu").performClick()
        compose.onNodeWithTag("theme-VIGIL").performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
        shot("sanctuary-vigil")
    }

    @Test fun firstPrayer() {
        compose.onNodeWithTag("start-rosary").performClick()
        shot("prayer-first")
    }

    private fun tap(tag: String) {
        compose.onNodeWithTag(tag).performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
    }

    private fun rosaryOffering(mode: String, suffix: String) {
        tap("theme-menu")
        tap("theme-$mode")
        tap("start-rosary")
        tap("next-prayer")
        compose.onNodeWithTag("prayer-scroll-cue").assertIsDisplayed()
        shot("prayer-reading-$suffix")
        if (mode == "VIGIL") {
            compose.onNodeWithTag("prayer-scroll").performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 100_000f) }
            compose.waitForIdle()
            shot("prayer-reading-end-vigil")
        }
        tap("decade-0")
        tap("bead-2")
        shot("prayer-$suffix")
        if (mode == "DAY") {
            tap("eyes-closed-toggle")
            shot("prayer-eyes-closed")
            tap("eyes-closed-toggle")
        }
        tap("decade-4")
        tap("bead-9")
        repeat(5) { tap("next-prayer") }
        shot(if (mode == "DAY") "crown-delivered" else "crown-delivered-vigil")
    }

    @Test fun dayPrayerAndOffering() = rosaryOffering("DAY", "day")

    @Test fun vigilPrayerAndOffering() = rosaryOffering("VIGIL", "vigil")
}
