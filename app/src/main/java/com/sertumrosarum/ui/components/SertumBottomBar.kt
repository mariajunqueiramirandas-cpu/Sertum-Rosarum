package com.sertumrosarum.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.ui.canvas.RoseMark
import com.sertumrosarum.ui.theme.SertumTheme

private val destinations = listOf(SanctuaryTab.CATECHISM, SanctuaryTab.GUIDES, SanctuaryTab.HOME, SanctuaryTab.TREASURY, SanctuaryTab.GARDEN)
private val SanctuaryTab.icon get() = when (this) {
    SanctuaryTab.CATECHISM -> SacredIcon.CROSS
    SanctuaryTab.GUIDES -> SacredIcon.BEADS
    SanctuaryTab.HOME -> SacredIcon.ARCH
    SanctuaryTab.TREASURY -> SacredIcon.BOOK
    SanctuaryTab.GARDEN -> SacredIcon.SPROUT
}

/** Five stable positions. The home rose stays physically central at every text size. */
@Composable
fun SertumBottomBar(selected: SanctuaryTab, onSelect: (SanctuaryTab) -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val shape = RoundedCornerShape(30.dp)
    val style = SertumTheme.typography.micro.copy(fontSize = 11.sp, lineHeight = 15.sp)
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val labelWidth = with(density) { destinations.flatMap { it.label.split(' ') }.maxOf { measurer.measure(it, style, softWrap = false).size.width }.toDp() + 8.dp }
    BoxWithConstraints(modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 8.dp)) {
        val showLabels = maxWidth / 5 >= labelWidth
        Column(Modifier.fillMaxWidth().shadow(if (colors.isDark) 0.dp else 12.dp, shape, ambientColor = colors.marian.copy(alpha = .2f))
            .clip(shape).background(colors.elevated).border(1.dp, colors.border, shape).selectableGroup().testTag("bottom-bar").padding(horizontal = 4.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
                destinations.forEach { tab ->
                    val active = selected == tab
                    val fill by animateColorAsState(if (active) colors.surface else colors.elevated, label = "destination")
                    Column(Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(22.dp)).background(fill)
                        .selectable(active, role = Role.Tab, onClick = { onSelect(tab) })
                        .semantics { contentDescription = "${tab.label}: ${tab.caption}" }
                        .testTag("tab-${tab.name}").heightIn(min = 58.dp).padding(horizontal = 2.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)) {
                        if (tab == SanctuaryTab.HOME) Box(Modifier.size(48.dp).shadow(3.dp, CircleShape).clip(CircleShape).background(colors.petal).border(1.dp, colors.ruby.copy(alpha = .35f), CircleShape), contentAlignment = Alignment.Center) {
                            RoseMark(Modifier.size(35.dp))
                        } else SacredIcon(tab.icon, Modifier.padding(vertical = 5.dp).size(23.dp), if (active) colors.accentInk else colors.secondary)
                        if (showLabels) SacredText(tab.label, Modifier.fillMaxWidth().testTag("tab-label-${tab.name}"), style.copy(fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold), if (active) colors.ink else colors.secondary, TextAlign.Center)
                    }
                }
            }
            // Large text gets one full-width label; all five icon targets retain spoken destination names.
            if (!showLabels) SacredText(selected.label, Modifier.fillMaxWidth().padding(top = 5.dp).testTag("tab-label-${selected.name}"), SertumTheme.typography.micro, colors.ink, TextAlign.Center)
        }
    }
}
