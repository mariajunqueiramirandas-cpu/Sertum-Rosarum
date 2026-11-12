package com.sertumrosarum.feature.prayer

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.domain.prayer.PrayerPhase
import com.sertumrosarum.ui.canvas.BloomingBeadsArc
import com.sertumrosarum.ui.canvas.CompactMysteries
import com.sertumrosarum.ui.canvas.FiveMysteriesArc
import com.sertumrosarum.ui.canvas.RoseCrownEmblem
import com.sertumrosarum.ui.canvas.RoseMark
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredIconButton
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme
import kotlin.math.abs

@Composable
fun ImmersivePrayerScreen(state: PrayerUiState, model: PrayerSessionViewModel, onNext: () -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val step = state.step
    val chapter = when (step.phase) {
        PrayerPhase.INTRODUCTION -> "Para aquietar o coração"
        PrayerPhase.DECADE -> "${requireNotNull(step.decadeIndex) + 1} de 5 · Mistérios ${state.sessionSet.label.lowercase()}"
        PrayerPhase.CLOSING -> "Sua coroa está florescendo"
    }
    val beadLabel = if (step.prayer == FixedPrayer.HAIL_MARY) "Ave-Maria ${requireNotNull(step.beadIndex) + 1} de ${step.beadCount}" else step.intention ?: "Passo ${state.stepIndex + 1} de ${state.sequence.size}"
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact = maxHeight < 650.dp
        val shortScreen = maxHeight < 430.dp
        val touchZoneHeight = maxHeight * .5f
        Column(Modifier.fillMaxSize().testTag("prayer-screen").padding(horizontal = 24.dp)) {
            Row(Modifier.fillMaxWidth().padding(vertical = if (compact) 4.dp else 13.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                SacredIconButton(SacredIcon.CLOSE, "Voltar ao santuário e pausar", model::returnHome, Modifier.testTag("pause-prayer"))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SacredText("Minha oração", style = type.label, color = colors.ink)
                    SacredText("Mistérios ${state.sessionSet.label.lowercase()}", style = type.micro, color = colors.secondary)
                }
                SacredIconButton(SacredIcon.HAPTIC, if (state.hapticsEnabled) "Desativar vibração" else "Ativar vibração", model::toggleHaptics, active = state.hapticsEnabled)
            }
            if (!state.eyesClosed) {
                if (compact) CompactMysteries(state.completedDecades, step.decadeIndex, model::jumpToDecade)
                else FiveMysteriesArc(state.completedDecades, step.decadeIndex, model::jumpToDecade)
                Spacer(Modifier.height(if (compact) 0.dp else 7.dp))
            }
            key(state.stepIndex, state.eyesClosed) {
                PrayerTextReader(Modifier.weight(1f).fillMaxWidth()) {
                    SacredText(chapter, style = type.micro.copy(fontSize = 12.sp), color = colors.marian, align = TextAlign.Center)
                    Spacer(Modifier.height(14.dp))
                    SacredText(step.title, Modifier.testTag("prayer-title"), type.heading, align = TextAlign.Center)
                    if (step.mystery != null && step.prayer == null) {
                        SacredText(step.mystery.scripture, Modifier.padding(top = 10.dp), type.body.copy(fontSize = 11.sp), colors.muted)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        repeat(3) { dot -> Box(Modifier.size(4.dp).background(colors.marian.copy(alpha = .25f + dot * .12f), CircleShape)) }
                    }
                    Spacer(Modifier.height(15.dp))
                    if (state.eyesClosed) {
                        SacredText("Reze no seu tempo.\nO toque acompanha sua oração.", Modifier.padding(horizontal = 3.dp), type.italic.copy(fontSize = 15.sp), colors.secondary, TextAlign.Center)
                    } else {
                        SacredText(step.text, Modifier.padding(horizontal = 3.dp).testTag("prayer-text"), type.prayer, colors.ink, TextAlign.Center)
                    }
                    if (step.intention != null) {
                        SacredText(step.intention, Modifier.padding(top = 15.dp), type.italic, colors.gold, TextAlign.Center)
                    }
                }
            }
            if (state.eyesClosed) {
                EyesClosedZone(beadLabel, onNext, model::previous, Modifier.height(touchZoneHeight).padding(top = 14.dp, bottom = 12.dp))
            } else {
                if (!shortScreen && step.phase == PrayerPhase.CLOSING) {
                    RoseCrownEmblem(Modifier.align(Alignment.CenterHorizontally).size(100.dp))
                } else if (!shortScreen) {
                    val count = if (step.phase == PrayerPhase.INTRODUCTION) 3 else 10
                    BloomingBeadsArc(count, step.beadIndex, step.prayer == FixedPrayer.GLORY_BE || step.prayer == FixedPrayer.FATIMA, model::jumpToBead)
                }
                SacredText(
                    beadLabel,
                    Modifier.fillMaxWidth().padding(bottom = if (compact) 8.dp else 18.dp).testTag("bead-counter").semantics { liveRegion = LiveRegionMode.Polite },
                    type.label.copy(fontSize = 12.sp), colors.secondary, TextAlign.Center,
                )
            }
            Column(Modifier.padding(top = 3.dp, bottom = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SacredIconButton(SacredIcon.BACK, "Oração anterior", model::previous, Modifier.testTag("previous-prayer"), enabled = state.stepIndex > 0)
                    SacredPrimaryButton(
                        if (state.stepIndex == state.sequence.lastIndex) "Entregar minha coroa" else if (step.prayer == FixedPrayer.HAIL_MARY) "Próxima conta" else "Próxima oração",
                        onNext, Modifier.weight(1f), "next-prayer",
                    )
                }
                Row(
                    Modifier.clip(RoundedCornerShape(50)).toggleable(state.eyesClosed, role = Role.Switch, onValueChange = { model.toggleEyesClosed() })
                        .testTag("eyes-closed-toggle").padding(horizontal = 19.dp, vertical = if (compact) 8.dp else 16.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SacredIcon(SacredIcon.EYE, Modifier.size(17.dp), if (state.eyesClosed) colors.gold else colors.muted)
                    SacredText(if (state.eyesClosed) "Olhos fechados · ativado" else "Rezar de olhos fechados", style = type.body.copy(fontSize = 11.sp), color = if (state.eyesClosed) colors.marian else colors.secondary)
                }
            }
        }
    }
}

@Composable
private fun EyesClosedZone(label: String, onNext: () -> Unit, onPrevious: () -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val next by rememberUpdatedState(onNext)
    val previous by rememberUpdatedState(onPrevious)
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(colors.surface)
            .testTag("eyes-closed-zone").semantics(mergeDescendants = true) {
                contentDescription = "Área de oração: toque para avançar; deslize para a direita para voltar"
                onClick("Próxima oração") { next(); true }
            }
            .pointerInput(Unit) { detectTapGestures { next() } }
            .pointerInput(Unit) {
                var movement = Offset.Zero
                detectDragGestures(
                    onDragStart = { movement = Offset.Zero },
                    onDrag = { change, amount -> movement += amount; change.consume() },
                    onDragEnd = {
                        val threshold = 36.dp.toPx()
                        if (abs(movement.x) > abs(movement.y) && movement.x > threshold) previous()
                        else if (movement.x < -threshold || movement.y < -threshold) next()
                    },
                )
            }
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        RoseMark(Modifier.size(59.dp), colors.ruby.copy(alpha = .75f))
        Spacer(Modifier.height(18.dp))
        SacredText("Seu toque acompanha a oração.", style = SertumTheme.typography.section.copy(fontSize = 18.sp), align = TextAlign.Center)
        SacredText(label, Modifier.padding(top = 8.dp).testTag("bead-counter"), SertumTheme.typography.body.copy(fontSize = 11.sp), colors.secondary)
        SacredText("Toque para seguir. Deslize para voltar.", Modifier.padding(top = 22.dp), SertumTheme.typography.micro.copy(fontSize = 11.sp), colors.secondary, TextAlign.Center)
    }
}
