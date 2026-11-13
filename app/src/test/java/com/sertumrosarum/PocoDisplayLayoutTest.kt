package com.sertumrosarum

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import com.github.takahirom.roborazzi.captureRoboImage
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.feature.guides.PrayerGuide
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.feature.prayer.ThemeMode
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * POCO X8 Pro: 1268 × 2756 px, 6.59 inches (Xiaomi's official specification).
 * 480 dpi and 560 dpi are test densities, not claims about the HyperOS default.
 * Rounded dp fixtures preserve its aspect ratio; a representative status/camera
 * area and gesture area are reserved independently of Robolectric's empty insets.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w423dp-h919dp-xxhdpi")
class PocoDisplayLayoutTest {
    @get:Rule val compose = createComposeRule()

    private fun render(fontScale: Float = 1f, guide: PrayerGuide? = null): PrayerSessionViewModel {
        val model = PrayerSessionViewModel(SavedStateHandle(), Clock.fixed(Instant.parse("2026-11-13T12:00:00Z"), ZoneOffset.UTC))
        model.setTheme(ThemeMode.DAY)
        model.selectMystery(MysterySet.SORROWFUL)
        if (guide != null) model.start(guide)
        compose.setContent {
            val density = LocalDensity.current
            val state by model.state.collectAsState()
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                Box(Modifier.fillMaxSize().background(if (state.themeMode == ThemeMode.VIGIL) Color(0xFF162A27) else Color(0xFFFBF8F2)).padding(top = 32.dp, bottom = 24.dp)) { SertumApp(model) }
            }
        }
        return model
    }

    private fun shot(name: String) {
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/poco-$name.png")
    }

    private fun allTextFits() {
        compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult), useUnmergedTree = true)
            .fetchSemanticsNodes().forEach { node ->
                val results = mutableListOf<TextLayoutResult>()
                node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(results)
                results.forEach { layout ->
                    // BasicText can keep a paragraph at its original constraint
                    // width while reporting its tight intrinsic size. Check the
                    // actual lines, not that empty paragraph width.
                    val widestLine = (0 until layout.lineCount).maxOfOrNull { layout.getLineRight(it) - layout.getLineLeft(it) } ?: 0f
                    val ellipsized = (0 until layout.lineCount).any { layout.isLineEllipsized(it) }
                    assertTrue("Clipped text: ${layout.layoutInput.text}", !ellipsized && widestLine <= layout.size.width + 1f && !layout.didOverflowHeight)
                }
            }
    }

    private fun prayerControlsFit() {
        listOf("previous-prayer", "next-prayer", "eyes-closed-toggle", "bead-counter").forEach {
            compose.onNodeWithTag(it, useUnmergedTree = true).assertIsDisplayed()
        }
        listOf("previous-prayer", "next-prayer", "eyes-closed-toggle").forEach {
            assertTrue("$it must keep a full touch target", compose.onNodeWithTag(it).getUnclippedBoundsInRoot().height >= 48.dp)
        }
    }

    private fun singleRowMysteries(fontScale: Float, profile: String) {
        val model = render(fontScale = fontScale)
        for (mode in listOf(ThemeMode.DAY, ThemeMode.VIGIL)) {
            compose.runOnIdle { model.setTheme(mode) }
            compose.onNodeWithTag("mystery-selector").performScrollTo()
            val rowTop = compose.onNodeWithTag("mystery-JOYFUL").getUnclippedBoundsInRoot().top
            val cardTop = compose.onNodeWithTag("mystery-card").getUnclippedBoundsInRoot().top
            MysterySet.entries.forEach { set ->
                val choice = compose.onNodeWithTag("mystery-${set.name}")
                choice.performClick().assertIsSelected()
                val bounds = choice.getUnclippedBoundsInRoot()
                assertEquals("All four mysteries should share a row at $profile", rowTop, bounds.top)
                assertTrue("A tighter selector must retain full touch targets", bounds.height >= 48.dp)
                val results = mutableListOf<TextLayoutResult>()
                compose.onNodeWithTag("mystery-label-${set.name}", useUnmergedTree = true)
                    .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
                assertEquals("The complete label should fit on one line", 1, results.single().lineCount)
                assertEquals("Fitting a row must not shrink the type", 13.5.sp, results.single().layoutInput.style.fontSize)
                assertEquals("Selecting a mystery must not move its card", cardTop, compose.onNodeWithTag("mystery-card").getUnclippedBoundsInRoot().top)
                allTextFits()
            }
            compose.runOnIdle { model.selectMystery(MysterySet.SORROWFUL) }
            shot("mysteries-one-row-$profile-${mode.name.lowercase()}")
        }
    }

    @Test fun treasuryBilingualReadingFitsPocoAtEnlargedText() {
        val model = render(fontScale = 1.5f)
        compose.runOnIdle { model.selectTab(SanctuaryTab.TREASURY) }
        compose.onNodeWithTag("treasury-search").performTextInput("Ave Maria")
        compose.onNodeWithTag("treasury-scroll").performScrollToNode(hasTestTag("prayer-ave-maria"))
        allTextFits()
        compose.onNodeWithTag("prayer-ave-maria").performSemanticsAction(SemanticsActions.OnClick)
        compose.onNodeWithTag("language-BILINGUAL").performScrollTo().performSemanticsAction(SemanticsActions.OnClick)
        compose.onNodeWithTag("verse-0").performScrollTo().performSemanticsAction(SemanticsActions.OnClick)
        compose.onNodeWithTag("word-by-word").assertExists()
        allTextFits()
        shot("treasury-bilingual-large-text")
    }

    @Test
    @Config(qualifiers = "w360dp-h783dp-xxhdpi")
    fun `a normal narrow phone keeps all four mystery choices on one row`() = singleRowMysteries(1.1f, "360dp")

    @Test
    @Config(qualifiers = "w393dp-h854dp-xxhdpi")
    fun `a modest font increase still keeps the mystery selector on one row`() = singleRowMysteries(1.2f, "393dp")

    @Test fun `portrait sanctuary and every tab fit in day and vigil`() {
        val model = render()
        for (mode in listOf(ThemeMode.DAY, ThemeMode.VIGIL)) {
            compose.runOnIdle { model.setTheme(mode); model.selectTab(SanctuaryTab.HOME) }
            compose.onNodeWithTag("sanctuary-greeting").assertIsDisplayed()
            compose.onNodeWithTag("start-rosary").assertIsDisplayed()
            allTextFits()
            shot("sanctuary-${mode.name.lowercase()}")
            compose.onNodeWithTag("mystery-selector").performScrollTo()
            MysterySet.entries.forEach { set ->
                compose.onNodeWithTag("mystery-${set.name}").performClick().assertIsSelected()
                allTextFits()
            }
            for (tab in SanctuaryTab.entries) {
                compose.onNodeWithTag("tab-${tab.name}").performClick()
                allTextFits()
            }
            shot("garden-${mode.name.lowercase()}")
        }
    }

    @Test
    @Config(qualifiers = "w362dp-h787dp-560dpi")
    fun `larger text reflows mysteries and navigation without hiding the home action`() {
        render(fontScale = 1.6f)
        compose.onNodeWithTag("start-rosary").assertIsDisplayed()
        val action = compose.onNodeWithTag("start-rosary").getUnclippedBoundsInRoot()
        val bar = compose.onNodeWithTag("bottom-bar").getUnclippedBoundsInRoot()
        assertTrue("Navigation must reserve its actual height", action.bottom <= bar.top)
        SanctuaryTab.entries.forEach { compose.onNodeWithTag("tab-${it.name}").assertIsDisplayed() }
        allTextFits()
        val tabLayout = mutableListOf<TextLayoutResult>()
        compose.onNodeWithTag("tab-label-GUIDES", useUnmergedTree = true).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(tabLayout) }
        assertEquals("A navigation word must fit without splitting", 1, tabLayout.single().lineCount)
        compose.onNodeWithTag("mystery-selector").performScrollTo()
        val firstRow = compose.onNodeWithTag("mystery-JOYFUL").getUnclippedBoundsInRoot()
        val secondRow = compose.onNodeWithTag("mystery-SORROWFUL").getUnclippedBoundsInRoot()
        assertTrue("Enlarged choices should reflow instead of squeezing their labels", secondRow.top >= firstRow.bottom)
        MysterySet.entries.forEach { set ->
            compose.onNodeWithTag("mystery-${set.name}").performClick().assertIsSelected()
            assertTrue(compose.onNodeWithTag("mystery-${set.name}").getUnclippedBoundsInRoot().height >= 48.dp)
            allTextFits()
        }
        shot("mysteries-large-text")
        compose.onNodeWithTag("prayer-mode-SOLO").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("prayer-mode-GROUP").assertIsDisplayed()
        compose.onNodeWithTag("tab-GUIDES").performClick()
        allTextFits()
        shot("guides-large-text")
        compose.onNodeWithTag("guide-DIVINE_MERCY").performScrollTo().performClick()
        compose.onNodeWithTag("guide-start").assertIsDisplayed()
        allTextFits()
        shot("guide-large-text")
    }

    @Test fun `prayer keeps a calm aligned reading column and visible controls`() {
        val model = render(guide = PrayerGuide.TRADITIONAL)
        compose.runOnIdle { model.jumpToDecade(0); model.jumpToBead(2) }
        for (mode in listOf(ThemeMode.DAY, ThemeMode.VIGIL)) {
            compose.runOnIdle { model.setTheme(mode) }
            prayerControlsFit()
            allTextFits()
            compose.onNodeWithTag("prayer-scroll-cue").assertDoesNotExist()
            val results = mutableListOf<TextLayoutResult>()
            compose.onNodeWithTag("prayer-text").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
            assertEquals(TextAlign.Start, results.single().layoutInput.style.textAlign)
            assertEquals(21.sp, results.single().layoutInput.style.fontSize)
            assertEquals(34.sp, results.single().layoutInput.style.lineHeight)
            assertEquals(
                compose.onNodeWithTag("previous-prayer").getUnclippedBoundsInRoot().left,
                compose.onNodeWithTag("prayer-text").getUnclippedBoundsInRoot().left,
            )
            shot("prayer-${mode.name.lowercase()}")
        }
    }

    @Test
    @Config(qualifiers = "w362dp-h787dp-560dpi")
    fun `long titles prayer paging and eyes closed mode fit with larger text`() {
        val model = render(fontScale = 1.8f, guide = PrayerGuide.TRADITIONAL)
        compose.runOnIdle { model.setTheme(ThemeMode.VIGIL); model.jumpToDecade(4); model.jumpToBead(0) }
        prayerControlsFit()
        allTextFits()
        val scroll = compose.onNodeWithTag("prayer-scroll")
        compose.onNodeWithTag("prayer-scroll-cue").assertIsDisplayed().performClick()
        compose.waitUntil { scroll.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value() > 0f }
        shot("prayer-large-text")
        compose.onNodeWithTag("eyes-closed-toggle").performClick()
        compose.onNodeWithTag("eyes-closed-zone").assertIsDisplayed()
        prayerControlsFit()
        allTextFits()
        shot("eyes-closed-large-text")
    }

    @Test
    @Config(qualifiers = "w362dp-h787dp-560dpi")
    fun `delivered crown and return action fit with larger text`() {
        val model = render(fontScale = 1.8f, guide = PrayerGuide.TRADITIONAL)
        compose.runOnIdle {
            model.jumpToDecade(4)
            model.jumpToBead(9)
            repeat(5) { model.next() }
        }
        compose.onNodeWithTag("crown-delivered").assertIsDisplayed()
        compose.onNodeWithTag("return-sanctuary").assertIsDisplayed()
        allTextFits()
        compose.onNodeWithTag("prayer-duration").performScrollTo().assertIsDisplayed()
        shot("delivered-large-text")
    }

    @Test
    @Config(qualifiers = "w919dp-h423dp-land-xxhdpi")
    fun `rotation keeps reading and tactile navigation within the safe area`() {
        val model = render(guide = PrayerGuide.TRADITIONAL)
        compose.runOnIdle { model.jumpToDecade(0); model.jumpToBead(0) }
        prayerControlsFit()
        allTextFits()
        assertTrue(compose.onNodeWithTag("prayer-scroll").getUnclippedBoundsInRoot().height >= 80.dp)
        shot("prayer-landscape")
        compose.onNodeWithTag("eyes-closed-toggle").performClick()
        prayerControlsFit()
        allTextFits()
        shot("eyes-closed-landscape")
    }
}
