package com.sertumrosarum.feature.treasury

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.domain.prayer.MysteryContent
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.prayer.MysteryStudyCatalog
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.feature.sanctuary.MysterySelector
import com.sertumrosarum.ui.canvas.FineArt
import com.sertumrosarum.ui.components.FineArtCredits
import com.sertumrosarum.ui.components.PageWelcome
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredEyebrow
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredPaintingThumb
import com.sertumrosarum.ui.components.SacredSearchField
import com.sertumrosarum.ui.components.SacredSecondaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SacredTextLink
import com.sertumrosarum.ui.components.SanctuaryPage
import com.sertumrosarum.ui.theme.SertumTheme

/** The twenty mysteries to study outside prayer: searchable across all 20, or browsed set by set. */
@Composable
internal fun MysteriesStudyPage(
    set: MysterySet,
    actions: TreasuryActions,
    bookmarkedMysteries: Set<String> = emptySet(),
    onPraySet: ((MysterySet) -> Unit)? = null,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val page = SertumTheme.spacing.page
    var query by rememberSaveable { mutableStateOf("") }

    SanctuaryPage(SanctuaryTab.TREASURY.label, "treasury-mysteries", onBack = actions::backToLibrary) {
        PageWelcome("Os vinte mistérios", "Escritura, contexto histórico, voz dos santos e meditação conta a conta.", Modifier.padding(top = 14.dp))

        Box(Modifier.padding(start = page, end = page, top = 16.dp)) {
            SacredSearchField(query, { query = it }, "Buscar mistério, santo, símbolo ou tema", "study-search")
        }

        if (query.isNotBlank()) {
            val matches = MysterySet.entries.flatMap { candidateSet ->
                candidateSet.mysteries.mapIndexedNotNull { index, mystery ->
                    val contemplation = MysteryContent.contemplationFor(candidateSet, index)
                    val study = MysteryStudyCatalog.studyFor(candidateSet, index)
                    val haystack = "${mystery.title} ${mystery.scripture} ${mystery.fruit} ${candidateSet.label} ${contemplation.scene} ${contemplation.meaning} ${study.searchableText}"
                    if (FormationCatalog.matches(query, haystack) > 0) Triple(candidateSet, index, mystery) else null
                }
            }
            SacredText(
                "${matches.size} ${if (matches.size == 1) "mistério encontrado" else "mistérios encontrados"}",
                Modifier.padding(start = page, end = page, top = 16.dp),
                type.micro,
                colors.secondary,
            )
            if (matches.isEmpty()) {
                SacredText(
                    "Nenhum mistério encontrado para “$query”. Tente palavras como Nazaré, Caná, Getsêmani, Eucaristia ou Montfort.",
                    Modifier.padding(horizontal = page, vertical = 14.dp),
                    type.body,
                    colors.secondary,
                )
            } else {
                SacredCard(Modifier.padding(start = page, end = page, top = 12.dp, bottom = 36.dp).fillMaxWidth()) {
                    Column(Modifier.padding(vertical = 6.dp)) {
                        matches.forEachIndexed { rowIndex, (matchSet, idx, mystery) ->
                            if (rowIndex > 0) Box(Modifier.padding(start = 90.dp, end = 20.dp).fillMaxWidth().height(1.dp).background(colors.border.copy(alpha = .8f)))
                            val saved = mysteryBookmarkId(matchSet, idx) in bookmarkedMysteries
                            Row(
                                Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Estudar este mistério") {
                                    actions.selectStudySet(matchSet)
                                    actions.openStudyMystery(idx)
                                }.testTag("study-match-${matchSet.name}-$idx")
                                    .heightIn(min = 78.dp).padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 14.dp),
                                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                SacredPaintingThumb(FineArt.forMystery(matchSet, idx), size = 56.dp)
                                Column(Modifier.weight(1f)) {
                                    SacredEyebrow("${idx + 1}º ${matchSet.singular.lowercase()} · ${mystery.scripture}")
                                    SacredText(mystery.title, Modifier.padding(top = 2.dp), type.label.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold))
                                    SacredText("Fruto: ${mystery.fruitInSentence}", Modifier.padding(top = 2.dp), type.body.copy(fontSize = 14.sp, lineHeight = 20.sp), colors.secondary)
                                }
                                if (saved) SacredIcon(SacredIcon.BOOKMARK, Modifier.size(16.dp), colors.accentInk)
                                SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), colors.muted)
                            }
                        }
                    }
                }
            }
            return@SanctuaryPage
        }

        Box(Modifier.padding(start = page, end = page, top = 20.dp)) { MysterySelector(set, actions::selectStudySet) }
        SacredText(set.days.lowercase().replaceFirstChar { it.uppercase() }, Modifier.fillMaxWidth().padding(top = 18.dp).testTag("study-days"), type.micro, colors.accentInk, TextAlign.Center)
        SacredText(set.invitation, Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp, top = 4.dp), type.whisper, colors.secondary, TextAlign.Center)

        SacredCard(Modifier.padding(start = page, end = page, top = 20.dp).fillMaxWidth()) {
            Column(Modifier.padding(vertical = 6.dp)) {
                set.mysteries.forEachIndexed { index, mystery ->
                    if (index > 0) Box(Modifier.padding(start = 90.dp, end = 20.dp).fillMaxWidth().height(1.dp).background(colors.border.copy(alpha = .8f)))
                    val study = MysteryStudyCatalog.studyFor(set, index)
                    val saved = mysteryBookmarkId(set, index) in bookmarkedMysteries
                    Row(
                        Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Estudar este mistério") { actions.openStudyMystery(index) }.testTag("study-mystery-$index")
                            .heightIn(min = 82.dp).padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 14.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        SacredPaintingThumb(FineArt.forMystery(set, index), size = 58.dp)
                        Column(Modifier.weight(1f)) {
                            SacredText(mystery.title, style = type.label.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold))
                            SacredText("Fruto: ${mystery.fruitInSentence}", Modifier.padding(top = 2.dp), type.body.copy(fontSize = 14.sp, lineHeight = 20.sp), colors.secondary)
                            SacredText("${mystery.scripture} · ${study.catechismRefs}", Modifier.padding(top = 5.dp), type.micro.copy(fontWeight = FontWeight.Medium), colors.marian)
                        }
                        if (saved) SacredIcon(SacredIcon.BOOKMARK, Modifier.size(16.dp), colors.accentInk)
                        SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), colors.muted)
                    }
                }
            }
        }
        if (onPraySet != null) {
            Box(Modifier.padding(start = page, end = page, top = 20.dp)) {
                SacredSecondaryButton(
                    "Rezar os Mistérios ${set.label.lowercase()}",
                    { onPraySet(set) },
                    Modifier.fillMaxWidth(),
                    tag = "study-pray-set",
                    icon = SacredIcon.BEADS,
                )
            }
        }
        SacredText(set.contemplation, Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp, top = 26.dp), type.whisper, colors.secondary, TextAlign.Center)
        var credits by rememberSaveable { mutableStateOf(false) }
        SacredTextLink("Obras de arte e créditos", { credits = true }, "mysteries-art-credits", Modifier.align(Alignment.CenterHorizontally).padding(top = 14.dp, bottom = 36.dp))
        if (credits) FineArtCredits { credits = false }
    }
}
