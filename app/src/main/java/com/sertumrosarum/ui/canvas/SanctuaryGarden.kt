package com.sertumrosarum.ui.canvas

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sertumrosarum.R
import com.sertumrosarum.ui.theme.SertumTheme
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

/** A shared slow loop, held still when system animations are disabled. */
@Composable
internal fun rememberGardenBreath(label: String, millis: Int = 9000): State<Float> {
    if (SertumTheme.reducedMotion) return remember { mutableFloatStateOf(0f) }
    return rememberInfiniteTransition(label = label)
        .animateFloat(0f, (2 * PI).toFloat(), infiniteRepeatable(tween(millis, easing = LinearEasing), RepeatMode.Restart), label = "$label-phase")
}

/**
 * Bundled, model-generated sacred paintings with separate day and moonlit editions.
 * Only the surrounding light and pollen move; the painted figures stay at rest.
 * See docs/artwork/garden/README.md for the art direction and generation prompts.
 */
@Composable
fun SanctuaryGarden(modifier: Modifier = Modifier) {
    val dark = SertumTheme.colors.isDark
    val accent = SertumTheme.atmosphere.accent
    val breath = rememberGardenBreath("sanctuary-garden", millis = 12000)
    Box(modifier) {
        Canvas(Modifier.matchParentSize()) {
            inGardenSpace {
                val warmth = .5f + .5f * sin(breath.value)
                val light = Offset(180f, 130f)
                drawCircle(
                    Brush.radialGradient(
                        listOf(accent.copy(alpha = (if (dark) .10f else .14f) + .025f * warmth), Color.Transparent),
                        light, 138f,
                    ), 138f, light,
                )
                val skyLight = Offset(326f, 28f)
                val honey = if (dark) Color(0xFFF1DEAD) else Color(0xFFEBD29D)
                drawCircle(
                    Brush.radialGradient(listOf(honey.copy(alpha = if (dark) .10f else .16f), Color.Transparent), skyLight, 26f),
                    26f, skyLight,
                )
                if (dark) {
                    val moon = Path().apply { addOval(Rect(skyLight, 9f)) }
                    val bite = Path().apply { addOval(Rect(skyLight + Offset(4f, -3f), 8f)) }
                    drawPath(Path.combine(PathOperation.Difference, moon, bite), honey.copy(alpha = .8f))
                } else {
                    drawCircle(honey.copy(alpha = .5f), 8f, skyLight)
                }
            }
        }
        Image(
            painter = painterResource(if (dark) R.drawable.sanctuary_garden_vigil else R.drawable.sanctuary_garden_day),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(4.dp),
            contentScale = ContentScale.Fit,
        )
        Canvas(Modifier.matchParentSize()) {
            inGardenSpace {
                val phase = breath.value
                // Sparse light at the edges, leaving the faces and hands clear.
                listOf(Offset(38f, 119f), Offset(298f, 104f), Offset(56f, 49f), Offset(320f, 183f), Offset(280f, 26f)).forEachIndexed { index, mote ->
                    val shimmer = .5f + .5f * sin(phase + index * 1.7f)
                    drawCircle(
                        (if (dark) Color(0xFFF1DEAD) else accent).copy(alpha = (if (dark) .22f else .16f) + .12f * shimmer),
                        if (index % 2 == 0) 1.1f else .8f,
                        mote + Offset(1.2f * sin(phase + index), 2.5f * sin(phase + index * 1.3f)),
                    )
                }
            }
        }
    }
}

/** Fit the atmosphere to the same 6:5 composition on narrow phones and wide layouts. */
private fun DrawScope.inGardenSpace(draw: DrawScope.() -> Unit) {
    val fit = min(size.width / 360f, size.height / 300f)
    withTransform({
        translate((size.width - 360f * fit) / 2f, (size.height - 300f * fit) / 2f)
        scale(fit, fit, Offset.Zero)
    }, draw)
}
