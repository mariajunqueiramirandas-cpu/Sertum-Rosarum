package com.sertumrosarum.screenshots

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.hasTestTag
import com.github.takahirom.roborazzi.captureRoboImage
import com.sertumrosarum.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * JVM screenshots of the Phase 3 screens: the library, a prayer in its three readings,
 * an explained expression, the mysteries to study, and Latin inside the rosary.
 * Record with `./gradlew recordRoborazziDebug`; images land in app/build/outputs/roborazzi/.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class TesouroScreenshotTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun shot(name: String) {
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    private fun tap(tag: String) {
        if (tag.startsWith("prayer-")) compose.onNodeWithTag("treasury-scroll").performScrollToNode(hasTestTag(tag))
        compose.onNodeWithTag(tag).performSemanticsAction(SemanticsActions.OnClick)
    }

    private fun appearance(mode: String) {
        compose.onNodeWithTag("theme-menu").performClick()
        tap("theme-$mode")
        compose.waitForIdle()
    }

    @Test fun libraryAndItsMorningShelf() {
        appearance("DAY")
        tap("tab-TREASURY")
        shot("tesouro-library")
        tap("category-WAKING")
        shot("tesouro-morning")
    }

    @Test fun prayerInThreeReadings() {
        appearance("DAY")
        tap("tab-TREASURY")
        tap("prayer-ave-maria")
        shot("tesouro-prayer")
        tap("term-0")
        shot("tesouro-term")
        tap("term-sheet-veil")
        tap("language-LATIN")
        shot("tesouro-prayer-latin")
        tap("language-BILINGUAL")
        shot("tesouro-prayer-bilingual")
        tap("verse-0")
        shot("tesouro-word-by-word")
    }

    @Test fun prayerAtNight() {
        appearance("VIGIL")
        tap("tab-TREASURY")
        shot("tesouro-library-vigil")
        tap("prayer-ave-maria")
        shot("tesouro-prayer-vigil")
        tap("language-BILINGUAL")
        tap("verse-0")
        shot("tesouro-word-by-word-vigil")
    }

    @Test
    @Config(qualifiers = "w320dp-h640dp-xxhdpi")
    fun narrowPhoneStacksTheVerses() {
        appearance("DAY")
        tap("tab-TREASURY")
        shot("tesouro-library-narrow")
        tap("prayer-ave-maria")
        tap("language-BILINGUAL")
        tap("verse-0")
        shot("tesouro-bilingual-narrow")
    }

    @Test fun mysteriesToStudy() {
        appearance("DAY")
        tap("tab-TREASURY")
        compose.onNodeWithTag("open-mysteries").performScrollTo()
        tap("open-mysteries")
        shot("tesouro-mysteries")
        tap("study-mystery-1")
        shot("tesouro-mystery-sheet")
    }

    @Test fun latinInsideTheRosary() {
        appearance("DAY")
        tap("start-rosary")
        tap("decade-0")
        tap("bead-2")
        shot("prayer-language-portuguese")
        tap("rosary-language-LATIN")
        shot("prayer-language-latin")
    }
}
