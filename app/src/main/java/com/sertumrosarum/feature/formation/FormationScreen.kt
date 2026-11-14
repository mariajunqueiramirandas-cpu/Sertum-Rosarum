package com.sertumrosarum.feature.formation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.domain.formation.FormationLesson
import com.sertumrosarum.feature.garden.GardenField
import com.sertumrosarum.feature.garden.GardenState
import com.sertumrosarum.feature.garden.GardenViewModel
import com.sertumrosarum.ui.components.SacredSecondaryButton
import com.sertumrosarum.ui.components.SacredTextLink
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SanctuaryPage
import com.sertumrosarum.ui.theme.SertumTheme

/** One of the four pillars of the course, as a chapter: its numeral, its verb and what it holds. */
internal data class Chapter(val pillar: String, val numeral: String, val verb: String, val caption: String)

internal val chapters = FormationCatalog.pillars.zip(
    listOf(
        Triple("I", "Crer", "O Credo: Deus que se revela, Jesus Cristo, a Igreja e Maria."),
        Triple("II", "Celebrar", "Os sacramentos e a Missa: Cristo que age na Igreja."),
        Triple("III", "Viver", "A vida em Cristo: dignidade, graça, misericórdia e perdão."),
        Triple("IV", "Rezar", "A oração cristã, o rosário e o ritmo de cada dia."),
    ),
) { pillar, (numeral, verb, caption) -> Chapter(pillar, numeral, verb, caption) }

internal fun chapterOf(lesson: FormationLesson) = chapters.first { it.pillar == lesson.pillar }
internal fun numberOf(lesson: FormationLesson) = FormationCatalog.lessons.indexOf(lesson) + 1

/** The lesson to take up next: one already opened and not finished, or else the first not yet walked. */
internal fun nextLesson(state: GardenState) = state.learning.firstOrNull { it.completedAt == null }?.lessonId?.let(FormationCatalog::lesson)
    ?: FormationCatalog.lessons.firstOrNull { item -> state.learning.none { it.lessonId == item.id && it.completedAt != null } }

internal fun GardenState.completed(id: String) = learning.any { it.lessonId == id && it.completedAt != null }

@Composable
fun FormationScreen(model: GardenViewModel?, state: GardenState, initialLesson: String?, onPray: () -> Unit) {
    var place by rememberSaveable { mutableStateOf(FormationPlace.LEARN) }
    var lessonId by rememberSaveable(initialLesson) { mutableStateOf(initialLesson) }
    var query by rememberSaveable { mutableStateOf("") }
    var journeyId by rememberSaveable { mutableStateOf<String?>(null) }
    val lesson = FormationCatalog.lesson(lessonId)
    val journey = FormationCatalog.journeys.find { it.id == journeyId }
    // Catequese is a tab of its own: only a lesson or a journey opened within it has a way back.
    val inside = lesson != null || journey != null
    val back = { if (lessonId != null) lessonId = null else journeyId = null }
    BackHandler(inside) { back() }
    LaunchedEffect(lessonId) { lessonId?.let { model?.lesson(it) } }
    val courseScroll = rememberScrollState()
    val readingScroll = rememberScrollState()
    LaunchedEffect(lessonId, journeyId) { readingScroll.scrollTo(0) }
    SanctuaryPage("Catequese", "formation", if (inside) readingScroll else courseScroll, onBack = if (inside) back else null) {
        when {
            lesson != null -> LessonPage(lesson, state, model, onPray) { lessonId = it }
            journey != null -> JourneyPage(journey, model, onPray)
            else -> CoursePage(state, model, place, { place = it }, query, { query = it }, { lessonId = it }, { journeyId = it })
        }
    }
}

/**
 * Who wrote this and how it may be corrected, said once and quietly at the foot of a page.
 * A suggestion stays on the device unless the reader chooses to share it.
 */
@Composable
internal fun EditorialNote(id: String, revision: String, model: GardenViewModel?, modifier: Modifier = Modifier, credit: Boolean = true) {
    var open by rememberSaveable(id) { mutableStateOf(false) }
    var text by rememberSaveable(id) { mutableStateOf("") }
    var saved by rememberSaveable(id) { mutableStateOf(false) }
    val context = LocalContext.current
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (credit) {
            SacredText("Conteúdo catequético editorial · $revision", style = type.micro, color = colors.muted, align = TextAlign.Center)
            SacredText("As referências indicam apoio, não citações literais. Revisão teológica independente pendente.", Modifier.padding(top = 4.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.muted, TextAlign.Center)
        }
        SacredTextLink(if (open) "Fechar sugestão" else "Sugerir uma correção", { open = !open }, "feedback-$id")
        if (open) Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            GardenField("Sua observação", text, { text = it; saved = false }, "feedback-text", multiline = true)
            SacredSecondaryButton(if (saved) "Observação guardada neste aparelho" else "Guardar neste aparelho", {
                if (text.isNotBlank() && !saved) { model?.repository?.feedback(id, text); saved = true }
            }, Modifier.fillMaxWidth(), "save-feedback", enabled = text.isNotBlank() && !saved)
            if (text.isNotBlank()) SacredTextLink("Compartilhar observação…", {
                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).setType("text/plain").putExtra(android.content.Intent.EXTRA_TEXT, "Sertum · $id · $revision\n\n$text")
                context.startActivity(android.content.Intent.createChooser(intent, "Compartilhar observação"))
            }, "share-feedback")
        }
    }
}
