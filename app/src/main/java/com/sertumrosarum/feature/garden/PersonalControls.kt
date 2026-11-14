package com.sertumrosarum.feature.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.sertumrosarum.ui.canvas.DevotionalArtwork
import com.sertumrosarum.ui.canvas.DevotionalIllustration
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.sertumrosarum.ui.components.*
import com.sertumrosarum.ui.theme.SertumTheme

@Composable
fun GardenField(label: String, value: String, onChange: (String) -> Unit, tag: String, multiline: Boolean = false, limit: Int = 4000) {
    val colors = SertumTheme.colors
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        SacredText(label, style = SertumTheme.typography.label, color = colors.secondary)
        BasicTextField(value, { onChange(it.take(limit)) },
            Modifier.padding(top = 7.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp)).heightIn(min = if (multiline) 112.dp else 52.dp)
                .padding(14.dp).semantics { contentDescription = label }.testTag(tag),
            textStyle = SertumTheme.typography.body.copy(color = colors.ink), cursorBrush = SolidColor(colors.marian), singleLine = !multiline)
    }
}

@Composable
fun QuietAction(label: String, onClick: () -> Unit, tag: String = label, selected: Boolean? = null) {
    val colors = SertumTheme.colors
    SacredText(label,
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).then(if (selected == true) Modifier.background(colors.surface) else Modifier)
            .clickable(role = if (selected == null) Role.Button else Role.Checkbox, onClick = onClick)
            .semantics { if (selected != null) toggleableState = if (selected) androidx.compose.ui.state.ToggleableState.On else androidx.compose.ui.state.ToggleableState.Off }
            .heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 14.dp).testTag(tag),
        SertumTheme.typography.label, if (selected == true) colors.ink else colors.accentInk)
}

@Composable
fun PersonalCard(title: String, detail: String, onClick: (() -> Unit)? = null, tag: String = title, modifier: Modifier = Modifier,
    illustration: DevotionalIllustration? = null, illustrationAbove: Boolean = false, content: @Composable ColumnScope.() -> Unit = {}) {
    SacredCard(modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier).testTag(tag).padding(18.dp)) {
            if (illustration != null && illustrationAbove) DevotionalArtwork(illustration, Modifier.align(Alignment.CenterHorizontally).fillMaxWidth().height(76.dp).padding(bottom = 6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                if (illustration != null && !illustrationAbove) DevotionalArtwork(illustration, Modifier.size(64.dp))
                Column(Modifier.weight(1f)) {
                    SacredText(title, style = SertumTheme.typography.section)
                    if (detail.isNotBlank()) SacredText(detail, Modifier.padding(top = 6.dp), SertumTheme.typography.body, SertumTheme.colors.secondary)
                }
            }
            content()
        }
    }
}
