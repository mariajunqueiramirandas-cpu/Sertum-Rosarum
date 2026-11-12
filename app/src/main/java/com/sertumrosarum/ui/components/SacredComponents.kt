package com.sertumrosarum.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.ui.theme.SertumTheme
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

enum class SacredIcon { SUN, MOON, SYSTEM, ARROW, BACK, CLOSE, EYE, HAPTIC, CHEVRON, CROSS, CHECK, ROSE }

@Composable
fun SacredIcon(icon: SacredIcon, modifier: Modifier = Modifier, color: Color = SertumTheme.colors.ink) {
    Canvas(modifier.size(22.dp)) {
        val unit = size.minDimension / 24f
        val stroke = 1.35f * unit
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
            SacredIcon.ARROW -> { line(4f, 12f, 20f, 12f); line(15f, 7f, 20f, 12f); line(15f, 17f, 20f, 12f) }
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
            SacredIcon.ROSE -> {
                repeat(6) { index -> rotate(index * 60f) {
                    drawOval(color, point(9f, 3f), Size(6 * unit, 10 * unit), style = Stroke(stroke))
                } }
                drawCircle(color, 2 * unit, center, style = Stroke(stroke))
                drawCircle(color, 10.5f * unit, center, style = Stroke(stroke * .65f))
            }
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
    Box(
        modifier.size(48.dp).clip(CircleShape)
            .background(if (active) colors.marian.copy(alpha = .17f) else colors.surface)
            .semantics { contentDescription = label }
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { SacredIcon(icon, color = if (!enabled) colors.muted.copy(alpha = .4f) else if (active) colors.marian else colors.ink) }
}

@Composable
fun SacredPrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, tag: String = "primary", icon: SacredIcon = SacredIcon.ARROW) {
    val colors = SertumTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) .975f else 1f, label = "sacred-button-press")
    Row(
        modifier.fillMaxWidth().scale(scale).shadow(5.dp, RoundedCornerShape(SertumTheme.shapes.control), ambientColor = colors.ruby.copy(alpha = .12f), spotColor = colors.ruby.copy(alpha = .18f)).clip(RoundedCornerShape(SertumTheme.shapes.control))
            .background(colors.ruby)
            .testTag(tag).clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .padding(horizontal = 23.dp, vertical = 19.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SacredText(text, Modifier.weight(1f), SertumTheme.typography.label.copy(fontSize = 14.sp), colors.onRuby)
        SacredIcon(icon, Modifier.padding(start = 10.dp), colors.onRuby)
    }
}

@Composable
fun SacredRule(modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.weight(1f).height(1.dp).background(colors.border))
        Canvas(Modifier.size(5.dp)) {
            rotate(45f) { drawRect(colors.gold.copy(alpha = .8f)) }
        }
        Box(Modifier.weight(1f).height(1.dp).background(colors.border))
    }
}

@Composable
fun SacredCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier.clip(RoundedCornerShape(SertumTheme.shapes.card))
        .background(SertumTheme.colors.elevated)) { content() }
}
