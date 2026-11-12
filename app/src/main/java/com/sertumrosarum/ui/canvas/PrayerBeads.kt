package com.sertumrosarum.ui.canvas

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CompactMysteries(completed: Int, current: Int?, onSelect: (Int) -> Unit) {
    val colors = SertumTheme.colors
    val art = LocalBotanicalArtwork.current
    Row(Modifier.fillMaxWidth()) {
        repeat(5) { index ->
            val reached = index < completed || index == current
            Box(Modifier.weight(1f).height(48.dp).testTag("decade-$index")
                .semantics { contentDescription = "Ir ao ${index + 1}º mistério"; selected = current == index }
                .clickable(role = Role.Button, onClick = { onSelect(index) }), contentAlignment = Alignment.Center) {
                Row(
                    Modifier.width(48.dp).height(30.dp).clip(RoundedCornerShape(16.dp))
                        .background(if (index == current) colors.petal else if (reached) colors.marian.copy(alpha = .16f) else colors.surface)
                        .border(1.dp, if (index == current) colors.gold.copy(alpha = .55f) else colors.border, RoundedCornerShape(16.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
                ) {
                    Canvas(Modifier.size(20.dp)) {
                        paintBotanical(if (reached) art.rose else art.bud, center, size.minDimension, if (reached) 1f else .78f)
                    }
                    SacredText("${index + 1}", style = SertumTheme.typography.label.copy(fontSize = 13.sp), color = if (index == current) colors.onPetal else if (reached) colors.marian else colors.muted)
                }
            }
        }
    }
}

/** Five mysteries along one stem. Mysteries already prayed are roses; those ahead are buds. */
@Composable
fun FiveMysteriesArc(completed: Int, current: Int?, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val art = LocalBotanicalArtwork.current
    val breath = rememberGardenBreath("mystery-stem", 5200)
    BoxWithConstraints(modifier.fillMaxWidth().height(100.dp)) {
        val usable = maxWidth - 44.dp
        fun stemX(u: Float) = 22.dp + usable * u
        fun stemY(u: Float) = 70.dp - 34.dp * sin(PI * u).toFloat()
        val grown = when {
            completed >= 5 -> 1f
            current != null -> (current + .5f) / 5f
            else -> completed / 5f
        }
        val growth by animateFloatAsState(grown, spring(dampingRatio = .9f, stiffness = 40f), label = "stem-growth")
        Canvas(Modifier.fillMaxSize()) {
            fun point(u: Float) = Offset(stemX(u).toPx(), stemY(u).toPx())
            fun stem(from: Float, to: Float) = Path().apply {
                repeat(33) { step ->
                    val p = point(from + (to - from) * step / 32f)
                    if (step == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
            }
            drawPath(stem(.03f, .97f), colors.marian.copy(alpha = .26f), style = Stroke(1.6.dp.toPx(), cap = StrokeCap.Round))
            if (growth > .04f) drawPath(stem(.03f, growth.coerceAtMost(.97f)), colors.marian.copy(alpha = .8f), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
            repeat(9) { index ->
                val u = .1f + index * .1f
                if (index % 2 == 1) {
                    val p = point(u)
                    val ahead = point(u + .01f) - p
                    val along = ahead / ahead.getDistance().coerceAtLeast(.01f)
                    val side = Offset(-along.y, along.x) * (if (index % 4 == 1) 1f else -1f)
                    val sway = .4.dp.toPx() * sin(breath.value + index * 1.3f)
                    paintLeaf(art.leaf, p, p + side * (10.dp.toPx() + sway) + along * 5.dp.toPx(), if (u <= growth) .9f else .5f)
                }
            }
        }
        repeat(5) { index ->
            val u = (index + .5f) / 5f
            val opened = index < completed || index == current || completed == 5
            val bloom by animateFloatAsState(if (opened) 1f else 0f, spring(dampingRatio = .75f, stiffness = 85f), label = "mystery-bloom-$index")
            Box(Modifier.offset(stemX(u) - 24.dp, stemY(u) - 24.dp).size(48.dp).clip(CircleShape).testTag("decade-$index")
                .semantics { contentDescription = "Ir ao ${index + 1}º mistério"; selected = current == index }
                .clickable(role = Role.Button, onClick = { onSelect(index) })) {
                Canvas(Modifier.fillMaxSize()) {
                    val glow = .5f + .5f * sin(breath.value)
                    if (index == current) {
                        drawCircle(colors.gold.copy(alpha = .07f + .04f * glow), 23.dp.toPx(), center)
                        drawCircle(colors.elevated, 18.5.dp.toPx(), center)
                        drawCircle(colors.gold.copy(alpha = .45f), 20.dp.toPx(), center, style = Stroke(.65.dp.toPx()))
                    }
                    val petals = bloom.coerceIn(0f, 1f)
                    paintBotanical(art.bud, center - Offset(0f, 4.dp.toPx() * (1f - petals)), 25.dp.toPx(), (1f - petals) * .8f)
                    paintBotanical(
                        if (index == current) art.ivoryRose else art.rose, center,
                        (if (index == current) 33.dp else 26.dp).toPx() * (.65f + .35f * bloom.coerceIn(0f, 1.15f)),
                        petals * (if (index == current) 1f else .94f), turn = index * 11f - 22f,
                    )
                }
            }
        }
    }
}

/** One decade as a garland. Each Ave-Maria opens its bud into a rose. */
@Composable
fun BloomingBeadsArc(count: Int, active: Int?, complete: Boolean, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val art = LocalBotanicalArtwork.current
    val breath = rememberGardenBreath("rose-breath", 4800)
    BoxWithConstraints(modifier.fillMaxWidth().height(96.dp)) {
        val slot = maxWidth / count
        fun beadY(index: Int) = 58.dp - 22.dp * sin(PI * index / (count - 1).coerceAtLeast(1)).toFloat()
        Canvas(Modifier.fillMaxSize()) {
            // The thread follows the same gentle rise as the beads it carries.
            val first = (slot * .5f).toPx()
            val span = (slot * (count - 1)).toPx()
            val thread = Path().apply {
                repeat(41) { step ->
                    val u = step / 40f
                    val y = (58.dp - 22.dp * sin(PI * u).toFloat()).toPx()
                    if (step == 0) moveTo(first, y) else lineTo(first + span * u, y)
                }
            }
            drawPath(thread, colors.marian.copy(alpha = .3f), style = Stroke(1.2.dp.toPx(), cap = StrokeCap.Round))
        }
        repeat(count) { index ->
            val bloomed = complete || (active != null && index < active)
            val isActive = index == active && !complete
            val bloom by animateFloatAsState(if (bloomed || isActive) 1f else 0f, spring(dampingRatio = .62f, stiffness = 90f), label = "rose-$index")
            Box(Modifier.offset(slot * index, beadY(index) - 24.dp).width(slot).height(48.dp).testTag("bead-$index")
                .semantics { contentDescription = "Ave-Maria ${index + 1} de $count"; selected = isActive }
                .clickable(role = Role.Button, onClick = { onSelect(index) })) {
                Canvas(Modifier.fillMaxSize()) {
                    if (isActive) {
                        val pulse = .5f + .5f * cos(breath.value)
                        drawCircle(colors.ruby.copy(alpha = .05f + .04f * pulse), (18.5f + 1.5f * pulse).dp.toPx(), center)
                        drawCircle(colors.gold.copy(alpha = .55f), 17.dp.toPx(), center, style = Stroke(.6.dp.toPx()))
                    }
                    val petals = bloom.coerceIn(0f, 1f)
                    paintBotanical(art.bud, center - Offset(0f, 4.dp.toPx() * (1f - petals)), 23.dp.toPx(), (1f - petals) * (if (colors.isDark) .85f else .75f))
                    paintBotanical(
                        art.rose, center,
                        (if (isActive) 29.dp else 24.dp).toPx() * (.6f + .4f * bloom.coerceIn(0f, 1.15f)),
                        petals * (if (isActive) 1f else .9f), turn = (index % 3 - 1) * 9f,
                    )
                }
            }
        }
    }
}
