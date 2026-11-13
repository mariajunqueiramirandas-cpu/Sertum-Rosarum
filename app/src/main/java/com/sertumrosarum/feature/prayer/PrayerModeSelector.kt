package com.sertumrosarum.feature.prayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sertumrosarum.domain.prayer.PrayerMode
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme

@Composable
internal fun PrayerModeSelector(selected: PrayerMode, onSelect: (PrayerMode) -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        SacredText("Como você quer rezar?", style = type.micro.copy(fontWeight = FontWeight.Normal), color = colors.secondary)
        Row(
            Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PrayerMode.entries.forEach { mode ->
                val active = selected == mode
                Row(
                    Modifier.clip(RoundedCornerShape(12.dp))
                        .selectable(active, role = Role.RadioButton, onClick = { onSelect(mode) })
                        .testTag("prayer-mode-${mode.name}").heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(Modifier.size(12.dp)) {
                        if (active) SacredIcon(SacredIcon.CHECK, Modifier.size(12.dp), colors.secondary)
                    }
                    SacredText(mode.label, style = type.micro.copy(fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal), color = if (active) colors.ink else colors.secondary, align = TextAlign.Center)
                }
            }
        }
    }
}
