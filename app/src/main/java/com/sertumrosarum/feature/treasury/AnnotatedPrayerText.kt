package com.sertumrosarum.feature.treasury

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sertumrosarum.domain.treasury.PrayerTermAnnotation
import com.sertumrosarum.ui.components.SacredFlourish
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme

/** Where each annotated expression sits in [text]: its first exact occurrence, or nowhere. */
internal fun termRanges(text: String, annotations: List<PrayerTermAnnotation>): List<Pair<PrayerTermAnnotation, IntRange>> =
    annotations.mapNotNull { annotation ->
        text.indexOf(annotation.term).takeIf { it >= 0 && annotation.term.isNotEmpty() }?.let { annotation to (it until it + annotation.term.length) }
    }

/**
 * A prayer whose explained expressions rest on a short string of honey beads.
 * The wording and its colour are untouched; touching an expression reports it through [onTerm].
 */
@Composable
internal fun AnnotatedPrayerText(
    text: String,
    annotations: List<PrayerTermAnnotation>,
    onTerm: (PrayerTermAnnotation) -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = SertumTheme.typography.prayer,
) {
    val colors = SertumTheme.colors
    val touched by rememberUpdatedState(onTerm)
    val ranges = remember(text, annotations) { termRanges(text, annotations) }
    val pressed = colors.petal.copy(alpha = if (colors.isDark) .28f else .7f)
    val annotated = remember(text, ranges, pressed) {
        buildAnnotatedString {
            append(text)
            ranges.forEach { (annotation, range) ->
                addLink(
                    LinkAnnotation.Clickable(annotation.term, TextLinkStyles(pressedStyle = SpanStyle(background = pressed))) { touched(annotation) },
                    range.first, range.last + 1,
                )
            }
        }
    }
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val bead = colors.gold.copy(alpha = if (colors.isDark) .9f else .8f)
    BasicText(
        annotated,
        modifier.drawBehind {
            val measured = layout ?: return@drawBehind
            // A layout from an earlier text can outlive it by a frame; never read past its end.
            val length = measured.layoutInput.text.length
            val beads = PathEffect.dashPathEffect(floatArrayOf(0f, 5.dp.toPx()))
            ranges.forEach { (_, range) ->
                if (range.last >= length) return@forEach
                val firstLine = measured.getLineForOffset(range.first)
                val lastLine = measured.getLineForOffset(range.last)
                for (line in firstLine..lastLine) {
                    val from = if (line == firstLine) measured.getHorizontalPosition(range.first, true) else measured.getLineLeft(line)
                    val end = range.last + 1
                    val to = if (line == lastLine && end < length && measured.getLineForOffset(end) == line) measured.getHorizontalPosition(end, true) else measured.getLineRight(line)
                    val y = measured.getLineBaseline(line) + 6.dp.toPx()
                    drawLine(bead, Offset(from + 1.dp.toPx(), y), Offset(to, y), 2.4.dp.toPx(), StrokeCap.Round, beads)
                }
            }
        },
        style.copy(color = colors.ink, textAlign = TextAlign.Start),
        onTextLayout = { layout = it },
    )
}

/** What stands behind one expression: where it comes from, where Scripture says it, and what it asks of us today. */
@Composable
internal fun ColumnScope.TermSheetContent(annotation: PrayerTermAnnotation) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SacredText("Toque para entender", Modifier.fillMaxWidth(), type.micro, colors.marian, TextAlign.Center)
    SacredText("“${annotation.term}”", Modifier.fillMaxWidth().padding(top = 6.dp).testTag("term-title").semantics { heading() }, type.heading, align = TextAlign.Center)
    SacredFlourish(Modifier.align(Alignment.CenterHorizontally).padding(top = 14.dp, bottom = 2.dp))

    TermHeading("De onde vem")
    SacredText(annotation.origin, Modifier.fillMaxWidth().testTag("term-origin"), type.whisper)

    if (annotation.scriptureQuote != null || annotation.scriptureReference != null) {
        TermHeading("Na Escritura")
        annotation.scriptureQuote?.let { SacredText("“$it”", Modifier.fillMaxWidth(), type.whisper, lerp(colors.marian, colors.ink, if (colors.isDark) 0f else .25f)) }
        annotation.scriptureReference?.let { SacredText(it, Modifier.padding(top = 4.dp), type.micro, colors.marian) }
    }

    TermHeading("O que rezamos hoje")
    SacredText(annotation.today, Modifier.fillMaxWidth().testTag("term-today"), type.whisper)

    annotation.catechism?.let {
        SacredText("Para aprofundar: $it", Modifier.fillMaxWidth().padding(top = 22.dp, bottom = 8.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.secondary)
    }
}

@Composable
private fun TermHeading(title: String) {
    SacredText(title, Modifier.padding(top = 22.dp, bottom = 6.dp).semantics { heading() }, SertumTheme.typography.section)
}
