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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import com.sertumrosarum.ui.theme.SertumTheme

@Composable
fun SertumScaffold(content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier.fillMaxSize().background(SertumTheme.colors.background).background(
            Brush.verticalGradient(listOf(lerp(SertumTheme.colors.background, SertumTheme.atmosphere.tint, .35f), SertumTheme.colors.background), endY = 900f),
        )
            .windowInsetsPadding(WindowInsets.safeDrawing),
        content = content,
    )
}
