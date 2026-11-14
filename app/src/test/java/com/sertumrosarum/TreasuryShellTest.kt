package com.sertumrosarum

import com.sertumrosarum.ensurePrayerTarget
import com.sertumrosarum.openRosarySetup
import com.sertumrosarum.awaitSertumReady
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.compose.ui.test.swipe
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.performClick
import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.domain.treasury.TreasuryCatalog
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** The offline Treasury walked through the real activity with the complete catalog. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class TreasuryShellTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @org.junit.Before fun awaitLocalRecords() { compose.awaitSertumReady() }
    private val ave = requireNotNull(TreasuryCatalog.find("ave-maria"))

    private fun tap(tag: String) {
        compose.ensurePrayerTarget(tag)
        if (tag.startsWith("prayer-") && !tag.startsWith("prayer-language")) {
            compose.onNodeWithTag("treasury-scroll").performScrollToNode(hasTestTag(tag))
        }
        compose.onNodeWithTag(tag, useUnmergedTree = true).performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
    }

    private fun back() {
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
    }

    @Test fun `a prayer opens from its shelf and reads in Portuguese, Latin, and side by side`() {
        tap("tab-TREASURY")

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
        compose.onNodeWithTag("prayer-ave-maria").assertIsDisplayed()
        back()
        compose.onNodeWithTag("sanctuary").assertIsDisplayed()
    }

    @Test fun `choosing a moment or a language never moves the controls beneath the heading`() {
        tap("tab-TREASURY")
        fun at(tag: String) = compose.onNodeWithTag(tag, useUnmergedTree = true).getUnclippedBoundsInRoot()
        val search = at("treasury-search").top
        val night = at("category-NIGHT").left
        listOf("category-WAKING", "category-NIGHT", "category-MARIAN", "category-ALL").forEach {
            tap(it)
            assertEquals("search moved after $it", search, at("treasury-search").top)
            assertEquals("choices moved after $it", night, at("category-NIGHT").left)
        }

        tap("prayer-sob-a-vossa-protecao")
        val choice = at("reading-language").top
        listOf("language-LATIN", "language-BILINGUAL", "language-PORTUGUESE").forEach {
            tap(it)
            assertEquals("language choice moved after $it", choice, at("reading-language").top)
        }
    }

    @Test fun `touching the actual annotated text opens its drawer and dragging the handle closes it`() {
        tap("tab-TREASURY")
        tap("prayer-ave-maria")
        val layouts = mutableListOf<TextLayoutResult>()
        val text = compose.onNodeWithTag("reading-text")
        text.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        val offset = layout.layoutInput.text.text.indexOf("cheia de graça")
        text.performTouchInput { click(layout.getBoundingBox(offset).center) }
        compose.waitForIdle()
        compose.onNodeWithTag("term-title").assertTextEquals("“cheia de graça”")
        compose.onNodeWithTag("term-sheet-handle").performTouchInput { swipe(center, center + Offset(0f, 450f), 350) }
        compose.waitForIdle()
        compose.onNodeWithTag("term-sheet").assertDoesNotExist()
        compose.onNodeWithTag("treasury-reading").assertIsDisplayed()
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

    @Test fun `every category is populated and search ignores accents`() {
        tap("tab-TREASURY")
        tap("category-WAKING")
        compose.onNodeWithTag("category-WAKING").assertIsSelected()
        compose.onNodeWithTag("treasury-search").performTextInput("espirito")
        compose.onNodeWithTag("treasury-scroll").performScrollToNode(hasTestTag("prayer-veni-sancte-spiritus"))
        compose.onNodeWithTag("prayer-veni-sancte-spiritus").assertExists()
        tap("prayer-veni-sancte-spiritus")
        compose.onNodeWithTag("reading-context").assertExists()
        back()
        compose.onNodeWithTag("treasury-scroll").performScrollToNode(hasTestTag("treasury-search"))
        tap("treasury-search-clear")
        compose.onNodeWithTag("treasury-search").performTextInput("xyznenhuma")
        compose.onNodeWithTag("treasury-empty-message", useUnmergedTree = true).assertTextEquals("Nenhuma oração encontrada para “xyznenhuma”.")
    }

    @Test fun `reading language and open explanation survive activity recreation`() {
        tap("tab-TREASURY")
        tap("prayer-ave-maria")
        tap("language-BILINGUAL")
        tap("term-0")
        compose.activityRule.scenario.recreate()
        compose.awaitSertumReady()
        compose.waitForIdle()
        compose.onNodeWithTag("term-title").assertTextEquals("“cheia de graça”")
        back()
        compose.onNodeWithTag("language-BILINGUAL").assertIsSelected()
    }

    @Test fun `submitted full text search opens the result and restores bilingual study and query`() {
        tap("tab-TREASURY")
        val search = compose.onNodeWithTag("treasury-search")
        search.performTextInput("consubstancial")
        search.assertTextEquals("consubstancial")
        search.performImeAction()
        search.assertIsNotFocused()
        compose.onNodeWithTag("treasury-scroll").performScrollToNode(hasTestTag("prayer-credo-niceno"))
        compose.onNodeWithTag("prayer-credo-niceno").assertIsDisplayed().performClick()
        compose.onNodeWithTag("reading-title").assertTextEquals("Credo Niceno-Constantinopolitano")
        tap("language-BILINGUAL"); tap("verse-0")
        compose.onNodeWithTag("word-by-word").assertExists()
        compose.activityRule.scenario.recreate()
        compose.awaitSertumReady()
        compose.onNodeWithTag("language-BILINGUAL").assertIsSelected()
        compose.onNodeWithTag("word-by-word").assertExists()
        back()
        compose.onNodeWithTag("treasury-search").assertTextContains("consubstancial")
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

    @Test fun `the rosary offers Latin on fixed prayers and keeps it across beads`() {
        tap("start-rosary")
        compose.onNodeWithTag("prayer-title").assertTextEquals("Sinal da Cruz")
        compose.onNodeWithTag("rosary-language-PORTUGUESE").assertIsSelected()
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
