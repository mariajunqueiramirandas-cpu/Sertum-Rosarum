package com.sertumrosarum.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.ui.canvas.LocalBotanicalArtwork
import com.sertumrosarum.ui.canvas.RoseMark
import com.sertumrosarum.ui.canvas.paintBotanical
import com.sertumrosarum.ui.canvas.paintLeaf
import com.sertumrosarum.ui.canvas.rememberGardenBreath
import com.sertumrosarum.ui.theme.SertumTheme
import kotlin.math.sin

/**
 * A tabbed page of the sanctuary: the rose and the page's name where the wordmark sits at home,
 * then one scrolling column that dissolves into the foot of the screen.
 * Children add their own side padding, so a row may run from edge to edge.
 */
@Composable
fun SanctuaryPage(name: String, tag: String, scroll: ScrollState = rememberScrollState(), content: @Composable ColumnScope.() -> Unit) {
    val colors = SertumTheme.colors
    Column(Modifier.fillMaxSize().testTag(tag)) {
        Row(
            Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 10.dp, bottom = 2.dp).height(48.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            RoseMark(Modifier.size(30.dp))
            SacredText(name, Modifier.semantics { heading() }, SertumTheme.typography.brand)
        }
        Box(Modifier.weight(1f)) {
            Column(Modifier.fillMaxSize().verticalScroll(scroll).testTag("$tag-scroll"), content = content)
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(28.dp).background(Brush.verticalGradient(listOf(colors.background.copy(alpha = 0f), colors.background))))
        }
    }
}

/** The opening words of a page: one welcoming line, and a sentence beneath it. */
@Composable
fun PageWelcome(title: String, lead: String, modifier: Modifier = Modifier, align: TextAlign = TextAlign.Start) {
    Column(modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        SacredText(title, Modifier.fillMaxWidth().semantics { heading() }, SertumTheme.typography.display, align = align)
        SacredText(lead, Modifier.fillMaxWidth().padding(top = 8.dp), SertumTheme.typography.body, SertumTheme.colors.secondary, align)
    }
}

/**
 * A flowerbed that has not opened yet. It names what will grow there, and promises nothing more.
 * Used by the tabs whose season comes later.
 */
@Composable
fun BuddingBed(title: String, lead: String, promiseTitle: String, promises: List<Pair<String, String>>, closing: String, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val art = LocalBotanicalArtwork.current
    val breath = rememberGardenBreath("budding-bed", 7200)
    val halo = SertumTheme.atmosphere.accent
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.padding(top = 18.dp).size(168.dp)) {
            // A single bud between two leaves, with slow rings of light for it to grow into.
            val swell = .5f + .5f * sin(breath.value)
            drawCircle(halo.copy(alpha = .10f + .05f * swell), size.minDimension * (.42f + .06f * swell))
            drawCircle(colors.ruby.copy(alpha = .09f), size.minDimension * .3f)
            val foot = center + Offset(0f, 30.dp.toPx())
            drawLine(colors.marian.copy(alpha = .7f), foot, center + Offset(0f, 4.dp.toPx()), 1.6.dp.toPx())
            paintLeaf(art.leaf, foot - Offset(0f, 6.dp.toPx()), foot + Offset(-34.dp.toPx(), -22.dp.toPx()), .92f)
            paintLeaf(art.leaf, foot - Offset(0f, 12.dp.toPx()), foot + Offset(34.dp.toPx(), -30.dp.toPx()), .92f)
            paintBotanical(art.bud, center - Offset(0f, 14.dp.toPx()), 62.dp.toPx())
        }
        SacredChip("Ainda em botão", Modifier.padding(top = 4.dp))
        PageWelcome(title, lead, Modifier.padding(top = 14.dp), TextAlign.Center)
        SacredFlourish(Modifier.padding(vertical = 24.dp))
        SacredCard(Modifier.padding(horizontal = 24.dp).fillMaxWidth()) {
            Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)) {
                SacredText(promiseTitle, Modifier.padding(bottom = 6.dp).semantics { heading() }, type.section)
                val stem = colors.marian.copy(alpha = .3f)
                promises.forEachIndexed { index, (name, detail) ->
                    Row(
                        Modifier.fillMaxWidth().drawBehind {
                            // The same stem that joins the mysteries at home joins what is to come.
                            val x = 13.dp.toPx()
                            val knot = 24.dp.toPx()
                            drawLine(stem, Offset(x, if (index == 0) knot else 0f), Offset(x, if (index == promises.lastIndex) knot else size.height), 1.5.dp.toPx())
                        }.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Canvas(Modifier.padding(top = 1.dp).size(26.dp).background(colors.elevated, CircleShape)) { paintBotanical(art.bud, center, size.minDimension) }
                        Column(Modifier.weight(1f)) {
                            SacredText(name, style = type.label.copy(fontWeight = FontWeight.SemiBold))
                            SacredText(detail, Modifier.padding(top = 2.dp), type.body.copy(fontSize = 14.sp, lineHeight = 21.sp), colors.secondary)
                        }
                    }
                }
            }
        }
        SacredText(closing, Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp, top = 30.dp, bottom = 44.dp), type.whisper, colors.secondary, TextAlign.Center)
    }
}
