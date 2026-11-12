package com.sertumrosarum

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** The Phase 2 interface on the JVM: tabs, Roteiros and its sheets, and the mystery's dimensions during prayer. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class SanctuaryShellTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun tap(tag: String) {
        compose.onNodeWithTag(tag, useUnmergedTree = true).performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
    }

    private fun back() {
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
    }

    @Test fun `four tabs lead to four places and back returns to the sanctuary`() {
        compose.onNodeWithTag("tab-HOME").assertIsSelected()
        listOf("GUIDES" to "guides", "TREASURY" to "treasury", "GARDEN" to "garden", "HOME" to "sanctuary").forEach { (tab, page) ->
            tap("tab-$tab")
            compose.onNodeWithTag("tab-$tab").assertIsSelected()
            compose.onNodeWithTag(page).assertIsDisplayed()
        }
        tap("other-guides")
        compose.onNodeWithTag("guides").assertIsDisplayed()
        back()
        compose.onNodeWithTag("sanctuary").assertIsDisplayed()
        compose.onNodeWithTag("tab-HOME").assertIsSelected()
    }

    @Test fun `the tabs leave during prayer and return with the sanctuary`() {
        compose.onNodeWithTag("bottom-bar").assertIsDisplayed()
        tap("start-rosary")
        compose.onNodeWithTag("prayer-screen").assertIsDisplayed()
        compose.onNodeWithTag("bottom-bar").assertDoesNotExist()
        back()
        compose.onNodeWithTag("bottom-bar").assertIsDisplayed()
        compose.onNodeWithTag("resume").assertIsDisplayed()
    }

    @Test fun `a crown still in bud shows its own shape and offers nothing to begin`() {
        tap("tab-GUIDES")
        tap("guide-TEARS")
        compose.onNodeWithTag("guide-title").assertTextEquals("Coroa das Lágrimas")
        compose.onNodeWithTag("crown-shape").assertTextEquals("Sete setenas de sete contas")
        compose.onNodeWithTag("decade-6").assertExists()
        compose.onNodeWithTag("decade-7").assertDoesNotExist()
        compose.onNodeWithTag("bead-6").assertExists()
        compose.onNodeWithTag("bead-7").assertDoesNotExist()
        tap("bead-5")
        compose.onNodeWithTag("bead-5").assertIsSelected()
        compose.onNodeWithTag("guide-budding").assertExists()
        compose.onNodeWithTag("guide-start").assertDoesNotExist()
        back()
        compose.onNodeWithTag("guide-sheet").assertDoesNotExist()
        compose.onNodeWithTag("guides").assertIsDisplayed()

        tap("guide-FRANCISCAN")
        compose.onNodeWithTag("crown-shape").assertTextEquals("Sete dezenas de dez contas")
        compose.onNodeWithTag("bead-9").assertExists()
        tap("guide-sheet-veil")
        compose.onNodeWithTag("guide-sheet").assertDoesNotExist()

        tap("guide-COMPLETE")
        compose.onNodeWithTag("litany-verses").assertExists()
        compose.onNodeWithTag("guide-start").assertDoesNotExist()
    }

    @Test fun `the catechetical guide begins from its sheet with the chosen mysteries and its notes`() {
        tap("tab-GUIDES")
        tap("guide-CATECHETICAL")
        tap("mystery-SORROWFUL")
        compose.onNodeWithTag("mystery-SORROWFUL").assertIsSelected()
        tap("guide-start")
        compose.onNodeWithTag("guide-sheet").assertDoesNotExist()
        compose.onNodeWithTag("prayer-guide").assertTextEquals("Roteiro catequético")
        compose.onNodeWithTag("prayer-title").assertTextEquals("Sinal da Cruz")
        compose.onNodeWithTag("catechetical-note").assertExists()
        tap("decade-0")
        compose.onNodeWithTag("prayer-title").assertTextEquals("A Agonia no Horto")
        compose.onNodeWithTag("catechetical-note").assertExists()
        compose.onNodeWithTag("mystery-dimensions").assertExists()
    }

    @Test fun `a themed rosary carries its intention into prayer`() {
        tap("tab-GUIDES")
        tap("guide-PEACE")
        tap("guide-start")
        compose.onNodeWithTag("prayer-guide").assertTextEquals("Terço pela paz")
        compose.onNodeWithTag("catechetical-note").assertDoesNotExist()
    }

    @Test fun `a mystery opens layer by layer and its sheet stays one touch away during the decade`() {
        tap("mystery-JOYFUL")
        tap("start-rosary")
        compose.onNodeWithTag("mystery-dimensions").assertDoesNotExist()
        tap("decade-0")
        compose.onNodeWithTag("prayer-title").assertTextEquals("A Anunciação")
        compose.onNodeWithTag("mystery-dimensions").assertExists()
        compose.onNodeWithTag("meditated-toggle").assertIsOff()
        tap("meditated-toggle")
        compose.onNodeWithTag("meditated-toggle").assertIsOn()

        tap("bead-2")
        compose.onNodeWithTag("bead-meditation", useUnmergedTree = true).assertTextEquals("“Alegra-te, cheia de graça, o Senhor está contigo.”")
        compose.onNodeWithTag("prayer-text").assertExists()
        tap("mystery-chip")
        compose.onNodeWithTag("mystery-sheet-title").assertTextEquals("A Anunciação")
        compose.onNodeWithTag("bead-by-bead").assertExists()
        back()
        compose.onNodeWithTag("mystery-sheet").assertDoesNotExist()
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Ave-Maria 3 de 10")

        // A mystery without written lines keeps its sheet and simply shows no bead line.
        tap("decade-1")
        tap("bead-0")
        compose.onNodeWithTag("bead-meditation", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithTag("prayer-title").assertTextEquals("Ave-Maria")
        tap("mystery-chip")
        compose.onNodeWithTag("mystery-sheet-title").assertTextEquals("A Visitação")
        compose.onNodeWithTag("bead-by-bead").assertDoesNotExist()
    }
}
