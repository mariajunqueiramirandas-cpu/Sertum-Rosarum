package com.sertumrosarum.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.ui.theme.SertumTheme

/**
 * The places within a page, as tabs: plain words over a hairline, with a sage bar under the chosen one.
 * The row runs to the screen's edges and fades where more places wait beyond them.
 *
 * [bleed] is the side padding of the column this row sits in, so the row can reach past it.
 */
@Composable
fun <T> SacredTabs(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    tag: (T) -> String = { label(it) },
    bleed: Dp = 0.dp,
) {
    val colors = SertumTheme.colors
    val style = SertumTheme.typography.label.copy(fontSize = 15.sp)
    val scroll = rememberScrollState()
    val edge = SertumTheme.spacing.page
    Box(modifier.bleed(bleed).fillMaxWidth()) {
        // The hairline the tabs stand on, from edge to edge.
        Box(Modifier.align(Alignment.BottomStart).padding(horizontal = edge).fillMaxWidth().height(1.dp).background(colors.border))
        Row(
            Modifier.fillMaxWidth().fadingEdges(scroll).horizontalScroll(scroll).selectableGroup().padding(horizontal = edge - 12.dp),
        ) {
            options.forEach { option ->
                val active = option == selected
                val bar by animateFloatAsState(if (active) 1f else 0f, label = "tab-bar")
                Column(
                    Modifier.width(IntrinsicSize.Max).clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .selectable(active, role = Role.Tab, onClick = { onSelect(option) })
                        .testTag(tag(option)).heightIn(min = 48.dp).padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(Modifier.padding(top = 12.dp, bottom = 10.dp)) { SteadyWord(label(option), style, active, if (active) colors.ink else colors.secondary) }
                    Box(Modifier.align(Alignment.CenterHorizontally).alpha(bar).fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)).background(colors.marian))
                }
            }
        }
    }
}

/**
 * A few filters as outlined pills; the chosen one is filled, so it is seen at a glance.
 * The row runs to the screen's edges and fades where more choices wait beyond them.
 *
 * [bleed] is the side padding of the column this row sits in, so the row can reach past it.
 */
@Composable
fun <T> SacredChips(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    tag: (T) -> String = { label(it) },
    bleed: Dp = 0.dp,
) {
    val colors = SertumTheme.colors
    val style = SertumTheme.typography.label.copy(fontSize = 14.sp)
    val scroll = rememberScrollState()
    val chosenFill = if (colors.isDark) colors.marian else colors.ink
    val chosenInk = colors.background
    Row(
        modifier.bleed(bleed).fillMaxWidth().fadingEdges(scroll).horizontalScroll(scroll).selectableGroup()
            .padding(horizontal = SertumTheme.spacing.page, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            val active = option == selected
            val pill = RoundedCornerShape(50)
            val fill by animateColorAsState(if (active) chosenFill else chosenFill.copy(alpha = 0f), label = "chip-fill")
            val outline by animateColorAsState(if (active) chosenFill else colors.marian.copy(alpha = if (colors.isDark) .45f else .55f), label = "chip-outline")
            Box(
                Modifier.heightIn(min = 48.dp).selectable(active, role = Role.RadioButton, onClick = { onSelect(option) }).testTag(tag(option)),
                contentAlignment = Alignment.Center,
            ) {
                // A 40dp pill inside a 48dp touch target.
                Box(Modifier.heightIn(min = 40.dp).clip(pill).background(fill).border(1.dp, outline, pill).padding(horizontal = 16.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                    SteadyWord(label(option), style, active, if (active) chosenInk else colors.ink.copy(alpha = .86f))
                }
            }
        }
    }
}

/** The word keeps the width of its bold form, so choosing it never nudges its neighbours. */
@Composable
private fun SteadyWord(text: String, style: TextStyle, active: Boolean, color: Color) {
    Box(contentAlignment = Alignment.Center) {
        SacredText(text, Modifier.alpha(0f).clearAndSetSemantics {}, style.copy(fontWeight = FontWeight.Bold), maxLines = 1)
        SacredText(text, style = style.copy(fontWeight = if (active) FontWeight.Bold else FontWeight.Medium), color = color, maxLines = 1)
    }
}

/** Lets a row reach past the side padding of its parent column, to the edges of the screen. */
private fun Modifier.bleed(horizontal: Dp) = if (horizontal == 0.dp) this else layout { measurable, constraints ->
    val extra = horizontal.roundToPx() * 2
    val placeable = measurable.measure(constraints.copy(minWidth = constraints.minWidth + extra, maxWidth = constraints.maxWidth + extra))
    layout(placeable.width - extra, placeable.height) { placeable.place(-extra / 2, 0) }
}

/** Softly fades the row's ends while there is more of it to scroll to. */
private fun Modifier.fadingEdges(scroll: ScrollState, width: Dp = 28.dp) = graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val fade = width.toPx()
        if (scroll.canScrollBackward) drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black), startX = 0f, endX = fade), blendMode = BlendMode.DstIn)
        if (scroll.canScrollForward) drawRect(Brush.horizontalGradient(listOf(Color.Black, Color.Transparent), startX = size.width - fade, endX = size.width), blendMode = BlendMode.DstIn)
    }

/** A quiet search on the page's surface colour; the cross appears only when there is something to clear. */
@Composable
fun SacredSearchField(query: String, onQuery: (String) -> Unit, placeholder: String, tag: String, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val focus = LocalFocusManager.current
    val shape = RoundedCornerShape(26.dp)
    val text = type.body.copy(fontSize = 16.sp, lineHeight = 22.sp, color = colors.ink)
    BasicTextField(
        query, { onQuery(it.take(300)) }, modifier.fillMaxWidth().semantics { contentDescription = placeholder }.testTag(tag),
        textStyle = text, singleLine = true, cursorBrush = SolidColor(colors.marian),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { focus.clearFocus() }),
        decorationBox = { field ->
            Row(
                Modifier.fillMaxWidth().clip(shape).background(colors.surface).border(1.dp, colors.border.copy(alpha = .7f), shape).heightIn(min = 52.dp).padding(start = 18.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SacredIcon(SacredIcon.SEARCH, Modifier.size(18.dp), colors.secondary)
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) SacredText(placeholder, style = text, color = colors.muted, maxLines = 1)
                    field()
                }
                if (query.isNotEmpty()) {
                    Box(
                        Modifier.size(48.dp).clip(CircleShape).semantics { contentDescription = "Limpar a busca" }
                            .clickable(role = Role.Button) { onQuery("") }.testTag("$tag-clear"),
                        contentAlignment = Alignment.Center,
                    ) { SacredIcon(SacredIcon.CLOSE, Modifier.size(14.dp), colors.secondary) }
                } else Box(Modifier.size(width = 14.dp, height = 48.dp))
            }
        },
    )
}
