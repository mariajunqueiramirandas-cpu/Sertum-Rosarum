package com.sertumrosarum.feature.prayer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import com.sertumrosarum.domain.treasury.PrayerLanguage
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme

/**
 * Português or Latim, as two plain words above a rosary prayer, in the same form as the
 * choice between praying alone and in a group. Shown only for a prayer that has a Latin text.
 */
@Composable
internal fun PrayerLanguageToggle(selected: PrayerLanguage, onSelect: (PrayerLanguage) -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Row(modifier.horizontalScroll(rememberScrollState()).selectableGroup().testTag("rosary-language"), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PrayerLanguage.entries.forEach { language ->
            val active = selected == language
            Row(
                Modifier.clip(RoundedCornerShape(12.dp)).selectable(active, role = Role.RadioButton, onClick = { onSelect(language) })
                    .testTag("rosary-language-${language.name}").heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(Modifier.size(12.dp)) { if (active) SacredIcon(SacredIcon.CHECK, Modifier.size(12.dp), colors.secondary) }
                SacredText(if (language == PrayerLanguage.BILINGUAL) "PT / LAT" else language.label, style = type.micro.copy(fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal), color = if (active) colors.ink else colors.secondary)
            }
        }
    }
}
