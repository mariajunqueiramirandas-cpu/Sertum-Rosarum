package com.sertumrosarum.feature.garden

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.ui.canvas.DevotionalArtwork
import com.sertumrosarum.ui.canvas.DevotionalIllustration
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredDivider
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme

/**
 * Refined stationery input field: warm parchment fill, soft placeholder, animated sage focus ring,
 * and proper vertical alignment for single-line and multi-line notes.
 */
@Composable
fun GardenField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    tag: String,
    multiline: Boolean = false,
    limit: Int = 4000,
    placeholder: String = "",
    modifier: Modifier = Modifier,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val restingFill = lerp(colors.elevated, colors.surface, if (colors.isDark) .65f else .42f)
    val fill by animateColorAsState(if (focused) colors.elevated else restingFill, label = "field-fill")
    val stroke by animateColorAsState(if (focused) colors.marian else colors.border, label = "field-border")
    val shape = RoundedCornerShape(20.dp)
    val fieldStyle = type.body.copy(fontSize = 15.5.sp, lineHeight = 23.sp, color = colors.ink)
    val hint = placeholder.ifBlank { if (multiline) "Escreva livremente…" else "Escreva aqui…" }

    Column(modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        if (label.isNotBlank()) {
            SacredText(
                label,
                Modifier.padding(start = 4.dp, bottom = 7.dp),
                type.micro.copy(fontWeight = FontWeight.SemiBold),
                if (focused) colors.accentInk else colors.secondary,
            )
        }
        BasicTextField(
            value = value,
            onValueChange = { onChange(it.take(limit)) },
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = label.ifBlank { hint } }.testTag(tag),
            textStyle = fieldStyle,
            cursorBrush = SolidColor(colors.marian),
            singleLine = !multiline,
            interactionSource = interaction,
            decorationBox = { inner ->
                Box(
                    Modifier.fillMaxWidth()
                        .clip(shape)
                        .background(fill)
                        .border(if (focused) 1.5.dp else 1.dp, stroke, shape)
                        .heightIn(min = if (multiline) 104.dp else 52.dp)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    contentAlignment = if (multiline) Alignment.TopStart else Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) {
                        SacredText(
                            hint,
                            style = fieldStyle,
                            color = colors.muted.copy(alpha = .75f),
                            maxLines = if (multiline) 3 else 1,
                        )
                    }
                    inner()
                }
            },
        )
    }
}

/**
 * A flush, aligned secondary action row: open sage text with a quiet chevron (or circular check
 * indicator when used as a toggleable choice), matching the garden's editorial rhythm.
 */
@Composable
fun QuietAction(
    label: String,
    onClick: () -> Unit,
    tag: String = label,
    selected: Boolean? = null,
    showDivider: Boolean = false,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Column(Modifier.fillMaxWidth()) {
        if (showDivider) SacredDivider(Modifier.padding(vertical = 2.dp))
        Row(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .then(
                    if (selected == true) Modifier.background(colors.marian.copy(alpha = if (colors.isDark) .18f else .12f))
                    else Modifier,
                )
                .clickable(role = if (selected == null) Role.Button else Role.Checkbox, onClick = onClick)
                .semantics {
                    if (selected != null) toggleableState = if (selected) ToggleableState.On else ToggleableState.Off
                }
                .heightIn(min = 48.dp)
                .padding(horizontal = if (selected != null) 12.dp else 2.dp, vertical = 12.dp)
                .testTag(tag),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (selected != null) {
                Box(
                    Modifier.size(22.dp)
                        .background(if (selected) colors.marian else Color.Transparent, CircleShape)
                        .border(1.5.dp, if (selected) colors.marian else colors.muted.copy(alpha = .55f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (selected) SacredIcon(SacredIcon.CHECK, Modifier.size(12.dp), colors.background)
                }
            }
            SacredText(
                label,
                Modifier.weight(1f),
                type.label.copy(fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold),
                if (selected == true) colors.ink else colors.accentInk,
            )
            if (selected == null) {
                SacredIcon(SacredIcon.CHEVRON, Modifier.size(12.dp), colors.marian.copy(alpha = .85f))
            }
        }
    }
}

/**
 * A calm editorial card for Home and personal sections, with uniform margins, balanced artwork
 * alignment, and a subtle trailing chevron when the entire card is a navigation target.
 */
@Composable
fun PersonalCard(
    title: String,
    detail: String,
    onClick: (() -> Unit)? = null,
    tag: String = title,
    modifier: Modifier = Modifier,
    illustration: DevotionalIllustration? = null,
    illustrationAbove: Boolean = false,
    content: @Composable (ColumnScope.() -> Unit)? = null,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SacredCard(modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
                .testTag(tag)
                .padding(horizontal = 18.dp, vertical = 18.dp),
        ) {
            if (illustration != null && illustrationAbove) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    DevotionalArtwork(illustration, Modifier.size(width = 96.dp, height = 68.dp))
                    SacredText(title, Modifier.padding(top = 10.dp), type.section.copy(fontSize = 19.sp, lineHeight = 25.sp), align = TextAlign.Center)
                    if (detail.isNotBlank()) {
                        SacredText(detail, Modifier.padding(top = 4.dp), type.body.copy(fontSize = 13.5.sp, lineHeight = 19.sp), colors.secondary, TextAlign.Center)
                    }
                }
            } else {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (illustration != null) {
                        DevotionalArtwork(illustration, Modifier.size(58.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        SacredText(title, style = type.section.copy(fontSize = 19.sp, lineHeight = 25.sp))
                        if (detail.isNotBlank()) {
                            SacredText(detail, Modifier.padding(top = 4.dp), type.body.copy(fontSize = 14.sp, lineHeight = 20.sp), colors.secondary)
                        }
                    }
                    if (onClick != null && content == null) {
                        SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), colors.muted)
                    }
                }
            }
            if (content != null) {
                Column(Modifier.fillMaxWidth().padding(top = 12.dp), content = content)
            }
        }
    }
}
