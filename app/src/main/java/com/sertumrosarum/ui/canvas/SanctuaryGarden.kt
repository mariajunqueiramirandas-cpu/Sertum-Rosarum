package com.sertumrosarum.ui.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Rect
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
import com.sertumrosarum.ui.theme.SertumTheme

/** Original soft, six-petal rose; each prayer grows the same flower in the garden. */
fun DrawScope.drawGardenFlower(center: Offset, radius: Float, petal: Color, heart: Color, bloom: Float = 1f) {
    val r = radius * (.65f + bloom * .35f)
    repeat(6) { index ->
        rotate(index * 60f, center) {
            drawOval(petal, Offset(center.x - r * .42f, center.y - r), Size(r * .84f, r * 1.12f))
            drawOval(Color.White.copy(alpha = .09f), Offset(center.x - r * .21f, center.y - r * .84f), Size(r * .25f, r * .5f))
        }
    }
    drawCircle(heart, r * .28f, center)
    drawCircle(Color.White.copy(alpha = .35f), r * .08f, center - Offset(r * .07f, r * .08f))
}

private fun DrawScope.gardenLeaf(start: Offset, end: Offset, width: Float, color: Color) {
    val direction = end - start
    val middle = (start + end) / 2f
    val normal = Offset(-direction.y, direction.x) / direction.getDistance().coerceAtLeast(1f) * width
    val leaf = Path().apply {
        moveTo(start.x, start.y)
        quadraticTo(middle.x + normal.x, middle.y + normal.y, end.x, end.y)
        quadraticTo(middle.x - normal.x, middle.y - normal.y, start.x, start.y)
    }
    drawPath(leaf, color)
    drawLine(Color.White.copy(alpha = .12f), start, end, .6f, StrokeCap.Round)
}

/** A tiny Marian garden, drawn entirely for Sertum: ceramic arch, soft figures and living roses. */
@Composable
fun SanctuaryGarden(modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val accent = SertumTheme.atmosphere.accent
    val dark = colors.isDark
    Canvas(modifier) {
        withTransform({ scale(size.width / 360f, size.height / 278f, Offset.Zero) }) {
            val sage = if (dark) Color(0xFF496653) else Color(0xFFB0C3A3)
            val deepSage = if (dark) Color(0xFF65846A) else Color(0xFF88A17E)
            val stone = if (dark) Color(0xFFB9AE8B) else Color(0xFFE7DCC4)
            val petal = if (dark) Color(0xFFD5A194) else Color(0xFFD69B8D)
            drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = .23f), Color.Transparent), Offset(191f, 126f), 140f), 140f, Offset(191f, 126f))
            drawOval(sage.copy(alpha = .15f), Offset(20f, 203f), Size(318f, 60f))
            drawOval(sage.copy(alpha = .3f), Offset(48f, 215f), Size(272f, 41f))
            drawOval(Color.Black.copy(alpha = .06f), Offset(94f, 233f), Size(166f, 17f))
            if (dark) {
                val moon = Path().apply { addOval(Rect(258f, 42f, 288f, 72f)) }
                val opening = Path().apply { addOval(Rect(266f, 38f, 292f, 64f)) }
                drawPath(Path.combine(PathOperation.Difference, moon, opening), Color(0xFFF4E5B6))
            } else {
                drawCircle(Color(0xFFF0D69A).copy(alpha = .45f), 31f, Offset(270f, 66f))
                drawCircle(Color(0xFFF2DFAD), 21f, Offset(270f, 66f))
                drawOval(Color.White.copy(alpha = .7f), Offset(41f, 83f), Size(46f, 12f))
                drawOval(Color.White.copy(alpha = .7f), Offset(53f, 73f), Size(23f, 18f))
            }
            val arch = Path().apply {
                moveTo(98f, 237f); lineTo(98f, 111f)
                cubicTo(98f, 24f, 244f, 24f, 244f, 111f)
                lineTo(244f, 237f); close()
            }
            drawPath(arch, Brush.horizontalGradient(listOf(stone, if (dark) Color(0xFFD5C8A3) else Color(0xFFF4ECDD), stone), 98f, 244f))
            val opening = Path().apply {
                moveTo(114f, 236f); lineTo(114f, 114f)
                cubicTo(114f, 45f, 228f, 45f, 228f, 114f)
                lineTo(228f, 236f); close()
            }
            drawPath(opening, Brush.verticalGradient(listOf(if (dark) Color(0xFF4D6453) else Color(0xFFE6ECD5), if (dark) Color(0xFF344E40) else Color(0xFFC0D0B1)), 72f, 237f))
            clipPath(opening) {
                drawOval(sage.copy(alpha = .35f), Offset(90f, 187f), Size(170f, 90f))
                drawCircle(Color(0xFFF1DFA5).copy(alpha = .18f), 42f, Offset(177f, 116f))
                drawCircle(Color(0xFFF1DFA5).copy(alpha = .75f), 25f, Offset(177f, 116f), style = Stroke(1f))
                // Quiet, faceless clay silhouettes of Mary holding the Child.
                val cloak = Path().apply {
                    moveTo(176f, 100f)
                    cubicTo(146f, 103f, 151f, 159f, 132f, 223f)
                    quadraticTo(175f, 244f, 214f, 222f)
                    cubicTo(194f, 170f, 201f, 116f, 176f, 100f)
                }
                drawPath(cloak, Brush.horizontalGradient(listOf(Color(0xFF71979A), Color(0xFF9CB6B3), Color(0xFF6E9599)), 132f, 214f))
                drawOval(Color(0xFFE6BEA1), Offset(165f, 111f), Size(22f, 29f))
                val veil = Path().apply {
                    moveTo(176f, 100f); cubicTo(152f, 102f, 153f, 135f, 151f, 153f)
                    cubicTo(158f, 132f, 162f, 109f, 176f, 108f)
                    cubicTo(185f, 110f, 188f, 130f, 195f, 153f)
                    cubicTo(196f, 123f, 192f, 105f, 176f, 100f)
                }
                drawPath(veil, Color(0xFF7D9FA0))
                val robe = Path().apply {
                    moveTo(165f, 146f); quadraticTo(156f, 179f, 155f, 226f)
                    quadraticTo(175f, 235f, 193f, 226f)
                    quadraticTo(187f, 172f, 183f, 147f); close()
                }
                drawPath(robe, Color(0xFFE8DFC5))
                rotate(-25f, Offset(174f, 168f)) {
                    drawOval(Color(0xFFF6EED9), Offset(166f, 157f), Size(17f, 35f))
                    drawCircle(Color(0xFFE9C6A6), 7.5f, Offset(174.5f, 155f))
                }
                val arm = Path().apply { moveTo(151f, 158f); quadraticTo(165f, 183f, 188f, 169f) }
                drawPath(arm, Color(0xFF91ADAB), style = Stroke(11f, cap = StrokeCap.Round))
                drawCircle(Color(0xFFE6BEA1), 4.5f, Offset(188f, 169f))
            }
            drawOval(stone, Offset(94f, 233f), Size(154f, 9f))
            drawOval(if (dark) Color(0xFFB3A788) else Color(0xFFDBCDB1), Offset(91f, 239f), Size(160f, 7f))
            fun rose(x: Float, y: Float, radius: Float, color: Color) {
                val stem = Path().apply { moveTo(x - 3f, 250f); quadraticTo(x + 5f, y + 17f, x, y) }
                drawPath(stem, deepSage, style = Stroke(2.3f, cap = StrokeCap.Round))
                gardenLeaf(Offset(x - 1f, y + 21f), Offset(x - 19f, y + 10f), 7f, sage)
                gardenLeaf(Offset(x - 1f, y + 29f), Offset(x + 17f, y + 19f), 7f, deepSage)
                drawGardenFlower(Offset(x, y), radius, color, Color(0xFFE8C77F))
            }
            rose(64f, 196f, 16f, petal)
            rose(41f, 222f, 11f, accent)
            rose(88f, 226f, 12f, Color(0xFFE6C9AC))
            rose(279f, 208f, 16f, petal)
            rose(306f, 230f, 10f, accent)
            rose(254f, 232f, 11f, Color(0xFFE6C9AC))
            gardenLeaf(Offset(115f, 250f), Offset(91f, 238f), 7f, sage)
            gardenLeaf(Offset(230f, 250f), Offset(252f, 240f), 7f, sage)
            listOf(Offset(69f, 136f), Offset(292f, 147f), Offset(262f, 112f), Offset(92f, 55f)).forEachIndexed { index, point ->
                drawCircle(accent.copy(alpha = if (dark) .75f else .5f), if (index % 2 == 0) 2f else 1.5f, point)
            }
            val bird = Path().apply { moveTo(59f, 47f); quadraticTo(66f, 40f, 73f, 47f); quadraticTo(79f, 40f, 85f, 45f) }
            drawPath(bird, deepSage.copy(alpha = .55f), style = Stroke(1.4f, cap = StrokeCap.Round))
        }
    }
}
