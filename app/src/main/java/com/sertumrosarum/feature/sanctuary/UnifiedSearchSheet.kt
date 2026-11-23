package com.sertumrosarum.feature.sanctuary

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.domain.prayer.MysteryContent
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.prayer.MysteryStudyCatalog
import com.sertumrosarum.domain.treasury.TreasuryCatalog
import com.sertumrosarum.feature.guides.PrayerGuide
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredEyebrow
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredSearchField
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme

/** Searches across Mysteries, Roteiros, Treasury prayers, Catequese lessons, FAQs, and journeys in one place. */
@Composable
internal fun ColumnScope.UnifiedSearchSheetContent(
    onOpenMystery: (MysterySet, Int) -> Unit,
    onOpenGuide: (PrayerGuide) -> Unit,
    onOpenPrayer: (String) -> Unit,
    onOpenLesson: (String) -> Unit,
    onOpenQuestion: (String) -> Unit,
    onOpenJourney: (String) -> Unit,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    var query by rememberSaveable { mutableStateOf("") }

    SacredSearchField(query, { query = it }, "Buscar oração, mistério, lição ou devoção", "unified-search-field")

    if (query.isBlank()) {
        SacredText(
            "Pesquise em todo o Sertum: os 20 mistérios, todos os roteiros e terços, as 61 orações do Tesouro e toda a Catequese.",
            Modifier.padding(top = 14.dp),
            type.body,
            colors.secondary,
        )
        return
    }

    val mysteries = MysterySet.entries.flatMap { set ->
        set.mysteries.mapIndexedNotNull { idx, m ->
            val c = MysteryContent.contemplationFor(set, idx)
            val s = MysteryStudyCatalog.studyFor(set, idx)
            val text = "${m.title} ${m.scripture} ${m.fruit} ${set.label} ${c.scene} ${c.meaning} ${s.searchableText}"
            if (FormationCatalog.matches(query, text) > 0) Triple(set, idx, m) else null
        }
    }.take(5)

    val guides = PrayerGuide.entries.filter {
        FormationCatalog.matches(query, "${it.title} ${it.kicker} ${it.summary} ${it.about}") > 0
    }.take(5)

    val prayers = TreasuryCatalog.search(query, null).take(6)

    val lessons = FormationCatalog.lessons.filter {
        FormationCatalog.matches(query, it.searchableText) > 0
    }.take(5)

    val questions = FormationCatalog.searchQuestions(query).take(4)

    val journeys = FormationCatalog.journeys.filter {
        FormationCatalog.matches(query, "${it.title} ${it.introduction} ${it.steps.joinToString(" ")}") > 0
    }.take(3)

    val total = mysteries.size + guides.size + prayers.size + lessons.size + questions.size + journeys.size
    if (total == 0) {
        SacredText(
            "Nada encontrado para “$query”. Tente palavras como Anunciação, Via-Sacra, São Miguel, Eucaristia, ansiedade ou perdão.",
            Modifier.padding(top = 18.dp),
            type.body,
            colors.secondary,
        )
        return
    }

    if (mysteries.isNotEmpty()) {
        SearchGroup("Mistérios do Rosário", mysteries.map { (set, idx, m) ->
            SearchRowData(
                title = m.title,
                detail = "${idx + 1}º ${set.singular.lowercase()} · ${m.scripture}",
                tag = "search-mystery-${set.name}-$idx",
                onClick = { onOpenMystery(set, idx) },
            )
        })
    }
    if (guides.isNotEmpty()) {
        SearchGroup("Roteiros e devoções", guides.map { guide ->
            SearchRowData(
                title = guide.title,
                detail = guide.summary,
                tag = "search-guide-${guide.name}",
                onClick = { onOpenGuide(guide) },
            )
        })
    }
    if (prayers.isNotEmpty()) {
        SearchGroup("Tesouro de orações", prayers.map { prayer ->
            SearchRowData(
                title = prayer.title,
                detail = prayer.category.label,
                tag = "search-prayer-${prayer.id}",
                onClick = { onOpenPrayer(prayer.id) },
            )
        })
    }
    if (lessons.isNotEmpty()) {
        SearchGroup("Catequese · Lições", lessons.map { lesson ->
            SearchRowData(
                title = lesson.title,
                detail = lesson.pillar,
                tag = "search-lesson-${lesson.id}",
                onClick = { onOpenLesson(lesson.id) },
            )
        })
    }
    if (questions.isNotEmpty()) {
        SearchGroup("Catequese · Perguntas", questions.map { q ->
            SearchRowData(
                title = q.question,
                detail = q.references,
                tag = "search-question-${q.id}",
                onClick = { onOpenQuestion(q.id) },
            )
        })
    }
    if (journeys.isNotEmpty()) {
        SearchGroup("Catequese · Na vida", journeys.map { j ->
            SearchRowData(
                title = j.title,
                detail = "${j.steps.size} passos",
                tag = "search-journey-${j.id}",
                onClick = { onOpenJourney(j.id) },
            )
        })
    }
}

private data class SearchRowData(val title: String, val detail: String, val tag: String, val onClick: () -> Unit)

@Composable
private fun SearchGroup(heading: String, items: List<SearchRowData>) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SacredEyebrow(heading, Modifier.padding(top = 18.dp, bottom = 8.dp))
    SacredCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(vertical = 4.dp)) {
            items.forEachIndexed { index, item ->
                if (index > 0) Box(Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(1.dp).background(colors.border.copy(alpha = .7f)))
                Row(
                    Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = item.onClick).testTag(item.tag)
                        .heightIn(min = 56.dp).padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(Modifier.weight(1f)) {
                        SacredText(item.title, style = type.label.copy(fontSize = 15.5.sp, fontWeight = FontWeight.SemiBold))
                        SacredText(item.detail, Modifier.padding(top = 2.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.secondary)
                    }
                    SacredIcon(SacredIcon.CHEVRON, Modifier.size(12.dp), colors.muted)
                }
            }
        }
    }
}
