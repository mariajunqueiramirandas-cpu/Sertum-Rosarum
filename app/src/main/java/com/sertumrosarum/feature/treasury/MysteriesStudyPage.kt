package com.sertumrosarum.feature.treasury

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.sertumrosarum.ui.canvas.FineArt
import com.sertumrosarum.ui.components.FineArtCredits
import com.sertumrosarum.ui.components.SacredPaintingThumb
import com.sertumrosarum.ui.components.SacredTextLink
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.feature.sanctuary.MysterySelector
import com.sertumrosarum.ui.components.PageWelcome
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SanctuaryPage
import com.sertumrosarum.ui.theme.SertumTheme

/** The twenty mysteries to study outside prayer: four sets, five mysteries each, every one opening its own sheet. */
@Composable
internal fun MysteriesStudyPage(set: MysterySet, actions: TreasuryActions) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SanctuaryPage(SanctuaryTab.TREASURY.label, "treasury-mysteries", onBack = actions::backToLibrary) {
        PageWelcome("Os vinte mistérios", "Cada cena do rosário, para ler com calma fora da oração.", Modifier.padding(top = 14.dp))
        Box(Modifier.padding(start = SertumTheme.spacing.page, end = SertumTheme.spacing.page, top = 22.dp)) { MysterySelector(set, actions::selectStudySet) }
        SacredText(set.days.lowercase().replaceFirstChar { it.uppercase() }, Modifier.fillMaxWidth().padding(top = 18.dp).testTag("study-days"), type.micro, colors.accentInk, TextAlign.Center)
        SacredText(set.invitation, Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp, top = 4.dp), type.whisper, colors.secondary, TextAlign.Center)

        SacredCard(Modifier.padding(start = SertumTheme.spacing.page, end = SertumTheme.spacing.page, top = 20.dp).fillMaxWidth()) {
            Column(Modifier.padding(vertical = 6.dp)) {
                set.mysteries.forEachIndexed { index, mystery ->
                    if (index > 0) Box(Modifier.padding(start = 90.dp, end = 20.dp).fillMaxWidth().height(1.dp).background(colors.border.copy(alpha = .8f)))
                    Row(
                        Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Estudar este mistério") { actions.openStudyMystery(index) }.testTag("study-mystery-$index")
                            .heightIn(min = 76.dp).padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 14.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        SacredPaintingThumb(FineArt.forMystery(set, index), size = 56.dp)
                        Column(Modifier.weight(1f)) {
                            SacredText(mystery.title, style = type.label.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold))
                            SacredText("Fruto: ${mystery.fruitInSentence}", Modifier.padding(top = 2.dp), type.body.copy(fontSize = 14.sp, lineHeight = 20.sp), colors.secondary)
                            SacredText(mystery.scripture, Modifier.padding(top = 5.dp), type.micro.copy(fontWeight = FontWeight.Medium), colors.marian)
                        }
                        SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), colors.muted)
                    }
                }
            }
        }
        SacredText(set.contemplation, Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp, top = 30.dp), type.whisper, colors.secondary, TextAlign.Center)
        var credits by rememberSaveable { mutableStateOf(false) }
        SacredTextLink("Obras de arte e créditos", { credits = true }, "mysteries-art-credits", Modifier.align(Alignment.CenterHorizontally).padding(top = 14.dp, bottom = 36.dp))
        if (credits) FineArtCredits { credits = false }
    }
}
