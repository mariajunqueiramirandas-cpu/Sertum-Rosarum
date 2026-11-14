package com.sertumrosarum.feature.formation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sertumrosarum.domain.formation.*
import com.sertumrosarum.feature.garden.*
import com.sertumrosarum.ui.components.*
import com.sertumrosarum.ui.theme.SertumTheme

@Composable
fun FormationScreen(model: GardenViewModel?, state: GardenState, initialLesson: String?, onBack: () -> Unit, onPray: () -> Unit) {
    var place by rememberSaveable { mutableStateOf("Aprender") }
    var lessonId by rememberSaveable(initialLesson) { mutableStateOf(initialLesson) }
    var query by rememberSaveable { mutableStateOf("") }
    var pillar by rememberSaveable { mutableStateOf<String?>(null) }
    var journeyId by rememberSaveable { mutableStateOf<String?>(null) }
    val lesson = FormationCatalog.lesson(lessonId)
    val journey = FormationCatalog.journeys.find { it.id == journeyId }
    val back = { if (lessonId != null) lessonId = null else if (journeyId != null) journeyId = null else onBack() }
    BackHandler { back() }
    LaunchedEffect(lessonId) { lessonId?.let { model?.lesson(it) } }
    SanctuaryPage("Catequese", "formation", onBack = back) {
        Column(Modifier.padding(horizontal = SertumTheme.spacing.page).padding(top = 18.dp, bottom = 48.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            if (lesson != null) {
                val progress = state.learning.find { it.lessonId == lesson.id }
                SacredText(lesson.pillar, style = SertumTheme.typography.micro, color = SertumTheme.colors.accentInk)
                SacredText(lesson.title, style = SertumTheme.typography.display)
                SacredText(lesson.objective, style = SertumTheme.typography.whisper, color = SertumTheme.colors.secondary)
                QuietAction(if (state.bookmarks.any { it.kind == "lesson" && it.itemId == lesson.id }) "✓ Lição guardada" else "Guardar esta lição", { model?.bookmark("lesson", lesson.id) }, "bookmark-lesson")
                SacredText("Para compreender", style = SertumTheme.typography.section)
                SacredText(lesson.teaching, style = SertumTheme.typography.body)
                PersonalCard("Na Escritura e no Catecismo", "${lesson.scripture}\n${lesson.catechism}")
                SacredText("Uma pausa para lembrar", style = SertumTheme.typography.section)
                SacredText(lesson.question, style = SertumTheme.typography.body)
                var answer by rememberSaveable(lesson.id) { mutableStateOf<Int?>(null) }
                val order = if (lesson.id.hashCode() % 2 == 0) lesson.choices.indices.toList() else lesson.choices.indices.reversed().toList()
                order.forEach { index -> QuietAction(lesson.choices[index], { answer = index }, "lesson-answer-$index", answer == index) }
                if (answer != null) PersonalCard(if (answer == lesson.correct) "Isso mesmo." else "Vamos olhar de novo, com calma.", lesson.explanation, tag = "lesson-answer-explanation")
                PersonalCard("Levar à vida", lesson.practice) {
                    QuietAction(if (progress?.practiceDone == true) "✓ Gesto vivido" else "Marcar quando tiver vivido este gesto", { model?.lesson(lesson.id, practice = progress?.practiceDone != true) }, "lesson-practice", progress?.practiceDone == true)
                }
                SacredText(lesson.prayer, style = SertumTheme.typography.whisper, color = SertumTheme.colors.accentInk)
                var reflection by rememberSaveable(lesson.id) { mutableStateOf(progress?.reflection.orEmpty()) }
                var saved by rememberSaveable(lesson.id) { mutableStateOf(false) }
                GardenField("Uma descoberta para guardar (opcional)", reflection, { reflection = it; saved = false }, "lesson-reflection", multiline = true, limit = 8000)
                QuietAction(if (saved) "Reflexão guardada" else "Guardar reflexão", { model?.lesson(lesson.id, reflection = reflection); saved = true }, "save-lesson-reflection")
                if (answer == lesson.correct || progress?.completedAt != null) SacredPrimaryButton(if (progress?.completedAt != null) "Lição percorrida · continuar" else "Concluir e continuar", {
                    model?.lesson(lesson.id, reflection = reflection, complete = true)
                    val index = FormationCatalog.lessons.indexOfFirst { it.id == lesson.id }
                    lessonId = FormationCatalog.lessons.getOrNull(index + 1)?.id
                }, tag = "complete-lesson")
                else SacredText("Escolha uma resposta acima para concluir. Você pode tentar de novo, sem pontuação.", style = SertumTheme.typography.micro, color = SertumTheme.colors.secondary)
                QuietAction("Levar esta intenção à oração", onPray, "lesson-pray")
                EditorialNote(lesson.id, "Revisão ${lesson.revision} · ${lesson.editorialDate}", model)
            } else if (journey != null) {
                SacredText(journey.title, style = SertumTheme.typography.display)
                SacredText(journey.introduction, style = SertumTheme.typography.whisper)
                journey.steps.forEachIndexed { index, step -> PersonalCard("${index + 1}. Um passo possível", step) }
                SacredText(journey.references, style = SertumTheme.typography.micro, color = SertumTheme.colors.secondary)
                QuietAction("Ir à oração", onPray, "journey-pray")
                EditorialNote("journey-${journey.id}", "Revisão 1 · 13/11/2026", model)
            } else {
                SacredText("Aprender, rezar e viver.", style = SertumTheme.typography.display)
                SacredText("Um caminho inicial pelos quatro pilares da fé. Leituras de cerca de cinco minutos, no seu ritmo.", style = SertumTheme.typography.body, color = SertumTheme.colors.secondary)
                Row(Modifier.horizontalScroll(rememberScrollState())) { listOf("Aprender", "Perguntas", "Na vida").forEach { tab -> Box(Modifier.widthIn(min = 115.dp)) { QuietAction(tab, { place = tab }, "formation-$tab", tab == place) } } }
                GardenField(if (place == "Perguntas") "Que pergunta você traz?" else "Buscar um tema", query, { query = it }, "formation-search", limit = 300)
                when (place) {
                    "Aprender" -> {
                        if (query.isBlank() && pillar == null) {
                            val next = state.learning.firstOrNull { it.completedAt == null }?.lessonId?.let(FormationCatalog::lesson)
                                ?: FormationCatalog.lessons.firstOrNull { item -> state.learning.none { it.lessonId == item.id && it.completedAt != null } }
                            if (next != null) PersonalCard("Seu próximo passo", next.title, { lessonId = next.id }, "continue-learning")
                        }
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            Box(Modifier.widthIn(min = 90.dp)) { QuietAction("Todos", { pillar = null }, "all-pillars", pillar == null) }
                            FormationCatalog.pillars.forEachIndexed { index, name -> Box(Modifier.widthIn(min = 98.dp)) {
                                QuietAction(listOf("Crer", "Celebrar", "Viver", "Rezar")[index], { pillar = name }, "pillar-$name", pillar == name)
                            } }
                        }
                        pillar?.let { SacredText(it, style = SertumTheme.typography.whisper, color = SertumTheme.colors.secondary) }
                        val lessons = FormationCatalog.lessons.filter { (pillar == null || it.pillar == pillar) && FormationCatalog.matches(query, "${it.title} ${it.teaching} ${it.keywords}") > 0 }
                        if (lessons.isEmpty()) PersonalCard("Ainda não encontramos esse tema.", "Tente uma palavra como graça, oração, Missa ou perdão. As perguntas também podem ajudar.")
                        lessons.forEach { item ->
                            val complete = state.learning.any { it.lessonId == item.id && it.completedAt != null }
                            PersonalCard((if (complete) "✓ " else "") + item.title, "${item.pillar}\n${item.objective}", { lessonId = item.id }, "lesson-${item.id}")
                        }
                    }
                    "Perguntas" -> {
                        val answers = FormationCatalog.searchQuestions(query)
                        SacredText("${answers.size} perguntas relacionadas", style = SertumTheme.typography.micro)
                        if (answers.isEmpty()) PersonalCard("Sua pergunta merece atenção.", "Tente outro termo ou converse com um catequista da sua comunidade. Esta biblioteca não responde a todas as situações pessoais.")
                        answers.forEach { question ->
                            var open by rememberSaveable(question.id) { mutableStateOf(false) }
                            PersonalCard(question.question, if (open) question.answer else "Toque para compreender", { open = !open }, "question-${question.id}") {
                                if (open) {
                                    SacredText(question.references, Modifier.padding(top = 12.dp), SertumTheme.typography.micro, SertumTheme.colors.secondary)
                                    EditorialNote("question-${question.id}", "Revisão 1 · 13/11/2026", model)
                                }
                            }
                        }
                    }
                    "Na vida" -> {
                        FormationCatalog.journeys.filter { FormationCatalog.matches(query, "${it.title} ${it.steps.joinToString(" ")}") > 0 }.forEach { item -> PersonalCard(item.title, item.introduction, { journeyId = item.id }, "journey-${item.id}") }
                        PersonalCard("O tempo da Igreja", "Advento e Natal, Quaresma e Páscoa, Pentecostes e Tempo Comum: a Igreja nos conduz pelo mistério de Cristo. Para festas e horários locais, consulte sua paróquia.", { lessonId = "year" }, "liturgical-year")
                    }
                }
                EditorialNote("formation", "Revisão 1 · 13/11/2026", model)
            }
        }
    }
}

@Composable
private fun EditorialNote(id: String, revision: String, model: GardenViewModel?) {
    var open by rememberSaveable(id) { mutableStateOf(false) }
    var text by rememberSaveable(id) { mutableStateOf("") }
    var saved by rememberSaveable(id) { mutableStateOf(false) }
    val context = LocalContext.current
    SacredText("Conteúdo catequético editorial · $revision\nReferências de apoio; explicações não são citações literais. Revisão teológica independente pendente.", Modifier.padding(top = 12.dp), SertumTheme.typography.micro, SertumTheme.colors.secondary)
    QuietAction("Sugerir uma correção", { open = !open }, "feedback-$id")
    if (open) {
        GardenField("Sua observação", text, { text = it; saved = false }, "feedback-text", multiline = true)
        QuietAction(if (saved) "Observação guardada neste aparelho" else "Guardar observação neste aparelho", { if (text.isNotBlank() && !saved) { model?.repository?.feedback(id, text); saved = true } }, "save-feedback")
        if (text.isNotBlank()) QuietAction("Compartilhar observação…", {
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).setType("text/plain").putExtra(android.content.Intent.EXTRA_TEXT, "Sertum · $id · $revision\n\n$text")
            context.startActivity(android.content.Intent.createChooser(intent, "Compartilhar observação"))
        }, "share-feedback")
    }
}
