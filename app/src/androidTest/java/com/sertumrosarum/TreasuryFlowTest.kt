package com.sertumrosarum

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.text.TextLayoutResult
import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.domain.treasury.TreasuryCatalog
import org.junit.Rule
import org.junit.Test

/** Real-device coverage of the completed offline Treasury and its in-session Latin integration. */
class TreasuryFlowTest {
    @get:Rule(order = 0) val freshGarden = FreshGardenRule()
    @get:Rule(order = 1) val compose = createAndroidComposeRule<MainActivity>()
    @org.junit.Before fun awaitLocalRecords() {
        compose.waitUntil(15_000) { compose.onAllNodes(androidx.compose.ui.test.hasTestTag("bottom-bar") or androidx.compose.ui.test.hasTestTag("prayer-screen"), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
    }
    private fun tap(tag: String) {
        compose.ensurePrayerTarget(tag)
        if (tag.startsWith("prayer-") && tag != "prayer-text") {
            compose.onNodeWithTag("treasury-scroll").performScrollToNode(hasTestTag(tag))
        }
        compose.onNodeWithTag(tag, useUnmergedTree = true).performClick()
        compose.waitForIdle()
    }
    private fun back() {
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
    }

    @Test fun inlineExpressionOpensItsSheetAndDragReturnsToReading() {
        tap("tab-TREASURY"); tap("prayer-ave-maria")
        val layouts = mutableListOf<TextLayoutResult>()
        val text = compose.onNodeWithTag("reading-text")
        text.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        val offset = layout.layoutInput.text.text.indexOf("cheia de graça")
        val bounds = layout.getBoundingBox(offset)
        text.performTouchInput { click(bounds.center) }
        compose.onNodeWithTag("term-title").assertTextEquals("“cheia de graça”")
        compose.onNodeWithTag("term-sheet-handle").performTouchInput {
            swipe(center, center + Offset(0f, 450f), durationMillis = 350)
        }
        compose.onNodeWithTag("term-sheet").assertDoesNotExist()
        compose.onNodeWithTag("treasury-reading").assertIsDisplayed()
    }

    @Test fun fullTextSearchBilingualGlossesAndRecreationWorkOffline() {
        tap("tab-TREASURY")
        compose.onNodeWithTag("treasury-search").performTextInput("consubstancial")
        tap("prayer-credo-niceno")
        tap("language-BILINGUAL"); tap("verse-0")
        compose.onNodeWithTag("word-by-word").assertExists()
        compose.activityRule.scenario.recreate(); compose.awaitSertumReady(); compose.waitForIdle()
        compose.onNodeWithTag("language-BILINGUAL").assertIsSelected()
        compose.onNodeWithTag("word-by-word").assertExists()
        back()
        compose.onNodeWithTag("treasury-search").assertTextContains("consubstancial")
    }

    @Test fun everyMysteryCanOpenItsFullStudyOutsidePrayer() {
        tap("tab-TREASURY")
        compose.onNodeWithTag("treasury-scroll").performScrollToNode(hasTestTag("open-mysteries"))
        tap("open-mysteries")
        for (set in listOf("JOYFUL", "LUMINOUS", "SORROWFUL", "GLORIOUS")) {
            compose.onNodeWithTag("mystery-$set").performScrollTo()
            tap("mystery-$set")
            repeat(5) { index ->
                compose.onNodeWithTag("study-mystery-$index").performScrollTo()
                tap("study-mystery-$index")
                compose.onNodeWithTag("mystery-scene").assertExists()
                compose.onNodeWithTag("bead-by-bead").assertExists()
                back()
            }
        }
    }

    @Test fun latinCoversTheWholeTraditionalRosaryAndRestoresAtTheLastPrayer() {
        tap("start-rosary"); tap("rosary-language-LATIN")
        fun assertLatin(fixed: FixedPrayer) {
            compose.onNodeWithTag("prayer-text").assertTextEquals(requireNotNull(TreasuryCatalog.latinFor(fixed)?.latin))
        }
        assertLatin(FixedPrayer.SIGN_OF_CROSS)
        tap("next-prayer")
        compose.onNodeWithTag("prayer-text").assertTextEquals(TreasuryCatalog.latinFor(FixedPrayer.OFFERING)!!.latinSolo!!)
        tap("next-prayer"); assertLatin(FixedPrayer.CREED)
        tap("next-prayer"); assertLatin(FixedPrayer.OUR_FATHER)
        tap("next-prayer"); assertLatin(FixedPrayer.HAIL_MARY)
        tap("decade-4"); tap("bead-9")
        tap("next-prayer"); assertLatin(FixedPrayer.GLORY_BE)
        tap("next-prayer"); assertLatin(FixedPrayer.FATIMA)
        tap("next-prayer")
        compose.onNodeWithTag("prayer-text").assertTextEquals(TreasuryCatalog.latinFor(FixedPrayer.THANKSGIVING)!!.latinSolo!!)
        tap("next-prayer"); assertLatin(FixedPrayer.HAIL_HOLY_QUEEN)
        compose.activityRule.scenario.recreate(); compose.awaitSertumReady(); compose.waitForIdle()
        compose.onNodeWithTag("rosary-language-LATIN").assertIsSelected()
        assertLatin(FixedPrayer.HAIL_HOLY_QUEEN)
        tap("rosary-language-PORTUGUESE")
        compose.onNodeWithTag("prayer-text").assertTextEquals(FixedPrayer.HAIL_HOLY_QUEEN.text)
    }
}
