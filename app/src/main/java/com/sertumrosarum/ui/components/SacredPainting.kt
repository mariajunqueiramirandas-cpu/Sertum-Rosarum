package com.sertumrosarum.ui.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sertumrosarum.ui.canvas.FineArt
import com.sertumrosarum.ui.canvas.rememberFineArt
import com.sertumrosarum.ui.theme.SertumTheme

/**
 * A work of sacred art in a thin gold frame, never cropped, with its title and maker beneath.
 * Touching it opens the painting large, with its full credit.
 */
@Composable
fun SacredPainting(art: FineArt, modifier: Modifier = Modifier, tag: String = "painting", tallest: Dp = 340.dp) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    var open by rememberSaveable(art) { mutableStateOf(false) }
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val density = LocalDensity.current
            val image = rememberFineArt(art, with(density) { maxWidth.roundToPx() })
            val ratio = image.width.toFloat() / image.height
            val mat = 6.dp
            // The frame hugs the painting: as wide as the column allows, never taller than [tallest].
            val width = minOf(maxWidth - mat * 2, tallest * ratio)
            Box(
                Modifier.shadow(if (colors.isDark) 0.dp else 10.dp, RoundedCornerShape(4.dp), ambientColor = colors.gold.copy(alpha = .25f), spotColor = colors.gold.copy(alpha = .3f))
                    .clip(RoundedCornerShape(4.dp)).background(colors.elevated).border(1.dp, colors.gold.copy(alpha = .6f), RoundedCornerShape(4.dp))
                    .clickable(role = Role.Button, onClickLabel = "Ver a obra ampliada") { open = true }
                    .semantics { contentDescription = "${art.title}, de ${art.artist}" }.testTag(tag).padding(mat),
            ) {
                Image(image, null, Modifier.size(width, width / ratio).border(.5.dp, colors.gold.copy(alpha = .35f)), contentScale = ContentScale.Fit, filterQuality = FilterQuality.High)
            }
        }
        SacredText(art.title, Modifier.fillMaxWidth().padding(top = 12.dp, start = 8.dp, end = 8.dp), type.whisper.copy(fontSize = 15.sp, lineHeight = 21.sp), colors.ink, TextAlign.Center)
        SacredText(art.byline, Modifier.fillMaxWidth().padding(top = 2.dp, start = 8.dp, end = 8.dp), type.micro.copy(fontWeight = FontWeight.Medium), colors.secondary, TextAlign.Center)
    }
    if (open) FineArtViewer(art) { open = false }
}

/** A small square of the painting, for a list; the whole work is one touch away. */
@Composable
fun SacredPaintingThumb(art: FineArt, modifier: Modifier = Modifier, size: Dp = 56.dp) {
    val colors = SertumTheme.colors
    val px = with(LocalDensity.current) { (size * 2).roundToPx() }
    Image(
        rememberFineArt(art, px), null,
        modifier.size(size).clip(RoundedCornerShape(10.dp)).border(1.dp, colors.gold.copy(alpha = .5f), RoundedCornerShape(10.dp)),
        contentScale = ContentScale.Crop, filterQuality = FilterQuality.High,
    )
}

/** The painting alone on a dark veil, as large as the screen allows, with where it lives and why it may be shown. */
@Composable
fun FineArtViewer(art: FineArt, onClose: () -> Unit) {
    val context = LocalContext.current
    val ink = Color(0xFFF3EFE3)
    Dialog(onClose, DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            Modifier.fillMaxSize().background(Color(0xFF0B1512)).clickable(role = Role.Button, onClickLabel = "Fechar", onClick = onClose)
                .systemBarsPadding().padding(16.dp).testTag("fine-art-viewer"),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
        ) {
            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                val image = rememberFineArt(art, with(LocalDensity.current) { maxOf(maxWidth, maxHeight).roundToPx() })
                Image(image, "${art.title}, de ${art.artist}", Modifier.fillMaxSize(), contentScale = ContentScale.Fit, filterQuality = FilterQuality.High)
            }
            SacredText(art.title, Modifier.padding(top = 16.dp).semantics { heading() }, SertumTheme.typography.section.copy(fontSize = 19.sp, lineHeight = 25.sp), ink, TextAlign.Center)
            SacredText(art.byline, Modifier.padding(top = 4.dp), SertumTheme.typography.body, ink.copy(alpha = .8f), TextAlign.Center)
            SacredText(art.collection, Modifier.padding(top = 2.dp), SertumTheme.typography.micro.copy(fontWeight = FontWeight.Medium), ink.copy(alpha = .7f), TextAlign.Center)
            SacredText(FineArt.LICENSE, Modifier.padding(top = 8.dp), SertumTheme.typography.micro.copy(fontWeight = FontWeight.Normal), ink.copy(alpha = .6f), TextAlign.Center)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SacredText("Ver no site do museu", Modifier.clip(RoundedCornerShape(14.dp)).clickable(role = Role.Button) {
                    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(art.source))) } catch (_: ActivityNotFoundException) {}
                }.testTag("fine-art-source").heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 14.dp), SertumTheme.typography.label, Color(0xFF9DBE9D))
                SacredText("Fechar", Modifier.clip(RoundedCornerShape(14.dp)).clickable(role = Role.Button, onClick = onClose).testTag("fine-art-close")
                    .heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 14.dp), SertumTheme.typography.label, ink)
            }
        }
    }
}

/** Every work shown in the app, with its maker, date and museum; each opens large. */
@Composable
fun FineArtCredits(onClose: () -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    var shown by rememberSaveable { mutableStateOf<FineArt?>(null) }
    Dialog(onClose, DialogProperties(usePlatformDefaultWidth = false)) {
        Column(Modifier.fillMaxSize().background(colors.background).systemBarsPadding().testTag("fine-art-credits")) {
            Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                SacredIconButton(SacredIcon.CLOSE, "Fechar", onClose, Modifier.testTag("fine-art-credits-close"))
                SacredText("Obras de arte", Modifier.semantics { heading() }, type.brand)
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = SertumTheme.spacing.page).padding(top = 12.dp, bottom = 32.dp)) {
                SacredText("Pinturas sacras em domínio público, de museus que oferecem suas imagens sob CC0. Agradecemos a cada um deles.", style = type.body, color = colors.secondary)
                SacredCard(Modifier.padding(top = 18.dp).fillMaxWidth()) {
                    Column(Modifier.padding(vertical = 4.dp)) {
                        FineArt.entries.forEachIndexed { index, art ->
                            if (index > 0) SacredDivider(Modifier.padding(start = 86.dp, end = 18.dp))
                            Row(
                                Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = "Ver a obra ampliada") { shown = art }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                SacredPaintingThumb(art, size = 54.dp)
                                Column(Modifier.weight(1f)) {
                                    SacredText(art.title, style = type.label.copy(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold))
                                    SacredText(art.byline, Modifier.padding(top = 2.dp), type.body.copy(fontSize = 13.5.sp, lineHeight = 19.sp), colors.secondary)
                                    SacredText(art.collection, Modifier.padding(top = 1.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.muted)
                                }
                            }
                        }
                    }
                }
                SacredText(FineArt.LICENSE, Modifier.fillMaxWidth().padding(top = 16.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.muted, TextAlign.Center)
            }
        }
    }
    shown?.let { FineArtViewer(it) { shown = null } }
}

