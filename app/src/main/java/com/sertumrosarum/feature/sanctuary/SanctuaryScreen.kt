package com.sertumrosarum.feature.sanctuary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import com.sertumrosarum.feature.prayer.PrayerUiState
import com.sertumrosarum.feature.prayer.ThemeMode
import com.sertumrosarum.ui.canvas.RoseMark
import com.sertumrosarum.ui.canvas.SanctuaryGarden
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredIconButton
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SanctuaryScreen(state: PrayerUiState, model: PrayerSessionViewModel) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Column(Modifier.fillMaxSize().testTag("sanctuary")) {
        SanctuaryHeader(state.themeMode, model::setTheme)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(14.dp))
            SacredText(
                state.today.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale.forLanguageTag("pt-BR"))).replaceFirstChar { it.uppercaseChar() },
                style = type.micro, color = colors.secondary,
            )
            SacredText("Que bom ter você aqui.", Modifier.padding(top = 7.dp), type.display.copy(fontSize = 27.sp, lineHeight = 34.sp))
            SacredText("Respire. Há um lugar de paz esperando por você.", Modifier.padding(top = 6.dp), type.body.copy(fontSize = 12.sp), colors.secondary)
            key(state.selectedSet, colors.isDark) {
                SanctuaryGarden(Modifier.fillMaxWidth().height(263.dp))
            }
            SacredText("Com Maria, um passo de cada vez.", Modifier.fillMaxWidth().padding(bottom = 24.dp), type.italic.copy(fontSize = 14.sp), colors.secondary, TextAlign.Center)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                SacredText("O que vamos contemplar?", style = type.section.copy(fontSize = 17.sp))
                if (state.selectedSet == MysterySet.forDay(state.today.dayOfWeek)) {
                    Row(Modifier.clip(RoundedCornerShape(50)).background(colors.marian.copy(alpha = .12f)).padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(Modifier.size(5.dp).background(colors.marian, CircleShape))
                        SacredText("Hoje", style = type.micro, color = colors.marian)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            MysterySelector(state.selectedSet, model::selectMystery)
            Spacer(Modifier.height(14.dp))
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(colors.elevated).padding(17.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp),
            ) {
                Box(Modifier.size(47.dp).background(SertumTheme.atmosphere.accent.copy(alpha = .17f), RoundedCornerShape(17.dp)), contentAlignment = Alignment.Center) {
                    SacredIcon(when (state.selectedSet) { MysterySet.LUMINOUS -> SacredIcon.SUN; MysterySet.SORROWFUL -> SacredIcon.CROSS; else -> SacredIcon.ROSE }, Modifier.size(25.dp), colors.gold)
                }
                Column(Modifier.weight(1f)) {
                    SacredText("Mistérios ${state.selectedSet.label.lowercase()}", style = type.section.copy(fontSize = 18.sp))
                    SacredText(state.selectedSet.contemplation, Modifier.padding(top = 4.dp), type.body.copy(fontSize = 11.sp, lineHeight = 18.sp), colors.secondary)
                }
            }
            SacredText("Cinco momentos com Jesus", Modifier.padding(top = 25.dp, bottom = 13.dp), type.section.copy(fontSize = 17.sp))
            state.selectedSet.mysteries.forEachIndexed { index, mystery ->
                var expanded by remember(state.selectedSet, index) { mutableStateOf(false) }
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).clickable(role = Role.Button, onClick = { expanded = !expanded }).padding(vertical = 10.dp, horizontal = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                        Box(Modifier.size(33.dp).background(colors.marian.copy(alpha = .12f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            SacredText("${index + 1}", style = type.label, color = colors.marian)
                        }
                        Column(Modifier.weight(1f)) {
                            SacredText(mystery.title, style = type.body.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium))
                            SacredText(mystery.scripture, Modifier.padding(top = 2.dp), type.micro.copy(fontSize = 10.sp), colors.muted)
                        }
                        SacredIcon(SacredIcon.CHEVRON, Modifier.size(14.dp), colors.muted)
                    }
                    AnimatedVisibility(expanded) {
                        SacredText("Uma semente para hoje: ${mystery.fruit.lowercase()}.", Modifier.padding(start = 46.dp, top = 9.dp, bottom = 3.dp), type.body.copy(fontSize = 12.sp), colors.secondary)
                    }
                }
            }
            SacredText("“A minha alma engrandece o Senhor.”", Modifier.fillMaxWidth().padding(top = 25.dp), type.italic.copy(fontSize = 14.sp), colors.secondary, TextAlign.Center)
            SacredText("Lucas 1, 46", Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 26.dp), type.micro, colors.muted, TextAlign.Center)
        }
        Column(Modifier.fillMaxWidth().background(colors.background).padding(horizontal = 24.dp).padding(top = 14.dp, bottom = 13.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (state.hasSession) {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(colors.surface).clickable(onClick = model::resume).testTag("resume").padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    RoseMark(Modifier.size(25.dp), colors.ruby)
                    SacredText("Sua oração espera por você", Modifier.weight(1f), type.body.copy(fontSize = 12.sp), colors.ink)
                    SacredIcon(SacredIcon.ARROW, Modifier.size(16.dp), colors.marian)
                }
                Spacer(Modifier.height(10.dp))
            }
            SacredPrimaryButton("Tecer a Coroa de Hoje", model::start, tag = "start-rosary")
            SacredText("Uma oração de cada vez. Sem pressa.", Modifier.padding(top = 10.dp), type.micro, colors.secondary)
        }
    }
}

@Composable
private fun SanctuaryHeader(mode: ThemeMode, onTheme: (ThemeMode) -> Unit) {
    val type = SertumTheme.typography
    val colors = SertumTheme.colors
    val density = LocalDensity.current
    var menuOpen by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(46.dp).background(colors.ruby.copy(alpha = .09f), RoundedCornerShape(17.dp)), contentAlignment = Alignment.Center) {
                RoseMark(Modifier.size(31.dp), colors.ruby)
            }
            Column {
                SacredText("sertum", style = type.brand)
                SacredText("seu jardim de oração", style = type.micro.copy(fontSize = 10.sp), color = colors.secondary)
            }
        }
        Box {
            SacredIconButton(if (colors.isDark) SacredIcon.MOON else SacredIcon.SUN, "Aparência: ${mode.label}", { menuOpen = !menuOpen }, Modifier.testTag("theme-menu"))
            if (menuOpen) {
                Popup(alignment = Alignment.TopEnd, offset = IntOffset(0, with(density) { 58.dp.roundToPx() }), onDismissRequest = { menuOpen = false }, properties = PopupProperties(focusable = true)) {
                    SacredCard(Modifier.width(215.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            SacredText("Uma luz que acolhe você", Modifier.padding(bottom = 8.dp), type.body.copy(fontSize = 12.sp), colors.secondary)
                            ThemeMode.entries.forEach { option ->
                                Row(
                                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).selectable(option == mode, role = Role.RadioButton, onClick = { onTheme(option); menuOpen = false }).testTag("theme-${option.name}").padding(vertical = 13.dp, horizontal = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    SacredIcon(when (option) { ThemeMode.DAY -> SacredIcon.SUN; ThemeMode.VIGIL -> SacredIcon.MOON; ThemeMode.SYSTEM -> SacredIcon.SYSTEM }, Modifier.size(19.dp), colors.marian)
                                    SacredText(option.label, Modifier.weight(1f), type.label)
                                    if (mode == option) SacredIcon(SacredIcon.CHECK, Modifier.size(16.dp), colors.marian)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MysterySelector(selected: MysterySet, onSelect: (MysterySet) -> Unit) {
    val colors = SertumTheme.colors
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(colors.surface).padding(5.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        MysterySet.entries.forEach { set ->
            val active = set == selected
            Box(
                Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(if (active) colors.elevated else Color.Transparent)
                    .selectable(active, role = Role.RadioButton, onClick = { onSelect(set) }).testTag("mystery-${set.name}").padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                SacredText(set.label, style = SertumTheme.typography.label.copy(fontSize = 10.sp), color = if (active) colors.ink else colors.secondary)
            }
        }
    }
}
