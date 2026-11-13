package com.sertumrosarum.feature.prayer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.prayer.Mystery
import com.sertumrosarum.domain.prayer.MysteryContemplation
import com.sertumrosarum.ui.canvas.LocalBotanicalArtwork
import com.sertumrosarum.ui.canvas.paintBotanical
import com.sertumrosarum.ui.canvas.DevotionalArtwork
import com.sertumrosarum.ui.canvas.DevotionalIllustration
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SacredToggleRow
import com.sertumrosarum.ui.theme.SertumTheme

private val Ordinals = listOf("Primeiro", "Segundo", "Terceiro", "Quarto", "Quinto", "Sexto", "Sétimo")
internal fun ordinal(index: Int) = Ordinals.getOrElse(index) { "${index + 1}º" }

/**
 * A mystery in its dimensions: the scene, its meaning, and its fruit, joined by one stem,
 * with the bead-by-bead rosary as the fourth, optional layer.
 *
 * Before the decade the layers open one at a time; inside the contemplation sheet
 * ([unfolded]) they are all laid out to read.
 */
@Composable
internal fun MysteryDimensions(
    mystery: Mystery,
    contemplation: MysteryContemplation,
    meditated: Boolean,
    onToggleMeditated: () -> Unit,
    modifier: Modifier = Modifier,
    unfolded: Boolean = false,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    var open by rememberSaveable(mystery.title) { mutableIntStateOf(0) }
    SacredCard(modifier.fillMaxWidth().testTag("mystery-dimensions")) {
        Column(Modifier.padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 8.dp)) {
            val layers = listOf("A cena bíblica", "O sentido da contemplação", "O fruto do mistério")
            layers.forEachIndexed { index, name ->
                DimensionLayer(index, name, shown = unfolded || open == index, foldable = !unfolded, last = index == layers.lastIndex, onClick = { open = if (open == index) -1 else index }) {
                    when (index) {
                        0 -> {
                            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                SacredText(mystery.scripture, style = type.micro, color = colors.marian)
                                DevotionalArtwork(DevotionalIllustration.PRAYER_BOOK, Modifier.size(42.dp))
                            }
                            SacredText("“${contemplation.scriptureQuote}”", Modifier.padding(bottom = 10.dp).testTag("scripture-quote"), type.whisper, colors.marian)
                            SacredText(contemplation.scene, style = type.whisper)
                        }
                        1 -> SacredText(contemplation.meaning, style = type.whisper)
                        else -> {
                            SacredText(mystery.fruit, Modifier.padding(bottom = 4.dp), type.section, lerp(colors.ruby, colors.ink, if (colors.isDark) .1f else .4f))
                            SacredText(contemplation.practice, style = type.whisper)
                        }
                    }
                }
            }
            Box(Modifier.padding(top = 8.dp, bottom = 4.dp).fillMaxWidth().height(1.dp).background(colors.border))
            SacredToggleRow("Terço meditado conta a conta", "Uma frase para contemplar em cada Ave-Maria.", meditated, onToggleMeditated, tag = "meditated-toggle")
        }
    }
}

@Composable
private fun DimensionLayer(index: Int, name: String, shown: Boolean, foldable: Boolean, last: Boolean, onClick: () -> Unit, body: @Composable () -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val turn by animateFloatAsState(if (shown) 90f else 0f, label = "dimension-chevron")
    val stem = colors.marian.copy(alpha = .3f)
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
            .then(if (foldable) Modifier.clickable(role = Role.Button, onClick = onClick).semantics { stateDescription = if (shown) "Aberto" else "Fechado" } else Modifier)
            .testTag("dimension-$index")
            .drawBehind {
                val x = 20.dp.toPx()
                val knot = 26.dp.toPx()
                drawLine(stem, Offset(x, if (index == 0) knot else 0f), Offset(x, if (last) knot else size.height), 1.5.dp.toPx())
            }
            .padding(vertical = 10.dp, horizontal = 4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(32.dp).background(colors.elevated, CircleShape).background(colors.marian.copy(alpha = .14f), CircleShape), contentAlignment = Alignment.Center) {
                SacredText("${index + 1}", style = type.label.copy(fontSize = 14.sp), color = colors.marian)
            }
            SacredText(name, Modifier.weight(1f).semantics { heading() }, type.label.copy(fontWeight = FontWeight.SemiBold))
            if (foldable) SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp).rotate(turn), colors.muted)
        }
        AnimatedVisibility(shown) {
            Column(Modifier.padding(start = 46.dp, end = 6.dp, top = 8.dp, bottom = 4.dp)) { body() }
        }
    }
}

/** During a decade, the mystery stays one touch away: this opens its contemplation sheet. */
@Composable
internal fun MysteryLink(decade: Int, mystery: Mystery, onOpen: () -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val ink = lerp(colors.secondary, colors.ink, .3f)
    val art = LocalBotanicalArtwork.current
    Row(
        modifier.clip(RoundedCornerShape(12.dp)).clickable(role = Role.Button, onClickLabel = "Contemplar este mistério", onClick = onOpen)
            .testTag("mystery-chip").heightIn(min = 48.dp).padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(Modifier.size(18.dp)) { paintBotanical(art.ivoryRose, center, size.minDimension) }
        SacredText("${decade + 1}º mistério · ${mystery.title}", Modifier.weight(1f, fill = false), SertumTheme.typography.micro, ink, maxLines = 1)
        SacredIcon(SacredIcon.CHEVRON, Modifier.size(11.dp), ink)
    }
}

/**
 * One Ave-Maria of the bead-by-bead rosary: the mystery it belongs to, and the line to hold while praying.
 * It takes the place of the heading so the whole prayer still fits beneath it, and opens the same sheet as [MysteryLink].
 */
@Composable
internal fun MeditatedBead(decade: Int, mystery: Mystery, line: String, onOpen: () -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val art = LocalBotanicalArtwork.current
    val shape = RoundedCornerShape(26.dp)
    val honey = lerp(colors.gold, colors.ink, if (colors.isDark) 0f else .4f)
    Column(
        modifier.fillMaxWidth().clip(shape).background(colors.elevated.copy(alpha = if (colors.isDark) .55f else .75f)).border(1.dp, colors.gold.copy(alpha = .32f), shape)
            .clickable(role = Role.Button, onClickLabel = "Contemplar este mistério", onClick = onOpen).testTag("mystery-chip")
            .padding(start = 16.dp, end = 16.dp, top = 11.dp, bottom = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Canvas(Modifier.size(17.dp)) { paintBotanical(art.ivoryRose, center, size.minDimension) }
            SacredText("${decade + 1}º mistério · ${mystery.title}", Modifier.weight(1f, fill = false), SertumTheme.typography.micro, honey, maxLines = 1)
            SacredIcon(SacredIcon.CHEVRON, Modifier.size(11.dp), honey)
        }
        SacredText(line, Modifier.padding(top = 6.dp).testTag("bead-meditation"), SertumTheme.typography.whisper, colors.ink, TextAlign.Center)
    }
}

/** A small note from the catechetical guide: why this prayer is prayed here. */
@Composable
internal fun CatecheticalNote(text: String, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val shape = RoundedCornerShape(24.dp)
    val honey = lerp(colors.gold, colors.ink, if (colors.isDark) 0f else .4f)
    Row(
        modifier.fillMaxWidth().clip(shape).background(colors.gold.copy(alpha = if (colors.isDark) .12f else .1f)).border(1.dp, colors.gold.copy(alpha = .26f), shape)
            .testTag("catechetical-note").padding(start = 12.dp, end = 16.dp, top = 12.dp, bottom = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(32.dp).background(colors.gold.copy(alpha = .18f), CircleShape), contentAlignment = Alignment.Center) {
            DevotionalArtwork(DevotionalIllustration.PRAYER_BOOK, Modifier.size(30.dp))
        }
        Column(Modifier.weight(1f)) {
            SacredText("Por que rezamos assim?", style = type.micro, color = honey)
            SacredText(text, Modifier.padding(top = 3.dp), type.body.copy(fontSize = 14.5.sp, lineHeight = 22.sp), lerp(colors.secondary, colors.ink, .5f))
        }
    }
}

/** The ten lines of a meditated decade, with the bead being prayed resting on a petal. */
@Composable
internal fun BeadByBeadList(lines: List<String>, current: Int?, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Column(modifier.fillMaxWidth().testTag("bead-by-bead")) {
        lines.forEachIndexed { index, line ->
            val now = index == current
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(if (now) colors.petal.copy(alpha = if (colors.isDark) .18f else .6f) else colors.petal.copy(alpha = 0f))
                    .semantics { selected = now }.padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SacredText("${index + 1}", Modifier.padding(top = 3.dp).size(width = 20.dp, height = 20.dp), type.micro, if (now) colors.ruby else colors.muted, TextAlign.Center)
                SacredText(line, Modifier.weight(1f), type.whisper.copy(fontSize = 16.sp, lineHeight = 25.sp), if (now) colors.ink else colors.secondary)
            }
        }
    }
}

/** A litany is two voices: one calls, and everyone answers. The answer steps in, in rose. */
@Composable
internal fun LitanyVerses(verses: List<Pair<String, String>>, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val answer = lerp(colors.ruby, colors.ink, if (colors.isDark) .05f else .35f)
    Column(modifier.fillMaxWidth().testTag("litany-verses"), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        verses.forEach { (call, response) ->
            Column(Modifier.fillMaxWidth().semantics(mergeDescendants = true) {}) {
                SacredText(call, Modifier.testTag("litany-call"), style = type.whisper)
                Row(Modifier.padding(start = 22.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(5.dp).background(answer.copy(alpha = .7f), CircleShape))
                    SacredText(response, Modifier.testTag("litany-response"), style = type.whisper, color = answer)
                }
            }
        }
    }
}
