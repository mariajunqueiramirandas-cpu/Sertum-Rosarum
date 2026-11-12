package com.sertumrosarum.ui.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.sertumrosarum.ui.theme.SertumTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RoseMark(modifier: Modifier = Modifier, color: Color = SertumTheme.colors.ruby) {
    val heart = SertumTheme.colors.gold
    Canvas(modifier) { drawGardenFlower(center, size.minDimension * .46f, color, heart) }
}

@Composable
fun RoseCrownEmblem(modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    Canvas(modifier) {
        val radius = size.minDimension * .39f
        drawCircle(Brush.radialGradient(listOf(colors.gold.copy(alpha = .12f), Color.Transparent), center, radius * 1.25f), radius * 1.25f, center)
        drawCircle(colors.marian.copy(alpha = .2f), radius, center, style = Stroke(1.5f))
        repeat(50) { index ->
            val angle = (-90 + index * 7.2f) * PI / 180
            val position = center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * radius
            drawGardenFlower(position, radius * (if (index % 10 == 0) .075f else .042f), if (index % 10 == 0) colors.ruby else colors.gold.copy(alpha = .6f), colors.gold)
        }
        drawCircle(colors.ruby.copy(alpha = .055f), radius * .68f, center)
        drawGardenFlower(center, radius * .4f, colors.ruby.copy(alpha = .7f), colors.gold)
        repeat(5) { index ->
            val angle = (-90 + index * 72f) * PI / 180
            val point = center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * (radius * .77f)
            drawCircle(colors.marian.copy(alpha = .5f), radius * .02f, point)
        }
    }
}
