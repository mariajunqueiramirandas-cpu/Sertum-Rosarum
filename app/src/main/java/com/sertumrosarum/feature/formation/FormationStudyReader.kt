package com.sertumrosarum.feature.formation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.formation.FormationLesson
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredEyebrow
import com.sertumrosarum.ui.components.SacredPainting
import com.sertumrosarum.ui.components.SacredText
import kotlinx.coroutines.launch
import com.sertumrosarum.ui.theme.SertumTheme

/** Expanded chapters stay open; a small contents list gives direct access to each study stage. */
@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun StudyReader(lesson: FormationLesson) {
    val study = lesson.study
    val type = SertumTheme.typography
    val colors = SertumTheme.colors
    val scope = rememberCoroutineScope()
    val anchors = remember(lesson.id) { List(study.sections.size + 3) { BringIntoViewRequester() } }
    val contents = remember(lesson.id) { BringIntoViewRequester() }
    val labels = study.sections.map { it.title } + listOf("Bíblia aberta", "Uma situação para discernir", "Síntese e revisão")
    SacredCard(Modifier.fillMaxWidth().padding(top = 24.dp).bringIntoViewRequester(contents).testTag("lesson-contents")) {
        Column(Modifier.padding(18.dp)) {
            SacredEyebrow("Roteiro de estudo")
            SacredText("Cerca de ${lesson.readingMinutes} min de leitura, além das atividades. Leia em etapas, no seu ritmo.", Modifier.padding(top = 8.dp), type.body, colors.secondary)
            labels.forEachIndexed { index, title ->
                SacredText(title, Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Ir para $title") { scope.launch { anchors[index].bringIntoView() } }
                    .testTag("lesson-jump-$index").heightIn(min = 48.dp).padding(vertical = 12.dp), type.label, colors.accentInk)
            }
        }
    }
    SacredText("Para começar", Modifier.padding(top = 30.dp).semantics { heading() }, type.section)
    StudyParagraphs(lesson.teaching)
    study.sections.forEachIndexed { index, section ->
        Column(Modifier.fillMaxWidth().padding(top = 30.dp).testTag("lesson-section-$index")) {
            SacredText(section.title, Modifier.bringIntoViewRequester(anchors[index]).semantics { heading() }, type.section)
            StudyParagraphs(section.body)
            SacredText(section.reference, Modifier.padding(top = 12.dp), type.micro, colors.accentInk)
            if (index == 0) ConceptMap(lesson.id)
            if (index == 1) TeachingIllustration(lesson.id)
            if (index > 0) SacredText("Voltar ao roteiro ↑", Modifier.clickable(role = Role.Button) { scope.launch { contents.bringIntoView() } }.heightIn(min = 48.dp).padding(vertical = 14.dp), type.micro, colors.accentInk)
        }
    }
    Column(Modifier.fillMaxWidth().padding(top = 30.dp).testTag("lesson-scripture-study")) {
        SacredText("Bíblia aberta", Modifier.bringIntoViewRequester(anchors[study.sections.size]).testTag("lesson-scripture-heading").semantics { heading() }, type.section)
        SacredText(study.scripture.reference, Modifier.padding(top = 10.dp), type.label, colors.accentInk)
        StudyParagraphs(study.scripture.context)
        SacredCard(Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Column(Modifier.padding(18.dp)) {
                SacredEyebrow("Para acompanhar a leitura")
                SacredText(study.scripture.reading, Modifier.padding(top = 10.dp), type.body.copy(fontSize = 16.sp, lineHeight = 27.sp))
                SacredText("Resumo editorial · não é transcrição da Bíblia", Modifier.padding(top = 10.dp), type.micro, colors.muted)
            }
        }
        StudyParagraphs(study.scripture.notice)
        lessonArtStudy(lesson.id)?.let { (art, prompt) ->
            SacredPainting(art, Modifier.padding(top = 24.dp), "lesson-painting")
            SacredEyebrow("Olhar a obra com atenção", Modifier.padding(top = 16.dp))
            StudyParagraphs(prompt)
        }
    }
    Column(Modifier.fillMaxWidth().padding(top = 30.dp).testTag("lesson-case-study")) {
        SacredText("Uma situação para discernir", Modifier.bringIntoViewRequester(anchors[study.sections.size + 1]).semantics { heading() }, type.section)
        StudyParagraphs(study.example.situation)
        RevealAnswer("Pensar a situação", study.example.response, "case-${lesson.id}")
    }
    Column(Modifier.fillMaxWidth().padding(top = 30.dp).testTag("lesson-summary")) {
        SacredText("O essencial para guardar", Modifier.bringIntoViewRequester(anchors[study.sections.size + 2]).semantics { heading() }, type.section)
        study.takeaways.forEachIndexed { index, text ->
            Row(Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SacredText("${index + 1}", style = type.label, color = colors.accentInk)
                SacredText(text, Modifier.weight(1f), type.body)
            }
        }
        SacredText("Palavras para compreender", Modifier.padding(top = 26.dp).semantics { heading() }, type.section)
        study.terms.forEach { term ->
            SacredText(term.name, Modifier.padding(top = 14.dp), type.label, colors.accentInk)
            SacredText(term.meaning, Modifier.padding(top = 5.dp), type.body)
        }
        SacredText("Tente explicar com suas palavras", Modifier.padding(top = 26.dp).semantics { heading() }, type.section)
        SacredText("Pense numa resposta antes de abrir a explicação. Volte ao capítulo sempre que precisar.", Modifier.padding(top = 10.dp), type.body, colors.secondary)
        study.recall.forEachIndexed { index, item ->
            SacredText(item.question, Modifier.padding(top = 18.dp), type.label)
            RevealAnswer("Conferir a explicação", item.answer, "recall-${lesson.id}-$index")
        }
    }
}

@Composable
private fun StudyParagraphs(text: String) {
    text.split("\n\n").forEach { paragraph ->
        SacredText(paragraph.trim(), Modifier.padding(top = 12.dp), SertumTheme.typography.body.copy(fontSize = 16.sp, lineHeight = 27.sp))
    }
}

/** Reveals aid recall; they never claim a score or overwrite the learner's progress. */
@Composable
internal fun RevealAnswer(label: String, answer: String, key: String) {
    var expanded by rememberSaveable(key) { mutableStateOf(false) }
    val colors = SertumTheme.colors
    Column(Modifier.fillMaxWidth()) {
        SacredText(if (expanded) "Fechar explicação ↑" else "$label ↓", Modifier.fillMaxWidth().clickable(role = Role.Button) { expanded = !expanded }
            .semantics { stateDescription = if (expanded) "Explicação aberta" else "Explicação fechada" }
            .testTag("reveal-$key").heightIn(min = 48.dp).padding(vertical = 14.dp), SertumTheme.typography.label, colors.accentInk)
        AnimatedVisibility(expanded) {
            SacredText(answer, Modifier.padding(bottom = 12.dp).testTag("explanation-$key"), SertumTheme.typography.body.copy(fontSize = 16.sp, lineHeight = 27.sp), colors.secondary)
        }
    }
}
