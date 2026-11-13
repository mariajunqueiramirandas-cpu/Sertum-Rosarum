package com.sertumrosarum.feature.sanctuary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.sertumrosarum.domain.prayer.Mystery
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.prayer.PrayerMode
import com.sertumrosarum.feature.prayer.PrayerModeSelector
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import com.sertumrosarum.feature.prayer.PrayerUiState
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.feature.prayer.ThemeMode
import com.sertumrosarum.ui.canvas.RoseMark
import com.sertumrosarum.ui.canvas.SanctuaryGarden
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredFlourish
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredIconButton
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.ScrollHeaderShadow
import com.sertumrosarum.ui.components.softScrollTop
import com.sertumrosarum.ui.theme.SertumTheme
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun SanctuaryScreen(state: PrayerUiState, model: PrayerSessionViewModel) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val scroll = rememberScrollState()
    val scope = rememberCoroutineScope()
    val startLabel = if (state.hasSession) "Tecer uma nova coroa" else "Tecer a coroa de hoje"
    Column(Modifier.fillMaxSize().testTag("sanctuary")) {
        SanctuaryHeader(state.themeMode, model::setTheme)
        Box(Modifier.weight(1f)) {
            Column(Modifier.fillMaxSize().softScrollTop(scroll).verticalScroll(scroll).testTag("sanctuary-scroll").padding(horizontal = 24.dp)) {
                SanctuaryGarden(Modifier.fillMaxWidth().height(292.dp))
                SacredText(
                    state.today.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale.forLanguageTag("pt-BR"))).replaceFirstChar { it.uppercaseChar() },
                    Modifier.fillMaxWidth().padding(top = 10.dp), type.micro, colors.marian, TextAlign.Center,
                )
                SacredText("Que bom ter você aqui.", Modifier.fillMaxWidth().padding(top = 8.dp), type.display, align = TextAlign.Center)
                SacredText("Respire. Há um lugar de paz esperando por você.", Modifier.fillMaxWidth().padding(top = 8.dp), type.body, colors.secondary, TextAlign.Center)
                SacredFlourish(Modifier.align(Alignment.CenterHorizontally).padding(top = 26.dp, bottom = 26.dp))

                Row(Modifier.fillMaxWidth().heightIn(min = 32.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    SacredText(if (state.selectedMode == PrayerMode.SOLO) "O que quer contemplar?" else "O que vamos contemplar?", Modifier.weight(1f), type.section)
                    val isToday = state.selectedSet == MysterySet.forDay(state.today.dayOfWeek)
                    // Reserve the label's width and height for every set so changing
                    // mysteries never reflows the heading or moves the selector.
                    Row(
                        Modifier.then(if (isToday) Modifier else Modifier.clearAndSetSemantics {})
                            .alpha(if (isToday) 1f else 0f)
                            .padding(start = 12.dp, top = 5.dp, bottom = 5.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(Modifier.size(6.dp).background(colors.marian, CircleShape))
                        SacredText("Hoje", style = type.micro, color = colors.marian)
                    }
                }
                Spacer(Modifier.height(14.dp))
                MysterySelector(state.selectedSet, model::selectMystery)
                Spacer(Modifier.height(16.dp))
                MysteryCard(state.selectedSet)
                PrayerModeSelector(state.selectedMode, model::selectPrayerMode, Modifier.padding(top = 24.dp))
                Row(
                    Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp).clip(CircleShape).clickable(role = Role.Button) { model.selectTab(SanctuaryTab.GUIDES) }
                        .testTag("other-guides").heightIn(min = 48.dp).padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    SacredText("Rezar com outro roteiro", style = type.label, color = colors.marian)
                    SacredIcon(SacredIcon.CHEVRON, Modifier.size(12.dp), colors.marian)
                }

                SacredText("“A minha alma engrandece o Senhor.”", Modifier.fillMaxWidth().padding(top = 24.dp), type.whisper, colors.secondary, TextAlign.Center)
                SacredText("Lucas 1, 46", Modifier.fillMaxWidth().padding(top = 6.dp), type.micro, colors.muted, TextAlign.Center)
                SacredText("Uma oração de cada vez. Sem pressa.", Modifier.fillMaxWidth().padding(top = 22.dp, bottom = 40.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.secondary, TextAlign.Center)
            }
            ScrollHeaderShadow(scroll, Modifier.align(Alignment.TopCenter))
            // The page dissolves into the action instead of being cut by it.
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(32.dp).background(Brush.verticalGradient(listOf(colors.background.copy(alpha = 0f), colors.background))))
        }
        Column(Modifier.fillMaxWidth().background(colors.background).padding(horizontal = 24.dp).padding(top = 4.dp, bottom = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            // Reserve the cue's touch target outside the content, even at the end,
            // so it never covers words or changes the viewport when it fades away.
            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                androidx.compose.animation.AnimatedVisibility(scroll.canScrollForward, enter = fadeIn(), exit = fadeOut()) {
                    Box(
                        Modifier.fillMaxSize().clip(CircleShape)
                            .clickable(role = Role.Button) { scope.launch { scroll.animateScrollBy(scroll.viewportSize * .7f) } }
                            .semantics { contentDescription = "Ver mais no santuário" }
                            .testTag("sanctuary-scroll-cue"),
                        contentAlignment = Alignment.Center,
                    ) {
                        SacredIcon(SacredIcon.CHEVRON, Modifier.size(14.dp).rotate(90f), colors.muted)
                    }
                }
            }
            if (state.hasSession) {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(colors.surface).border(1.dp, colors.border, RoundedCornerShape(24.dp))
                        .clickable(role = Role.Button, onClick = model::resume).testTag("resume").heightIn(min = 56.dp).padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    RoseMark(Modifier.size(28.dp))
                    Column(Modifier.weight(1f)) {
                        SacredText("Sua oração espera por você", style = type.label)
                        SacredText("${state.sessionGuide.title} · ${state.sessionMode.label} · ${state.stepIndex + 1}/${state.sequence.size}", style = type.micro.copy(fontWeight = FontWeight.Normal), color = colors.secondary)
                    }
                    SacredIcon(SacredIcon.CHEVRON, Modifier.size(15.dp), colors.marian)
                }
                Spacer(Modifier.height(12.dp))
            }
            SacredPrimaryButton(startLabel, model::start, tag = "start-rosary")
        }
    }
}

@Composable
private fun SanctuaryHeader(mode: ThemeMode, onTheme: (ThemeMode) -> Unit) {
    val type = SertumTheme.typography
    val colors = SertumTheme.colors
    val density = LocalDensity.current
    var menuOpen by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 10.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            RoseMark(Modifier.size(30.dp))
            SacredText("sertum", style = type.brand)
        }
        Box {
            SacredIconButton(if (colors.isDark) SacredIcon.MOON else SacredIcon.SUN, "Aparência: ${mode.label}", { menuOpen = !menuOpen }, Modifier.testTag("theme-menu"))
            if (menuOpen) {
                Popup(alignment = Alignment.TopEnd, offset = IntOffset(0, with(density) { 58.dp.roundToPx() }), onDismissRequest = { menuOpen = false }, properties = PopupProperties(focusable = true)) {
                    // Room around the card lets its shadow fall softly inside the popup window.
                    SacredCard(Modifier.padding(start = 20.dp, bottom = 24.dp).width(228.dp), lifted = true) {
                        Column(Modifier.padding(horizontal = 10.dp, vertical = 14.dp)) {
                            SacredText("Uma luz que acolhe você", Modifier.padding(start = 10.dp, bottom = 6.dp), type.micro, colors.secondary)
                            ThemeMode.entries.forEach { option ->
                                val chosen = option == mode
                                Row(
                                    Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(if (chosen) colors.marian.copy(alpha = .12f) else Color.Transparent)
                                        .selectable(chosen, role = Role.RadioButton, onClick = { onTheme(option); menuOpen = false }).testTag("theme-${option.name}").heightIn(min = 48.dp).padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    SacredIcon(when (option) { ThemeMode.DAY -> SacredIcon.SUN; ThemeMode.VIGIL -> SacredIcon.MOON; ThemeMode.SYSTEM -> SacredIcon.SYSTEM }, Modifier.size(19.dp), colors.marian)
                                    SacredText(option.label, Modifier.weight(1f), type.label)
                                    if (chosen) SacredIcon(SacredIcon.CHECK, Modifier.size(16.dp), colors.marian)
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
internal fun MysterySelector(selected: MysterySet, onSelect: (MysterySet) -> Unit) {
    val colors = SertumTheme.colors
    val track = RoundedCornerShape(26.dp)
    Row(Modifier.fillMaxWidth().testTag("mystery-selector").clip(track).background(colors.surface).border(1.dp, colors.border.copy(alpha = .7f), track).padding(5.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        MysterySet.entries.forEach { set ->
            val active = set == selected
            val fill by animateColorAsState(if (active) colors.elevated else colors.elevated.copy(alpha = 0f), label = "mystery-fill")
            val pill = RoundedCornerShape(21.dp)
            Box(
                Modifier.weight(1f)
                    .shadow(if (active && !colors.isDark) 5.dp else 0.dp, pill, ambientColor = colors.marian.copy(alpha = .2f), spotColor = colors.marian.copy(alpha = .28f))
                    .clip(pill).background(fill)
                    .selectable(active, role = Role.RadioButton, onClick = { onSelect(set) }).testTag("mystery-${set.name}").heightIn(min = 48.dp),
                contentAlignment = Alignment.Center,
            ) {
                SacredText(set.label, style = SertumTheme.typography.label.copy(fontSize = 13.5.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium), color = if (active) colors.ink else colors.secondary, align = TextAlign.Center)
            }
        }
    }
}

/** The chosen mysteries in one place: their invitation, then five moments joined by a stem. */
@Composable
private fun MysteryCard(set: MysterySet) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SacredCard(Modifier.fillMaxWidth().testTag("mystery-card")) {
        Column(Modifier.padding(start = 18.dp, end = 18.dp, top = 20.dp, bottom = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(Modifier.size(50.dp).background(SertumTheme.atmosphere.accent.copy(alpha = if (colors.isDark) .2f else .26f), CircleShape), contentAlignment = Alignment.Center) {
                    SacredIcon(when (set) { MysterySet.LUMINOUS -> SacredIcon.SUN; MysterySet.SORROWFUL -> SacredIcon.CROSS; else -> SacredIcon.ROSE }, Modifier.size(24.dp), if (colors.isDark) colors.gold else colors.ink.copy(alpha = .75f))
                }
                Column(Modifier.weight(1f)) {
                    SacredText("Mistérios ${set.label.lowercase()}", style = type.section)
                    SacredText(set.contemplation, Modifier.padding(top = 2.dp), type.body.copy(fontSize = 14.sp, lineHeight = 21.sp), colors.secondary)
                }
            }
            Box(Modifier.padding(top = 18.dp, bottom = 6.dp).fillMaxWidth().height(1.dp).background(colors.border))
            set.mysteries.forEachIndexed { index, mystery -> MysteryRow(set, index, mystery, last = index == set.mysteries.lastIndex) }
        }
    }
}

@Composable
private fun MysteryRow(set: MysterySet, index: Int, mystery: Mystery, last: Boolean) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    var expanded by remember(set, index) { mutableStateOf(false) }
    val turn by animateFloatAsState(if (expanded) 90f else 0f, label = "mystery-chevron")
    val stem = colors.marian.copy(alpha = .3f)
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).clickable(role = Role.Button, onClick = { expanded = !expanded })
            .drawBehind {
                // The stem passes behind each numbered bud, from the first mystery to the fifth.
                val x = 20.dp.toPx()
                val bud = 30.dp.toPx()
                drawLine(stem, Offset(x, if (index == 0) bud else 0f), Offset(x, if (last) bud else size.height), 1.5.dp.toPx())
            }
            .padding(vertical = 10.dp, horizontal = 4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(32.dp).background(colors.elevated, CircleShape).background(colors.marian.copy(alpha = .14f), CircleShape), contentAlignment = Alignment.Center) {
                SacredText("${index + 1}", style = type.label.copy(fontSize = 14.sp), color = colors.marian)
            }
            Column(Modifier.weight(1f)) {
                SacredText(mystery.title, style = type.label.copy(fontWeight = FontWeight.SemiBold))
                SacredText(mystery.scripture, Modifier.padding(top = 1.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.muted)
            }
            SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp).rotate(turn), colors.muted)
        }
        AnimatedVisibility(expanded) {
            SacredText("Uma semente para hoje: ${mystery.fruitInSentence}.", Modifier.padding(start = 46.dp, top = 8.dp, bottom = 2.dp), type.body.copy(fontSize = 14.sp, lineHeight = 21.sp), colors.secondary)
        }
    }
}
