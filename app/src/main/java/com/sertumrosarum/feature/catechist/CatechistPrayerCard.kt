package com.sertumrosarum.feature.catechist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sertumrosarum.domain.lumen.CatechistPrayer
import com.sertumrosarum.domain.lumen.webUriOrNull
import com.sertumrosarum.ui.components.SacredBottomSheet
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredDivider
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredKeepToggle
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SacredTextLink
import com.sertumrosarum.ui.theme.SertumTheme

@Composable
internal fun CatechistPrayerCard(
    prayer: CatechistPrayer,
    saved: Boolean,
    onOpen: () -> Unit,
    onToggleSaved: (() -> Unit)?,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SacredCard(Modifier.fillMaxWidth().testTag("prayer-card-${prayer.id}")) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(36.dp).background(colors.surface, CircleShape), contentAlignment = Alignment.Center) {
                    SacredIcon(SacredIcon.BOOK, Modifier.size(18.dp), colors.accentInk)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    SacredText(prayer.title, Modifier.semantics { heading() }, type.section)
                    if (prayer.description.isNotBlank()) SacredText(prayer.description, style = type.body, color = colors.secondary)
                    SacredText(prayer.attribution, style = type.micro, color = colors.accentInk, maxLines = 1)
                }
            }
            SacredDivider(Modifier.padding(top = 12.dp, bottom = 3.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SacredTextLink(
                    if (prayer.guideName == null) "Ler oração" else "Ler roteiro completo",
                    onOpen,
                    "read-prayer-${prayer.id}",
                    Modifier.weight(1f),
                    align = TextAlign.Start,
                    flush = true,
                )
                if (onToggleSaved != null) {
                    SacredKeepToggle(saved, "save-prayer-${prayer.id}", onToggleSaved, Modifier.heightIn(min = 48.dp))
                }
            }
        }
    }
}

@Composable
internal fun CatechistPrayerSheet(
    prayer: CatechistPrayer,
    visible: Boolean,
    saved: Boolean,
    onDismiss: () -> Unit,
    onToggleSaved: (() -> Unit)?,
    onStartGuide: (() -> Unit)?,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    SacredBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        label = prayer.title,
        tag = "catechist-prayer-sheet",
        footer = if (onToggleSaved != null || onStartGuide != null) {
            {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                ) {
                    if (onStartGuide != null) {
                        SacredPrimaryButton("Rezar com guia", onStartGuide, Modifier.weight(1f), "catechist-prayer-start", compact = true)
                    }
                    if (onToggleSaved != null) {
                        SacredKeepToggle(
                            kept = saved,
                            tag = "catechist-prayer-save",
                            onToggle = onToggleSaved,
                            modifier = Modifier.heightIn(min = 48.dp),
                            unkeptLabel = if (onStartGuide == null) "Guardar oração" else "Guardar",
                        )
                    }
                }
            }
        } else null,
    ) {
        SacredText(prayer.title, Modifier.semantics { heading() }, type.heading)
        SacredText(prayer.attribution, Modifier.padding(top = 6.dp), type.micro, colors.accentInk)
        SacredDivider(Modifier.padding(vertical = 16.dp))
        CatechistMarkdown(prayer.body)
        prayer.sourceUrl?.let(::webUriOrNull)?.let { source ->
            val uriHandler = LocalUriHandler.current
            SacredDivider(Modifier.padding(top = 16.dp))
            SacredTextLink(
                "Abrir fonte · ${source.host}",
                { runCatching { uriHandler.openUri(source.toString()) } },
                "catechist-prayer-source",
                align = TextAlign.Start,
                flush = true,
            )
        }
    }
}
