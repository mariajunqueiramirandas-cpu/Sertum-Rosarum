package com.sertumrosarum.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class SertumColors(
    val primary: Color,
    val onPrimary: Color,
    val background: Color,
    val onBackground: Color
)

fun lightSertumColors() = SertumColors(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    background = LightBackground,
    onBackground = LightOnBackground
)

fun darkSertumColors() = SertumColors(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    background = DarkBackground,
    onBackground = DarkOnBackground
)

val LocalSertumColors = staticCompositionLocalOf { lightSertumColors() }
val LocalSertumTypography = staticCompositionLocalOf { DefaultTypography }

@Composable
fun SertumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkSertumColors() else lightSertumColors()
    CompositionLocalProvider(
        LocalSertumColors provides colors,
        LocalSertumTypography provides DefaultTypography
    ) {
        content()
    }
}
