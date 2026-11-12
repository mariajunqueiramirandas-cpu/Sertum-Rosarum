package com.sertumrosarum.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.sertumrosarum.ui.theme.SertumTheme

/** The page is morning air: the mystery's colour gathers softly at the top and thins out. */
@Composable
fun SertumScaffold(content: @Composable BoxScope.() -> Unit) {
    val colors = SertumTheme.colors
    val atmosphere = SertumTheme.atmosphere
    Box(
        Modifier.fillMaxSize().background(colors.background).drawBehind {
            val wash = lerp(colors.background, atmosphere.tint, if (colors.isDark) .5f else .55f)
            drawRect(Brush.verticalGradient(listOf(wash, colors.background), endY = size.height * .62f))
            val bloom = Offset(size.width * .5f, size.height * .2f)
            drawCircle(
                Brush.radialGradient(listOf(atmosphere.accent.copy(alpha = if (colors.isDark) .09f else .13f), Color.Transparent), bloom, size.width * .85f),
                size.width * .85f, bloom,
            )
        }.windowInsetsPadding(WindowInsets.safeDrawing),
        content = content,
    )
}
