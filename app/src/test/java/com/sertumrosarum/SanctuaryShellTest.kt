package com.sertumrosarum

import com.sertumrosarum.ensurePrayerTarget
import com.sertumrosarum.openRosarySetup
import com.sertumrosarum.awaitSertumReady
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performClick
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
    @org.junit.Before fun awaitLocalRecords() { compose.awaitSertumReady() }

    private fun tap(tag: String) {
        compose.ensurePrayerTarget(tag)
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

    @Test fun `each devotional crown shows its shape and can start from its sheet`() {
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
        compose.onNodeWithTag("guide-budding").assertDoesNotExist()
        compose.onNodeWithTag("guide-start").assertExists()
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
        compose.onNodeWithTag("guide-start").assertExists()
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

        // The following mystery also has its own complete bead meditations.
        tap("decade-1")
        tap("bead-0")
        compose.onNodeWithTag("bead-meditation", useUnmergedTree = true).assertTextEquals("Maria se põe a caminho com prontidão.")
        tap("mystery-chip")
        compose.onNodeWithTag("mystery-sheet-title").assertTextEquals("A Visitação")
        compose.onNodeWithTag("bead-by-bead").assertExists()
    }

    @Test fun `Crown of Tears prays seven actual setenas and resumes its seventh bead`() {
        tap("tab-GUIDES")
        tap("guide-TEARS")
        tap("guide-start")
        compose.onNodeWithTag("bottom-bar").assertDoesNotExist()
        tap("decade-6")
        compose.onNodeWithTag("decade-7").assertDoesNotExist()
        compose.onNodeWithTag("prayer-title").assertTextEquals("Vede, ó Jesus")
        tap("bead-6")
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Conta 7 de 7")
        compose.onNodeWithTag("bead-7").assertDoesNotExist()
        compose.onNodeWithTag("prayer-text").assertTextEquals("Meu Jesus, ouvi os nossos rogos, pelas lágrimas de Vossa Mãe Santíssima.")
        tap("pause-prayer")
        tap("tab-HOME")
        tap("resume")
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Conta 7 de 7")
        repeat(4) { tap("next-prayer") }
        compose.onNodeWithTag("prayer-title").assertTextEquals("Ó Maria, Mãe de amor")
        tap("next-prayer")
        compose.onNodeWithTag("crown-delivered").assertIsDisplayed()
        compose.onNodeWithTag("delivered-guide").assertTextEquals("Coroa das Lágrimas")
    }

    @Test fun `Franciscan crown prays its seventh joy and the two final Ave Marias`() {
        tap("tab-GUIDES")
        tap("guide-FRANCISCAN")
        tap("guide-start")
        tap("decade-6")
        compose.onNodeWithTag("prayer-title").assertTextEquals("A Assunção e Coroação de Maria")
        tap("bead-9")
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Ave-Maria 10 de 10")
        repeat(2) { tap("next-prayer") }
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Ave-Maria 1 de 2")
        compose.onNodeWithTag("bead-2").assertDoesNotExist()
        tap("bead-1")
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Ave-Maria 2 de 2")
        tap("next-prayer")
        compose.onNodeWithTag("prayer-title").assertTextEquals("Sinal da Cruz")
        tap("next-prayer")
        compose.onNodeWithTag("crown-delivered").assertIsDisplayed()
        compose.onNodeWithTag("delivered-guide").assertTextEquals("Coroa Franciscana")
    }

    @Test fun `complete script continues from Salve Rainha through responsive litany and final collect`() {
        tap("tab-GUIDES")
        tap("guide-COMPLETE")
        tap("guide-start")
        tap("decade-4")
        tap("bead-9")
        repeat(6) { tap("next-prayer") }
        compose.onNodeWithTag("prayer-title").assertTextEquals("Ladainha de Nossa Senhora")
        compose.onNodeWithTag("litany-call", useUnmergedTree = true).assertTextEquals("Senhor, tende piedade de nós.")
        compose.onNodeWithTag("litany-response", useUnmergedTree = true).assertTextEquals("Senhor, tende piedade de nós.")
        tap("next-prayer")
        compose.onNodeWithTag("litany-response", useUnmergedTree = true).assertTextEquals("Cristo, tende piedade de nós.")
        tap("previous-prayer")
        compose.onNodeWithTag("litany-call", useUnmergedTree = true).assertTextEquals("Senhor, tende piedade de nós.")
        repeat(67) { tap("next-prayer") }
        compose.onNodeWithTag("prayer-title").assertTextEquals("Oremos")
        compose.onNodeWithTag("crown-delivered").assertDoesNotExist()
        tap("next-prayer")
        compose.onNodeWithTag("crown-delivered").assertIsDisplayed()
    }

    @Test
    @Config(qualifiers = "w320dp-h640dp-xxhdpi")
    fun `seven group and seven bead controls can be touched on a narrow screen`() {
        tap("tab-GUIDES")
        tap("guide-TEARS")
        tap("guide-start")
        repeat(7) { group ->
            compose.onNodeWithTag("decade-$group").performClick().assertIsSelected()
            repeat(7) { bead ->
                compose.onNodeWithTag("bead-$bead").performClick().assertIsSelected()
                compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Conta ${bead + 1} de 7")
            }
        }
        compose.onNodeWithTag("next-prayer").assertIsDisplayed()
    }
}
