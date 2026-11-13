package com.sertumrosarum

import android.graphics.Typeface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.font.FontWeight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sertumrosarum.ui.theme.SanctuaryFont
import com.sertumrosarum.ui.theme.VoiceDisplay
import com.sertumrosarum.ui.theme.VoiceText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Check the loaded Android typefaces, including the weights used on pale actions. */
@RunWith(AndroidJUnit4::class)
class TypographyTest {
    @get:Rule val compose = createComposeRule()

    @Test fun bundled_fonts_resolve_to_the_requested_weights() {
        var resolved = emptyList<Pair<Int, Typeface>>()
        compose.setContent {
            val resolver = LocalFontFamilyResolver.current
            val samples = listOf(
                VoiceDisplay to FontWeight.Normal,
                VoiceDisplay to FontWeight.Medium,
                VoiceText to FontWeight.Normal,
                SanctuaryFont to FontWeight.Normal,
                SanctuaryFont to FontWeight.Medium,
                SanctuaryFont to FontWeight.SemiBold,
                SanctuaryFont to FontWeight.Bold,
            ).map { (family, weight) -> weight.weight to (resolver.resolve(family, weight).value as Typeface) }
            SideEffect { resolved = samples }
        }
        compose.runOnIdle {
            assertEquals(7, resolved.size)
            resolved.forEach { (expected, typeface) -> assertEquals("Android loaded the wrong font weight", expected, typeface.weight) }
        }
    }
}
