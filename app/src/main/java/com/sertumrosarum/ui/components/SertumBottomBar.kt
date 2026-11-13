package com.sertumrosarum.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.ui.theme.SertumTheme

private val SanctuaryTab.icon get() = when (this) {
    SanctuaryTab.HOME -> SacredIcon.ARCH
    SanctuaryTab.GUIDES -> SacredIcon.BEADS
    SanctuaryTab.TREASURY -> SacredIcon.BOOK
    SanctuaryTab.GARDEN -> SacredIcon.SPROUT
}

/** Four places in the garden, on one soft floating shelf. The chosen place rests on a sage leaf of colour. */
@Composable
fun SertumBottomBar(selected: SanctuaryTab, onSelect: (SanctuaryTab) -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val shelf = RoundedCornerShape(32.dp)
    val style = SertumTheme.typography.micro.copy(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Bold)
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val minimumCell = with(density) {
        SanctuaryTab.entries.flatMap { it.label.split(' ') }
            .maxOf { measurer.measure(it, style, softWrap = false).size.width }.toDp() + 8.dp
    }
    BoxWithConstraints(
        modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 10.dp)
            .shadow(if (colors.isDark) 0.dp else 16.dp, shelf, ambientColor = colors.marian.copy(alpha = .22f), spotColor = colors.marian.copy(alpha = .3f))
            .clip(shelf).background(colors.elevated).border(1.dp, colors.border.copy(alpha = if (colors.isDark) .9f else .8f), shelf)
            .selectableGroup().testTag("bottom-bar").padding(5.dp),
    ) {
        // A word such as Roteiros should never become "Roteiro / s". When four
        // words no longer fit, use two rows with the icon beside each label.
        val horizontal = maxWidth < minimumCell * 4 + 6.dp
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            SanctuaryTab.entries.chunked(if (horizontal) 2 else 4).forEach { tabs ->
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    tabs.forEach { tab ->
                        BottomDestination(tab, tab == selected, onSelect, horizontal, Modifier.weight(1f).fillMaxHeight())
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomDestination(tab: SanctuaryTab, active: Boolean, onSelect: (SanctuaryTab) -> Unit, horizontal: Boolean, modifier: Modifier) {
    val colors = SertumTheme.colors
    val fill by animateColorAsState(colors.marian.copy(alpha = if (active) (if (colors.isDark) .2f else .14f) else 0f), label = "tab-fill")
    val ink by animateColorAsState(if (active) colors.ink else colors.secondary, label = "tab-ink")
    val target = modifier.clip(RoundedCornerShape(27.dp)).background(fill)
        .semantics { contentDescription = "${tab.label}: ${tab.caption}" }
        .selectable(active, role = Role.Tab, onClick = { onSelect(tab) }).testTag("tab-${tab.name}")
        .heightIn(min = if (horizontal) 48.dp else 58.dp).padding(horizontal = if (horizontal) 10.dp else 4.dp, vertical = 8.dp)
    val label: @Composable (Modifier) -> Unit = { textModifier ->
        SacredText(tab.label, textModifier.testTag("tab-label-${tab.name}"), style = SertumTheme.typography.micro.copy(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold), color = ink, align = TextAlign.Center)
    }
    if (horizontal) {
        Row(target, horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
            SacredIcon(tab.icon, Modifier.size(21.dp), if (active) colors.marian else ink)
            label(Modifier.weight(1f, fill = false))
        }
    } else {
        Column(target, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterVertically)) {
            SacredIcon(tab.icon, Modifier.size(21.dp), if (active) colors.marian else ink)
            label(Modifier.fillMaxWidth())
        }
    }
}
