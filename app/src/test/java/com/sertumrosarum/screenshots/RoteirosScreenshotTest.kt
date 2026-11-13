package com.sertumrosarum.screenshots

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import com.github.takahirom.roborazzi.captureRoboImage
import com.sertumrosarum.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * JVM screenshots of the Phase 2 screens: the tabs, Roteiros and its sheets, the mystery
 * in its dimensions, the bead-by-bead rosary, and the catechetical notes.
 * Record with `./gradlew recordRoborazziDebug`; images land in app/build/outputs/roborazzi/.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class RoteirosScreenshotTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun shot(name: String) {
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    private fun tap(tag: String) = compose.onNodeWithTag(tag).performSemanticsAction(SemanticsActions.OnClick)

    private fun appearance(mode: String) {
        compose.onNodeWithTag("theme-menu").performClick()
        tap("theme-$mode")
        compose.waitForIdle()
    }

    @Test fun roteirosDay() {
        appearance("DAY")
        tap("tab-GUIDES")
        shot("roteiros-day")
        compose.onNodeWithTag("guide-FRANCISCAN").performScrollTo()
        shot("roteiros-crowns")
    }

    @Test fun roteirosVigil() {
        appearance("VIGIL")
        tap("tab-GUIDES")
        shot("roteiros-vigil")
    }

    @Test fun crownOfTearsSheet() {
        appearance("DAY")
        tap("tab-GUIDES")
        tap("guide-TEARS")
        shot("guide-tears")
        tap("guide-start")
        tap("decade-6")
        tap("bead-6")
        shot("prayer-tears")
        repeat(5) { tap("next-prayer") }
        shot("offering-tears")
    }

    @Test fun franciscanCrownSheetAtNight() {
        appearance("VIGIL")
        tap("tab-GUIDES")
        tap("guide-FRANCISCAN")
        shot("guide-franciscan-vigil")
        tap("guide-start")
        tap("decade-6")
        tap("bead-9")
        repeat(2) { tap("next-prayer") }
        shot("prayer-franciscan-final-pair")
        tap("bead-1")
        repeat(2) { tap("next-prayer") }
        shot("offering-franciscan-vigil")
    }

    @Test fun completeRosarySheetShowsTheLitany() {
        appearance("DAY")
        tap("tab-GUIDES")
        tap("guide-COMPLETE")
        compose.onNodeWithTag("litany-verses").performScrollTo()
        shot("guide-litany")
        tap("guide-start")
        tap("decade-4")
        tap("bead-9")
        repeat(6) { tap("next-prayer") }
        shot("prayer-litany")
    }

    @Test fun catecheticalGuideFromSheetToFirstPrayer() {
        appearance("DAY")
        tap("tab-GUIDES")
        tap("guide-CATECHETICAL")
        shot("guide-catechetical")
        compose.onNodeWithTag("guide-meditated").performScrollTo()
        shot("guide-catechetical-choices")
        tap("guide-start")
        shot("prayer-catechetical")
    }

    @Test fun mysteryInItsDimensionsAndBeadByBead() {
        appearance("DAY")
        tap("mystery-JOYFUL")
        tap("start-rosary")
        tap("decade-0")
        shot("mystery-dimensions")
        tap("dimension-2")
        tap("meditated-toggle")
        compose.onNodeWithTag("meditated-toggle").performScrollTo()
        shot("mystery-fruit")
        tap("bead-2")
        shot("prayer-meditated")
        tap("mystery-chip")
        shot("mystery-sheet")
    }

    @Test fun beadByBeadAtNight() {
        appearance("VIGIL")
        tap("mystery-SORROWFUL")
        tap("start-rosary")
        tap("decade-0")
        tap("meditated-toggle")
        tap("bead-5")
        shot("prayer-meditated-vigil")
    }

    @Test fun tesouroAndThePlaceStillInBud() {
        appearance("DAY")
        tap("tab-TREASURY")
        shot("tesouro")
        tap("tab-GARDEN")
        shot("meu-jardim")
        tap("tab-HOME")
        appearance("VIGIL")
        tap("tab-TREASURY")
        shot("tesouro-vigil")
    }

    @Test fun devotionalPortraitsAndThemedCards() {
        appearance("DAY")
        tap("tab-GUIDES")
        compose.onNodeWithTag("shelf-THEMED").performScrollTo()
        // The shelf title alone can fit at the viewport's foot; bring its paintings into view too.
        compose.onNodeWithTag("guides-scroll").performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 1600f) }
        shot("roteiros-themed")
        compose.onNodeWithTag("guide-DIVINE_MERCY").performScrollTo()
        tap("guide-DIVINE_MERCY")
        shot("guide-divine-mercy")
        tap("guide-sheet-veil")
        compose.onNodeWithTag("guide-SAINT_JOSEPH").performScrollTo()
        tap("guide-SAINT_JOSEPH")
        shot("guide-saint-joseph")
    }

    @Test fun scrolledHeaderDay() {
        appearance("DAY")
        compose.onNodeWithTag("sanctuary-scroll").performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 192f) }
        shot("sanctuary-scrolled-day")
    }

    @Test fun scrolledHeaderVigil() {
        appearance("VIGIL")
        compose.onNodeWithTag("sanctuary-scroll").performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 192f) }
        shot("sanctuary-scrolled-vigil")
    }
}
