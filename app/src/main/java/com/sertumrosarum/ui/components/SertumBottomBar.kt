package com.sertumrosarum.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.ui.theme.SertumTheme

/** The room every tabbed page leaves at its foot, so the bar rests on the page and never on its words. */
val SertumBottomBarSpace = 86.dp

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
    Row(
        modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 10.dp)
            .shadow(if (colors.isDark) 0.dp else 16.dp, shelf, ambientColor = colors.marian.copy(alpha = .22f), spotColor = colors.marian.copy(alpha = .3f))
            .clip(shelf).background(colors.elevated).border(1.dp, colors.border.copy(alpha = if (colors.isDark) .9f else .8f), shelf)
            .selectableGroup().testTag("bottom-bar").padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        SanctuaryTab.entries.forEach { tab ->
            val active = tab == selected
            val fill by animateColorAsState(colors.marian.copy(alpha = if (active) (if (colors.isDark) .2f else .14f) else 0f), label = "tab-fill")
            val ink by animateColorAsState(if (active) colors.ink else colors.muted, label = "tab-ink")
            Column(
                Modifier.weight(1f).clip(RoundedCornerShape(27.dp)).background(fill)
                    .semantics { contentDescription = "${tab.label}: ${tab.caption}" }
                    .selectable(active, role = Role.Tab, onClick = { onSelect(tab) }).testTag("tab-${tab.name}")
                    .heightIn(min = 58.dp).padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterVertically),
            ) {
                SacredIcon(tab.icon, Modifier.size(21.dp), if (active) colors.marian else ink)
                SacredText(tab.label, style = SertumTheme.typography.micro.copy(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold), color = ink, maxLines = 1)
            }
        }
    }
}
