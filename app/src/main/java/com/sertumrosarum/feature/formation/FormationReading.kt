package com.sertumrosarum.feature.formation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.domain.formation.FormationLesson
import com.sertumrosarum.domain.formation.PracticeJourney
import com.sertumrosarum.feature.garden.GardenField
import com.sertumrosarum.feature.garden.GardenState
import com.sertumrosarum.feature.garden.GardenViewModel
import com.sertumrosarum.ui.canvas.DevotionalArtwork
import com.sertumrosarum.ui.canvas.DevotionalIllustration
import com.sertumrosarum.ui.canvas.FormationPainting
import com.sertumrosarum.ui.canvas.journeyIllustration
import com.sertumrosarum.ui.canvas.lessonIllustration
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredEyebrow
import com.sertumrosarum.ui.components.SacredFlourish
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredSecondaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SacredToggleRow
import com.sertumrosarum.ui.theme.SertumTheme

/** One lesson, read like a short chapter: the teaching, its sources, a pause, a gesture and a prayer. */
@Composable
internal fun ColumnScope.LessonPage(lesson: FormationLesson, state: GardenState, model: GardenViewModel?, onPray: () -> Unit, openLesson: (String?) -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val progress = state.learning.find { it.lessonId == lesson.id }
    val done = progress?.completedAt != null
    val chapter = chapterOf(lesson)
    val kept = state.bookmarks.any { it.kind == "lesson" && it.itemId == lesson.id }
    Column(Modifier.fillMaxWidth().padding(horizontal = SertumTheme.spacing.page).padding(top = 8.dp, bottom = 44.dp)) {
        FormationPainting(lessonIllustration(lesson.id), "lesson-artwork", compact = true)
        SacredEyebrow("${chapter.numeral} · ${chapter.verb}  ·  Lição ${numberOf(lesson)} de ${FormationCatalog.lessons.size}")
        SacredText(lesson.title, Modifier.padding(top = 10.dp).semantics { heading() }, type.display)
        SacredText(lesson.objective, Modifier.padding(top = 10.dp), type.whisper, colors.secondary)
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SacredText(if (done) "✓ Lição percorrida" else "Cerca de 5 min de leitura", Modifier.weight(1f), type.micro.copy(fontWeight = FontWeight.Medium), if (done) colors.accentInk else colors.muted)
            KeepToggle(kept) { model?.bookmark("lesson", lesson.id) }
        }
        SacredFlourish(Modifier.align(Alignment.CenterHorizontally).padding(vertical = 22.dp))

        SacredText("Para compreender", Modifier.semantics { heading() }, type.section)
        lesson.teaching.split("\n\n").forEach { paragraph ->
            SacredText(paragraph.trim(), Modifier.padding(top = 12.dp), type.body.copy(fontSize = 16.sp, lineHeight = 27.sp))
        }
        Sources(lesson, Modifier.padding(top = 24.dp))

        var answer by rememberSaveable(lesson.id) { mutableStateOf<Int?>(null) }
        PauseCard(lesson, answer, { answer = it }, Modifier.padding(top = 30.dp))

        SacredCard(Modifier.padding(top = 16.dp).fillMaxWidth()) {
            Column(Modifier.padding(start = 20.dp, end = 14.dp, top = 20.dp, bottom = 8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        SacredEyebrow("Levar à vida")
                        SacredText(lesson.practice, Modifier.padding(top = 8.dp), type.whisper)
                    }
                    DevotionalArtwork(DevotionalIllustration.CHARITY, Modifier.size(64.dp))
                }
                SacredToggleRow("Vivi este gesto", "Marque quando o tiver vivido, sem pressa.", progress?.practiceDone == true,
                    { model?.lesson(lesson.id, practice = progress?.practiceDone != true) }, Modifier.padding(top = 8.dp), "lesson-practice")
            }
        }

        Column(Modifier.fillMaxWidth().padding(top = 30.dp, start = 12.dp, end = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            SacredEyebrow("Uma oração")
            SacredText(lesson.prayer, Modifier.padding(top = 8.dp), type.whisper.copy(fontSize = 19.sp, lineHeight = 30.sp), colors.accentInk, TextAlign.Center)
        }

        var reflection by rememberSaveable(lesson.id) { mutableStateOf(progress?.reflection.orEmpty()) }
        var saved by rememberSaveable(lesson.id) { mutableStateOf(false) }
        Column(Modifier.padding(top = 28.dp)) {
            GardenField("Uma descoberta para guardar (opcional)", reflection, { reflection = it; saved = false }, "lesson-reflection", multiline = true, limit = 8000)
            SacredText("Fica no seu Diário, neste aparelho.", style = type.micro.copy(fontWeight = FontWeight.Normal), color = colors.muted)
            if (reflection.isNotBlank()) SacredSecondaryButton(if (saved) "Reflexão guardada" else "Guardar reflexão",
                { model?.lesson(lesson.id, reflection = reflection); saved = true }, Modifier.padding(top = 10.dp).align(Alignment.End), "save-lesson-reflection", enabled = !saved)
        }

        val next = FormationCatalog.lessons.getOrNull(FormationCatalog.lessons.indexOf(lesson) + 1)
        Column(Modifier.fillMaxWidth().padding(top = 28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (done || answer == lesson.correct) SacredPrimaryButton(
                when { done && next != null -> "Seguir para a próxima lição"; done -> "Voltar ao caminho"; next != null -> "Concluir e continuar"; else -> "Concluir o caminho" },
                { model?.lesson(lesson.id, reflection = reflection, complete = true); openLesson(next?.id) }, tag = "complete-lesson",
            ) else SacredText("Responda à pausa acima para concluir a lição.", style = type.body.copy(fontSize = 14.sp), color = colors.secondary, align = TextAlign.Center)
            if (next != null) SacredText("A seguir: ${next.title}", style = type.micro.copy(fontWeight = FontWeight.Medium), color = colors.muted, align = TextAlign.Center)
            SacredSecondaryButton("Levar à oração", onPray, Modifier.fillMaxWidth(), "lesson-pray", icon = SacredIcon.BEADS)
        }
        EditorialNote(lesson.id, "Revisão ${lesson.revision} · ${lesson.editorialDate}", model, Modifier.padding(top = 32.dp))
    }
}

/** Keep this page among the Guardados: a small ribbon that fills when kept. */
@Composable
private fun KeepToggle(kept: Boolean, onToggle: () -> Unit) {
    val colors = SertumTheme.colors
    val shape = RoundedCornerShape(50)
    Row(
        Modifier.clip(shape).background(if (kept) colors.marian.copy(alpha = .16f) else Color.Transparent).border(1.dp, if (kept) colors.marian.copy(alpha = .6f) else colors.border, shape)
            .clickable(role = Role.Button, onClick = onToggle).testTag("bookmark-lesson").heightIn(min = 40.dp).padding(start = 12.dp, end = 14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SacredIcon(SacredIcon.BOOKMARK, Modifier.size(16.dp), if (kept) colors.accentInk else colors.secondary)
        SacredText(if (kept) "Guardada" else "Guardar", style = SertumTheme.typography.micro, color = if (kept) colors.accentInk else colors.secondary)
    }
}

/** Where the teaching rests: Scripture and the Catechism, set off by a thin gold rule. */
@Composable
private fun Sources(lesson: FormationLesson, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Row(modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(Modifier.width(2.dp).fillMaxHeight().background(colors.gold.copy(alpha = .55f), RoundedCornerShape(1.dp)))
        Column(Modifier.weight(1f).padding(vertical = 2.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Column {
                SacredText("Na Escritura", style = type.micro, color = colors.secondary)
                SacredText(lesson.scripture, Modifier.padding(top = 2.dp), type.body.copy(fontSize = 15.sp))
            }
            Column {
                SacredText("No Catecismo", style = type.micro, color = colors.secondary)
                SacredText(lesson.catechism, Modifier.padding(top = 2.dp), type.body.copy(fontSize = 15.sp))
            }
        }
    }
}

/** A pause to remember: one question, two answers to choose between, and a gentle word after. */
@Composable
private fun PauseCard(lesson: FormationLesson, answer: Int?, onAnswer: (Int) -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val order = if (lesson.id.hashCode() % 2 == 0) lesson.choices.indices.toList() else lesson.choices.indices.reversed().toList()
    SacredCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            SacredEyebrow("Uma pausa para lembrar")
            SacredText(lesson.question, Modifier.padding(top = 8.dp), type.section.copy(fontSize = 19.sp, lineHeight = 26.sp))
            Column(Modifier.padding(top = 16.dp).selectableGroup(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                order.forEach { index ->
                    val chosen = answer == index
                    val right = index == lesson.correct
                    val tone = when { !chosen -> null; right -> colors.marian; else -> colors.ruby }
                    val fill by animateColorAsState(tone?.copy(alpha = .14f) ?: Color.Transparent, label = "answer-fill")
                    val shape = RoundedCornerShape(18.dp)
                    Row(
                        Modifier.fillMaxWidth().clip(shape).background(fill).border(1.dp, tone ?: colors.border, shape)
                            .selectable(chosen, role = Role.RadioButton) { onAnswer(index) }.testTag("lesson-answer-$index")
                            .heightIn(min = 56.dp).padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            Modifier.size(24.dp).background(tone ?: Color.Transparent, CircleShape).border(1.5.dp, tone ?: colors.muted.copy(alpha = .6f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) { if (tone != null) SacredIcon(if (right) SacredIcon.CHECK else SacredIcon.CLOSE, Modifier.size(13.dp), colors.background) }
                        SacredText(lesson.choices[index], Modifier.weight(1f), type.body)
                    }
                }
            }
            AnimatedVisibility(answer != null) {
                val right = answer == lesson.correct
                Column(Modifier.padding(top = 16.dp).testTag("lesson-answer-explanation")) {
                    SacredText(if (right) "Isso mesmo." else "Vamos olhar de novo, com calma.", style = type.label, color = if (right) colors.accentInk else colors.ruby)
                    SacredText(lesson.explanation, Modifier.padding(top = 4.dp), type.body, colors.secondary)
                    if (!right) SacredText("Escolha a outra resposta quando quiser. Sem pontuação.", Modifier.padding(top = 6.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.muted)
                }
            }
        }
    }
}

/** A practice for ordinary life, its steps joined by one stem like the beads of a decade. */
@Composable
internal fun ColumnScope.JourneyPage(journey: PracticeJourney, model: GardenViewModel?, onPray: () -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Column(Modifier.fillMaxWidth().padding(horizontal = SertumTheme.spacing.page).padding(top = 8.dp, bottom = 44.dp)) {
        FormationPainting(journeyIllustration(journey.id), "journey-artwork", compact = true)
        SacredEyebrow("Na vida · ${journey.steps.size} passos")
        SacredText(journey.title, Modifier.padding(top = 10.dp).semantics { heading() }, type.display)
        SacredText(journey.introduction, Modifier.padding(top = 10.dp), type.whisper, colors.secondary)
        SacredFlourish(Modifier.align(Alignment.CenterHorizontally).padding(vertical = 22.dp))
        SacredCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(start = 16.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)) {
                val stem = colors.marian.copy(alpha = .3f)
                journey.steps.forEachIndexed { index, step ->
                    Row(
                        Modifier.fillMaxWidth().drawBehind {
                            val x = 16.dp.toPx()
                            val knot = 26.dp.toPx()
                            drawLine(stem, Offset(x, if (index == 0) knot else 0f), Offset(x, if (index == journey.steps.lastIndex) knot else size.height), 1.5.dp.toPx())
                        }.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(Modifier.padding(top = 2.dp).size(32.dp).background(colors.elevated, CircleShape).border(1.dp, colors.marian.copy(alpha = .45f), CircleShape), contentAlignment = Alignment.Center) {
                            SacredText("${index + 1}", style = type.label.copy(fontSize = 13.5.sp), color = colors.accentInk)
                        }
                        SacredText(step, Modifier.weight(1f).padding(top = 5.dp), type.body.copy(fontSize = 16.sp, lineHeight = 25.sp))
                    }
                }
            }
        }
        SacredText(journey.references, Modifier.padding(top = 16.dp), type.micro.copy(fontWeight = FontWeight.Medium), colors.marian)
        SacredPrimaryButton("Ir à oração", onPray, Modifier.padding(top = 28.dp), tag = "journey-pray")
        EditorialNote("journey-${journey.id}", "Revisão 1 · 13/11/2026", model, Modifier.padding(top = 32.dp))
    }
}
