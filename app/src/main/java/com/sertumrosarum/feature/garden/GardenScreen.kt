package com.sertumrosarum.feature.garden

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.sertumrosarum.data.garden.*
import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.domain.treasury.TreasuryCatalog
import com.sertumrosarum.feature.guides.PrayerGuide
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.ui.canvas.LocalBotanicalArtwork
import com.sertumrosarum.ui.canvas.paintBotanical
import com.sertumrosarum.ui.components.*
import com.sertumrosarum.ui.theme.SertumTheme
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale

private val pt = Locale.forLanguageTag("pt-BR")
private fun date(time: Long) = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate()
private fun stamp(time: Long) = date(time).format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", pt))

@Composable
fun GardenScreen(model: GardenViewModel? = null, state: GardenState = GardenState(), onPrayer: (String) -> Unit = {}, onLesson: (String?) -> Unit = {}, onResume: () -> Unit = {}) {
    var section by rememberSaveable { mutableStateOf("Intenções") }
    var editId by rememberSaveable { mutableStateOf<String?>(null) }
    var editor by rememberSaveable { mutableStateOf(false) }
    var sessionId by rememberSaveable { mutableStateOf<String?>(null) }
    var archived by rememberSaveable { mutableStateOf(false) }
    var category by rememberSaveable { mutableStateOf<String?>(null) }
    var monthValue by rememberSaveable { mutableStateOf(YearMonth.now().toString()) }
    var week by rememberSaveable { mutableStateOf(false) }
    var day by rememberSaveable { mutableStateOf<String?>(null) }
    var noteId by rememberSaveable { mutableStateOf<String?>(null) }
    var noteEditor by rememberSaveable { mutableStateOf(false) }
    var noteSession by rememberSaveable { mutableStateOf<String?>(null) }
    BackHandler(editor || noteEditor || sessionId != null) { editor = false; noteEditor = false; sessionId = null }
    SanctuaryPage("Meu Jardim", "garden") {
        PageWelcome("O que cria raízes em você.", "Suas intenções, orações e descobertas. Um retorno de cada vez.", Modifier.padding(top = 16.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 14.dp)) {
            listOf("Intenções", "Coroas", "Diário", "Guardados", "Caminho").forEach { name -> Box(Modifier.widthIn(min = 100.dp)) { QuietAction(name, { section = name; editor = false; noteEditor = false }, "garden-$name", section == name) } }
        }
        Column(Modifier.padding(horizontal = SertumTheme.spacing.page).padding(bottom = 44.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            when (section) {
                "Intenções" -> {
                    if (editor) IntentionEditor(state.intentions.find { it.id == editId }, onSave = { title, detail, cat, thanks, answer -> model?.saveIntention(title, detail, cat, state.intentions.find { it.id == editId }, thanks, answer); editor = false }, onCancel = { editor = false })
                    else {
                        SacredPrimaryButton("Nova intenção", { editId = null; editor = true }, tag = "new-intention")
                        QuietAction(if (archived) "Ver intenções atuais" else "Ver arquivadas", { archived = !archived }, "intention-archive-filter")
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            (listOf<String?>(null) + intentionCategories).forEach { cat -> Box(Modifier.widthIn(min = 110.dp)) { QuietAction(cat ?: "Todas", { category = cat }, "filter-${cat ?: "all"}", category == cat) } }
                        }
                        val filtered = state.intentions.filter { it.archived == archived && (category == null || it.category == category) }
                        if (filtered.isEmpty()) PersonalCard("Um espaço para confiar.", if (archived) "Nenhuma intenção arquivada neste filtro." else "Escreva por quem ou pelo que deseja rezar. Você também pode começar a oração sem escolher uma intenção.")
                        filtered.forEach { intention ->
                            val count = state.offerings.count { link -> link.intentionId == intention.id && state.sessions.any { it.id == link.sessionId && it.status == "COMPLETED" } }
                            PersonalCard(if (intention.thanksgiving) "✧ ${intention.title}" else intention.title, "${intention.category} · $count ${if (count == 1) "coroa oferecida" else "coroas oferecidas"}", tag = "intention-${intention.id}") {
                                if (intention.thanksgiving) SacredText("Em ação de graças", style = SertumTheme.typography.micro, color = SertumTheme.colors.gold)
                                if (intention.description.isNotBlank()) SacredText(intention.description, Modifier.padding(top = 10.dp), SertumTheme.typography.body)
                                if (intention.answeredNote.isNotBlank()) SacredText(intention.answeredNote, Modifier.padding(top = 10.dp), SertumTheme.typography.whisper)
                                QuietAction("Editar intenção", { editId = intention.id; editor = true }, "edit-${intention.id}")
                                QuietAction(if (intention.archived) "Restaurar intenção" else "Arquivar intenção", { model?.archive(intention) }, "archive-${intention.id}")
                            }
                        }
                    }
                }
                "Coroas" -> {
                    val month = YearMonth.parse(monthValue)
                    Row { Box(Modifier.weight(1f)) { QuietAction("‹ Mês anterior", { monthValue = month.minusMonths(1).toString(); day = null }, "previous-month") }; Box(Modifier.weight(1f)) { QuietAction("Próximo mês ›", { monthValue = month.plusMonths(1).toString(); day = null }, "next-month") } }
                    SacredText(month.format(DateTimeFormatter.ofPattern("MMMM 'de' yyyy", pt)), style = SertumTheme.typography.section)
                    QuietAction(if (week) "Mostrar mês inteiro" else "Mostrar esta semana", { week = !week; day = null }, "garden-period")
                    val offered = state.sessions.filter { it.status == "COMPLETED" }
                    val start = if (week) LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) else month.atDay(1)
                    val end = if (week) start.plusDays(6) else month.atEndOfMonth()
                    val period = offered.filter { date(it.finishedAt ?: it.startedAt) in start..end }
                    SacredText("${period.size} ${if (period.size == 1) "coroa oferecida" else "coroas oferecidas"} · ${period.sumOf { it.groupCount }} mistérios ou grupos contemplados", style = SertumTheme.typography.body, color = SertumTheme.colors.secondary)
                    val days = (0 until java.time.temporal.ChronoUnit.DAYS.between(start, end).toInt() + 1).map { start.plusDays(it.toLong()) }
                    val art = LocalBotanicalArtwork.current
                    BoxWithConstraints {
                    val columns = (maxWidth.value / 48.5f).toInt().coerceIn(1, 7)
                    Column { days.chunked(columns).forEach { row -> Row(Modifier.fillMaxWidth()) { row.forEach { d ->
                        val count = period.count { date(it.finishedAt ?: it.startedAt) == d }
                        Column(Modifier.weight(1f).clip(RoundedCornerShape(14.dp))
                            .then(if (day == d.toString()) Modifier.background(SertumTheme.colors.surface) else Modifier)
                            .clickable(role = Role.Button) { day = if (day == d.toString()) null else d.toString() }
                            .semantics { contentDescription = "${d.format(DateTimeFormatter.ofPattern("d 'de' MMMM", pt))}: $count ${if (count == 1) "coroa oferecida" else "coroas oferecidas"}" }
                            .testTag("garden-day-$d").heightIn(min = 64.dp).padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            if (count > 0) Canvas(Modifier.size(24.dp)) { paintBotanical(art.rose, center, size.minDimension) } else Spacer(Modifier.height(24.dp))
                            SacredText(d.dayOfMonth.toString(), style = SertumTheme.typography.micro, align = TextAlign.Center)
                            if (count > 1) SacredText("×$count", style = SertumTheme.typography.micro, color = SertumTheme.colors.accentInk, align = TextAlign.Center)
                        }
                    }; repeat(columns - row.size) { Spacer(Modifier.weight(1f)) } } } } }
                    if (period.isEmpty()) PersonalCard("Seu jardim recebe cada retorno.", "As coroas oferecidas neste período aparecerão aqui. Sem metas nem dias perdidos.")
                    period.filter { day == null || date(it.finishedAt ?: it.startedAt).toString() == day }.forEach { session -> PersonalCard(PrayerGuide.entries.find { it.name == session.guide }?.title ?: "Coroa oferecida", stamp(session.finishedAt ?: session.startedAt), { sessionId = session.id }, "crown-${session.id}") }
                    state.sessions.find { it.status == "ACTIVE" }?.let { PersonalCard("Uma oração em andamento", "Sua posição está guardada. Você pode continuar quando desejar.", onResume, "garden-resume") }
                }
                "Diário" -> {
                    if (noteEditor) JournalEditor(state.journal.find { it.id == noteId }, { text -> model?.journal(text, noteSession, state.journal.find { it.id == noteId }); noteEditor = false }, { noteEditor = false })
                    else {
                        SacredPrimaryButton("Guardar uma reflexão", { noteId = null; noteSession = null; noteEditor = true }, tag = "new-reflection")
                        if (state.journal.isEmpty()) PersonalCard("Uma palavra pode ficar.", "Registre uma descoberta, um agradecimento ou algo que deseja levar à oração.")
                        state.journal.forEach { note -> PersonalCard(stamp(note.createdAt), note.text, tag = "journal-${note.id}") {
                            QuietAction("Editar reflexão", { noteId = note.id; noteSession = note.sessionId; noteEditor = true }, "edit-note-${note.id}")
                            var confirm by rememberSaveable(note.id) { mutableStateOf(false) }
                            QuietAction(if (confirm) "Confirmar exclusão desta reflexão" else "Excluir reflexão", { if (confirm) model?.deleteJournal(note.id) else confirm = true }, "delete-note-${note.id}")
                            if (confirm) QuietAction("Manter reflexão", { confirm = false })
                        } }
                    }
                }
                "Guardados" -> {
                    if (state.bookmarks.isEmpty()) PersonalCard("Volte ao que tocou seu coração.", "Guarde uma oração no Tesouro ou uma lição no caminho de formação.")
                    state.bookmarks.forEach { item ->
                        val title = if (item.kind == "prayer") TreasuryCatalog.find(item.itemId)?.title else FormationCatalog.lesson(item.itemId)?.title
                        if (title != null) PersonalCard(title, if (item.kind == "prayer") "Oração guardada" else "Lição guardada", { if (item.kind == "prayer") onPrayer(item.itemId) else onLesson(item.itemId) }) { QuietAction("Remover dos guardados", { model?.bookmark(item.kind, item.itemId) }, "remove-${item.itemId}") }
                    }
                }
                "Caminho" -> {
                    val completed = state.learning.count { it.completedAt != null }
                    PersonalCard("Aprender, rezar e viver", "$completed de ${FormationCatalog.lessons.size} lições percorridas. Retome com liberdade.", { onLesson(null) }, "garden-learning")
                    state.learning.forEach { progress -> FormationCatalog.lesson(progress.lessonId)?.let { lesson -> PersonalCard(lesson.title, if (progress.completedAt != null) "Lição percorrida" else "Continuar a leitura", { onLesson(lesson.id) }) {
                        if (progress.reflection.isNotBlank()) SacredText(progress.reflection, Modifier.padding(top = 8.dp), SertumTheme.typography.whisper)
                        QuietAction(if (progress.practiceDone) "✓ Gesto levado à vida" else "Marcar gesto vivido", { model?.lesson(lesson.id, practice = !progress.practiceDone) }, "practice-${lesson.id}", progress.practiceDone)
                    } } }
                }
            }
            SacredText("Guardado neste aparelho. Sem pontuações nem sequências de dias.", Modifier.padding(top = 22.dp), SertumTheme.typography.micro, SertumTheme.colors.secondary)
        }
    }
    val session = state.sessions.find { it.id == sessionId }
    SacredBottomSheet(session != null, { sessionId = null }, "Coroa oferecida", tag = "crown-detail") {
        if (session != null) {
            SacredText(PrayerGuide.entries.find { it.name == session.guide }?.title ?: "Coroa", style = SertumTheme.typography.heading)
            SacredText(stamp(session.finishedAt ?: session.startedAt), Modifier.padding(top = 8.dp), SertumTheme.typography.body)
            val guide = PrayerGuide.entries.find { it.name == session.guide }
            val structure = if (guide?.script?.isRosary == true) MysterySet.entries.find { it.name == session.mysterySet }?.label.orEmpty() else guide?.shape?.badge.orEmpty()
            SacredText("$structure · ${if (session.mode == "SOLO") "A sós" else "Em grupo"}", Modifier.padding(top = 8.dp), SertumTheme.typography.body)
            val seconds = ((session.finishedAt ?: session.startedAt) - session.startedAt).coerceAtLeast(0) / 1000
            SacredText("${seconds / 60} min ${seconds % 60} s · ${session.groupCount} grupos", Modifier.padding(top = 8.dp), SertumTheme.typography.body)
            val offerings = state.offerings.filter { it.sessionId == session.id }
            SacredText(if (offerings.isEmpty()) "Oferecida no silêncio do seu coração." else "Intenções oferecidas", Modifier.padding(top = 18.dp), SertumTheme.typography.section)
            offerings.forEach { SacredText((if (it.thanksgiving) "Em ação de graças: " else "Por ") + it.title, Modifier.padding(top = 8.dp), SertumTheme.typography.body) }
            QuietAction("Registrar uma reflexão sobre esta coroa", { noteSession = session.id; noteId = null; noteEditor = true; section = "Diário"; sessionId = null }, "crown-reflect")
        }
    }
}

@Composable
fun IntentionEditor(existing: IntentionEntity?, onSave: (String, String, String, Boolean, String) -> Unit, onCancel: () -> Unit) {
    var title by rememberSaveable(existing?.id) { mutableStateOf(existing?.title.orEmpty()) }
    var detail by rememberSaveable(existing?.id) { mutableStateOf(existing?.description.orEmpty()) }
    var category by rememberSaveable(existing?.id) { mutableStateOf(existing?.category ?: intentionCategories.first()) }
    var thanks by rememberSaveable(existing?.id) { mutableStateOf(existing?.thanksgiving ?: false) }
    var answer by rememberSaveable(existing?.id) { mutableStateOf(existing?.answeredNote.orEmpty()) }
    var attempted by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SacredText(if (existing == null) "Uma intenção para confiar" else "Cuidar desta intenção", style = SertumTheme.typography.section)
        GardenField("Por quem ou pelo que você reza?", title, { title = it }, "intention-title", limit = 120)
        if (attempted && title.isBlank()) SacredText("Escreva um título para guardar a intenção.", style = SertumTheme.typography.body)
        GardenField("O que deseja guardar (opcional)", detail, { detail = it }, "intention-description", multiline = true)
        Row(Modifier.horizontalScroll(rememberScrollState())) { intentionCategories.forEach { cat -> Box(Modifier.widthIn(min = 115.dp)) { QuietAction(cat, { category = cat }, "edit-category-$cat", cat == category) } } }
        QuietAction(if (thanks) "✓ Em ação de graças" else "Marcar em ação de graças", { thanks = !thanks }, "intention-thanksgiving", thanks)
        if (thanks) GardenField("Uma graça ou agradecimento (opcional)", answer, { answer = it }, "intention-answer", multiline = true)
        SacredPrimaryButton("Guardar intenção", { attempted = true; if (title.isNotBlank()) onSave(title, detail, category, thanks, answer) }, tag = "save-intention")
        QuietAction("Cancelar", onCancel, "cancel-intention")
    }
}

@Composable
private fun JournalEditor(existing: JournalEntry?, save: (String) -> Unit, cancel: () -> Unit) {
    var text by rememberSaveable(existing?.id) { mutableStateOf(existing?.text.orEmpty()) }
    var attempted by rememberSaveable { mutableStateOf(false) }
    GardenField("O que deseja levar consigo?", text, { text = it }, "journal-text", multiline = true, limit = 8000)
    if (attempted && text.isBlank()) SacredText("Escreva uma palavra antes de guardar.", style = SertumTheme.typography.body)
    SacredPrimaryButton("Guardar reflexão", { attempted = true; if (text.isNotBlank()) save(text) }, tag = "save-reflection")
    QuietAction("Cancelar", cancel)
}

@Composable
fun IntentionPicker(model: GardenViewModel, state: GardenState, selected: Set<String>, close: () -> Unit) {
    var create by rememberSaveable { mutableStateOf(false) }
    if (create) IntentionEditor(null, { title, detail, category, thanks, answer -> model.saveIntention(title, detail, category, thanksgiving = thanks, answer = answer, select = true); create = false }, { create = false })
    else {
        SacredText("Escolha intenções ou comece no silêncio do coração. A escolha vale para a próxima coroa.", style = SertumTheme.typography.body)
        state.intentions.filter { !it.archived }.forEach { intention -> QuietAction((if (intention.id in selected) "✓ " else "") + intention.title + if (intention.thanksgiving) " · Ação de graças" else "", { model.toggleIntention(intention.id) }, "pick-${intention.id}", intention.id in selected) }
        QuietAction("Escrever uma nova intenção", { create = true }, "picker-new")
        SacredPrimaryButton("Pronto", close, tag = "picker-done")
    }
}
