package com.sertumrosarum.feature.formation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.domain.formation.FormationLesson
import com.sertumrosarum.feature.garden.GardenState
import com.sertumrosarum.feature.garden.GardenViewModel
import com.sertumrosarum.ui.canvas.DevotionalArtwork
import com.sertumrosarum.ui.canvas.DevotionalIllustration
import com.sertumrosarum.ui.canvas.FormationPainting
import com.sertumrosarum.ui.canvas.LocalBotanicalArtwork
import com.sertumrosarum.ui.canvas.journeyIllustration
import com.sertumrosarum.ui.canvas.lessonIllustration
import com.sertumrosarum.ui.canvas.pillarIllustration
import com.sertumrosarum.ui.canvas.questionIllustration
import com.sertumrosarum.ui.canvas.paintBotanical
import com.sertumrosarum.ui.components.FineArtCredits
import com.sertumrosarum.ui.components.PageWelcome
import com.sertumrosarum.ui.components.SacredTextLink
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredDivider
import com.sertumrosarum.ui.components.SacredEyebrow
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredSearchField
import com.sertumrosarum.ui.components.SacredTabs
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme

internal enum class FormationPlace(val label: String) { LEARN("Aprender"), QUESTIONS("Perguntas"), LIFE("Na vida") }

/** The course at a glance: where you are on the path, then the four pillars as chapters, or the questions, or the practices. */
@Composable
internal fun ColumnScope.CoursePage(
    state: GardenState, model: GardenViewModel?,
    place: FormationPlace, onPlace: (FormationPlace) -> Unit,
    query: String, onQuery: (String) -> Unit,
    openLesson: (String) -> Unit, openJourney: (String) -> Unit,
) {
    val page = SertumTheme.spacing.page
    // One frame for every painting, so changing place never moves the words beneath it.
    FormationPainting(when (place) { FormationPlace.QUESTIONS -> DevotionalIllustration.QUESTIONS; FormationPlace.LIFE -> DevotionalIllustration.CHARITY; else -> DevotionalIllustration.CATECHESIS }, "formation-hero")
    PageWelcome("Aprender, rezar e viver.", "Um caminho pelos quatro pilares da fé, em leituras de cerca de cinco minutos, no seu ritmo.", Modifier.padding(top = 6.dp))
    PathBeads(state, Modifier.padding(start = page, end = page, top = 22.dp))
    SacredTabs(FormationPlace.entries, place, { it.label }, onPlace, Modifier.padding(top = 18.dp), tag = { "formation-${it.label}" })
    Column(Modifier.fillMaxWidth().padding(horizontal = page).padding(top = 20.dp, bottom = 44.dp)) {
        when (place) {
            FormationPlace.LEARN -> LearnPlace(state, query, onQuery, openLesson)
            FormationPlace.QUESTIONS -> QuestionsPlace(model, query, onQuery)
            FormationPlace.LIFE -> LifePlace(openJourney, openLesson)
        }
        EditorialNote("formation", "Revisão 1 · 13/11/2026", model, Modifier.padding(top = 36.dp))
        var credits by rememberSaveable { mutableStateOf(false) }
        SacredTextLink("Obras de arte e créditos", { credits = true }, "formation-art-credits", Modifier.align(Alignment.CenterHorizontally))
        if (credits) FineArtCredits { credits = false }
    }
}

/** Sixteen beads on one thread: the walked lessons are sage, the next one blooms rose. */
@Composable
private fun PathBeads(state: GardenState, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val lessons = FormationCatalog.lessons
    val done = lessons.count { state.completed(it.id) }
    val next = nextLesson(state)
    Column(modifier.fillMaxWidth().semantics(mergeDescendants = true) {}) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            SacredText("$done de ${lessons.size} lições percorridas", Modifier.weight(1f), SertumTheme.typography.micro, colors.secondary)
            if (done == lessons.size) SacredText("Caminho completo", style = SertumTheme.typography.micro, color = colors.accentInk)
        }
        Canvas(Modifier.padding(top = 10.dp).fillMaxWidth().height(14.dp).clearAndSetSemantics {}) {
            val radius = 4.5.dp.toPx()
            val step = (size.width - radius * 2) / (lessons.size - 1)
            drawLine(colors.border, Offset(radius, center.y), Offset(size.width - radius, center.y), 1.dp.toPx())
            lessons.forEachIndexed { index, lesson ->
                val at = Offset(radius + step * index, center.y)
                when {
                    state.completed(lesson.id) -> drawCircle(colors.marian, radius, at)
                    lesson == next -> { drawCircle(colors.petal, radius + 1.dp.toPx(), at); drawCircle(colors.ruby.copy(alpha = .7f), radius + 1.dp.toPx(), at, style = Stroke(1.2.dp.toPx())) }
                    else -> { drawCircle(colors.background, radius, at); drawCircle(colors.muted.copy(alpha = .55f), radius, at, style = Stroke(1.2.dp.toPx())) }
                }
            }
        }
    }
}

@Composable
private fun LearnPlace(state: GardenState, query: String, onQuery: (String) -> Unit, openLesson: (String) -> Unit) {
    SacredSearchField(query, onQuery, "Buscar um tema", "formation-search")
    if (query.isNotBlank()) {
        val found = FormationCatalog.lessons.filter { FormationCatalog.matches(query, "${it.title} ${it.teaching} ${it.keywords}") > 0 }
        SacredText("${found.size} ${if (found.size == 1) "lição encontrada" else "lições encontradas"}", Modifier.padding(top = 18.dp, bottom = 10.dp), SertumTheme.typography.micro, SertumTheme.colors.secondary)
        if (found.isEmpty()) QuietNote("Ainda não encontramos esse tema.", "Tente uma palavra como graça, oração, Missa ou perdão. As perguntas também podem ajudar.")
        else LessonList(found, state, null, openLesson)
        return
    }
    val next = nextLesson(state)
    if (next != null) ContinueCard(next, state, Modifier.padding(top = 20.dp)) { openLesson(next.id) }
    else QuietNote("Você percorreu todo o caminho.", "Volte a qualquer lição quando quiser. Cada leitura pode ser rezada de novo.", Modifier.padding(top = 20.dp))
    chapters.forEach { chapter ->
        val lessons = FormationCatalog.lessons.filter { it.pillar == chapter.pillar }
        val done = lessons.count { state.completed(it.id) }
        ChapterHeading(chapter, done, lessons.size, Modifier.padding(top = 34.dp, bottom = 12.dp))
        LessonList(lessons, state, next, openLesson)
    }
}

/** The lesson to take up next, raised a little from the page, with one clear way in. */
@Composable
private fun ContinueCard(lesson: FormationLesson, state: GardenState, modifier: Modifier = Modifier, onOpen: () -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val started = state.learning.any { it.lessonId == lesson.id }
    val chapter = chapterOf(lesson)
    SacredCard(modifier.fillMaxWidth(), lifted = true) {
        Column(Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Abrir a lição", onClick = onOpen).testTag("continue-learning").padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SacredEyebrow(if (started) "Continuar de onde parou" else if (state.learning.isEmpty()) "Comece por aqui" else "Seu próximo passo", Modifier.weight(1f))
                SacredText("Lição ${numberOf(lesson)} de ${FormationCatalog.lessons.size}", style = type.micro, color = colors.secondary)
            }
            Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(Modifier.weight(1f)) {
                    SacredText(lesson.title, style = type.section.copy(fontSize = 23.sp, lineHeight = 30.sp))
                    SacredText(lesson.objective, Modifier.padding(top = 6.dp), type.body, colors.secondary)
                }
                DevotionalArtwork(lessonIllustration(lesson.id), Modifier.size(84.dp))
            }
            Row(Modifier.fillMaxWidth().padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                SacredText("${chapter.numeral} · ${chapter.verb} · cerca de 5 min", Modifier.weight(1f), type.micro.copy(fontWeight = FontWeight.Medium), colors.muted)
                Row(
                    Modifier.clip(RoundedCornerShape(50)).background(colors.petal).padding(start = 18.dp, end = 14.dp, top = 9.dp, bottom = 9.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    SacredText(if (started) "Continuar" else "Começar", style = type.label.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold), color = colors.onPetal)
                    SacredIcon(SacredIcon.CHEVRON, Modifier.size(11.dp), colors.onPetal)
                }
            }
        }
    }
}

/** A pillar as a chapter: its numeral and verb, its name, what it holds, and how far you have come. */
@Composable
private fun ChapterHeading(chapter: Chapter, done: Int, total: Int, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Row(modifier.fillMaxWidth().semantics(mergeDescendants = true) { heading() }, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        DevotionalArtwork(pillarIllustration(chapters.indexOf(chapter)), Modifier.size(68.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SacredEyebrow("${chapter.numeral} · ${chapter.verb}", Modifier.weight(1f))
                SacredText(if (done == total) "Percorrido" else "$done de $total", style = type.micro, color = if (done == total) colors.accentInk else colors.secondary)
            }
            SacredText(chapter.pillar, Modifier.padding(top = 2.dp), type.section)
            SacredText(chapter.caption, Modifier.padding(top = 3.dp), type.body.copy(fontSize = 14.sp, lineHeight = 21.sp), colors.secondary)
        }
    }
}

/** Lessons in one card: a numbered knot (a rose once walked), the title, what it teaches. */
@Composable
private fun LessonList(lessons: List<FormationLesson>, state: GardenState, next: FormationLesson?, openLesson: (String) -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val art = LocalBotanicalArtwork.current
    SacredCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(vertical = 4.dp)) {
            lessons.forEachIndexed { index, lesson ->
                if (index > 0) SacredDivider(Modifier.padding(start = 64.dp, end = 18.dp))
                val done = state.completed(lesson.id)
                val upNext = lesson == next
                Row(
                    Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Abrir a lição") { openLesson(lesson.id) }.testTag("lesson-${lesson.id}")
                        .semantics { stateDescription = if (done) "Lição percorrida" else if (upNext) "Próxima lição" else "" }
                        .heightIn(min = 76.dp).padding(start = 16.dp, end = 18.dp, top = 14.dp, bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        Modifier.size(32.dp).background(if (upNext) colors.petal else colors.marian.copy(alpha = if (done) .1f else .14f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (done) Canvas(Modifier.size(28.dp)) { paintBotanical(art.rose, center, size.minDimension) }
                        else SacredText("${numberOf(lesson)}", style = type.label.copy(fontSize = 13.5.sp), color = if (upNext) colors.onPetal else colors.accentInk)
                    }
                    Column(Modifier.weight(1f)) {
                        SacredText(lesson.title, style = type.label.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold))
                        SacredText(lesson.objective, Modifier.padding(top = 3.dp), type.body.copy(fontSize = 14.sp, lineHeight = 20.sp), colors.secondary)
                    }
                    SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), colors.muted)
                }
            }
        }
    }
}

/** Common questions, each opening in place beneath itself. */
@Composable
private fun QuestionsPlace(model: GardenViewModel?, query: String, onQuery: (String) -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SacredSearchField(query, onQuery, "Que pergunta você traz?", "formation-search")
    val answers = FormationCatalog.searchQuestions(query)
    SacredText(if (query.isBlank()) "Perguntas que muitos fazem, respondidas com a fé da Igreja." else "${answers.size} ${if (answers.size == 1) "pergunta relacionada" else "perguntas relacionadas"}",
        Modifier.padding(top = 18.dp, bottom = 10.dp), type.micro, colors.secondary)
    if (answers.isEmpty()) {
        QuietNote("Sua pergunta merece atenção.", "Tente outro termo ou converse com um catequista da sua comunidade. Esta biblioteca não responde a todas as situações pessoais.")
        return
    }
    SacredCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(vertical = 4.dp)) {
            answers.forEachIndexed { index, question ->
                if (index > 0) SacredDivider(Modifier.padding(horizontal = 18.dp))
                var open by rememberSaveable(question.id) { mutableStateOf(false) }
                val turn by animateFloatAsState(if (open) 90f else 0f, label = "question-chevron")
                Column(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = if (open) "Recolher a resposta" else "Ler a resposta") { open = !open }
                            .testTag("question-${question.id}").heightIn(min = 64.dp).padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        DevotionalArtwork(questionIllustration(question.id), Modifier.size(44.dp))
                        SacredText(question.question, Modifier.weight(1f), type.label.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold))
                        SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp).rotate(turn), colors.muted)
                    }
                    AnimatedVisibility(open) {
                        Column(Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, bottom = 16.dp)) {
                            SacredText(question.answer, style = type.body.copy(fontSize = 16.sp, lineHeight = 26.sp))
                            SacredText(question.references, Modifier.padding(top = 12.dp), type.micro.copy(fontWeight = FontWeight.Medium), colors.marian)
                            EditorialNote("question-${question.id}", "Revisão 1 · 13/11/2026", model, Modifier.padding(top = 4.dp), credit = false)
                        }
                    }
                }
            }
        }
    }
}

/** Practices for ordinary days, each a short path of steps. */
@Composable
private fun LifePlace(openJourney: (String) -> Unit, openLesson: (String) -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SacredText("Caminhos curtos para levar a fé à Missa, à confissão e ao dia a dia.", Modifier.padding(bottom = 6.dp), type.body, colors.secondary)
    FormationCatalog.journeys.forEach { journey ->
        PathCard("${journey.steps.size} passos", journey.title, journey.introduction, journeyIllustration(journey.id), "journey-${journey.id}", Modifier.padding(top = 12.dp)) { openJourney(journey.id) }
    }
    PathCard("Uma lição", "O tempo da Igreja", "Advento e Natal, Quaresma e Páscoa, Pentecostes e Tempo Comum: a Igreja nos conduz pelo mistério de Cristo.", DevotionalIllustration.LITURGICAL_YEAR, "liturgical-year", Modifier.padding(top = 12.dp)) { openLesson("year") }
}

@Composable
private fun PathCard(eyebrow: String, title: String, detail: String, illustration: DevotionalIllustration, tag: String, modifier: Modifier = Modifier, onOpen: () -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SacredCard(modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onOpen).testTag(tag).padding(start = 14.dp, end = 20.dp, top = 18.dp, bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            DevotionalArtwork(illustration, Modifier.size(72.dp))
            Column(Modifier.weight(1f)) {
                SacredEyebrow(eyebrow)
                SacredText(title, Modifier.padding(top = 6.dp), type.section)
                SacredText(detail, Modifier.padding(top = 4.dp), type.body.copy(fontSize = 14.sp, lineHeight = 21.sp), colors.secondary)
            }
            SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), colors.muted)
        }
    }
}

/** A gentle word where a list would be: a title and one line, never an error. */
@Composable
internal fun QuietNote(title: String, detail: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(SertumTheme.colors.surface).padding(20.dp)) {
        SacredText(title, style = SertumTheme.typography.section.copy(fontSize = 18.sp, lineHeight = 24.sp))
        SacredText(detail, Modifier.padding(top = 6.dp), SertumTheme.typography.body, SertumTheme.colors.secondary)
    }
}
