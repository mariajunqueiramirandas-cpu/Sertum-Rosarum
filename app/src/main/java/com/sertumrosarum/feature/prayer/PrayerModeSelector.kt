package com.sertumrosarum.feature.prayer

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sertumrosarum.domain.prayer.PrayerMode
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme

@Composable
internal fun PrayerModeSelector(selected: PrayerMode, onSelect: (PrayerMode) -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        SacredText("Como você quer rezar?", Modifier.padding(bottom = 6.dp), type.micro, colors.secondary)
        val track = RoundedCornerShape(26.dp)
        Row(
            Modifier.fillMaxWidth().selectableGroup().clip(track).background(colors.surface)
                .border(1.dp, colors.border, track).padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            PrayerMode.entries.forEach { mode ->
                val active = selected == mode
                val fill by animateColorAsState(if (active) colors.elevated else colors.surface, label = "prayer-mode-fill")
                Box(
                    Modifier.weight(1f).clip(RoundedCornerShape(22.dp)).background(fill)
                        .selectable(active, role = Role.RadioButton, onClick = { onSelect(mode) })
                        .testTag("prayer-mode-${mode.name}").heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    SacredText(mode.label, style = type.label.copy(fontWeight = if (active) FontWeight.Bold else FontWeight.Medium), color = if (active) colors.ink else colors.secondary, align = TextAlign.Center)
                }
            }
        }
    }
}
