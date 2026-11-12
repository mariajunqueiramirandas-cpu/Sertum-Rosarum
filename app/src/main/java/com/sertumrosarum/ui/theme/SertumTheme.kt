package com.sertumrosarum.ui.theme

import android.provider.Settings
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.R
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.feature.prayer.ThemeMode

/**
 * [ruby] is the rose of the flowers and small accents. Actions are petals:
 * a pale [petal] fill carrying a deep [onPetal] label, so nothing on screen shouts.
 */
@Immutable
data class SertumColors(
    val background: Color, val surface: Color, val elevated: Color,
    val ink: Color, val secondary: Color, val muted: Color,
    val gold: Color, val ruby: Color, val marian: Color, val border: Color,
    val petal: Color, val petalDeep: Color, val onPetal: Color, val isDark: Boolean,
)

private val DayColors = SertumColors(
    background = Color(0xFFFBF8F2), surface = Color(0xFFF1F0E6), elevated = Color(0xFFFFFDF9),
    ink = Color(0xFF324B40), secondary = Color(0xFF667A6E), muted = Color(0xFF8A968B),
    gold = Color(0xFFB8935A), ruby = Color(0xFFC07A72), marian = Color(0xFF789A81), border = Color(0xFFE6E9DD),
    petal = Color(0xFFF8E3DA), petalDeep = Color(0xFFEFCDC2), onPetal = Color(0xFF7A433D), isDark = false,
)
private val VigilColors = SertumColors(
    background = Color(0xFF162A27), surface = Color(0xFF1F3731), elevated = Color(0xFF27433A),
    ink = Color(0xFFF3EFE3), secondary = Color(0xFFBACBBB), muted = Color(0xFF8CA594),
    gold = Color(0xFFD8BE84), ruby = Color(0xFFE0A99C), marian = Color(0xFF9DBE9D), border = Color(0xFF314D40),
    petal = Color(0xFFEDC6BA), petalDeep = Color(0xFFDFA99C), onPetal = Color(0xFF4A2925), isDark = true,
)

@OptIn(ExperimentalTextApi::class)
private fun variableFamily(resource: Int, vararg axes: FontVariation.Setting) = FontFamily(
    listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold).map { weight ->
        Font(resource, weight = weight, variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight), *axes))
    },
)

/**
 * Fraunces is the voice of the garden. Its softness axis is fully open, so every
 * serif ends rounded. Titles use the display cut; prayers use the sturdier text cut.
 */
@OptIn(ExperimentalTextApi::class)
val VoiceDisplay = variableFamily(R.font.fraunces, FontVariation.Setting("SOFT", 100f), FontVariation.Setting("WONK", 0f), FontVariation.Setting("opsz", 48f))
@OptIn(ExperimentalTextApi::class)
val VoiceText = variableFamily(R.font.fraunces, FontVariation.Setting("SOFT", 100f), FontVariation.Setting("WONK", 0f), FontVariation.Setting("opsz", 30f))

/** Nunito carries controls and small supporting copy. */
val SanctuaryFont = variableFamily(R.font.nunito)

@Immutable
data class SertumTypography(
    val brand: TextStyle = TextStyle(fontFamily = VoiceDisplay, fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Medium),
    val display: TextStyle = TextStyle(fontFamily = VoiceDisplay, fontSize = 31.sp, lineHeight = 39.sp, fontWeight = FontWeight.Normal),
    val heading: TextStyle = TextStyle(fontFamily = VoiceDisplay, fontSize = 28.sp, lineHeight = 35.sp, fontWeight = FontWeight.Normal),
    val section: TextStyle = TextStyle(fontFamily = VoiceDisplay, fontSize = 20.sp, lineHeight = 27.sp, fontWeight = FontWeight.Medium),
    val prayer: TextStyle = TextStyle(fontFamily = VoiceText, fontSize = 21.sp, lineHeight = 34.sp),
    val whisper: TextStyle = TextStyle(fontFamily = VoiceText, fontSize = 17.sp, lineHeight = 27.sp),
    val body: TextStyle = TextStyle(fontFamily = SanctuaryFont, fontSize = 15.sp, lineHeight = 23.sp),
    val label: TextStyle = TextStyle(fontFamily = SanctuaryFont, fontSize = 15.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold),
    val micro: TextStyle = TextStyle(fontFamily = SanctuaryFont, fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold),
)

@Immutable
data class SertumSpacing(val small: Dp = 8.dp, val medium: Dp = 16.dp, val page: Dp = 24.dp, val large: Dp = 32.dp)
@Immutable
data class SertumShapes(val card: Dp = 30.dp, val control: Dp = 30.dp)
@Immutable
data class MysteryAtmosphere(val tint: Color, val accent: Color, val chapel: Color)

private fun atmosphere(set: MysterySet, dark: Boolean): MysteryAtmosphere = when (set) {
    MysterySet.JOYFUL -> MysteryAtmosphere(if (dark) Color(0xFF343A2C) else Color(0xFFF8E9DC), Color(0xFFE0AFA3), Color(0xFF819A77))
    MysterySet.LUMINOUS -> MysteryAtmosphere(if (dark) Color(0xFF2B3D2B) else Color(0xFFF4EFD6), Color(0xFFE2CA8E), Color(0xFF829F86))
    MysterySet.SORROWFUL -> MysteryAtmosphere(if (dark) Color(0xFF32333E) else Color(0xFFF1E9F0), Color(0xFFBFADC6), Color(0xFF87968E))
    MysterySet.GLORIOUS -> MysteryAtmosphere(if (dark) Color(0xFF263D40) else Color(0xFFE6F1EE), Color(0xFFA6CBD0), Color(0xFF789B98))
}

val LocalSertumColors = staticCompositionLocalOf { DayColors }
val LocalSertumTypography = staticCompositionLocalOf { SertumTypography() }
val LocalSertumSpacing = staticCompositionLocalOf { SertumSpacing() }
val LocalSertumShapes = staticCompositionLocalOf { SertumShapes() }
val LocalMysteryAtmosphere = staticCompositionLocalOf { atmosphere(MysterySet.LUMINOUS, false) }
val LocalReducedMotion = staticCompositionLocalOf { false }

object SertumTheme {
    val colors: SertumColors @Composable get() = LocalSertumColors.current
    val typography: SertumTypography @Composable get() = LocalSertumTypography.current
    val spacing: SertumSpacing @Composable get() = LocalSertumSpacing.current
    val shapes: SertumShapes @Composable get() = LocalSertumShapes.current
    val atmosphere: MysteryAtmosphere @Composable get() = LocalMysteryAtmosphere.current
    /** True when the person has turned animations off; ambient motion then stays still. */
    val reducedMotion: Boolean @Composable get() = LocalReducedMotion.current
}

@Composable
fun SertumTheme(mode: ThemeMode, mysterySet: MysterySet, content: @Composable () -> Unit) {
    val dark = when (mode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DAY -> false
        ThemeMode.VIGIL -> true
    }
    val colors = if (dark) VigilColors else DayColors
    val context = LocalContext.current
    val reducedMotion = remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    CompositionLocalProvider(
        LocalSertumColors provides colors,
        LocalIndication provides remember(colors.marian) { SacredIndication(colors.marian) },
        LocalSertumTypography provides SertumTypography(),
        LocalSertumSpacing provides SertumSpacing(),
        LocalSertumShapes provides SertumShapes(),
        LocalMysteryAtmosphere provides atmosphere(mysterySet, dark),
        LocalReducedMotion provides reducedMotion,
        content = content,
    )
}
