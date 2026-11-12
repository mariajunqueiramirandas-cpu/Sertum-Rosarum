package com.sertumrosarum.ui.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import com.sertumrosarum.ui.theme.SertumTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RoseMark(modifier: Modifier = Modifier, color: Color = SertumTheme.colors.ruby) {
    val heart = SertumTheme.colors.gold
    Canvas(modifier) { drawGardenFlower(center, size.minDimension * .46f, color, heart) }
}

/** The finished crown: fifty small roses, five larger ones, woven with leaves around one blossom. */
@Composable
fun RoseCrownEmblem(modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val accent = SertumTheme.atmosphere.accent
    val breath = rememberGardenBreath("rose-crown", 7000)
    Canvas(modifier) {
        val radius = size.minDimension * .4f
        val warmth = .5f + .5f * sin(breath.value)
        fun onRing(degrees: Float, distance: Float): Offset {
            val angle = degrees * PI / 180
            return center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * distance
        }
        drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = .2f + .1f * warmth), Color.Transparent), center, radius * 1.25f), radius * 1.25f, center)
        drawCircle(Brush.radialGradient(listOf(colors.ruby.copy(alpha = .14f), Color.Transparent), center, radius * .78f), radius * .78f, center)
        drawCircle(colors.marian.copy(alpha = .35f), radius, center, style = Stroke(radius * .012f))
        repeat(50) { index ->
            val degrees = -90f + index * 7.2f + 3.6f
            val leaf = colors.marian.copy(alpha = if (index % 2 == 0) .55f else .38f)
            val reach = if (index % 2 == 0) 1.085f else .915f
            gardenLeaf(onRing(degrees - 1.4f, radius), onRing(degrees + 2.2f, radius * reach), radius * .022f, leaf)
        }
        repeat(50) { index ->
            val decade = index % 10 == 0
            drawGardenFlower(
                onRing(-90f + index * 7.2f, radius), radius * (if (decade) .085f else .045f),
                if (decade) colors.ruby else colors.ruby.copy(alpha = .55f), colors.gold,
            )
        }
        drawGardenFlower(center, radius * .4f, lerp(colors.background, colors.ruby, .82f), colors.gold)
    }
}
