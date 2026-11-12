package com.sertumrosarum.ui.canvas

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import com.sertumrosarum.ui.theme.SertumTheme
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

/**
 * A slow loop from 0 to 2π that drives every ambient movement in the garden.
 * It stays at rest when the person has turned animations off.
 */
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

private fun cubic(t: Float, a: Offset, b: Offset, c: Offset, d: Offset): Offset {
    val u = 1 - t
    return a * (u * u * u) + b * (3 * u * u * t) + c * (3 * u * t * t) + d * (t * t * t)
}

/** A climbing stem that rises along the left of the arch and leans over its crown. */
private fun vinePoint(u: Float): Offset = if (u < .42f) {
    val rise = u / .42f
    Offset(101f + 2.4f * sin(rise * 9f), 252f - rise * 124f)
} else {
    cubic((u - .42f) / .58f * .8f, Offset(101f, 128f), Offset(101f, 24f), Offset(259f, 24f), Offset(259f, 128f))
}

/** A tiny Marian garden, drawn entirely for Sertum: a sheltering arch, soft figures and living roses. */
@Composable
fun SanctuaryGarden(modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val accent = SertumTheme.atmosphere.accent
    val dark = colors.isDark
    val breath = rememberGardenBreath("sanctuary-garden")
    Canvas(modifier) {
        val phase = breath.value
        val fit = min(size.width / 360f, size.height / 300f)
        withTransform({
            translate((size.width - 360f * fit) / 2f, (size.height - 300f * fit) / 2f)
            scale(fit, fit, Offset.Zero)
        }) {
            val sage = if (dark) Color(0xFF4B6955) else Color(0xFFB7C9AB)
            val deepSage = if (dark) Color(0xFF6A8A6F) else Color(0xFF8DA683)
            val stone = if (dark) Color(0xFFB9AE8C) else Color(0xFFEDE3CE)
            val stoneLight = if (dark) Color(0xFFD8CCA9) else Color(0xFFFCF7EC)
            val stoneShade = if (dark) Color(0xFF9E9373) else Color(0xFFDCCFB4)
            val rose = if (dark) Color(0xFFDCA79A) else Color(0xFFE0A397)
            val peach = if (dark) Color(0xFFE5C7A9) else Color(0xFFF0D2B8)
            val honey = Color(0xFFEFD08D)
            val skin = Color(0xFFEBC7AD)
            val glow = Offset(180f, 132f)

            // Air and light behind the arch.
            drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = if (dark) .2f else .34f), Color.Transparent), glow, 168f), 168f, glow)
            if (dark) {
                val moonCenter = Offset(288f, 66f)
                drawCircle(Brush.radialGradient(listOf(Color(0xFFF6E8BB).copy(alpha = .22f), Color.Transparent), moonCenter, 46f), 46f, moonCenter)
                val moon = Path().apply { addOval(Rect(moonCenter, 16f)) }
                val bite = Path().apply { addOval(Rect(moonCenter + Offset(8f, -6f), 14f)) }
                drawPath(Path.combine(PathOperation.Difference, moon, bite), Color(0xFFF6E8BB))
                listOf(Offset(52f, 62f), Offset(84f, 104f), Offset(318f, 132f), Offset(246f, 34f), Offset(30f, 132f)).forEachIndexed { index, star ->
                    drawCircle(Color(0xFFF6E8BB).copy(alpha = .35f + .3f * (.5f + .5f * sin(phase * 2f + index * 1.7f))), if (index % 2 == 0) 1.5f else 1.1f, star)
                }
            } else {
                val sun = Offset(288f, 68f)
                drawCircle(Brush.radialGradient(listOf(Color(0xFFF7E3AE).copy(alpha = .6f), Color.Transparent), sun, 52f), 52f, sun)
                drawCircle(Color(0xFFF6DDA2), 16f, sun)
                fun cloud(x: Float, y: Float, scale: Float, alpha: Float) {
                    val white = Color.White.copy(alpha = alpha)
                    drawRoundRect(white, Offset(x - 24f * scale, y - 5f * scale), Size(48f * scale, 11f * scale), CornerRadius(5.5f * scale))
                    drawCircle(white, 9f * scale, Offset(x - 7f * scale, y - 6f * scale))
                    drawCircle(white, 6.5f * scale, Offset(x + 6f * scale, y - 4f * scale))
                }
                cloud(58f + 3f * sin(phase), 92f, 1f, .85f)
                cloud(318f - 2f * sin(phase), 134f, .62f, .6f)
            }

            // Far hills and the sheltered lawn.
            drawOval(sage.copy(alpha = .16f), Offset(-30f, 196f), Size(210f, 96f))
            drawOval(sage.copy(alpha = .13f), Offset(190f, 204f), Size(210f, 90f))
            drawOval(sage.copy(alpha = .22f), Offset(6f, 234f), Size(348f, 58f))
            drawOval(sage.copy(alpha = .34f), Offset(40f, 246f), Size(280f, 40f))
            drawOval(Color.Black.copy(alpha = if (dark) .14f else .05f), Offset(96f, 250f), Size(168f, 13f))

            // The ceramic arch and its niche.
            val arch = Path().apply {
                moveTo(104f, 252f); lineTo(104f, 128f)
                cubicTo(104f, 30f, 256f, 30f, 256f, 128f)
                lineTo(256f, 252f); close()
            }
            drawPath(arch, Brush.horizontalGradient(listOf(stone, stoneLight, stone), 104f, 256f))
            drawPath(arch, stoneShade.copy(alpha = .55f), style = Stroke(1f))
            val niche = Path().apply {
                moveTo(120f, 252f); lineTo(120f, 130f)
                cubicTo(120f, 52f, 240f, 52f, 240f, 130f)
                lineTo(240f, 252f); close()
            }
            drawPath(niche, Brush.verticalGradient(listOf(if (dark) Color(0xFF587460) else Color(0xFFFCF8E9), if (dark) Color(0xFF36513F) else Color(0xFFD5E1C6)), 70f, 252f))
            clipPath(niche) {
                drawPath(niche, Color.Black.copy(alpha = if (dark) .16f else .06f), style = Stroke(9f))
                drawOval(sage.copy(alpha = .45f), Offset(100f, 214f), Size(160f, 80f))

                val halo = Offset(180f, 117f)
                val warmth = .5f + .5f * sin(phase)
                drawCircle(Brush.radialGradient(listOf(honey.copy(alpha = .42f + .16f * warmth), Color.Transparent), halo, 44f), 44f, halo)
                drawCircle(honey.copy(alpha = .85f), 22.5f, halo, style = Stroke(1.1f))

                // Quiet, faceless silhouettes of Mary holding the Child.
                val mantle = Path().apply {
                    moveTo(180f, 92f)
                    cubicTo(161f, 92f, 153f, 111f, 152f, 132f)
                    cubicTo(150f, 172f, 141f, 214f, 132f, 247f)
                    quadraticTo(180f, 262f, 228f, 247f)
                    cubicTo(219f, 214f, 210f, 172f, 208f, 132f)
                    cubicTo(207f, 111f, 199f, 92f, 180f, 92f)
                }
                drawPath(mantle, Brush.horizontalGradient(listOf(Color(0xFF7FA3AB), Color(0xFFB3CBCC), Color(0xFF7B9FA8)), 134f, 226f))
                val robe = Path().apply {
                    moveTo(171f, 136f); quadraticTo(163f, 192f, 157f, 251f)
                    quadraticTo(180f, 258f, 203f, 251f)
                    quadraticTo(197f, 192f, 189f, 136f); close()
                }
                drawPath(robe, Brush.verticalGradient(listOf(Color(0xFFFAF4E6), Color(0xFFE9DFC8)), 136f, 252f))
                drawPath(Path().apply { moveTo(149f, 164f); quadraticTo(146f, 208f, 138f, 244f) }, Color.White.copy(alpha = .2f), style = Stroke(1.4f, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(211f, 164f); quadraticTo(214f, 208f, 222f, 244f) }, Color.White.copy(alpha = .14f), style = Stroke(1.4f, cap = StrokeCap.Round))
                rotate(-7f, Offset(180f, 118f)) {
                    drawOval(Color(0xFFF8F1E3), Offset(167.5f, 102.5f), Size(25f, 30f))
                    drawOval(skin, Offset(170.5f, 106.5f), Size(19f, 23f))
                    drawPath(Path().apply {
                        moveTo(165.5f, 119f); cubicTo(164f, 96f, 196f, 96f, 194.5f, 119f)
                        cubicTo(191f, 104f, 169f, 104f, 165.5f, 119f)
                    }, Color(0xFF9DBBBF))
                }
                drawPath(Path().apply { moveTo(203f, 154f); quadraticTo(204f, 174f, 192f, 180f) }, Color(0xFF8FAFB4), style = Stroke(9.5f, cap = StrokeCap.Round))
                rotate(-32f, Offset(176f, 176f)) {
                    drawOval(Color(0xFFFDF9EF), Offset(167.5f, 160f), Size(17f, 34f))
                }
                drawCircle(honey.copy(alpha = .7f), 9.6f, Offset(166f, 159f), style = Stroke(.9f))
                drawCircle(skin, 6.6f, Offset(166f, 159f))
                drawPath(Path().apply { moveTo(158f, 154f); quadraticTo(159f, 188f, 187f, 185f) }, Color(0xFFA6C1C3), style = Stroke(9.5f, cap = StrokeCap.Round))
                drawCircle(skin, 3.9f, Offset(188.5f, 184.5f))
            }
            drawPath(niche, stoneShade.copy(alpha = .7f), style = Stroke(1.1f))
            drawRoundRect(stoneLight, Offset(95f, 248f), Size(170f, 6.5f), CornerRadius(3.2f))
            drawRoundRect(stone, Offset(90f, 253.5f), Size(180f, 6.5f), CornerRadius(3.2f))

            // The vine that crowns the arch.
            val vine = Path()
            repeat(41) { index ->
                val point = vinePoint(index / 40f)
                if (index == 0) vine.moveTo(point.x, point.y) else vine.lineTo(point.x, point.y)
            }
            drawPath(vine, deepSage, style = Stroke(1.8f, cap = StrokeCap.Round))
            repeat(15) { index ->
                val u = (index + 1) / 16f
                val point = vinePoint(u)
                val ahead = vinePoint(u + .012f) - point
                val along = ahead / ahead.getDistance().coerceAtLeast(.01f)
                val side = Offset(-along.y, along.x) * (if (index % 2 == 0) 1f else -1f)
                gardenLeaf(point, point + side * 9f + along * 5f, 3.4f, if (index % 3 == 0) deepSage else sage)
            }
            listOf(Triple(.33f, 6.5f, rose), Triple(.62f, 5.4f, peach), Triple(.83f, 6.6f, accent), Triple(1f, 5.6f, rose)).forEach { (u, radius, color) ->
                drawGardenFlower(vinePoint(u), radius, color, honey)
            }

            // Roses sway a little, each in its own time.
            fun gardenRose(x: Float, y: Float, radius: Float, color: Color, turn: Float) {
                rotate(1.7f * sin(phase + turn), Offset(x, 264f)) {
                    drawPath(Path().apply { moveTo(x - 3f, 266f); quadraticTo(x + 6f, y + 22f, x, y) }, deepSage, style = Stroke(2.2f, cap = StrokeCap.Round))
                    gardenLeaf(Offset(x, y + 24f), Offset(x - 19f, y + 12f), 6.5f, sage)
                    gardenLeaf(Offset(x + 1f, y + 33f), Offset(x + 18f, y + 22f), 6.5f, deepSage)
                    drawGardenFlower(Offset(x, y), radius, color, honey)
                }
            }
            gardenRose(62f, 204f, 15.5f, rose, 0f)
            gardenRose(34f, 233f, 10.5f, accent, 1.9f)
            gardenRose(86f, 236f, 11.5f, peach, 3.4f)
            gardenRose(298f, 208f, 15.5f, rose, 2.6f)
            gardenRose(327f, 236f, 10.5f, accent, .8f)
            gardenRose(274f, 239f, 11.5f, peach, 4.4f)
            listOf(14f to -1f, 112f to -1f, 248f to 1f, 346f to 1f).forEach { (x, lean) ->
                gardenLeaf(Offset(x, 268f), Offset(x + lean * 9f, 252f), 3.4f, sage)
                gardenLeaf(Offset(x, 268f), Offset(x - lean * 4f, 250f), 3f, deepSage)
            }

            // Pollen in the light, and two birds far away.
            listOf(Offset(70f, 150f), Offset(300f, 160f), Offset(266f, 116f), Offset(88f, 60f), Offset(42f, 186f), Offset(326f, 96f)).forEachIndexed { index, mote ->
                drawCircle(accent.copy(alpha = if (dark) .8f else .7f), if (index % 2 == 0) 1.9f else 1.4f, mote + Offset(0f, 3.5f * sin(phase + index * 1.3f)))
            }
            fun bird(x: Float, y: Float, span: Float) = drawPath(
                Path().apply { moveTo(x, y); quadraticTo(x + span * .5f, y - span * .5f, x + span, y); quadraticTo(x + span * 1.5f, y - span * .5f, x + span * 2f, y - span * .15f) },
                deepSage.copy(alpha = .6f), style = Stroke(1.3f, cap = StrokeCap.Round),
            )
            bird(66f, 48f, 9f)
            bird(98f, 36f, 6f)
        }
    }
}
