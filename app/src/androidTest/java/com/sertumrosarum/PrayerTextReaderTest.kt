package com.sertumrosarum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.feature.prayer.PrayerTextReader
import com.sertumrosarum.feature.prayer.ThemeMode
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PrayerTextReaderTest {
    @get:Rule val compose = createComposeRule()

    @Test fun long_text_can_be_paged_to_the_end_and_returned_to_the_start_at_large_font_size() {
        reader(FixedPrayer.CREED.text, largeFont = true)
        val scroll = compose.onNodeWithTag("prayer-scroll")
        val cue = compose.onNodeWithTag("prayer-scroll-cue")
        compose.onNodeWithTag("prayer-scroll-position").assertIsDisplayed()
        cue.assertIsDisplayed().assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Há mais texto abaixo"))

        cue.performClick()
        compose.waitUntil { scroll.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value() > 0f }
        assertTrue(scroll.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].maxValue() > 0f)
        // Native accessibility scrolling and the visible cue share the same reader.
        scroll.performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 100_000f) }
        compose.waitUntil {
            val range = scroll.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange]
            range.value() == range.maxValue()
        }
        cue.assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Fim do texto"))
        compose.onNodeWithText("Voltar ao início").assertIsDisplayed()
        cue.performClick()
        compose.waitUntil { scroll.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value() == 0f }
        compose.onNodeWithText("Continue lendo").assertIsDisplayed()
    }

    @Test fun text_that_fits_has_no_scroll_cues_even_in_vigil_mode() {
        reader("Amém.", largeFont = false)
        compose.onNodeWithText("Amém.").assertIsDisplayed()
        compose.onNodeWithTag("prayer-scroll-position").assertDoesNotExist()
        compose.onNodeWithTag("prayer-scroll-cue").assertDoesNotExist()
    }

    private fun reader(text: String, largeFont: Boolean) {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if (largeFont) 1.8f else 1f)) {
                SertumTheme(ThemeMode.VIGIL, MysterySet.LUMINOUS) {
                    PrayerTextReader(Modifier.fillMaxWidth().height(220.dp).background(SertumTheme.colors.background)) {
                        SacredText(text, style = SertumTheme.typography.prayer)
                    }
                }
            }
        }
    }
}
