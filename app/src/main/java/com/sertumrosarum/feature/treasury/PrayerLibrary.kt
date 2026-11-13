package com.sertumrosarum.feature.treasury

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.semantics.heading
import com.sertumrosarum.ui.canvas.RoseMark
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
import com.sertumrosarum.ui.canvas.CrownGlyph
import com.sertumrosarum.ui.canvas.DevotionalArtwork
import com.sertumrosarum.ui.canvas.DevotionalIllustration
import com.sertumrosarum.ui.canvas.LocalBotanicalArtwork
import com.sertumrosarum.ui.canvas.paintBotanical
import com.sertumrosarum.ui.components.PageWelcome
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme

/** The library: a search, the moments of life, and one shelf for each moment that has prayers. */
@Composable
internal fun PrayerLibrary(state: TreasuryUiState, actions: TreasuryActions, scroll: LazyListState) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Column(Modifier.fillMaxSize().testTag("treasury")) {
        Row(Modifier.fillMaxWidth().padding(start = SertumTheme.spacing.page, top = 10.dp, bottom = 2.dp).heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            RoseMark(Modifier.size(30.dp))
            SacredText("Tesouro", Modifier.semantics { heading() }, type.brand)
        }
        LazyColumn(Modifier.fillMaxSize().testTag("treasury-scroll"), state = scroll) {
            if (state.query.isBlank()) item("welcome") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val category = state.category
                    if (category == null) {
                        DevotionalArtwork(DevotionalIllustration.TREASURY_ENTRANCE, Modifier.padding(top = 10.dp).size(width = 256.dp, height = 144.dp))
                    } else {
                        TreasuryArtwork(category, Modifier.padding(top = 12.dp).size(120.dp))
                    }
                    PageWelcome(category?.label ?: "Rezar e compreender.", category?.caption ?: "Orações para cada momento, e o sentido de cada palavra.", Modifier.padding(top = 10.dp))
                }
            }
            item("search") { PrayerSearchField(state.query, actions::search, Modifier.padding(start = SertumTheme.spacing.page, end = SertumTheme.spacing.page, top = 22.dp)) }
            item("categories") { CategoryChoices(state.category, actions::selectCategory, Modifier.padding(top = 4.dp)) }
            if (state.query.isBlank() && state.category == null) {
                item("formation") {
                    SacredCard(Modifier.padding(horizontal = SertumTheme.spacing.page, vertical = 14.dp).fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Abrir o guia de estudo", onClick = actions::openMysteries)
                            .testTag("open-mysteries").heightIn(min = 84.dp).padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            CrownGlyph(5, 10, Modifier.size(48.dp))
                            Column(Modifier.weight(1f)) {
                                SacredText("Os vinte mistérios", style = type.label)
                                SacredText("A cena, o sentido, o fruto e as dez meditações.", Modifier.padding(top = 4.dp), type.body, colors.secondary)
                            }
                            SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), colors.muted)
                        }
                    }
                }
            }
            item("result-count") {
                SacredText("${state.prayers.size} ${if (state.prayers.size == 1) "oração" else "orações"}",
                    Modifier.padding(horizontal = SertumTheme.spacing.page, vertical = 8.dp).testTag("treasury-result-count"), type.micro, colors.secondary)
            }
            if (state.prayers.isEmpty()) {
                item("empty") { EmptyShelf(if (state.query.isNotBlank()) "Nenhuma oração encontrada para “${state.query.trim()}”." else "Nenhuma oração neste momento.") }
            } else {
                val shelves = state.prayers.groupBy { it.category }
                PrayerCategory.entries.filter { it in shelves }.forEach { category ->
                    if (state.category == null) item("heading-${category.name}") {
                        Row(Modifier.fillMaxWidth().padding(start = SertumTheme.spacing.page - 4.dp, end = SertumTheme.spacing.page, top = 12.dp, bottom = 10.dp).testTag("shelf-${category.name}"),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TreasuryArtwork(category, Modifier.size(76.dp))
                            Column(Modifier.weight(1f)) {
                                SacredText(category.label, Modifier.semantics { heading() }, type.section)
                                SacredText(category.caption, Modifier.padding(top = 4.dp), type.body.copy(fontSize = 14.sp, lineHeight = 21.sp), colors.secondary)
                            }
                        }
                    }
                    items(shelves.getValue(category), key = { "prayer-${it.id}" }, contentType = { "prayer" }) { prayer ->
                        SacredCard(Modifier.padding(horizontal = SertumTheme.spacing.page).padding(bottom = 6.dp).fillMaxWidth()) { PrayerRow(prayer) { actions.openPrayer(prayer) } }
                    }
                }
            }
            item("closing") {
                SacredText("“Maria guardava todas estas coisas, meditando-as no seu coração.”", Modifier.fillMaxWidth().padding(start = SertumTheme.spacing.page, end = SertumTheme.spacing.page, top = 28.dp), type.whisper, colors.secondary, TextAlign.Center)
                SacredText("Lucas 2, 19", Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 44.dp), type.micro, colors.muted, TextAlign.Center)
            }
        }
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
    Row(
        Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Ler esta oração", onClick = onOpen).testTag("prayer-${prayer.id}")
            .heightIn(min = 76.dp).padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TreasuryArtwork(prayer.category, Modifier.size(56.dp))
        Column(Modifier.weight(1f)) {
            SacredText(prayer.title, style = type.label.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold))
            SacredText(prayer.summary, Modifier.padding(top = 2.dp), type.body.copy(fontSize = 14.sp, lineHeight = 20.sp), colors.secondary)
            // What the prayer offers beyond its text stays typographic, as on the Roteiros shelves.
            val details = buildList {
                if (prayer.latin != null) add("Português e latim")
                if (prayer.annotations.isNotEmpty()) add("Toque para entender")
            }
            if (details.isNotEmpty()) SacredText(details.joinToString(" · "), Modifier.padding(top = 7.dp), type.micro.copy(fontWeight = FontWeight.Medium), colors.accentInk)
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
        SacredText("Ainda em botão", Modifier.padding(top = 6.dp), type.micro, colors.accentInk)
        SacredText(message, Modifier.padding(top = 8.dp).testTag("treasury-empty-message"), type.whisper, colors.secondary, TextAlign.Center)
    }
}
