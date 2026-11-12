package com.sertumrosarum.screenshots

import androidx.compose.ui.semantics.SemanticsActions
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
}
