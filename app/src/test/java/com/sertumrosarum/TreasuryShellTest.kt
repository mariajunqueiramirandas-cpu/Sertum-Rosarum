package com.sertumrosarum

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.domain.treasury.TreasurySamples
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** The Phase 3 interface on the JVM, walked through with its one example prayer. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class TreasuryShellTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val ave = TreasurySamples.AVE_MARIA

    private fun tap(tag: String) {
        compose.onNodeWithTag(tag, useUnmergedTree = true).performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
    }

    private fun back() {
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
    }

    @Test fun `a prayer opens from its shelf and reads in Portuguese, Latin, and side by side`() {
        tap("tab-TREASURY")
        compose.onNodeWithTag("shelf-MARIAN").assertExists()
        tap("prayer-ave-maria")
        compose.onNodeWithTag("treasury-reading").assertIsDisplayed()
        compose.onNodeWithTag("reading-title").assertTextEquals("Ave-Maria")
        compose.onNodeWithTag("language-PORTUGUESE").assertIsSelected()
        compose.onNodeWithTag("reading-text").assertTextEquals(FixedPrayer.HAIL_MARY.text)

        tap("language-LATIN")
        compose.onNodeWithTag("reading-title").assertTextEquals("Ave, Maria")
        compose.onNodeWithTag("reading-text").assertTextEquals(requireNotNull(ave.latin))
        compose.onNodeWithTag("term-links").assertDoesNotExist()

        tap("language-BILINGUAL")
        compose.onNodeWithTag("bilingual-verses").assertExists()
        compose.onNodeWithTag("verse-${ave.verses.lastIndex}").assertExists()
        compose.onNodeWithTag("word-by-word").assertDoesNotExist()
        tap("verse-0")
        compose.onNodeWithTag("word-by-word").assertExists()
        tap("verse-0")
        compose.onNodeWithTag("word-by-word").assertDoesNotExist()

        back()
        compose.onNodeWithTag("treasury").assertIsDisplayed()
        compose.onNodeWithTag("tab-TREASURY").assertIsSelected()
        back()
        compose.onNodeWithTag("sanctuary").assertIsDisplayed()
    }

    @Test fun `an expression opens its explanation and the sheet closes back to the prayer`() {
        tap("tab-TREASURY")
        tap("prayer-ave-maria")
        tap("term-0")
        compose.onNodeWithTag("term-sheet").assertIsDisplayed()
        compose.onNodeWithTag("term-title").assertTextEquals("“cheia de graça”")
        compose.onNodeWithTag("term-origin").assertExists()
        compose.onNodeWithTag("term-today").assertExists()
        back()
        compose.onNodeWithTag("term-sheet").assertDoesNotExist()
        compose.onNodeWithTag("treasury-reading").assertIsDisplayed()
        tap("treasury-reading-back")
        compose.onNodeWithTag("treasury").assertIsDisplayed()
    }

    @Test fun `an empty moment and a search without results say so`() {
        tap("tab-TREASURY")
        compose.onNodeWithTag("category-ALL").assertIsSelected()
        tap("category-WAKING")
        compose.onNodeWithTag("category-WAKING").assertIsSelected()
        compose.onNodeWithTag("treasury-empty-message", useUnmergedTree = true).assertTextEquals("As orações para este momento ainda não foram plantadas.")
        compose.onNodeWithTag("open-mysteries").assertDoesNotExist()
        tap("category-MARIAN")
        compose.onNodeWithTag("prayer-ave-maria").assertExists()
        compose.onNodeWithTag("treasury-search").performTextInput("Credo")
        compose.onNodeWithTag("treasury-empty-message", useUnmergedTree = true).assertTextEquals("Nenhuma oração encontrada para “Credo”.")
        tap("treasury-search-clear")
        compose.onNodeWithTag("prayer-ave-maria").assertExists()
    }

    @Test fun `the twenty mysteries can be studied outside prayer`() {
        tap("tab-TREASURY")
        tap("open-mysteries")
        compose.onNodeWithTag("treasury-mysteries").assertIsDisplayed()
        compose.onNodeWithTag("study-days").assertTextEquals("Segunda e sábado")
        tap("mystery-GLORIOUS")
        compose.onNodeWithTag("mystery-GLORIOUS").assertIsSelected()
        compose.onNodeWithTag("study-mystery-4").assertExists()
        tap("study-mystery-0")
        compose.onNodeWithTag("study-sheet-title").assertTextEquals("A Ressurreição de Jesus")
        compose.onNodeWithTag("mystery-scene").assertExists()
        compose.onNodeWithTag("bead-by-bead").assertExists()
        compose.onNodeWithTag("meditated-toggle").assertDoesNotExist()
        back()
        compose.onNodeWithTag("study-sheet").assertDoesNotExist()
        back()
        compose.onNodeWithTag("treasury").assertIsDisplayed()
    }

    @Test fun `the rosary offers Latin only on a prayer that has it`() {
        tap("start-rosary")
        compose.onNodeWithTag("prayer-title").assertTextEquals("Sinal da Cruz")
        compose.onNodeWithTag("rosary-language").assertDoesNotExist()
        tap("decade-0")
        tap("bead-0")
        compose.onNodeWithTag("rosary-language-PORTUGUESE").assertIsSelected()
        compose.onNodeWithTag("prayer-text").assertTextEquals(FixedPrayer.HAIL_MARY.text)
        tap("rosary-language-LATIN")
        compose.onNodeWithTag("rosary-language-LATIN").assertIsSelected()
        compose.onNodeWithTag("prayer-title").assertTextEquals("Ave, Maria")
        compose.onNodeWithTag("prayer-text").assertTextEquals(requireNotNull(ave.latin))
        tap("next-prayer")
        compose.onNodeWithTag("prayer-text").assertTextEquals(requireNotNull(ave.latin))
        compose.onNodeWithTag("bead-counter", useUnmergedTree = true).assertTextEquals("Ave-Maria 2 de 10")
    }
}
