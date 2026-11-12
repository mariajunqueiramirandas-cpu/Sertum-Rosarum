package com.sertumrosarum.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.ExperimentalTextApi
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

@Immutable
data class SertumColors(
    val background: Color, val surface: Color, val elevated: Color,
    val ink: Color, val secondary: Color, val muted: Color,
    val gold: Color, val ruby: Color, val marian: Color, val border: Color,
    val onRuby: Color, val isDark: Boolean,
)

private val DayColors = SertumColors(
    Color(0xFFFAF8F1), Color(0xFFEEF0E6), Color(0xFFFFFEFA),
    Color(0xFF2F493E), Color(0xFF6A7C70), Color(0xFF839084),
    Color(0xFFBA9655), Color(0xFFAB625C), Color(0xFF789A81), Color(0xFFDFE5D8),
    Color(0xFFFFFAF2), false,
)
private val VigilColors = SertumColors(
    Color(0xFF172C29), Color(0xFF233D35), Color(0xFF2C493E),
    Color(0xFFF4F1E5), Color(0xFFBDCEBD), Color(0xFF8EA896),
    Color(0xFFD4B879), Color(0xFFDFA79A), Color(0xFF9DBE9D), Color(0xFF365246),
    Color(0xFF283D31), true,
)

@OptIn(ExperimentalTextApi::class)
private fun variableFamily(resource: Int) = FontFamily(
    Font(resource, weight = FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(resource, weight = FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(resource, weight = FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(resource, weight = FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
)

val SanctuaryFont = variableFamily(R.font.nunito)
val Jakarta = variableFamily(R.font.jakarta)

@Immutable
data class SertumTypography(
    val brand: TextStyle = TextStyle(fontFamily = SanctuaryFont, fontSize = 25.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold),
    val display: TextStyle = TextStyle(fontFamily = SanctuaryFont, fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold),
    val heading: TextStyle = TextStyle(fontFamily = SanctuaryFont, fontSize = 26.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold),
    val section: TextStyle = TextStyle(fontFamily = SanctuaryFont, fontSize = 19.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold),
    val prayer: TextStyle = TextStyle(fontFamily = SanctuaryFont, fontSize = 20.sp, lineHeight = 31.sp),
    val italic: TextStyle = TextStyle(fontFamily = SanctuaryFont, fontSize = 16.sp, lineHeight = 24.sp),
    val body: TextStyle = TextStyle(fontFamily = Jakarta, fontSize = 13.sp, lineHeight = 21.sp),
    val label: TextStyle = TextStyle(fontFamily = Jakarta, fontSize = 13.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    val micro: TextStyle = TextStyle(fontFamily = SanctuaryFont, fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold),
)

@Immutable
data class SertumSpacing(val small: Dp = 8.dp, val medium: Dp = 16.dp, val page: Dp = 24.dp, val large: Dp = 32.dp)
@Immutable
data class SertumShapes(val card: Dp = 28.dp, val control: Dp = 22.dp)
@Immutable
data class MysteryAtmosphere(val tint: Color, val accent: Color, val chapel: Color)

private fun atmosphere(set: MysterySet, dark: Boolean): MysteryAtmosphere = when (set) {
    MysterySet.JOYFUL -> MysteryAtmosphere(if (dark) Color(0xFF343A2C) else Color(0xFFF6EBDD), Color(0xFFD6A49A), Color(0xFF819A77))
    MysterySet.LUMINOUS -> MysteryAtmosphere(if (dark) Color(0xFF2B3D2B) else Color(0xFFF1EFD8), Color(0xFFDCC385), Color(0xFF829F86))
    MysterySet.SORROWFUL -> MysteryAtmosphere(if (dark) Color(0xFF32333E) else Color(0xFFF0E9EF), Color(0xFFB6A3BB), Color(0xFF87968E))
    MysterySet.GLORIOUS -> MysteryAtmosphere(if (dark) Color(0xFF263D40) else Color(0xFFE7F0EC), Color(0xFFA0C4CA), Color(0xFF789B98))
}

val LocalSertumColors = staticCompositionLocalOf { DayColors }
val LocalSertumTypography = staticCompositionLocalOf { SertumTypography() }
val LocalSertumSpacing = staticCompositionLocalOf { SertumSpacing() }
val LocalSertumShapes = staticCompositionLocalOf { SertumShapes() }
val LocalMysteryAtmosphere = staticCompositionLocalOf { atmosphere(MysterySet.LUMINOUS, false) }

object SertumTheme {
    val colors: SertumColors @Composable get() = LocalSertumColors.current
    val typography: SertumTypography @Composable get() = LocalSertumTypography.current
    val spacing: SertumSpacing @Composable get() = LocalSertumSpacing.current
    val shapes: SertumShapes @Composable get() = LocalSertumShapes.current
    val atmosphere: MysteryAtmosphere @Composable get() = LocalMysteryAtmosphere.current
}

@Composable
fun SertumTheme(mode: ThemeMode, mysterySet: MysterySet, content: @Composable () -> Unit) {
    val dark = when (mode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DAY -> false
        ThemeMode.VIGIL -> true
    }
    val colors = if (dark) VigilColors else DayColors
    CompositionLocalProvider(
        LocalSertumColors provides colors,
        LocalIndication provides remember(colors.gold) { SacredIndication(colors.gold) },
        LocalSertumTypography provides SertumTypography(),
        LocalSertumSpacing provides SertumSpacing(),
        LocalSertumShapes provides SertumShapes(),
        LocalMysteryAtmosphere provides atmosphere(mysterySet, dark),
        content = content,
    )
}
