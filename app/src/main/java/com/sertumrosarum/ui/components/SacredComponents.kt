package com.sertumrosarum.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.ui.theme.SertumTheme
import com.sertumrosarum.ui.canvas.LocalBotanicalArtwork
import com.sertumrosarum.ui.canvas.RoseMark
import com.sertumrosarum.ui.canvas.paintBotanical
import com.sertumrosarum.ui.canvas.paintLeaf
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SacredText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = SertumTheme.typography.body,
    color: Color = SertumTheme.colors.ink,
    align: TextAlign? = null,
) = BasicText(text, modifier, style.copy(color = color, textAlign = align ?: style.textAlign))

enum class SacredIcon { SUN, MOON, SYSTEM, BACK, CLOSE, EYE, HAPTIC, CHEVRON, CROSS, CHECK, ROSE }

@Composable
fun SacredIcon(icon: SacredIcon, modifier: Modifier = Modifier, color: Color = SertumTheme.colors.ink) {
    if (icon == SacredIcon.ROSE) {
        RoseMark(modifier.size(22.dp), alpha = color.alpha)
        return
    }
    Canvas(modifier.size(22.dp)) {
        val unit = size.minDimension / 24f
        val stroke = 1.5f * unit
        fun point(x: Float, y: Float) = Offset(x * unit, y * unit)
        fun line(x1: Float, y1: Float, x2: Float, y2: Float) = drawLine(color, point(x1, y1), point(x2, y2), stroke, StrokeCap.Round)
        when (icon) {
            SacredIcon.SUN -> {
                drawCircle(color, 4f * unit, center, style = Stroke(stroke))
                repeat(8) { index ->
                    val angle = index * PI / 4
                    drawLine(color, center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * (7.2f * unit), center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * (9.5f * unit), stroke, StrokeCap.Round)
                }
            }
            SacredIcon.MOON -> {
                val path = Path().apply {
                    moveTo(17.8f * unit, 16.2f * unit)
                    cubicTo(8f * unit, 20f * unit, 4f * unit, 9f * unit, 11f * unit, 4f * unit)
                    cubicTo(0f * unit, 5f * unit, 2f * unit, 23f * unit, 15f * unit, 20f * unit)
                    close()
                }
                drawPath(path, color, style = Stroke(stroke))
                line(17f, 3f, 17f, 7f); line(15f, 5f, 19f, 5f)
            }
            SacredIcon.SYSTEM -> {
                drawCircle(color, 8f * unit, center, style = Stroke(stroke))
                drawArc(color, -90f, 180f, true, point(4f, 4f), Size(16 * unit, 16 * unit))
            }
            SacredIcon.BACK -> { line(5f, 12f, 20f, 12f); line(5f, 12f, 10f, 7f); line(5f, 12f, 10f, 17f) }
            SacredIcon.CLOSE -> { line(6f, 6f, 18f, 18f); line(6f, 18f, 18f, 6f) }
            SacredIcon.CROSS -> { line(12f, 3f, 12f, 21f); line(6f, 9f, 18f, 9f) }
            SacredIcon.CHEVRON -> { line(9f, 5f, 16f, 12f); line(9f, 19f, 16f, 12f) }
            SacredIcon.CHECK -> { line(5f, 12f, 10f, 17f); line(10f, 17f, 19f, 7f) }
            SacredIcon.EYE -> {
                val eye = Path().apply {
                    moveTo(2f * unit, 12f * unit)
                    quadraticTo(12f * unit, 0f * unit, 22f * unit, 12f * unit)
                    quadraticTo(12f * unit, 24f * unit, 2f * unit, 12f * unit)
                }
                drawPath(eye, color, style = Stroke(stroke))
                drawCircle(color, 3f * unit, center, style = Stroke(stroke))
            }
            SacredIcon.HAPTIC -> {
                drawRoundRect(color, point(8f, 5f), Size(8f * unit, 14f * unit), androidx.compose.ui.geometry.CornerRadius(2 * unit), style = Stroke(stroke))
                line(4f, 8f, 4f, 16f); line(20f, 8f, 20f, 16f)
                line(1f, 10f, 1f, 14f); line(23f, 10f, 23f, 14f)
            }
            SacredIcon.ROSE -> Unit // The painted rose is rendered above.
        }
    }
}

@Composable
fun SacredIconButton(
    icon: SacredIcon,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    enabled: Boolean = true,
) {
    val colors = SertumTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) .94f else 1f, label = "sacred-icon-press")
    Box(
        modifier.size(48.dp).scale(scale).clip(CircleShape)
            .background(if (active) colors.marian.copy(alpha = .16f) else colors.elevated.copy(alpha = if (colors.isDark) .7f else .85f))
            .border(1.dp, if (active) colors.marian.copy(alpha = .28f) else colors.border, CircleShape)
            .semantics { contentDescription = label }
            .clickable(interaction, LocalIndication.current, enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { SacredIcon(icon, Modifier.size(20.dp), if (!enabled) colors.muted.copy(alpha = .4f) else if (active) colors.marian else colors.ink.copy(alpha = .82f)) }
}

/** The one action of each screen: a pale petal with a rosewood label, never a slab. */
@Composable
fun SacredPrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, tag: String = "primary", compact: Boolean = false) {
    val colors = SertumTheme.colors
    val shape = RoundedCornerShape(SertumTheme.shapes.control)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) .975f else 1f, label = "sacred-button-press")
    Box(
        modifier.fillMaxWidth().scale(scale)
            .shadow(if (colors.isDark) 0.dp else 14.dp, shape, ambientColor = colors.ruby.copy(alpha = .28f), spotColor = colors.ruby.copy(alpha = .34f))
            .clip(shape)
            .background(Brush.verticalGradient(listOf(colors.petal, colors.petalDeep)))
            .border(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = if (colors.isDark) .28f else .75f), colors.ruby.copy(alpha = .16f))), shape)
            .testTag(tag).clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .heightIn(min = if (compact) 48.dp else 60.dp).padding(horizontal = 24.dp, vertical = if (compact) 10.dp else 17.dp),
        contentAlignment = Alignment.Center,
    ) {
        SacredText(text, style = SertumTheme.typography.label.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold), color = colors.onPetal, align = TextAlign.Center)
    }
}

/** A painted rosebud and sage sprig mark a quiet pause in the prayer. */
@Composable
fun SacredFlourish(modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val art = LocalBotanicalArtwork.current
    Canvas(modifier.size(width = 92.dp, height = 18.dp)) {
        drawLine(
            Brush.horizontalGradient(listOf(Color.Transparent, colors.gold.copy(alpha = .32f), Color.Transparent)),
            Offset(0f, center.y + 2.dp.toPx()), Offset(size.width, center.y + 2.dp.toPx()), .55.dp.toPx(),
        )
        paintLeaf(art.leaf, center + Offset(-3.dp.toPx(), 3.dp.toPx()), center + Offset(-23.dp.toPx(), -4.dp.toPx()), .85f)
        paintLeaf(art.leaf, center + Offset(3.dp.toPx(), 3.dp.toPx()), center + Offset(23.dp.toPx(), -4.dp.toPx()), .85f)
        paintBotanical(art.bud, center, 19.dp.toPx())
    }
}

@Composable
fun SacredCard(modifier: Modifier = Modifier, lifted: Boolean = false, content: @Composable () -> Unit) {
    val colors = SertumTheme.colors
    val shape = RoundedCornerShape(SertumTheme.shapes.card)
    Box(
        modifier
            .shadow(if (colors.isDark) 0.dp else if (lifted) 18.dp else 8.dp, shape, ambientColor = colors.marian.copy(alpha = .16f), spotColor = colors.marian.copy(alpha = .22f))
            .clip(shape).background(colors.elevated).border(1.dp, colors.border.copy(alpha = if (colors.isDark) .9f else .7f), shape),
    ) { content() }
}
