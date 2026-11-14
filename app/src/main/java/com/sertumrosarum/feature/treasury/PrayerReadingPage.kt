package com.sertumrosarum.feature.treasury

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.treasury.BilingualVerse
import com.sertumrosarum.domain.treasury.LatinGloss
import com.sertumrosarum.domain.treasury.PrayerLanguage
import com.sertumrosarum.domain.treasury.PrayerTermAnnotation
import com.sertumrosarum.domain.treasury.TreasuryPrayer
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.ui.components.SacredFlourish
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredSegmented
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SanctuaryPage
import com.sertumrosarum.ui.components.SteadySlot
import com.sertumrosarum.ui.theme.SertumTheme

/** Screen readers pronounce the text as Latin, not as misspelt Portuguese. */
internal val TextStyle.inLatin get() = copy(localeList = LocaleList("la"))

/**
 * One prayer and nothing else: its name, the choice of language when it has a Latin text,
 * and the words set as they are in the rosary.
 */
@Composable
internal fun PrayerReadingPage(prayer: TreasuryPrayer, language: PrayerLanguage, openVerse: Int?, actions: TreasuryActions, bookmark: @Composable () -> Unit = {}) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val uri = LocalUriHandler.current
    val latin = prayer.latin
    // A prayer without Latin is always read in Portuguese, whatever was chosen before.
    val shown = if (latin == null) PrayerLanguage.PORTUGUESE else language
    SanctuaryPage(SanctuaryTab.TREASURY.label, "treasury-reading", onBack = actions::backToLibrary) {
        Column(Modifier.fillMaxWidth().padding(horizontal = SertumTheme.spacing.page), horizontalAlignment = Alignment.CenterHorizontally) {
            TreasuryArtwork(prayer.category, Modifier.padding(top = 12.dp).size(88.dp))
            SacredText(prayer.category.label, Modifier.padding(top = 8.dp), type.micro, colors.accentInk, TextAlign.Center)
            val latinTitle = prayer.latinTitle
            // Every language's heading is laid out at once, so switching never moves the choice beneath it.
            SteadySlot(if (latin != null) PrayerLanguage.entries else listOf(PrayerLanguage.PORTUGUESE), shown, Modifier.fillMaxWidth()) { language, visible ->
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    val title = Modifier.padding(top = 6.dp).then(if (visible) Modifier.testTag("reading-title") else Modifier).semantics { heading() }
                    if (language == PrayerLanguage.LATIN && latinTitle != null) {
                        SacredText(latinTitle, title, type.heading.inLatin, align = TextAlign.Center)
                    } else {
                        SacredText(prayer.title, title, type.heading, align = TextAlign.Center)
                    }
                    if (language == PrayerLanguage.BILINGUAL && latinTitle != null) {
                        SacredText(latinTitle, Modifier.padding(top = 2.dp), type.whisper.inLatin, colors.secondary, TextAlign.Center)
                    }
                }
            }
            bookmark()
            SacredFlourish(Modifier.padding(top = 10.dp, bottom = 16.dp))
            if (latin != null) {
                SacredSegmented(PrayerLanguage.entries, shown, { it.label }, actions::selectLanguage, Modifier.testTag("reading-language"), tag = { "language-${it.name}" })
            }
            Crossfade(shown, Modifier.padding(top = 22.dp), tween(if (SertumTheme.reducedMotion) 0 else 220), label = "reading-language") { reading ->
                Column(Modifier.widthIn(max = 480.dp).fillMaxWidth()) {
                    when (reading) {
                        PrayerLanguage.PORTUGUESE -> {
                            AnnotatedPrayerText(prayer.portuguese, prayer.annotations, actions::openTerm, Modifier.fillMaxWidth().testTag("reading-text"))
                            if (prayer.annotations.isNotEmpty()) TermLinks(prayer.annotations, actions::openTerm, Modifier.padding(top = 26.dp))
                        }
                        PrayerLanguage.LATIN -> SacredText(latin.orEmpty(), Modifier.fillMaxWidth().testTag("reading-text"), type.prayer.inLatin, align = TextAlign.Start)
                        PrayerLanguage.BILINGUAL -> {
                            BilingualVerses(prayer.verses, openVerse, actions::toggleVerse, annotations = prayer.annotations, onTerm = actions::openTerm)
                            if (prayer.annotations.isNotEmpty()) TermLinks(prayer.annotations, actions::openTerm, Modifier.padding(top = 26.dp))
                        }
                    }
                }
            }
            if (prayer.context.isNotBlank()) {
                SacredText("Para rezar e compreender", Modifier.widthIn(max = 480.dp).fillMaxWidth().padding(top = 28.dp).semantics { heading() }, type.section)
                SacredText(prayer.context, Modifier.widthIn(max = 480.dp).fillMaxWidth().padding(top = 6.dp).testTag("reading-context"), type.body, colors.secondary)
            }
            prayer.source?.let {
                SacredText(it, Modifier.widthIn(max = 480.dp).fillMaxWidth().padding(top = 28.dp).testTag("reading-source"), type.micro.copy(fontWeight = FontWeight.Normal), colors.muted)
            }
            prayer.sourceUrls.forEachIndexed { index, url ->
                SacredText("Consultar fonte ${index + 1}", Modifier.widthIn(max = 480.dp).fillMaxWidth()
                    .clickable(role = Role.Button, onClickLabel = "Abrir referência no navegador") { runCatching { uri.openUri(url) } }
                    .heightIn(min = 48.dp).padding(vertical = 12.dp), type.label, colors.accentInk)
            }
            Box(Modifier.height(44.dp))
        }
    }
}

/**
 * The same expressions the text marks, as open links with a full touch target,
 * so no one has to hit three small words with a fingertip.
 */
@Composable
private fun TermLinks(annotations: List<PrayerTermAnnotation>, onTerm: (PrayerTermAnnotation) -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val honey = lerp(colors.gold, colors.ink, if (colors.isDark) 0f else .4f)
    Column(modifier.fillMaxWidth().testTag("term-links")) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
        SacredText("Toque para entender", Modifier.padding(top = 18.dp).semantics { heading() }, type.section)
        SacredText("As expressões marcadas no texto guardam uma história.", Modifier.padding(top = 2.dp, bottom = 4.dp), type.body.copy(fontSize = 14.sp, lineHeight = 21.sp), colors.secondary)
        annotations.forEachIndexed { index, annotation ->
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(role = Role.Button, onClickLabel = "Entender esta expressão") { onTerm(annotation) }
                    .testTag("term-$index").heightIn(min = 48.dp).padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SacredText("“${annotation.term}”", Modifier.weight(1f), type.whisper, honey)
                SacredIcon(SacredIcon.CHEVRON, Modifier.size(12.dp), honey)
            }
        }
    }
}

/**
 * The prayer verse by verse, Portuguese beside Latin. Where space is short the Latin steps in
 * beneath its verse, as the answers of a litany do. A verse that has been broken down opens
 * on touch to show its Latin word by word.
 */
@Composable
internal fun BilingualVerses(verses: List<BilingualVerse>, openVerse: Int?, onToggle: (Int) -> Unit, modifier: Modifier = Modifier, annotations: List<PrayerTermAnnotation> = emptyList(), onTerm: (PrayerTermAnnotation) -> Unit = {}) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val latinInk = lerp(colors.ruby, colors.ink, if (colors.isDark) .05f else .35f)
    val reducedMotion = SertumTheme.reducedMotion
    val largeText = LocalDensity.current.fontScale > 1.3f
    val anyGlossed = verses.any { it.glosses.isNotEmpty() }
    BoxWithConstraints(modifier.fillMaxWidth().testTag("bilingual-verses")) {
        val stacked = largeText || maxWidth < 320.dp
        Column(Modifier.fillMaxWidth()) {
            if (!stacked) {
                Row(Modifier.fillMaxWidth().padding(bottom = 6.dp, end = if (anyGlossed) 24.dp else 0.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SacredText(PrayerLanguage.PORTUGUESE.label, Modifier.weight(1f), type.micro, colors.secondary)
                    SacredText(PrayerLanguage.LATIN.label, Modifier.weight(1f), type.micro, colors.secondary)
                }
            }
            verses.forEachIndexed { index, verse ->
                val glossed = verse.glosses.isNotEmpty()
                val open = glossed && index == openVerse
                val turn by animateFloatAsState(if (open) -90f else 90f, tween(if (reducedMotion) 0 else 220), label = "verse-chevron")
                Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border.copy(alpha = .8f)))
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .then(if (glossed) Modifier.clickable(role = Role.Button, onClickLabel = "Ver o latim palavra por palavra") { onToggle(index) }.semantics { stateDescription = if (open) "Aberto" else "Fechado" } else Modifier)
                        .testTag("verse-$index").heightIn(min = 48.dp).padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (stacked) {
                        Column(Modifier.weight(1f)) {
                            AnnotatedPrayerText(verse.portuguese, annotations, onTerm, style = type.whisper)
                            SacredText(verse.latin, Modifier.padding(start = 22.dp), type.whisper.inLatin, latinInk)
                        }
                    } else {
                        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            AnnotatedPrayerText(verse.portuguese, annotations, onTerm, Modifier.weight(1f), type.whisper)
                            SacredText(verse.latin, Modifier.weight(1f), type.whisper.inLatin, latinInk)
                        }
                    }
                    if (anyGlossed) Box(Modifier.size(24.dp), contentAlignment = Alignment.CenterEnd) {
                        if (glossed) SacredIcon(SacredIcon.CHEVRON, Modifier.size(12.dp).rotate(turn), colors.secondary)
                    }
                }
                if (glossed) {
                    AnimatedVisibility(
                        open,
                        enter = if (reducedMotion) fadeIn(tween(0)) else expandVertically(tween(260)) + fadeIn(tween(220, delayMillis = 60)),
                        exit = if (reducedMotion) fadeOut(tween(0)) else shrinkVertically(tween(220)) + fadeOut(tween(140)),
                    ) { WordByWord(verse.glosses, Modifier.padding(bottom = 14.dp)) }
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border.copy(alpha = .8f)))
            if (anyGlossed) {
                SacredText("Toque num verso com seta para ler o latim palavra por palavra.", Modifier.padding(top = 12.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.secondary)
            }
        }
    }
}

/** The Latin of one verse, expression by expression, on a panel washed in the day's colour. */
@Composable
private fun WordByWord(glosses: List<LatinGloss>, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val shape = RoundedCornerShape(24.dp)
    val honey = lerp(colors.gold, colors.ink, if (colors.isDark) 0f else .4f)
    val latinInk = lerp(colors.ruby, colors.ink, if (colors.isDark) .05f else .35f)
    Column(
        modifier.fillMaxWidth().clip(shape)
            .background(lerp(colors.elevated, SertumTheme.atmosphere.tint, if (colors.isDark) .35f else .45f))
            .border(1.dp, colors.border.copy(alpha = if (colors.isDark) .9f else .7f), shape)
            .testTag("word-by-word").padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SacredText("Palavra por palavra", style = type.micro, color = honey)
        glosses.forEach { gloss ->
            Column(Modifier.fillMaxWidth().semantics(mergeDescendants = true) {}) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    SacredText(gloss.latin, style = type.whisper.inLatin, color = latinInk)
                    SacredText("·", style = type.whisper, color = colors.muted)
                    SacredText(gloss.portuguese, Modifier.weight(1f, fill = false), type.label)
                }
                gloss.note?.let { SacredText(it, Modifier.padding(top = 1.dp), type.body.copy(fontSize = 14.sp, lineHeight = 21.sp), colors.secondary) }
            }
        }
    }
}
