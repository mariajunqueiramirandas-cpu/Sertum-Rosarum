package com.sertumrosarum

import android.graphics.Bitmap
import android.content.pm.ActivityInfo
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

/** End-to-end Compose interactions and real-device screenshots; no Material test components. */
@RunWith(AndroidJUnit4::class)
class SanctuaryFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun `sanctuary has original day and vigil layouts and every mystery can be selected`() {
        compose.onNodeWithTag("sanctuary").assertIsDisplayed()
        compose.onNodeWithTag("theme-menu").performClick()
        compose.onNodeWithTag("theme-DAY").performClick()
        listOf("JOYFUL", "LUMINOUS", "SORROWFUL", "GLORIOUS").forEach { set ->
            compose.onNodeWithTag("mystery-$set").performClick().assertIsSelected()
        }
        compose.onNodeWithTag("mystery-LUMINOUS").performClick()
        screenshot("sanctuary-day")
        compose.onNodeWithTag("theme-menu").performClick()
        compose.onNodeWithTag("theme-VIGIL").performClick()
        compose.onNodeWithTag("sanctuary").assertIsDisplayed()
        screenshot("sanctuary-vigil")
    }

    @Test fun `complete rosary flows from the sign of the cross through all prayers to delivery`() {
        compose.onNodeWithTag("start-rosary").performClick()
        compose.onNodeWithTag("prayer-title").assertTextEquals("Sinal da Cruz")
        repeat(79) { compose.onNodeWithTag("next-prayer").performClick() }
        compose.onNodeWithTag("prayer-title").assertTextEquals("Salve Rainha")
        compose.onNodeWithTag("next-prayer").performClick()
        compose.onNodeWithTag("crown-delivered").assertIsDisplayed()
        screenshot("crown-delivered")
        compose.onNodeWithTag("return-sanctuary").performClick()
        compose.onNodeWithTag("sanctuary").assertIsDisplayed()
    }

    @Test fun `beads decades backward stepping and pause resume preserve the prayer position`() {
        compose.onNodeWithTag("mystery-LUMINOUS").performClick()
        compose.onNodeWithTag("start-rosary").performClick()
        compose.onNodeWithTag("decade-1").performClick()
        compose.onNodeWithTag("prayer-title").assertTextEquals("As Bodas de Caná")
        compose.onNodeWithTag("bead-3").performClick()
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Ave-Maria 4 de 10")
        screenshot("prayer-day")
        compose.onNodeWithTag("previous-prayer").performClick()
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Ave-Maria 3 de 10")
        compose.onNodeWithTag("pause-prayer").performClick()
        compose.onNodeWithTag("resume").performClick()
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Ave-Maria 3 de 10")
    }

    @Test fun `eyes closed area responds to tapping and directional swipes`() {
        compose.onNodeWithTag("start-rosary").performClick()
        compose.onNodeWithTag("decade-0").performClick()
        compose.onNodeWithTag("bead-0").performClick()
        compose.onNodeWithTag("eyes-closed-toggle").performClick()
        compose.onNodeWithTag("eyes-closed-zone").performTouchInput { click() }
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Ave-Maria 2 de 10")
        compose.onNodeWithTag("eyes-closed-zone").performTouchInput { swipeLeft() }
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Ave-Maria 3 de 10")
        compose.onNodeWithTag("eyes-closed-zone").performTouchInput { swipeRight() }
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Ave-Maria 2 de 10")
        screenshot("prayer-eyes-closed")
        compose.onNodeWithTag("eyes-closed-toggle").performClick()
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Ave-Maria 2 de 10")
    }

    @Test fun `landscape recreation preserves the bead and keeps the prayer controls visible`() {
        compose.onNodeWithTag("start-rosary").performClick()
        compose.onNodeWithTag("decade-2").performClick()
        compose.onNodeWithTag("bead-5").performClick()
        try {
            compose.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
            compose.waitUntil(60_000) {
                val bounds = compose.onRoot().fetchSemanticsNode().boundsInRoot
                bounds.width > bounds.height
            }
            compose.onNodeWithTag("next-prayer").assertIsDisplayed()
            compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Ave-Maria 6 de 10")
            screenshot("prayer-landscape")
        } finally {
            compose.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
            compose.waitUntil(60_000) {
                val bounds = compose.onRoot().fetchSemanticsNode().boundsInRoot
                bounds.height > bounds.width
            }
        }
    }

    private fun screenshot(name: String) {
        compose.waitForIdle()
        val directory = requireNotNull(compose.activity.getExternalFilesDir("screenshots"))
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        File(directory, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
