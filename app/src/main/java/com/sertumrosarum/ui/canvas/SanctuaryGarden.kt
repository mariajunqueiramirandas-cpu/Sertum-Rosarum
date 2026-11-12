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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
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

/** Original soft rose: five open petals around a paler inner whorl. Each prayer grows the same flower. */
fun DrawScope.drawGardenFlower(center: Offset, radius: Float, petal: Color, heart: Color, bloom: Float = 1f) {
    val r = radius * (.6f + bloom * .4f)
    val inner = lerp(petal.copy(alpha = 1f), Color.White, .34f).copy(alpha = petal.alpha)
    repeat(5) { index ->
        rotate(index * 72f, center) {
            drawOval(petal, Offset(center.x - r * .47f, center.y - r), Size(r * .94f, r * 1.04f))
        }
    }
    repeat(5) { index ->
        rotate(36f + index * 72f, center) {
            drawOval(inner, Offset(center.x - r * .3f, center.y - r * .68f), Size(r * .6f, r * .68f))
        }
    }
    drawCircle(heart, r * .22f, center)
    drawCircle(Color.White.copy(alpha = .45f), r * .07f, center - Offset(r * .06f, r * .07f))
}

/** A closed bud resting on two sepals: a prayer still waiting to be prayed. */
fun DrawScope.drawGardenBud(center: Offset, radius: Float, bud: Color, sepal: Color) {
    val r = radius
    val base = Offset(center.x, center.y + r * .85f)
    gardenLeaf(base, Offset(center.x - r * .95f, center.y - r * .1f), r * .3f, sepal)
    gardenLeaf(base, Offset(center.x + r * .95f, center.y - r * .1f), r * .3f, sepal)
    drawPath(Path().apply {
        moveTo(center.x, center.y - r)
        cubicTo(center.x + r * .95f, center.y - r * .2f, center.x + r * .62f, center.y + r * .85f, center.x, center.y + r * .85f)
        cubicTo(center.x - r * .62f, center.y + r * .85f, center.x - r * .95f, center.y - r * .2f, center.x, center.y - r)
    }, bud)
}

internal fun DrawScope.gardenLeaf(start: Offset, end: Offset, width: Float, color: Color) {
    val direction = end - start
    val middle = (start + end) / 2f
    val normal = Offset(-direction.y, direction.x) / direction.getDistance().coerceAtLeast(1f) * width
    drawPath(Path().apply {
        moveTo(start.x, start.y)
        quadraticTo(middle.x + normal.x, middle.y + normal.y, end.x, end.y)
        quadraticTo(middle.x - normal.x, middle.y - normal.y, start.x, start.y)
    }, color)
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
