package com.sertumrosarum.feature.guides

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.ui.canvas.CrownGlyph
import com.sertumrosarum.ui.canvas.LocalBotanicalArtwork
import com.sertumrosarum.ui.canvas.paintBotanical
import com.sertumrosarum.ui.canvas.paintLeaf
import com.sertumrosarum.ui.components.PageWelcome
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredChip
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SanctuaryPage
import com.sertumrosarum.ui.theme.SertumTheme

/** Roteiros: every way of praying the app knows, on three shelves. Touching one opens its sheet. */
@Composable
fun GuidesScreen(onOpen: (PrayerGuide) -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SanctuaryPage(SanctuaryTab.GUIDES.label, "guides") {
        PageWelcome("Um caminho para cada oração.", "Escolha o jeito de rezar que cabe no seu dia.", Modifier.padding(top = 14.dp))

        ShelfHeading(GuideShelf.ROSARY, Modifier.padding(top = 30.dp))
        GuideList(GuideShelf.ROSARY, onOpen)

        ShelfHeading(GuideShelf.THEMED, Modifier.padding(top = 34.dp))
        // This row runs from edge to edge, so the next card is always glimpsed.
        Row(Modifier.horizontalScroll(rememberScrollState()).height(IntrinsicSize.Max).padding(horizontal = 24.dp).padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PrayerGuide.on(GuideShelf.THEMED).forEach { ThemedGuideCard(it, onOpen) }
        }

        ShelfHeading(GuideShelf.CROWN, Modifier.padding(top = 26.dp))
        GuideList(GuideShelf.CROWN, onOpen)

        SacredText("“Fazei tudo o que ele vos disser.”", Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 34.dp), type.whisper, colors.secondary, TextAlign.Center)
        SacredText("João 2, 5", Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 44.dp), type.micro, colors.muted, TextAlign.Center)
    }
}

@Composable
private fun ShelfHeading(shelf: GuideShelf, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 14.dp)) {
        SacredText(shelf.title, Modifier.semantics { heading() }, SertumTheme.typography.section)
        SacredText(shelf.caption, Modifier.padding(top = 2.dp), SertumTheme.typography.body.copy(fontSize = 14.sp, lineHeight = 21.sp), SertumTheme.colors.secondary)
    }
}

/** One card for a whole shelf, its guides separated by a hairline. */
@Composable
private fun GuideList(shelf: GuideShelf, onOpen: (PrayerGuide) -> Unit) {
    val colors = SertumTheme.colors
    val guides = PrayerGuide.on(shelf)
    SacredCard(Modifier.padding(horizontal = 24.dp).fillMaxWidth()) {
        Column(Modifier.padding(vertical = 6.dp)) {
            guides.forEachIndexed { index, guide ->
                if (index > 0) Box(Modifier.padding(start = 86.dp, end = 20.dp).fillMaxWidth().height(1.dp).background(colors.border.copy(alpha = .8f)))
                GuideRow(guide, onOpen)
            }
        }
    }
}

@Composable
private fun GuideRow(guide: PrayerGuide, onOpen: (PrayerGuide) -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Row(
        Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Ver este roteiro") { onOpen(guide) }.testTag("guide-${guide.name}")
            .heightIn(min = 84.dp).padding(start = 16.dp, end = 18.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (guide.shelf == GuideShelf.CROWN) CrownGlyph(guide.shape.groups, guide.shape.beads, Modifier.size(54.dp))
        else Box(Modifier.padding(2.dp).size(50.dp).background(SertumTheme.atmosphere.accent.copy(alpha = if (colors.isDark) .2f else .26f), CircleShape), contentAlignment = Alignment.Center) {
            SacredIcon(
                when (guide) { PrayerGuide.CATECHETICAL -> SacredIcon.SPROUT; PrayerGuide.COMPLETE -> SacredIcon.BOOK; else -> SacredIcon.ROSE },
                Modifier.size(24.dp), if (colors.isDark) colors.gold else colors.ink.copy(alpha = .75f),
            )
        }
        Column(Modifier.weight(1f)) {
            SacredText(guide.title, style = type.label.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold))
            SacredText(guide.summary, Modifier.padding(top = 2.dp), type.body.copy(fontSize = 14.sp, lineHeight = 20.sp), colors.secondary)
            if (guide.shelf == GuideShelf.CROWN || guide.catechetical || guide.litany) Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Every rosary has the same five decades, so only the other crowns state their shape.
                if (guide.shelf == GuideShelf.CROWN) SacredChip(guide.shape.badge)
                if (guide.catechetical) SacredChip("Iniciante")
                if (guide.litany) SacredChip("Ladainha")
            }
        }
        SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), colors.muted)
    }
}

/** A themed rosary is a small painted card, washed in the colour of its intention. */
@Composable
private fun ThemedGuideCard(guide: PrayerGuide, onOpen: (PrayerGuide) -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val art = LocalBotanicalArtwork.current
    val wash = when (guide) {
        PrayerGuide.FAMILY -> Color(0xFFE0AFA3)
        PrayerGuide.SICK -> Color(0xFFA6CBD0)
        else -> Color(0xFFE2CA8E)
    }
    val shape = RoundedCornerShape(28.dp)
    Column(
        Modifier.width(158.dp).fillMaxHeight()
            .shadow(if (colors.isDark) 0.dp else 8.dp, shape, ambientColor = colors.marian.copy(alpha = .16f), spotColor = colors.marian.copy(alpha = .22f))
            .clip(shape).background(colors.elevated)
            .background(Brush.verticalGradient(listOf(wash.copy(alpha = if (colors.isDark) .22f else .34f), wash.copy(alpha = 0f))))
            .border(1.dp, colors.border.copy(alpha = if (colors.isDark) .9f else .7f), shape)
            .clickable(role = Role.Button, onClickLabel = "Ver este roteiro") { onOpen(guide) }.testTag("guide-${guide.name}")
            .heightIn(min = 176.dp).padding(start = 16.dp, end = 14.dp, top = 14.dp, bottom = 16.dp),
    ) {
        Canvas(Modifier.size(width = 76.dp, height = 52.dp)) {
            // Each intention has its own small bouquet from the same painted family.
            val heart = center + Offset(-8.dp.toPx(), 2.dp.toPx())
            when (guide) {
                PrayerGuide.FAMILY -> {
                    paintLeaf(art.leaf, heart, heart + Offset(36.dp.toPx(), -10.dp.toPx()), .9f)
                    paintBotanical(art.rose, heart + Offset(18.dp.toPx(), 4.dp.toPx()), 30.dp.toPx())
                    paintBotanical(art.bud, heart + Offset(38.dp.toPx(), 8.dp.toPx()), 22.dp.toPx())
                    paintBotanical(art.rose, heart, 40.dp.toPx())
                }
                PrayerGuide.SICK -> {
                    paintLeaf(art.leaf, heart, heart + Offset(34.dp.toPx(), -14.dp.toPx()), .9f)
                    paintLeaf(art.leaf, heart, heart + Offset(36.dp.toPx(), 10.dp.toPx()), .9f)
                    paintBotanical(art.ivoryRose, heart, 42.dp.toPx())
                }
                else -> {
                    paintLeaf(art.leaf, heart, heart + Offset(30.dp.toPx(), -16.dp.toPx()), .9f)
                    paintLeaf(art.leaf, heart + Offset(8.dp.toPx(), 0f), heart + Offset(42.dp.toPx(), 2.dp.toPx()), .9f)
                    paintLeaf(art.leaf, heart, heart + Offset(26.dp.toPx(), 18.dp.toPx()), .9f)
                    paintBotanical(art.bud, heart, 34.dp.toPx())
                }
            }
        }
        SacredText(guide.title, Modifier.padding(top = 12.dp), type.section.copy(fontSize = 19.sp, lineHeight = 25.sp))
        SacredText(guide.summary, Modifier.padding(top = 4.dp), type.body.copy(fontSize = 13.5.sp, lineHeight = 19.sp), colors.secondary)
    }
}
