package com.sertumrosarum.ui.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sertumrosarum.ui.theme.SertumTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RoseMark(modifier: Modifier = Modifier, alpha: Float = 1f) {
    val art = LocalBotanicalArtwork.current
    Canvas(modifier) { paintBotanical(art.rose, center, size.minDimension, alpha) }
}

/** Fifty painted roses: five ivory blooms mark the mysteries within the woven crown. */
@Composable
fun RoseCrownEmblem(modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val accent = SertumTheme.atmosphere.accent
    val art = LocalBotanicalArtwork.current
    val breath = rememberGardenBreath("rose-crown", 7000)
    Canvas(modifier.semantics { contentDescription = "Coroa de cinquenta rosas, uma por Ave-Maria" }) {
        val radius = size.minDimension * .405f
        val warmth = .5f + .5f * sin(breath.value)
        fun onRing(degrees: Float, distance: Float): Offset {
            val angle = degrees * PI / 180
            return center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * distance
        }
        drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = .12f + .04f * warmth), Color.Transparent), center, radius * 1.2f), radius * 1.2f, center)
        drawCircle(Brush.radialGradient(listOf(colors.ruby.copy(alpha = .10f), Color.Transparent), center, radius * .8f), radius * .8f, center)
        // Two fine stems weave around the same fifty positions as the flowers.
        repeat(2) { strand ->
            val vine = Path().apply {
                repeat(201) { step ->
                    val degrees = -90f + step * 1.8f
                    val distance = radius * (1f + .018f * sin((step / 200f * PI * 20 + strand * PI).toFloat()))
                    val p = onRing(degrees, distance)
                    if (step == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
            }
            drawPath(vine, colors.marian.copy(alpha = if (strand == 0) .65f else .3f), style = Stroke(radius * .009f, cap = StrokeCap.Round))
        }
        repeat(50) { index ->
            val degrees = -90f + index * 7.2f + 2f
            val reach = if (index % 2 == 0) 1.14f else .88f
            val sway = .3f * sin(breath.value + index * .7f)
            paintLeaf(art.leaf, onRing(degrees, radius), onRing(degrees + 5f + sway, radius * reach), if (colors.isDark) .82f else .9f)
        }
        repeat(50) { index ->
            val mystery = index % 10 == 0
            paintBotanical(
                if (mystery) art.ivoryRose else art.rose,
                onRing(-90f + index * 7.2f, radius * (1f + .008f * sin(index * 1.7f))),
                radius * (if (mystery) .195f else .125f),
                alpha = if (index % 3 == 0 || mystery) 1f else .88f,
                turn = index * 137.5f % 50f - 25f,
            )
        }
        // A quiet gold setting and a single offered rose at the heart.
        drawCircle(colors.gold.copy(alpha = .3f), radius * .51f, center, style = Stroke(.55.dp.toPx()))
        repeat(12) { index ->
            drawCircle(colors.gold.copy(alpha = .45f), radius * .005f, onRing(index * 30f, radius * .56f))
        }
        paintLeaf(art.leaf, center + Offset(-radius * .10f, radius * .07f), center + Offset(-radius * .44f, -radius * .15f), .9f)
        paintLeaf(art.leaf, center + Offset(radius * .10f, radius * .07f), center + Offset(radius * .44f, -radius * .18f), .9f)
        paintBotanical(art.rose, center + Offset(0f, radius * .015f), radius * .76f)
    }
}
