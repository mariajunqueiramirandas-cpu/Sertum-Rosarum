package com.sertumrosarum.ui.canvas

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun CompactMysteries(completed: Int, current: Int?, onSelect: (Int) -> Unit) {
    val colors = SertumTheme.colors
    Row(Modifier.fillMaxWidth()) {
        repeat(5) { index ->
            Box(Modifier.weight(1f).height(36.dp).testTag("decade-$index")
                .semantics { contentDescription = "Ir ao ${index + 1}º mistério"; selected = current == index }
                .clickable(role = Role.Button, onClick = { onSelect(index) }), contentAlignment = Alignment.Center) {
                Box(Modifier.size(26.dp).background(if (index <= completed) colors.marian.copy(alpha = .15f) else colors.surface, CircleShape), contentAlignment = Alignment.Center) {
                    SacredText("${index + 1}", style = SertumTheme.typography.label.copy(fontSize = 12.sp), color = if (index <= completed) colors.marian else colors.muted)
                }
            }
        }
    }
}

@Composable
fun FiveMysteriesArc(completed: Int, current: Int?, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val breathing = rememberInfiniteTransition(label = "garden-breath")
    val glow by breathing.animateFloat(.15f, .45f, infiniteRepeatable(tween(2400), RepeatMode.Reverse), label = "mystery-glow")
    BoxWithConstraints(modifier.fillMaxWidth().height(102.dp)) {
        val usable = maxWidth - 44.dp
        Canvas(Modifier.fillMaxSize()) {
            repeat(5) { index ->
                val path = Path()
                repeat(12) { point ->
                    val u = (index + point / 11f * .88f) / 5f
                    val x = 22.dp.toPx() + usable.toPx() * u
                    val y = (72.dp - 38.dp * sin(PI * u).toFloat()).toPx()
                    if (point == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, if (index < completed) colors.marian else colors.border, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
            }
        }
        repeat(5) { index ->
            val u = (index + .5f) / 5f
            val x = 22.dp + usable * u
            val y = 72.dp - 38.dp * sin(PI * u).toFloat()
            Box(Modifier.offset(x - 24.dp, y - 24.dp).size(48.dp).testTag("decade-$index")
                .semantics { contentDescription = "Ir ao ${index + 1}º mistério"; selected = current == index }
                .clickable(role = Role.Button, onClick = { onSelect(index) })) {
                Canvas(Modifier.fillMaxSize()) {
                    if (index == current) drawCircle(colors.ruby.copy(alpha = glow * .25f), 23.dp.toPx(), center)
                    if (index < completed || index == current || completed == 5) {
                        drawGardenFlower(center, (if (index == current) 15.dp else 12.dp).toPx(), colors.ruby.copy(alpha = if (index == current) .9f else .6f), colors.gold)
                    } else {
                        drawCircle(colors.surface, 11.dp.toPx(), center)
                        drawCircle(colors.marian.copy(alpha = .45f), 4.dp.toPx(), center)
                    }
                }
            }
        }
    }
}

@Composable
fun BloomingBeadsArc(count: Int, active: Int?, complete: Boolean, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val breathing = rememberInfiniteTransition(label = "rose-breath")
    val pulse by breathing.animateFloat(.25f, .65f, infiniteRepeatable(tween(2400), RepeatMode.Reverse), label = "bead-halo")
    BoxWithConstraints(modifier.fillMaxWidth().height(96.dp)) {
        val slot = maxWidth / count
        fun beadY(index: Int) = 57.dp - 24.dp * sin(PI * index / (count - 1).coerceAtLeast(1)).toFloat()
        Canvas(Modifier.fillMaxSize()) {
            val thread = Path()
            repeat(count) { index ->
                val point = Offset((slot * (index + .5f)).toPx(), beadY(index).toPx())
                if (index == 0) thread.moveTo(point.x, point.y) else thread.lineTo(point.x, point.y)
            }
            drawPath(thread, colors.marian.copy(alpha = .22f), style = Stroke(1.dp.toPx()))
        }
        repeat(count) { index ->
            val bloomed = complete || (active != null && index < active)
            val bloom by animateFloatAsState(if (bloomed) 1f else 0f, spring(dampingRatio = .7f, stiffness = 90f), label = "rose-$index")
            val isActive = index == active && !complete
            Box(Modifier.offset(slot * index, beadY(index) - 24.dp).width(slot).height(48.dp).testTag("bead-$index")
                .semantics { contentDescription = "Ave-Maria ${index + 1} de $count"; selected = isActive }
                .clickable(role = Role.Button, onClick = { onSelect(index) })) {
                Canvas(Modifier.fillMaxSize()) {
                    if (isActive) drawCircle(colors.ruby.copy(alpha = pulse * .16f), 17.dp.toPx(), center)
                    if (bloom > .01f || isActive) {
                        drawGardenFlower(center, (if (isActive) 11.5.dp else 10.dp).toPx(), colors.ruby.copy(alpha = if (isActive) .9f else .6f), colors.gold, if (isActive) 1f else bloom)
                    } else {
                        drawCircle(colors.elevated, 8.dp.toPx(), center)
                        drawCircle(colors.marian.copy(alpha = .35f), 8.dp.toPx(), center, style = Stroke(1.2.dp.toPx()))
                        drawCircle(colors.marian.copy(alpha = .3f), 2.dp.toPx(), center)
                    }
                }
            }
        }
    }
}
