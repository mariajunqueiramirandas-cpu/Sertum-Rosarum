package com.sertumrosarum

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.SavedStateHandle
import com.github.takahirom.roborazzi.captureRoboImage
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.feature.guides.PrayerGuide
import com.sertumrosarum.feature.prayer.PrayerTextReader
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import com.sertumrosarum.feature.prayer.ThemeMode
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
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class PrayerReadingLayoutTest {
    @get:Rule val compose = createComposeRule()

    private fun reading(mode: ThemeMode, fontScale: Float = 1f, guide: PrayerGuide = PrayerGuide.TRADITIONAL): PrayerSessionViewModel {
        val model = PrayerSessionViewModel(SavedStateHandle(), Clock.fixed(Instant.parse("2026-11-12T12:00:00Z"), ZoneOffset.UTC))
        model.selectMystery(MysterySet.LUMINOUS)
        model.setTheme(mode)
        model.start(guide)
        model.jumpToDecade(1)
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                SertumApp(model)
            }
        }
        return model
    }

    private fun controlsAreVisible() {
        listOf("previous-prayer", "next-prayer", "eyes-closed-toggle", "bead-counter").forEach {
            compose.onNodeWithTag(it).assertIsDisplayed()
        }
    }

    private fun spaciousReading(mode: ThemeMode) {
        reading(mode)
        val screen = compose.onNodeWithTag("prayer-screen").getUnclippedBoundsInRoot()
        val viewport = compose.onNodeWithTag("prayer-scroll").getUnclippedBoundsInRoot()
        val scene = compose.onNodeWithTag("mystery-scene").getUnclippedBoundsInRoot()
        assertTrue("Reading should occupy most of the screen", viewport.height >= screen.height * .6f)
        assertTrue("Narrative text should use the page width", scene.width >= screen.width * .82f)
        assertTrue("The biblical scene should fit before scrolling", scene.bottom <= viewport.bottom)
        controlsAreVisible()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/prayer-reading-redesign-${mode.name.lowercase()}.png")
    }

    @Test fun `day reading gives the biblical scene most of the screen`() = spaciousReading(ThemeMode.DAY)

    @Test fun `vigil reading gives the biblical scene most of the screen`() = spaciousReading(ThemeMode.VIGIL)

    @Test fun `reading position survives recreation and a different section starts at the top`() {
        val key = mutableIntStateOf(0)
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            SertumTheme(ThemeMode.DAY, MysterySet.LUMINOUS) {
                PrayerTextReader(Modifier.fillMaxWidth().height(220.dp), readingKey = key.intValue) {
                    SacredText(FixedPrayer.CREED.text, style = SertumTheme.typography.prayer)
                }
            }
        }
        val scroll = compose.onNodeWithTag("prayer-scroll")
        fun position() = scroll.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value()
        compose.onNodeWithTag("prayer-scroll-cue").performClick()
        compose.waitUntil { position() > 0f }
        compose.waitForIdle()
        val before = position()
        restoration.emulateSavedInstanceStateRestore()
        assertEquals(before, position())
        compose.runOnIdle { key.intValue = 1 }
        compose.waitUntil { position() == 0f }
    }

    @Test fun `changing dimensions holds the reading heading in place`() {
        reading(ThemeMode.DAY)
        val before = compose.onNodeWithTag("dimension-0").getUnclippedBoundsInRoot().top
        compose.onNodeWithTag("dimension-1").performClick().assertIsSelected()
        compose.onNodeWithTag("mystery-meaning").assertIsDisplayed()
        assertEquals(before, compose.onNodeWithTag("dimension-0").getUnclippedBoundsInRoot().top)
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/prayer-reading-meaning-day.png")
        compose.onNodeWithTag("dimension-2").performClick().assertIsSelected()
        compose.onNodeWithTag("mystery-practice").assertIsDisplayed()
        assertEquals(before, compose.onNodeWithTag("dimension-0").getUnclippedBoundsInRoot().top)
        controlsAreVisible()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/prayer-reading-fruit-day.png")
    }

    @Test
    @Config(qualifiers = "w320dp-h640dp-xxhdpi")
    fun `long devotional titles wrap without clipping the title or controls`() {
        reading(ThemeMode.VIGIL, fontScale = 1.8f, guide = PrayerGuide.DIVINE_MERCY)
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithTag("prayer-session-title").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertTrue("The complete session title should remain visible", !layouts.single().hasVisualOverflow)
        controlsAreVisible()
        assertTrue(compose.onNodeWithTag("prayer-scroll").getUnclippedBoundsInRoot().height > 100.dp)
    }

    @Test
    @Config(qualifiers = "w320dp-h640dp-xxhdpi")
    fun `a narrow phone keeps enlarged text and full touch targets`() {
        reading(ThemeMode.VIGIL, fontScale = 1.8f)
        controlsAreVisible()
        listOf("dimension-0", "dimension-1", "dimension-2", "eyes-closed-toggle", "next-prayer").forEach {
            assertTrue("$it should retain a 48 dp target", compose.onNodeWithTag(it).getUnclippedBoundsInRoot().height >= 48.dp)
        }
        compose.onNodeWithTag("prayer-scroll-cue").assertIsDisplayed().performClick()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/prayer-reading-narrow-large-text.png")
    }

    @Test fun `large text can be paged without changing the prayer and the next prayer starts at the top`() {
        val model = reading(ThemeMode.VIGIL, fontScale = 1.8f)
        val step = model.state.value.stepIndex
        val scroll = compose.onNodeWithTag("prayer-scroll")
        val cue = compose.onNodeWithTag("prayer-scroll-cue")
        controlsAreVisible()
        compose.onNodeWithTag("prayer-scroll-position").assertIsDisplayed()
        assertTrue(scroll.getUnclippedBoundsInRoot().height > 180.dp)
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/prayer-reading-redesign-large-text.png")
        cue.performClick()
        compose.waitUntil { scroll.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value() > 0f }
        assertEquals(step, model.state.value.stepIndex)
        scroll.performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 100_000f) }
        compose.onNodeWithText("Voltar ao início").assertIsDisplayed()
        cue.performClick()
        compose.waitUntil { scroll.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value() == 0f }
        compose.onNodeWithTag("next-prayer").performClick()
        compose.onNodeWithTag("prayer-title").assertTextEquals("Pai-Nosso")
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithTag("prayer-text").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(TextAlign.Start, layouts.single().layoutInput.style.textAlign)
        assertEquals(21.sp, layouts.single().layoutInput.style.fontSize)
        assertEquals(34.sp, layouts.single().layoutInput.style.lineHeight)
        assertEquals(0f, scroll.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value())
        controlsAreVisible()
        compose.onNodeWithTag("eyes-closed-toggle").performClick().assertIsOn()
        compose.onNodeWithTag("eyes-closed-zone").assertIsDisplayed()
    }

    @Test
    @Config(qualifiers = "w851dp-h393dp-land-xxhdpi")
    fun `landscape keeps reading and navigation available`() {
        reading(ThemeMode.DAY)
        controlsAreVisible()
        compose.onNodeWithTag("prayer-scroll-cue").assertIsDisplayed().performClick()
        assertTrue(compose.onNodeWithTag("prayer-scroll").getUnclippedBoundsInRoot().height > 100.dp)
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/prayer-reading-redesign-landscape.png")
    }
}
