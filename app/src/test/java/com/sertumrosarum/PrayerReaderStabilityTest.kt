package com.sertumrosarum

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.github.takahirom.roborazzi.captureRoboImage
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import com.sertumrosarum.feature.prayer.PrayerTextReader
import com.sertumrosarum.feature.prayer.ThemeMode
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme
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

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w423dp-h919dp-xxhdpi")
class PrayerReaderStabilityTest {
    @get:Rule val compose = createComposeRule()

    private fun bounds(tag: String) = compose.onNodeWithTag(tag).getUnclippedBoundsInRoot()

    private fun shot(name: String) {
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/reader-stability/$name.png")
    }

    private fun render(fontScale: Float = 1f): PrayerSessionViewModel {
        val model = PrayerSessionViewModel(SavedStateHandle(), Clock.fixed(Instant.parse("2026-11-13T12:00:00Z"), ZoneOffset.UTC))
        model.setTheme(ThemeMode.VIGIL)
        model.selectMystery(MysterySet.SORROWFUL)
        model.start()
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                Box(Modifier.fillMaxSize().padding(top = 32.dp, bottom = 24.dp)) { SertumApp(model) }
            }
        }
        return model
    }

    @Test fun `the offering and scrollable creed keep the same reading anchors`() {
        val model = render()
        compose.runOnIdle { model.next() }
        compose.onNodeWithTag("prayer-scroll-cue").assertDoesNotExist()
        val titleTop = bounds("prayer-title").top
        val textTop = bounds("prayer-text").top
        val controls = listOf("pause-prayer", "previous-prayer", "next-prayer", "eyes-closed-toggle", "bead-counter")
            .associateWith { bounds(it) }
        shot("offering")
        compose.runOnIdle { model.next() }
        compose.onNodeWithTag("prayer-scroll-cue").assertIsDisplayed()
        assertEquals("Adding scroll controls must not lift the heading", titleTop, bounds("prayer-title").top)
        assertEquals("The first line must keep its position", textTop, bounds("prayer-text").top)
        controls.forEach { (tag, before) -> assertEquals("$tag must stay in place", before, bounds(tag)) }
        shot("creed")
        compose.runOnIdle { model.previous() }
        compose.onNodeWithTag("prayer-scroll-cue").assertDoesNotExist()
        assertEquals(titleTop, bounds("prayer-title").top)
        assertEquals(textTop, bounds("prayer-text").top)
    }

    @Test fun `appearing and disappearing paging controls never recenter the text`() {
        val bodyHeight = mutableStateOf(100.dp)
        compose.setContent {
            SertumTheme(ThemeMode.VIGIL, MysterySet.SORROWFUL) {
                Column {
                    PrayerTextReader(Modifier.fillMaxWidth().height(220.dp)) {
                        SacredText("Creio em Deus Pai", Modifier.testTag("reading-anchor"), SertumTheme.typography.heading)
                        Box(Modifier.fillMaxWidth().height(bodyHeight.value))
                    }
                    SacredPrimaryButton("Próxima oração", {}, tag = "stable-next")
                }
            }
        }
        compose.onNodeWithTag("prayer-scroll-cue").assertDoesNotExist()
        val anchor = bounds("reading-anchor")
        val next = bounds("stable-next")
        compose.mainClock.autoAdvance = false
        for (height in listOf(340.dp, 100.dp, 340.dp)) {
            compose.runOnIdle { bodyHeight.value = height }
            repeat(6) {
                compose.mainClock.advanceTimeByFrame()
                compose.waitForIdle()
                assertEquals("Paging must not move the paragraph", anchor, bounds("reading-anchor"))
                assertEquals("Paging must not move navigation", next, bounds("stable-next"))
            }
            if (height > 220.dp) compose.onNodeWithTag("prayer-scroll-cue").assertIsDisplayed()
            else compose.onNodeWithTag("prayer-scroll-cue").assertDoesNotExist()
        }
        compose.mainClock.autoAdvance = true
    }

    @Test
    @Config(qualifiers = "w393dp-h854dp-xxhdpi")
    fun `switching mystery sections from the bottom has no stale offset or resizing frames`() {
        val model = render(fontScale = 1.1f)
        compose.runOnIdle { model.jumpToDecade(0) }
        val titleTop = bounds("prayer-title").top
        val tabsTop = bounds("dimension-0").top
        val previous = bounds("previous-prayer")
        val next = bounds("next-prayer")
        val scroll = compose.onNodeWithTag("prayer-scroll")
        fun range() = scroll.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange]
        assertTrue("The scene must exercise actual overflow", range().maxValue() > 0f)
        listOf(1, 0, 2, 0).forEachIndexed { transition, section ->
            compose.mainClock.autoAdvance = true
            scroll.performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 100_000f) }
            compose.waitForIdle()
            assertEquals(range().maxValue(), range().value())
            shot("switch-$transition-bottom")
            compose.mainClock.autoAdvance = false
            compose.onNodeWithTag("dimension-$section").performSemanticsAction(SemanticsActions.OnClick) { check(it()) }
            // BoxWithConstraints subcomposes during Android measurement. Pump each
            // frame through layout, then sample from the first displayed selection.
            var selected = false
            repeat(8) {
                if (!selected) {
                    compose.mainClock.advanceTimeByFrame()
                    compose.waitForIdle()
                    selected = compose.onNodeWithTag("dimension-$section").fetchSemanticsNode().config[SemanticsProperties.Selected]
                }
            }
            assertTrue("The selected section should be displayed", selected)
            compose.waitForIdle()
            val body = bounds("dimension-body")
            val card = bounds("mystery-dimensions")
            var elapsed = 0L
            for ((frame, time) in listOf(0L, 32L, 64L, 96L, 128L, 160L, 240L, 480L).withIndex()) {
                if (time > elapsed) compose.mainClock.advanceTimeBy(time - elapsed)
                elapsed = time
                compose.waitForIdle()
                assertEquals("A new section must start at the top on every frame", 0f, range().value())
                compose.onAllNodesWithTag("prayer-scroll-cue").fetchSemanticsNodes().forEach { cue ->
                    assertEquals("The cue must describe the new section", "Há mais texto abaixo", cue.config[SemanticsProperties.StateDescription])
                }
                assertEquals(titleTop, bounds("prayer-title").top)
                assertEquals(tabsTop, bounds("dimension-0").top)
                assertEquals("The body must use its final bounds throughout its fade", body, bounds("dimension-body"))
                assertEquals("The card must not spring between heights", card, bounds("mystery-dimensions"))
                assertEquals(previous, bounds("previous-prayer"))
                assertEquals(next, bounds("next-prayer"))
                if (section != 0) compose.onNodeWithTag("mystery-scene").assertDoesNotExist()
                shot("switch-$transition-frame-$frame")
            }
            compose.onNodeWithTag("dimension-$section").assertIsSelected()
        }
        compose.mainClock.autoAdvance = true
    }
}
