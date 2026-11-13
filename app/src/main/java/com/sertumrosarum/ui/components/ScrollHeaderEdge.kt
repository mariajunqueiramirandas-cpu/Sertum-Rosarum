package com.sertumrosarum.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sertumrosarum.ui.theme.SertumTheme

/** Fade the scrolling content itself, revealing the real page wash below its fixed header. */
internal fun Modifier.softScrollTop(scroll: ScrollState, depth: Dp = 32.dp): Modifier = drawWithCache {
    val maskPaint = Paint()
    onDrawWithContent {
        val fade = scroll.value.toFloat().coerceAtMost(depth.toPx()).coerceAtMost(size.height)
        if (fade <= 0f) {
            drawContent()
        } else {
            val canvas = drawContext.canvas
            canvas.saveLayer(Rect(Offset.Zero, size), maskPaint)
            try {
                drawContent()
                drawRect(
                    Brush.verticalGradient(0f to Color.Black, fade / size.height to Color.Transparent, 1f to Color.Transparent, endY = size.height),
                    blendMode = BlendMode.DstOut,
                )
            } finally {
                canvas.restore()
            }
        }
    }
}

/** A small sage-tinted shadow appears only after the page begins to pass under the header. */
@Composable
internal fun ScrollHeaderShadow(scroll: ScrollState, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    Canvas(modifier.fillMaxWidth().height(20.dp)) {
        val strength = (scroll.value / 24.dp.toPx()).coerceIn(0f, 1f)
        if (strength > 0f) {
            val shade = if (colors.isDark) Color(0xFF071510) else colors.ink
            drawRect(Brush.verticalGradient(listOf(shade.copy(alpha = strength * if (colors.isDark) .10f else .045f), Color.Transparent)))
        }
    }
}
