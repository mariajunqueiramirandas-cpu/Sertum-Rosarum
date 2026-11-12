package com.sertumrosarum.feature.prayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.ui.canvas.RoseCrownEmblem
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme
import java.util.Locale

@Composable
fun CrownDeliveredScreen(state: PrayerUiState, onReturn: () -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Column(Modifier.fillMaxSize().testTag("crown-delivered").padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        SacredText("sertum", Modifier.padding(vertical = 24.dp), type.brand, colors.ink)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            RoseCrownEmblem(Modifier.size(265.dp))
            SacredText("Uma oração feita com amor", Modifier.padding(top = 8.dp), type.micro.copy(fontSize = 12.sp), colors.marian)
            Spacer(Modifier.height(17.dp))
            SacredText("Sua Coroa de Rosas\nfoi entregue", style = type.display.copy(fontSize = 29.sp, lineHeight = 36.sp), align = TextAlign.Center)
            SacredText("Que a paz desta oração\nacompanhe o seu dia.", Modifier.padding(top = 20.dp), type.italic, colors.secondary, TextAlign.Center)
            Row(Modifier.padding(vertical = 30.dp), horizontalArrangement = Arrangement.spacedBy(26.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SacredText("5", style = type.heading, color = colors.gold)
                    SacredText("mistérios contemplados", style = type.micro, color = colors.secondary)
                }
                Box(Modifier.width(1.dp).height(35.dp).background(colors.border))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SacredText(String.format(Locale.ROOT, "%02d:%02d", state.durationSeconds / 60, state.durationSeconds % 60), Modifier.testTag("prayer-duration"), type.heading, colors.gold)
                    SacredText("em oração", style = type.micro, color = colors.secondary)
                }
            }
        }
        SacredPrimaryButton("Voltar ao santuário", onReturn, tag = "return-sanctuary")
        SacredText("Leve essa paz com você.", Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp), type.micro, colors.secondary, TextAlign.Center)
    }
}
