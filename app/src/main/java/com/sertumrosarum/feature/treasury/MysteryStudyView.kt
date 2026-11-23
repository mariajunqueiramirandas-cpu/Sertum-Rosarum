package com.sertumrosarum.feature.treasury

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.sertumrosarum.domain.prayer.MysteryContent
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.prayer.MysteryStudy
import com.sertumrosarum.domain.prayer.MysteryStudyCatalog
import com.sertumrosarum.feature.prayer.BeadByBeadList
import com.sertumrosarum.feature.prayer.MysteryDimensions
import com.sertumrosarum.feature.prayer.ordinal
import com.sertumrosarum.ui.canvas.FineArt
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredEyebrow
import com.sertumrosarum.ui.components.SacredFlourish
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredPainting
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredSecondaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme

/** Canonical bookmark key for a Rosary mystery, e.g., "JOYFUL:0". */
fun mysteryBookmarkId(set: MysterySet, index: Int): String = "${set.name}:$index"

/** Parse a canonical mystery bookmark key back into its set and decade index. */
fun parseMysteryBookmark(id: String): Pair<MysterySet, Int>? {
    val parts = id.split(':')
    if (parts.size != 2) return null
    val set = MysterySet.entries.firstOrNull { it.name == parts[0] } ?: return null
    val index = parts[1].toIntOrNull()?.takeIf { it in set.mysteries.indices } ?: return null
    return set to index
}

/**
 * The complete editorial study of one Rosary mystery:
 * Fine art, Gospel reading, Cena/Sentido/Fruto, historical & Old Testament context,
 * voices of the Saints, symbols, reflection questions, all 10 bead meditations,
 * closing prayer, bookmarking, and direct actions to pray this decade or full set.
 */
@Composable
internal fun ColumnScope.MysteryStudyContent(
    set: MysterySet,
    index: Int,
    bookmarked: Boolean = false,
    onToggleBookmark: (() -> Unit)? = null,
    onPraySingleDecade: (() -> Unit)? = null,
    onPraySet: (() -> Unit)? = null,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val mystery = set.mysteries[index]
    val contemplation = MysteryContent.contemplationFor(set, index)
    val study = MysteryStudyCatalog.studyFor(set, index)

    SacredText(
        "${ordinal(index)} mistério ${set.singular.lowercase()} · ${mystery.scripture}",
        Modifier.fillMaxWidth(),
        type.micro,
        colors.accentInk,
        TextAlign.Center,
    )
    SacredText(
        mystery.title,
        Modifier.fillMaxWidth().padding(top = 6.dp).testTag("study-sheet-title").semantics { heading() },
        type.heading,
        align = TextAlign.Center,
    )
    SacredText(
        "Fruto espiritual: ${mystery.fruitInSentence}",
        Modifier.fillMaxWidth().padding(top = 6.dp),
        type.whisper,
        colors.secondary,
        TextAlign.Center,
    )

    if (onToggleBookmark != null) {
        val pillShape = RoundedCornerShape(50)
        Row(
            Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp).clip(pillShape)
                .background(if (bookmarked) colors.marian.copy(alpha = .16f) else Color.Transparent)
                .border(1.dp, if (bookmarked) colors.marian.copy(alpha = .6f) else colors.border, pillShape)
                .clickable(role = Role.Button, onClick = onToggleBookmark)
                .testTag("bookmark-mystery")
                .heightIn(min = 40.dp).padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SacredIcon(SacredIcon.BOOKMARK, Modifier.size(16.dp), if (bookmarked) colors.accentInk else colors.secondary)
            SacredText(
                if (bookmarked) "Mistério guardado" else "Guardar este mistério",
                style = type.micro,
                color = if (bookmarked) colors.accentInk else colors.secondary,
            )
        }
    }

    SacredPainting(FineArt.forMystery(set, index), Modifier.padding(top = 18.dp), "study-painting")
    SacredFlourish(Modifier.align(Alignment.CenterHorizontally).padding(top = 14.dp, bottom = 18.dp))

    // 1. Extended Gospel narrative
    SacredCard(Modifier.fillMaxWidth().testTag("study-gospel-card")) {
        Column(Modifier.padding(20.dp)) {
            SacredEyebrow("Na Escritura · ${mystery.scripture}")
            SacredText(study.scriptureTitle, Modifier.padding(top = 6.dp).semantics { heading() }, type.section)
            SacredText("“${contemplation.scriptureQuote}”", Modifier.padding(top = 10.dp), type.whisper, colors.accentInk)
            SacredText(study.scriptureReading, Modifier.padding(top = 10.dp), type.body)
            SacredText(
                "Síntese editorial para acompanhamento; leia também a passagem completa na sua Bíblia.",
                Modifier.padding(top = 10.dp),
                type.micro.copy(fontWeight = FontWeight.Normal),
                colors.muted,
            )
        }
    }

    // 2. Classic 3 dimensions (Cena, Sentido, Fruto)
    Box(Modifier.height(16.dp))
    MysteryDimensions(mystery, contemplation, meditated = false, onToggleMeditated = {}, unfolded = true, showMeditatedToggle = false)

    // 3. Historical, Biblical & Theological depth + Saints + Symbols + Questions
    MysteryDeepStudySections(study, Modifier.padding(top = 20.dp))

    // 4. All 10 bead meditations
    SacredText("Conta a conta · 10 Ave-Marias", Modifier.padding(start = 6.dp, top = 26.dp, bottom = 8.dp).semantics { heading() }, type.section)
    BeadByBeadList(contemplation.beads, current = null)

    // 5. Closing prayer of the mystery
    SacredCard(Modifier.fillMaxWidth().padding(top = 22.dp)) {
        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            SacredEyebrow("Oração deste mistério")
            SacredText(study.closingPrayer, Modifier.padding(top = 8.dp), type.whisper, colors.accentInk, TextAlign.Center)
        }
    }

    // 6. Direct bridge into prayer
    if (onPraySingleDecade != null || onPraySet != null) {
        Column(Modifier.fillMaxWidth().padding(top = 22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (onPraySingleDecade != null) {
                SacredPrimaryButton("Rezar apenas esta dezena", onPraySingleDecade, tag = "pray-single-decade")
            }
            if (onPraySet != null) {
                SacredSecondaryButton(
                    "Rezar os 5 mistérios ${set.label.lowercase()}",
                    onPraySet,
                    Modifier.fillMaxWidth(),
                    tag = "pray-mystery-set",
                    icon = SacredIcon.BEADS,
                )
            }
        }
    }
}

/**
 * Reusable deep study panels: Historical & Old Testament context, Catechism depth,
 * Saints' voices, Symbols to contemplate, and personal reflection questions.
 * Used both in [MysteryStudyContent] (Tesouro) and optionally inside the live prayer sheet.
 */
@Composable
internal fun MysteryDeepStudySections(study: MysteryStudy, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val honey = lerp(colors.gold, colors.ink, if (colors.isDark) 0f else .4f)

    Column(modifier.fillMaxWidth().testTag("mystery-deep-study"), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Historical & Biblical context
        SacredCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                SacredEyebrow("Contexto histórico e bíblico")
                SacredText(study.historicalContext, Modifier.padding(top = 8.dp), type.body)
                Box(Modifier.padding(vertical = 14.dp).fillMaxWidth().height(1.dp).background(colors.border))
                SacredEyebrow("À luz da fé da Igreja")
                SacredText(study.theologicalDepth, Modifier.padding(top = 8.dp), type.body)
                SacredText("Catecismo: ${study.catechismRefs}", Modifier.padding(top = 10.dp), type.micro, colors.accentInk)
            }
        }

        // Voices of the Saints
        SacredCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SacredEyebrow("Na voz dos santos e da Igreja")
                study.saintsVoices.forEachIndexed { idx, voice ->
                    if (idx > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border.copy(alpha = .7f)))
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.width(2.5.dp).fillMaxHeight().background(colors.gold.copy(alpha = .6f), RoundedCornerShape(2.dp)))
                        Column(Modifier.weight(1f)) {
                            SacredText(voice.saint, style = type.label, color = honey)
                            SacredText(voice.work, Modifier.padding(top = 1.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.muted)
                            SacredText(voice.reflection, Modifier.padding(top = 6.dp), type.whisper)
                        }
                    }
                }
            }
        }

        // Symbols to contemplate
        SacredCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SacredEyebrow("Símbolos e detalhes para contemplar")
                study.symbols.forEach { symbol ->
                    Column(Modifier.fillMaxWidth()) {
                        SacredText(symbol.name, style = type.label, color = colors.accentInk)
                        SacredText(symbol.meaning, Modifier.padding(top = 3.dp), type.body, colors.secondary)
                    }
                }
            }
        }

        // Personal reflection questions
        SacredCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SacredEyebrow("Perguntas para levar à oração")
                study.reflectionQuestions.forEachIndexed { idx, question ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            Modifier.padding(top = 2.dp).size(22.dp).background(colors.marian.copy(alpha = .16f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            SacredText("${idx + 1}", style = type.micro, color = colors.accentInk)
                        }
                        SacredText(question, Modifier.weight(1f), type.body)
                    }
                }
            }
        }
    }
}
