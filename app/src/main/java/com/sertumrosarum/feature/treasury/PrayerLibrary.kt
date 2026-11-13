package com.sertumrosarum.feature.treasury

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.treasury.PrayerCategory
import com.sertumrosarum.domain.treasury.TreasuryPrayer
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.ui.canvas.CrownGlyph
import com.sertumrosarum.ui.canvas.DevotionalArtwork
import com.sertumrosarum.ui.canvas.DevotionalIllustration
import com.sertumrosarum.ui.canvas.LocalBotanicalArtwork
import com.sertumrosarum.ui.canvas.paintBotanical
import com.sertumrosarum.ui.components.PageWelcome
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SanctuaryPage
import com.sertumrosarum.ui.components.ShelfHeading
import com.sertumrosarum.ui.theme.SertumTheme

/** The library: a search, the moments of life, and one shelf for each moment that has prayers. */
@Composable
internal fun PrayerLibrary(state: TreasuryUiState, actions: TreasuryActions, scroll: ScrollState) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SanctuaryPage(SanctuaryTab.TREASURY.label, "treasury", scroll) {
        DevotionalArtwork(DevotionalIllustration.PRAYER_BOOK, Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp).size(width = 144.dp, height = 88.dp))
        PageWelcome("Rezar e compreender.", "Orações para cada momento, e o sentido de cada palavra.", Modifier.padding(top = 14.dp))
        PrayerSearchField(state.query, actions::search, Modifier.padding(start = 24.dp, end = 24.dp, top = 22.dp))
        CategoryChoices(state.category, actions::selectCategory, Modifier.padding(top = 4.dp))

        if (state.prayers.isEmpty()) {
            EmptyShelf(if (state.query.isNotBlank()) "Nenhuma oração encontrada para “${state.query.trim()}”." else "As orações para este momento ainda não foram plantadas.")
        } else {
            val shelves = state.prayers.groupBy { it.category }
            PrayerCategory.entries.filter { it in shelves }.forEach { category ->
                ShelfHeading(category.label, category.caption, Modifier.padding(top = 22.dp).testTag("shelf-${category.name}"))
                SacredCard(Modifier.padding(horizontal = 24.dp).fillMaxWidth()) {
                    Column(Modifier.padding(vertical = 6.dp)) {
                        shelves.getValue(category).forEachIndexed { index, prayer ->
                            if (index > 0) Box(Modifier.padding(start = 64.dp, end = 20.dp).fillMaxWidth().height(1.dp).background(colors.border.copy(alpha = .8f)))
                            PrayerRow(prayer) { actions.openPrayer(prayer) }
                        }
                    }
                }
            }
        }

        // Formation keeps its shelf whenever the reader is not looking for one prayer.
        if (state.query.isBlank() && state.category == null) {
            ShelfHeading("Formação", "Para conhecer o que se reza.", Modifier.padding(top = 30.dp).testTag("shelf-FORMATION"))
            SacredCard(Modifier.padding(horizontal = 24.dp).fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Abrir o guia de estudo", onClick = actions::openMysteries).testTag("open-mysteries")
                        .heightIn(min = 84.dp).padding(start = 16.dp, end = 18.dp, top = 14.dp, bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    CrownGlyph(5, 10, Modifier.size(64.dp))
                    Column(Modifier.weight(1f)) {
                        SacredText("Os vinte mistérios", style = type.label.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold))
                        SacredText("A cena, o sentido e o fruto de cada mistério do rosário.", Modifier.padding(top = 2.dp), type.body.copy(fontSize = 14.sp, lineHeight = 20.sp), colors.secondary)
                        SacredText("Guia de estudo", Modifier.padding(top = 7.dp), type.micro.copy(fontWeight = FontWeight.Medium), colors.marian)
                    }
                    SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), colors.muted)
                }
            }
        }

        SacredText("“Maria guardava todas estas coisas, meditando-as no seu coração.”", Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 34.dp), type.whisper, colors.secondary, TextAlign.Center)
        SacredText("Lucas 2, 19", Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 44.dp), type.micro, colors.muted, TextAlign.Center)
    }
}

/** A quiet field on the page's surface colour; the cross appears only when there is something to clear. */
@Composable
private fun PrayerSearchField(query: String, onQuery: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val focus = LocalFocusManager.current
    val shape = RoundedCornerShape(26.dp)
    val text = type.body.copy(fontSize = 16.sp, lineHeight = 22.sp, color = colors.ink)
    BasicTextField(
        query, onQuery, modifier.fillMaxWidth().testTag("treasury-search"),
        textStyle = text, singleLine = true, cursorBrush = SolidColor(colors.marian),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { focus.clearFocus() }),
        decorationBox = { field ->
            Row(
                Modifier.fillMaxWidth().clip(shape).background(colors.surface).border(1.dp, colors.border.copy(alpha = .7f), shape).heightIn(min = 52.dp).padding(start = 18.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SacredIcon(SacredIcon.SEARCH, Modifier.size(18.dp), colors.secondary)
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) SacredText("Buscar uma oração", style = text, color = colors.muted, maxLines = 1)
                    field()
                }
                if (query.isNotEmpty()) {
                    Box(
                        Modifier.size(48.dp).clip(CircleShape).semantics { contentDescription = "Limpar a busca" }
                            .clickable(role = Role.Button) { onQuery("") }.testTag("treasury-search-clear"),
                        contentAlignment = Alignment.Center,
                    ) { SacredIcon(SacredIcon.CLOSE, Modifier.size(14.dp), colors.secondary) }
                } else Box(Modifier.size(width = 14.dp, height = 48.dp))
            }
        },
    )
}

/** The moments of life as plain words in a row that runs past the edge; a small sage bead rests under the chosen one. */
@Composable
private fun CategoryChoices(selected: PrayerCategory?, onSelect: (PrayerCategory?) -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Row(modifier.horizontalScroll(rememberScrollState()).selectableGroup().testTag("category-choices").padding(horizontal = 14.dp)) {
        (listOf<PrayerCategory?>(null) + PrayerCategory.entries).forEach { category ->
            val active = category == selected
            Column(
                Modifier.clip(RoundedCornerShape(14.dp)).selectable(active, role = Role.RadioButton, onClick = { onSelect(category) })
                    .testTag("category-${category?.name ?: "ALL"}").heightIn(min = 48.dp).padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
            ) {
                SacredText(category?.label ?: "Todas", style = type.label.copy(fontSize = 14.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium), color = if (active) colors.ink else colors.secondary, maxLines = 1)
                Box(Modifier.padding(top = 4.dp).size(5.dp).background(colors.marian.copy(alpha = if (active) 1f else 0f), CircleShape))
            }
        }
    }
}

@Composable
private fun PrayerRow(prayer: TreasuryPrayer, onOpen: () -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val art = LocalBotanicalArtwork.current
    Row(
        Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Ler esta oração", onClick = onOpen).testTag("prayer-${prayer.id}")
            .heightIn(min = 76.dp).padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Canvas(Modifier.size(30.dp)) { paintBotanical(art.ivoryRose, center, size.minDimension) }
        Column(Modifier.weight(1f)) {
            SacredText(prayer.title, style = type.label.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold))
            SacredText(prayer.summary, Modifier.padding(top = 2.dp), type.body.copy(fontSize = 14.sp, lineHeight = 20.sp), colors.secondary)
            // What the prayer offers beyond its text stays typographic, as on the Roteiros shelves.
            val details = buildList {
                if (prayer.latin != null) add("Português e latim")
                if (prayer.annotations.isNotEmpty()) add("Toque para entender")
            }
            if (details.isNotEmpty()) SacredText(details.joinToString(" · "), Modifier.padding(top = 7.dp), type.micro.copy(fontWeight = FontWeight.Medium), colors.marian)
        }
        SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), colors.muted)
    }
}

/** A shelf with nothing on it yet says so plainly, under the same bud as the places still to open. */
@Composable
private fun EmptyShelf(message: String) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val art = LocalBotanicalArtwork.current
    Column(Modifier.fillMaxWidth().testTag("treasury-empty").padding(start = 32.dp, end = 32.dp, top = 34.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.size(54.dp)) { paintBotanical(art.bud, center, size.minDimension) }
        SacredText("Ainda em botão", Modifier.padding(top = 6.dp), type.micro, colors.marian)
        SacredText(message, Modifier.padding(top = 8.dp).testTag("treasury-empty-message"), type.whisper, colors.secondary, TextAlign.Center)
    }
}
