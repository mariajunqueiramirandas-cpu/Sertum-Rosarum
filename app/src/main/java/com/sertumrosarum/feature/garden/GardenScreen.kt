package com.sertumrosarum.feature.garden

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.data.garden.IntentionEntity
import com.sertumrosarum.data.garden.JournalEntry
import com.sertumrosarum.data.garden.PrayerSessionEntity
import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.domain.formation.FormationLesson
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.treasury.TreasuryCatalog
import com.sertumrosarum.feature.formation.chapterOf
import com.sertumrosarum.feature.formation.numberOf
import com.sertumrosarum.feature.guides.PrayerGuide
import com.sertumrosarum.ui.canvas.DevotionalArtwork
import com.sertumrosarum.ui.canvas.DevotionalIllustration
import com.sertumrosarum.ui.canvas.FormationPainting
import com.sertumrosarum.ui.canvas.LocalBotanicalArtwork
import com.sertumrosarum.ui.canvas.lessonIllustration
import com.sertumrosarum.ui.canvas.paintBotanical
import com.sertumrosarum.ui.components.PageWelcome
import com.sertumrosarum.ui.components.SacredBottomSheet
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredChips
import com.sertumrosarum.ui.components.SacredDivider
import com.sertumrosarum.ui.components.SacredEyebrow
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredIconButton
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredSecondaryButton
import com.sertumrosarum.ui.components.SacredTabs
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SacredTextLink
import com.sertumrosarum.ui.components.SacredToggleRow
import com.sertumrosarum.ui.components.SanctuaryPage
import com.sertumrosarum.ui.theme.SertumTheme
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val pt = Locale.forLanguageTag("pt-BR")
private fun date(time: Long) = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate()
private fun stamp(time: Long) = date(time).format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", pt))
private fun PrayerSessionEntity.day() = date(finishedAt ?: startedAt)
private fun PrayerSessionEntity.title() = PrayerGuide.entries.find { it.name == guide }?.title ?: "Coroa oferecida"
private fun crowns(count: Int) = "$count ${if (count == 1) "coroa" else "coroas"}"

private enum class GardenPlace(val label: String) { INTENTIONS("Intenções"), CROWNS("Coroas"), JOURNAL("Diário"), KEPT("Guardados") }

/** Sentinel for a sheet that writes something new rather than tending what exists. */
private const val NEW = ""

@Composable
fun GardenScreen(model: GardenViewModel? = null, state: GardenState = GardenState(), onPrayer: (String) -> Unit = {}, onLesson: (String?) -> Unit = {}, onResume: () -> Unit = {}) {
    var place by rememberSaveable { mutableStateOf(GardenPlace.INTENTIONS) }
    var intentionSheet by rememberSaveable { mutableStateOf<String?>(null) }
    var noteSheet by rememberSaveable { mutableStateOf<String?>(null) }
    var noteSession by rememberSaveable { mutableStateOf<String?>(null) }
    var sessionId by rememberSaveable { mutableStateOf<String?>(null) }
    Box(Modifier.fillMaxSize()) {
        SanctuaryPage("Meu Jardim", "garden") {
            FormationPainting(DevotionalIllustration.PERSONAL_GARDEN, "garden-hero", compact = true)
            PageWelcome("O que cria raízes em você.", "Suas intenções, suas coroas e o que você descobre pelo caminho.", Modifier.padding(top = 6.dp))
            SacredTabs(GardenPlace.entries, place, { it.label }, { place = it }, Modifier.padding(top = 22.dp), tag = { "garden-${it.label}" })
            Column(Modifier.fillMaxWidth().padding(horizontal = SertumTheme.spacing.page).padding(top = 22.dp, bottom = 44.dp)) {
                when (place) {
                    GardenPlace.INTENTIONS -> IntentionsPlace(state) { intentionSheet = it }
                    GardenPlace.CROWNS -> CrownsPlace(state, onResume) { sessionId = it }
                    GardenPlace.JOURNAL -> JournalPlace(state, onLesson, { noteSession = null; noteSheet = NEW }) { noteSession = it.sessionId; noteSheet = it.id }
                    GardenPlace.KEPT -> KeptPlace(state, model, onPrayer, onLesson)
                }
                SacredText("Guardado neste aparelho. Sem pontuações nem sequências de dias.", Modifier.fillMaxWidth().padding(top = 36.dp), SertumTheme.typography.micro.copy(fontWeight = FontWeight.Normal), SertumTheme.colors.muted, TextAlign.Center)
            }
        }

        val tending = state.intentions.find { it.id == intentionSheet }
        SacredBottomSheet(intentionSheet != null, { intentionSheet = null }, if (tending == null) "Nova intenção" else "Cuidar desta intenção", tag = "intention-sheet") {
            IntentionEditor(tending,
                onSave = { title, detail, cat, thanks, answer -> model?.saveIntention(title, detail, cat, tending, thanks, answer); intentionSheet = null },
                onCancel = { intentionSheet = null },
                onArchive = tending?.let { { model?.archive(it); intentionSheet = null } })
        }

        val note = state.journal.find { it.id == noteSheet }
        SacredBottomSheet(noteSheet != null, { noteSheet = null }, if (note == null) "Nova reflexão" else "Sua reflexão", tag = "journal-sheet") {
            JournalEditor(note, noteSession?.let { id -> state.sessions.find { it.id == id } },
                save = { text -> model?.journal(text, noteSession, note); noteSheet = null },
                delete = note?.let { { model?.deleteJournal(it.id); noteSheet = null } },
                cancel = { noteSheet = null })
        }

        val session = state.sessions.find { it.id == sessionId }
        SacredBottomSheet(session != null, { sessionId = null }, "Coroa oferecida", tag = "crown-detail") {
            if (session != null) CrownDetail(session, state) {
                noteSession = session.id; sessionId = null; place = GardenPlace.JOURNAL; noteSheet = NEW
            }
        }
    }
}

/* ---------- Intenções ---------- */

@Composable
private fun IntentionsPlace(state: GardenState, open: (String) -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    var filter by rememberSaveable { mutableStateOf("all") }
    val current = state.intentions.filter { !it.archived }
    val archived = state.intentions.filter { it.archived }
    val used = intentionCategories.filter { cat -> current.any { it.category == cat } }
    // Filters appear only when they can tell intentions apart.
    val filters = buildList { add("all"); if (used.size >= 2) addAll(used); if (archived.isNotEmpty()) add("archived") }
    val chosen = filter.takeIf { it in filters } ?: "all"
    SacredPrimaryButton("Nova intenção", { open(NEW) }, tag = "new-intention", compact = true)
    if (filters.size > 1) SacredChips(filters, chosen, { when (it) { "all" -> "Todas"; "archived" -> "Arquivadas"; else -> it } }, { filter = it },
        Modifier.padding(top = 14.dp), tag = { "filter-$it" }, bleed = SertumTheme.spacing.page)
    val shown = when (chosen) { "all" -> current; "archived" -> archived; else -> current.filter { it.category == chosen } }
    if (state.intentions.isEmpty()) {
        GardenEmpty(DevotionalIllustration.INTENTIONS, "Um espaço para confiar.", "Escreva por quem ou pelo que deseja rezar. Você também pode começar a oração sem escolher uma intenção.")
        return
    }
    SacredText(
        when (chosen) { "archived" -> "${shown.size} ${if (shown.size == 1) "intenção arquivada" else "intenções arquivadas"}"; else -> "${shown.size} ${if (shown.size == 1) "intenção" else "intenções"} no coração" },
        Modifier.padding(top = 18.dp, bottom = 4.dp), type.micro, colors.secondary,
    )
    if (shown.isEmpty()) SacredText("Nenhuma intenção aqui por enquanto.", Modifier.padding(top = 8.dp), type.body, colors.secondary)
    shown.forEach { intention ->
        val count = state.offerings.count { link -> link.intentionId == intention.id && state.sessions.any { it.id == link.sessionId && it.status == "COMPLETED" } }
        IntentionCard(intention, count, Modifier.padding(top = 10.dp)) { open(intention.id) }
    }
}

/** An intention as a page in the garden: its kind, its name, what you keep with it and how often it was offered. */
@Composable
private fun IntentionCard(intention: IntentionEntity, offered: Int, modifier: Modifier = Modifier, onOpen: () -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SacredCard(modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Cuidar desta intenção", onClick = onOpen).testTag("intention-${intention.id}").padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val kind = if (intention.thanksgiving && intention.category != "Ação de graças") "${intention.category} · Em ação de graças" else intention.category
                SacredEyebrow(kind, Modifier.weight(1f), if (intention.thanksgiving) colors.gold else colors.accentInk)
                SacredIcon(SacredIcon.PEN, Modifier.size(16.dp), colors.muted)
            }
            SacredText(intention.title, Modifier.padding(top = 6.dp), type.section)
            if (intention.description.isNotBlank()) SacredText(intention.description, Modifier.padding(top = 4.dp), type.body, colors.secondary, maxLines = 3)
            if (intention.answeredNote.isNotBlank()) SacredText(intention.answeredNote, Modifier.padding(top = 8.dp), type.whisper, colors.gold, maxLines = 3)
            SacredText(if (offered == 0) "Ainda não oferecida numa coroa" else "Oferecida em ${crowns(offered)}", Modifier.padding(top = 12.dp),
                type.micro.copy(fontWeight = FontWeight.Medium), if (offered == 0) colors.muted else colors.secondary)
        }
    }
}

@Composable
fun IntentionEditor(existing: IntentionEntity?, onSave: (String, String, String, Boolean, String) -> Unit, onCancel: () -> Unit, onArchive: (() -> Unit)? = null) {
    var title by rememberSaveable(existing?.id) { mutableStateOf(existing?.title.orEmpty()) }
    var detail by rememberSaveable(existing?.id) { mutableStateOf(existing?.description.orEmpty()) }
    var category by rememberSaveable(existing?.id) { mutableStateOf(existing?.category ?: intentionCategories.first()) }
    var thanks by rememberSaveable(existing?.id) { mutableStateOf(existing?.thanksgiving ?: false) }
    var answer by rememberSaveable(existing?.id) { mutableStateOf(existing?.answeredNote.orEmpty()) }
    var attempted by rememberSaveable { mutableStateOf(false) }
    val colors = SertumTheme.colors
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FormationPainting(if (thanks) DevotionalIllustration.TREASURY_THANKSGIVING else DevotionalIllustration.INTENTIONS, "intention-editor-artwork", compact = true)
        SacredText(if (existing == null) "Uma intenção para confiar" else "Cuidar desta intenção", Modifier.semantics { heading() }, SertumTheme.typography.section)
        GardenField("Por quem ou pelo que você reza?", title, { title = it }, "intention-title", limit = 120)
        if (attempted && title.isBlank()) SacredText("Escreva um título para guardar a intenção.", style = SertumTheme.typography.micro, color = colors.ruby)
        GardenField("O que deseja guardar (opcional)", detail, { detail = it }, "intention-description", multiline = true)
        SacredText("Categoria", Modifier.padding(top = 6.dp), SertumTheme.typography.label, colors.secondary)
        SacredChips(intentionCategories, category, { it }, { category = it }, tag = { "edit-category-$it" }, bleed = SertumTheme.spacing.page)
        SacredToggleRow("Em ação de graças", "Para agradecer uma graça já recebida.", thanks, { thanks = !thanks }, tag = "intention-thanksgiving")
        if (thanks) GardenField("Uma graça ou agradecimento (opcional)", answer, { answer = it }, "intention-answer", multiline = true)
        SacredPrimaryButton("Guardar intenção", { attempted = true; if (title.isNotBlank()) onSave(title, detail, category, thanks, answer) }, Modifier.padding(top = 10.dp), tag = "save-intention")
        if (existing != null && onArchive != null) SacredSecondaryButton(if (existing.archived) "Restaurar intenção" else "Arquivar intenção", onArchive, Modifier.fillMaxWidth(), "archive-${existing.id}")
        SacredTextLink("Cancelar", onCancel, "cancel-intention", Modifier.align(Alignment.CenterHorizontally))
    }
}

/* ---------- Coroas ---------- */

@Composable
private fun CrownsPlace(state: GardenState, onResume: () -> Unit, open: (String) -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    var monthValue by rememberSaveable { mutableStateOf(YearMonth.now().toString()) }
    var day by rememberSaveable { mutableStateOf<String?>(null) }
    val month = YearMonth.parse(monthValue)
    val offered = state.sessions.filter { it.status == "COMPLETED" }
    val inMonth = offered.filter { YearMonth.from(it.day()) == month }

    state.sessions.find { it.status == "ACTIVE" }?.let {
        SacredCard(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Row(Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onResume).testTag("garden-resume").padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    SacredEyebrow("Em andamento")
                    SacredText("Sua oração espera por você", Modifier.padding(top = 4.dp), type.section.copy(fontSize = 18.sp, lineHeight = 24.sp))
                }
                SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), colors.muted)
            }
        }
    }

    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        SacredIconButton(SacredIcon.CHEVRON_BACK, "Mês anterior", { monthValue = month.minusMonths(1).toString(); day = null }, Modifier.testTag("previous-month"))
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            SacredText(month.format(DateTimeFormatter.ofPattern("MMMM 'de' yyyy", pt)).replaceFirstChar { it.uppercaseChar() }, Modifier.semantics { heading() }, type.section, align = TextAlign.Center)
            SacredText(if (inMonth.isEmpty()) "Nenhuma coroa ainda" else "${crowns(inMonth.size)} · ${inMonth.sumOf { it.groupCount }} mistérios ou grupos",
                Modifier.padding(top = 2.dp), type.micro.copy(fontWeight = FontWeight.Medium), colors.secondary, TextAlign.Center)
        }
        SacredIconButton(SacredIcon.CHEVRON, "Próximo mês", { monthValue = month.plusMonths(1).toString(); day = null }, Modifier.testTag("next-month"), enabled = month < YearMonth.now())
    }
    MonthCalendar(month, inMonth, day, Modifier.padding(top = 16.dp)) { picked -> day = if (day == picked.toString()) null else picked.toString() }

    val listed = inMonth.filter { day == null || it.day().toString() == day }.sortedByDescending { it.finishedAt ?: it.startedAt }
    Row(Modifier.fillMaxWidth().padding(top = 26.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        SacredText(day?.let { "Em " + LocalDate.parse(it).format(DateTimeFormatter.ofPattern("d 'de' MMMM", pt)) } ?: "Neste mês", Modifier.weight(1f).semantics { heading() }, type.label)
        if (day != null) SacredTextLink("Ver o mês", { day = null }, "garden-whole-month")
    }
    if (listed.isEmpty()) {
        SacredText(if (day != null) "Nenhuma coroa neste dia." else "As coroas oferecidas neste mês aparecerão aqui. Sem metas nem dias perdidos.", style = type.body, color = colors.secondary)
        return
    }
    SacredCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(vertical = 4.dp)) {
            listed.forEachIndexed { index, session ->
                if (index > 0) SacredDivider(Modifier.padding(start = 64.dp, end = 18.dp))
                CrownRow(session, state.offerings.count { it.sessionId == session.id }) { open(session.id) }
            }
        }
    }
}

/** A month as it hangs on the wall: weekdays above, and a painted rose on each day a crown was offered. */
@Composable
private fun MonthCalendar(month: YearMonth, sessions: List<PrayerSessionEntity>, selected: String?, modifier: Modifier = Modifier, onDay: (LocalDate) -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val art = LocalBotanicalArtwork.current
    val today = LocalDate.now()
    val lead = month.atDay(1).dayOfWeek.value % 7
    val cells = List<LocalDate?>(lead) { null } + (1..month.lengthOfMonth()).map { month.atDay(it) }
    SacredCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 14.dp)) {
            Row(Modifier.fillMaxWidth().clearAndSetSemantics {}) {
                listOf("D", "S", "T", "Q", "Q", "S", "S").forEach { SacredText(it, Modifier.weight(1f), type.micro, colors.muted, TextAlign.Center) }
            }
            cells.chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth().padding(top = 6.dp)) {
                    week.forEach { d ->
                        if (d == null) { Spacer(Modifier.weight(1f)); return@forEach }
                        val count = sessions.count { it.day() == d }
                        val chosen = selected == d.toString()
                        Column(
                            Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).clickable(role = Role.Button) { onDay(d) }
                                .semantics(mergeDescendants = true) {
                                    this.selected = chosen
                                    contentDescription = "${d.format(DateTimeFormatter.ofPattern("d 'de' MMMM", pt))}: ${if (count == 0) "nenhuma coroa" else crowns(count)}"
                                }
                                .testTag("garden-day-$d").heightIn(min = 52.dp).padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                Modifier.size(30.dp)
                                    .background(if (chosen) colors.marian.copy(alpha = .22f) else Color.Transparent, CircleShape)
                                    .then(if (d == today) Modifier.border(1.dp, colors.accentInk.copy(alpha = .7f), CircleShape) else Modifier),
                                contentAlignment = Alignment.Center,
                            ) {
                                SacredText(d.dayOfMonth.toString(), style = type.micro.copy(fontSize = 13.sp, fontWeight = if (d == today || chosen) FontWeight.Bold else FontWeight.Medium),
                                    color = if (d > today) colors.muted else colors.ink, align = TextAlign.Center)
                            }
                            if (count > 0) Canvas(Modifier.size(16.dp)) { paintBotanical(art.rose, center, size.minDimension) } else Spacer(Modifier.height(16.dp))
                        }
                    }
                    repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun CrownRow(session: PrayerSessionEntity, intentions: Int, onOpen: () -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val art = LocalBotanicalArtwork.current
    Row(
        Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Ver esta coroa", onClick = onOpen).testTag("crown-${session.id}")
            .heightIn(min = 72.dp).padding(start = 16.dp, end = 18.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Canvas(Modifier.size(32.dp)) { paintBotanical(art.rose, center, size.minDimension) }
        Column(Modifier.weight(1f)) {
            SacredText(session.title(), style = type.label.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold))
            val detail = listOfNotNull(
                session.day().format(DateTimeFormatter.ofPattern("d 'de' MMMM", pt)),
                if (session.mode == "SOLO") "A sós" else "Em grupo",
                if (intentions > 0) "$intentions ${if (intentions == 1) "intenção" else "intenções"}" else null,
            ).joinToString(" · ")
            SacredText(detail, Modifier.padding(top = 2.dp), type.body.copy(fontSize = 14.sp, lineHeight = 20.sp), colors.secondary)
        }
        SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), colors.muted)
    }
}

@Composable
private fun ColumnScope.CrownDetail(session: PrayerSessionEntity, state: GardenState, reflect: () -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val guide = PrayerGuide.entries.find { it.name == session.guide }
    FormationPainting(DevotionalIllustration.CONTEMPLATIVE_PRAYER, "crown-detail-artwork", compact = true)
    SacredEyebrow(stamp(session.finishedAt ?: session.startedAt))
    SacredText(session.title(), Modifier.padding(top = 6.dp), type.heading)
    val structure = if (guide?.script?.isRosary == true) MysterySet.entries.find { it.name == session.mysterySet }?.label.orEmpty() else guide?.shape?.badge.orEmpty()
    val seconds = ((session.finishedAt ?: session.startedAt) - session.startedAt).coerceAtLeast(0) / 1000
    SacredText("$structure · ${if (session.mode == "SOLO") "A sós" else "Em grupo"}", Modifier.padding(top = 10.dp), type.body)
    SacredText("${seconds / 60} min ${seconds % 60} s · ${session.groupCount} grupos", Modifier.padding(top = 2.dp), type.body, colors.secondary)
    val offerings = state.offerings.filter { it.sessionId == session.id }
    SacredCard(Modifier.padding(top = 20.dp).fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            SacredEyebrow(if (offerings.isEmpty()) "Oferecida" else "Intenções oferecidas")
            if (offerings.isEmpty()) SacredText("No silêncio do seu coração.", Modifier.padding(top = 6.dp), type.whisper)
            offerings.forEach { SacredText((if (it.thanksgiving) "Em ação de graças: " else "Por ") + it.title, Modifier.padding(top = 6.dp), type.whisper) }
        }
    }
    SacredSecondaryButton("Escrever uma reflexão sobre esta coroa", reflect, Modifier.padding(top = 18.dp).fillMaxWidth(), "crown-reflect", icon = SacredIcon.PEN)
}

/* ---------- Diário ---------- */

@Composable
private fun JournalPlace(state: GardenState, onLesson: (String?) -> Unit, write: () -> Unit, open: (JournalEntry) -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SacredPrimaryButton("Escrever uma reflexão", write, tag = "new-reflection", compact = true)
    // Notes written here and discoveries kept at the end of a lesson, newest first, on one page.
    val entries = state.journal.map { JournalItem.Note(it) } +
        state.learning.filter { it.reflection.isNotBlank() }.mapNotNull { progress -> FormationCatalog.lesson(progress.lessonId)?.let { JournalItem.Discovery(it, progress.reflection, progress.openedAt) } }
    if (entries.isEmpty()) {
        GardenEmpty(DevotionalIllustration.JOURNAL, "Uma palavra pode ficar.", "Registre uma descoberta, um agradecimento ou algo que deseja levar à oração.")
        return
    }
    entries.sortedByDescending { it.time }.forEach { entry ->
        when (entry) {
            is JournalItem.Note -> with(entry.note) {
                JournalCard(stamp(createdAt) + if (sessionId != null) " · Depois de uma coroa" else "", text, "journal-$id", Modifier.padding(top = 12.dp)) { open(this) }
            }
            is JournalItem.Discovery -> JournalCard("Da lição · ${entry.lesson.title}", entry.text, "journal-lesson-${entry.lesson.id}", Modifier.padding(top = 12.dp), colors.gold) { onLesson(entry.lesson.id) }
        }
    }
    SacredText("As descobertas das lições também aparecem aqui.", Modifier.padding(top = 14.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.muted)
}

/** What the Diário shows: notes written here, and discoveries kept at the end of a lesson. */
private sealed interface JournalItem {
    val time: Long
    class Note(val note: JournalEntry) : JournalItem { override val time get() = note.createdAt }
    class Discovery(val lesson: FormationLesson, val text: String, override val time: Long) : JournalItem
}

@Composable
private fun JournalCard(eyebrow: String, text: String, tag: String, modifier: Modifier = Modifier, tone: Color = SertumTheme.colors.accentInk, onOpen: () -> Unit) {
    SacredCard(modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onOpen).testTag(tag).padding(20.dp)) {
            SacredEyebrow(eyebrow, color = tone)
            SacredText(text, Modifier.padding(top = 8.dp), SertumTheme.typography.whisper, maxLines = 8)
        }
    }
}

@Composable
private fun ColumnScope.JournalEditor(existing: JournalEntry?, session: PrayerSessionEntity?, save: (String) -> Unit, delete: (() -> Unit)?, cancel: () -> Unit) {
    var text by rememberSaveable(existing?.id) { mutableStateOf(existing?.text.orEmpty()) }
    var attempted by rememberSaveable { mutableStateOf(false) }
    var confirm by rememberSaveable(existing?.id) { mutableStateOf(false) }
    val colors = SertumTheme.colors
    FormationPainting(DevotionalIllustration.JOURNAL, "journal-editor-artwork", compact = true)
    SacredText(if (existing == null) "O que deseja levar consigo?" else "Sua reflexão", Modifier.semantics { heading() }, SertumTheme.typography.section)
    if (session != null) SacredText("Sobre a coroa de ${session.day().format(DateTimeFormatter.ofPattern("d 'de' MMMM", pt))}", Modifier.padding(top = 4.dp), SertumTheme.typography.micro, colors.accentInk)
    GardenField(if (existing == null) "Uma descoberta, um agradecimento, um pedido" else stamp(existing.createdAt), text, { text = it }, "journal-text", multiline = true, limit = 8000)
    if (attempted && text.isBlank()) SacredText("Escreva uma palavra antes de guardar.", style = SertumTheme.typography.micro, color = colors.ruby)
    SacredPrimaryButton("Guardar reflexão", { attempted = true; if (text.isNotBlank()) save(text) }, Modifier.padding(top = 10.dp), tag = "save-reflection")
    if (existing != null && delete != null) {
        if (confirm) Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SacredSecondaryButton("Manter", { confirm = false }, Modifier.weight(1f), "keep-note-${existing.id}")
            SacredSecondaryButton("Excluir de vez", delete, Modifier.weight(1f), "confirm-delete-note-${existing.id}")
        } else SacredSecondaryButton("Excluir reflexão", { confirm = true }, Modifier.padding(top = 10.dp).fillMaxWidth(), "delete-note-${existing.id}")
    }
    SacredTextLink("Cancelar", cancel, "cancel-reflection", Modifier.align(Alignment.CenterHorizontally))
}

/* ---------- Guardados ---------- */

@Composable
private fun KeptPlace(state: GardenState, model: GardenViewModel?, onPrayer: (String) -> Unit, onLesson: (String?) -> Unit) {
    val prayers = state.bookmarks.filter { it.kind == "prayer" }.mapNotNull { item -> TreasuryCatalog.find(item.itemId)?.let { item.itemId to it.title } }
    val lessons = state.bookmarks.filter { it.kind == "lesson" }.mapNotNull { item -> FormationCatalog.lesson(item.itemId) }
    if (prayers.isEmpty() && lessons.isEmpty()) {
        GardenEmpty(DevotionalIllustration.BOOKMARKS, "Volte ao que tocou seu coração.", "Guarde uma oração no Tesouro ou uma lição da Catequese, e ela esperará por você aqui.")
        return
    }
    if (prayers.isNotEmpty()) KeptShelf("Orações", prayers.map { (id, title) -> Kept(id, title, "Do Tesouro", DevotionalIllustration.BOOKMARKS) { onPrayer(id) } }) { model?.bookmark("prayer", it) }
    if (lessons.isNotEmpty()) KeptShelf("Lições", lessons.map { lesson ->
        val chapter = chapterOf(lesson)
        Kept(lesson.id, lesson.title, "Lição ${numberOf(lesson)} · ${chapter.verb}", lessonIllustration(lesson.id)) { onLesson(lesson.id) }
    }, Modifier.padding(top = if (prayers.isEmpty()) 0.dp else 26.dp)) { model?.bookmark("lesson", it) }
}

private class Kept(val id: String, val title: String, val detail: String, val illustration: DevotionalIllustration, val open: () -> Unit)

@Composable
private fun KeptShelf(name: String, items: List<Kept>, modifier: Modifier = Modifier, remove: (String) -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SacredEyebrow(name, modifier.padding(bottom = 10.dp).semantics { heading() })
    SacredCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(vertical = 4.dp)) {
            items.forEachIndexed { index, item ->
                if (index > 0) SacredDivider(Modifier.padding(start = 80.dp, end = 18.dp))
                Row(Modifier.fillMaxWidth().padding(end = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        Modifier.weight(1f).clickable(role = Role.Button, onClick = item.open).testTag("kept-${item.id}").heightIn(min = 72.dp).padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        DevotionalArtwork(item.illustration, Modifier.size(48.dp))
                        Column(Modifier.weight(1f)) {
                            SacredText(item.title, style = type.label.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold))
                            SacredText(item.detail, Modifier.padding(top = 2.dp), type.body.copy(fontSize = 14.sp, lineHeight = 20.sp), colors.secondary)
                        }
                    }
                    Box(
                        Modifier.size(48.dp).clip(CircleShape).clickable(role = Role.Button) { remove(item.id) }
                            .semantics { contentDescription = "Remover dos guardados" }.testTag("remove-${item.id}"),
                        contentAlignment = Alignment.Center,
                    ) { SacredIcon(SacredIcon.BOOKMARK, Modifier.size(18.dp), colors.accentInk) }
                }
            }
        }
    }
}

/* ---------- Shared ---------- */

/** An empty bed: its painting, a title and one line about what will grow here. */
@Composable
private fun GardenEmpty(illustration: DevotionalIllustration, title: String, detail: String) {
    val colors = SertumTheme.colors
    Column(Modifier.fillMaxWidth().padding(top = 22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        DevotionalArtwork(illustration, Modifier.size(132.dp))
        SacredText(title, Modifier.padding(top = 10.dp), SertumTheme.typography.section, align = TextAlign.Center)
        SacredText(detail, Modifier.padding(top = 6.dp, start = 12.dp, end = 12.dp), SertumTheme.typography.body, colors.secondary, TextAlign.Center)
    }
}

@Composable
fun IntentionPicker(model: GardenViewModel, state: GardenState, selected: Set<String>, close: () -> Unit) {
    var create by rememberSaveable { mutableStateOf(false) }
    if (create) IntentionEditor(null, { title, detail, category, thanks, answer -> model.saveIntention(title, detail, category, thanksgiving = thanks, answer = answer, select = true); create = false }, { create = false })
    else {
        FormationPainting(DevotionalIllustration.INTENTIONS, "intention-picker-artwork", compact = true)
        SacredText("Escolha intenções ou comece no silêncio do coração. A escolha vale para a próxima coroa.", style = SertumTheme.typography.body)
        state.intentions.filter { !it.archived }.forEach { intention -> QuietAction((if (intention.id in selected) "✓ " else "") + intention.title + if (intention.thanksgiving) " · Ação de graças" else "", { model.toggleIntention(intention.id) }, "pick-${intention.id}", intention.id in selected) }
        QuietAction("Escrever uma nova intenção", { create = true }, "picker-new")
        SacredPrimaryButton("Pronto", close, tag = "picker-done")
    }
}
