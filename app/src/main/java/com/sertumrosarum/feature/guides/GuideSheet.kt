package com.sertumrosarum.feature.guides

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.prayer.PrayerMode
import com.sertumrosarum.feature.prayer.PrayerModeSelector
import com.sertumrosarum.domain.prayer.LitanyOfLoreto
import com.sertumrosarum.feature.prayer.LitanyVerses
import com.sertumrosarum.feature.sanctuary.MysterySelector
import com.sertumrosarum.ui.canvas.BloomingBeadsArc
import com.sertumrosarum.ui.canvas.FiveMysteriesArc
import com.sertumrosarum.ui.canvas.LocalBotanicalArtwork
import com.sertumrosarum.ui.canvas.paintBotanical
import com.sertumrosarum.ui.canvas.DevotionalArtwork
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SacredToggleRow
import com.sertumrosarum.ui.theme.SertumTheme

/** Everything about one guide before praying it: what it is, how its beads run, and its path. */
@Composable
internal fun ColumnScope.GuideSheetContent(
    guide: PrayerGuide,
    selectedSet: MysterySet,
    onSelectSet: (MysterySet) -> Unit,
    meditated: Boolean,
    onToggleMeditated: () -> Unit,
    mode: PrayerMode,
    onSelectMode: (PrayerMode) -> Unit,
    focusMystery: Int = 0,
    onSelectFocusMystery: (Int) -> Unit = {},
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    DevotionalArtwork(guide.illustration, Modifier.align(Alignment.CenterHorizontally).size(128.dp))
    SacredText(guide.kicker, Modifier.fillMaxWidth(), type.micro, colors.accentInk, TextAlign.Center)
    SacredText(guide.title, Modifier.fillMaxWidth().padding(top = 10.dp).testTag("guide-title").semantics { heading() }, type.heading, align = TextAlign.Center)
    SacredText(guide.about, Modifier.fillMaxWidth().padding(top = 10.dp), type.body, colors.secondary, TextAlign.Center)

    PrayerModeSelector(mode, onSelectMode, Modifier.padding(top = 18.dp))
    CrownPreview(guide, Modifier.padding(top = 22.dp))

    if (guide.litany) {
        SheetSection(if (mode == PrayerMode.SOLO) "A ladainha na oração a sós" else "Duas vozes na ladainha")
        Panel {
            LitanyVerses(LitanyOfLoreto.invocations.filter { it.text in listOf("Senhor, tende piedade de nós.", "Santa Maria,", "Rosa mística,", "Rainha da paz,") }.map { it.text to requireNotNull(it.response) })
            SacredText(if (mode == PrayerMode.SOLO) "Reze a invocação e a resposta, no seu ritmo." else "Uma voz chama, e todos respondem.", Modifier.padding(top = 14.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.secondary)
        }
    }

    SheetSection("O caminho desta oração")
    val art = LocalBotanicalArtwork.current
    val stem = colors.marian.copy(alpha = .3f)
    guide.path.forEachIndexed { index, stop ->
        Row(
            Modifier.fillMaxWidth().drawBehind {
                val x = 19.dp.toPx()
                drawLine(stem, Offset(x, if (index == 0) center.y else 0f), Offset(x, if (index == guide.path.lastIndex) center.y else size.height), 1.5.dp.toPx())
            }.padding(start = 6.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Canvas(Modifier.size(26.dp).background(colors.background, CircleShape)) {
                paintBotanical(if (index == guide.path.lastIndex) art.rose else art.bud, center, size.minDimension)
            }
            SacredText(stop, Modifier.weight(1f), type.body)
        }
    }

    if (guide.usesMysterySet) {
        SheetSection("Quais mistérios?")
        MysterySelector(selectedSet, onSelectSet)
        SacredText(selectedSet.contemplation, Modifier.fillMaxWidth().padding(top = 10.dp), type.body.copy(fontSize = 14.sp, lineHeight = 21.sp), colors.secondary, TextAlign.Center)
        if (guide == PrayerGuide.SINGLE_DECADE) {
            SheetSection("Qual mistério deseja contemplar?")
            Column(Modifier.fillMaxWidth().selectableGroup(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                selectedSet.mysteries.forEachIndexed { index, mystery ->
                    val chosen = index == focusMystery.coerceIn(0, 4)
                    val shape = RoundedCornerShape(18.dp)
                    Row(
                        Modifier.fillMaxWidth().clip(shape)
                            .background(if (chosen) colors.marian.copy(alpha = .14f) else Color.Transparent)
                            .border(1.dp, if (chosen) colors.marian.copy(alpha = .6f) else colors.border, shape)
                            .selectable(chosen, role = Role.RadioButton) { onSelectFocusMystery(index) }
                            .testTag("focus-mystery-$index")
                            .heightIn(min = 52.dp).padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(Modifier.size(26.dp).background(if (chosen) colors.petal else colors.surface, CircleShape), contentAlignment = Alignment.Center) {
                            SacredText("${index + 1}", style = type.micro, color = if (chosen) colors.onPetal else colors.accentInk)
                        }
                        Column(Modifier.weight(1f)) {
                            SacredText(mystery.title, style = type.label)
                            SacredText("${mystery.scripture} · ${mystery.fruit}", style = type.micro.copy(fontWeight = FontWeight.Normal), color = colors.secondary)
                        }
                        if (chosen) SacredIcon(SacredIcon.CHECK, Modifier.size(16.dp), colors.accentInk)
                    }
                }
            }
        }
        Box(Modifier.padding(top = 16.dp, bottom = 6.dp).fillMaxWidth().height(1.dp).background(colors.border))
        SacredToggleRow("Terço meditado conta a conta", "Uma frase para contemplar em cada Ave-Maria.", meditated, onToggleMeditated, tag = "guide-meditated")
    } else if (guide == PrayerGuide.FULL_ROSARY) {
        Box(Modifier.padding(top = 16.dp, bottom = 6.dp).fillMaxWidth().height(1.dp).background(colors.border))
        SacredToggleRow("Terço meditado conta a conta", "Uma frase para contemplar em cada Ave-Maria.", meditated, onToggleMeditated, tag = "guide-meditated")
    }
}

/** The standing action remains visible while the explanation scrolls. */
@Composable
internal fun GuideSheetFooter(onStart: () -> Unit) {
    SacredPrimaryButton("Começar esta oração", onStart, tag = "guide-start")
}

@Composable
private fun SheetSection(title: String) {
    SacredText(title, Modifier.padding(top = 26.dp, bottom = 12.dp).semantics { heading() }, SertumTheme.typography.section)
}

@Composable
private fun Panel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val colors = SertumTheme.colors
    val shape = RoundedCornerShape(28.dp)
    Column(
        modifier.fillMaxWidth().clip(shape)
            .background(lerp(colors.elevated, SertumTheme.atmosphere.tint, if (colors.isDark) .35f else .45f))
            .border(1.dp, colors.border.copy(alpha = if (colors.isDark) .9f else .7f), shape).padding(horizontal = 18.dp, vertical = 16.dp),
        content = content,
    )
}

/**
 * The crown's own shape, drawn with the same stem and garland the prayer screen uses,
 * so seven septets, nine choirs, fourteen stations, or one decade can be felt before they are prayed.
 */
@Composable
private fun CrownPreview(guide: PrayerGuide, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val shape = guide.shape
    val defaultGroup = if (shape.groups > 1) 1 else 0
    val defaultBead = (shape.beads / 3).coerceIn(0, (shape.beads - 1).coerceAtLeast(0))
    var group by rememberSaveable(guide) { mutableIntStateOf(defaultGroup) }
    var bead by rememberSaveable(guide) { mutableIntStateOf(defaultBead) }
    val frame = RoundedCornerShape(28.dp)
    Column(
        modifier.fillMaxWidth().clip(frame)
            .background(lerp(colors.elevated, SertumTheme.atmosphere.tint, if (colors.isDark) .35f else .45f))
            .border(1.dp, colors.border.copy(alpha = if (colors.isDark) .9f else .7f), frame)
            .testTag("crown-preview").padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FiveMysteriesArc(group, group, { group = it; bead = 0 }, groups = shape.groups, groupLabel = shape.groupLabel)
        if (shape.beads > 1) {
            BloomingBeadsArc(shape.beads, bead, false, { bead = it }, beadLabel = "Conta")
        }
        SacredText(shape.summary, Modifier.padding(top = 4.dp).testTag("crown-shape"), type.label, align = TextAlign.Center)
        SacredText(if (shape.beads > 1) "Toque nas contas para sentir o ritmo." else "Toque nas estações para percorrer o caminho.", Modifier.padding(top = 2.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.secondary, TextAlign.Center)
    }
}
