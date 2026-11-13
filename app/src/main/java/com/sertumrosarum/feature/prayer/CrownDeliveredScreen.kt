package com.sertumrosarum.feature.prayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.prayer.PrayerMode
import com.sertumrosarum.ui.canvas.RoseCrownEmblem
import com.sertumrosarum.ui.canvas.RoseMark
import com.sertumrosarum.feature.guides.offeringIllustration
import com.sertumrosarum.ui.components.SacredFlourish
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme
import java.util.Locale

@Composable
fun CrownDeliveredScreen(state: PrayerUiState, onReturn: () -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Column(Modifier.fillMaxSize().testTag("crown-delivered").padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.padding(top = 19.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            RoseMark(Modifier.size(26.dp))
            SacredText("sertum", style = type.brand)
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            RoseCrownEmblem(Modifier.size(286.dp), state.groupCount, state.crownBeadCount, state.sessionGuide.offeringIllustration)
            SacredText(state.sessionGuide.title, Modifier.padding(top = 10.dp).testTag("delivered-guide"), type.micro, colors.marian, TextAlign.Center)
            SacredText(if (state.sessionMode == PrayerMode.SOLO) "Sua Coroa de Rosas\nfoi entregue" else "Nossa Coroa de Rosas\nfoi entregue", Modifier.padding(top = 12.dp), type.display, align = TextAlign.Center)
            SacredText("Que a paz desta oração\nacompanhe o seu dia.", Modifier.padding(top = 14.dp), type.whisper, colors.secondary, TextAlign.Center)
            SacredFlourish(Modifier.padding(top = 24.dp, bottom = 20.dp))
            // A quiet record of the time given, with no score to keep.
            Row(
                Modifier.padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SacredText("${state.groupCount} ${if (state.script.isRosary) "mistérios" else if (state.groupLabel == "setena") "setenas" else "dezenas"}, ", style = type.body.copy(fontSize = 14.sp), color = colors.secondary)
                SacredText(String.format(Locale.ROOT, "%02d:%02d", state.durationSeconds / 60, state.durationSeconds % 60), Modifier.testTag("prayer-duration"), type.body.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold), colors.ink)
                SacredText(" em oração", style = type.body.copy(fontSize = 14.sp), color = colors.secondary)
            }
        }
        SacredPrimaryButton("Voltar ao santuário", onReturn, Modifier.padding(top = 12.dp), tag = "return-sanctuary")
        SacredText("Leve essa paz com você.", Modifier.fillMaxWidth().padding(top = 11.dp, bottom = 14.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.secondary, TextAlign.Center)
    }
}
