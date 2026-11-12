package com.sertumrosarum.feature.prayer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
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
import com.sertumrosarum.ui.canvas.rememberGardenBreath
import com.sertumrosarum.ui.components.SacredFlourish
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredIconButton
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun ImmersivePrayerScreen(state: PrayerUiState, model: PrayerSessionViewModel, onNext: () -> Unit) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val step = state.step
    val chapter = when (step.phase) {
        PrayerPhase.INTRODUCTION -> "Para aquietar o coração"
        PrayerPhase.DECADE -> "${listOf("Primeiro", "Segundo", "Terceiro", "Quarto", "Quinto")[requireNotNull(step.decadeIndex)]} mistério"
        PrayerPhase.CLOSING -> "Sua coroa está florescendo"
    }
    val beadLabel = if (step.prayer == FixedPrayer.HAIL_MARY) "Ave-Maria ${requireNotNull(step.beadIndex) + 1} de ${step.beadCount}" else step.intention ?: "Passo ${state.stepIndex + 1} de ${state.sequence.size}"
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact = maxHeight < 650.dp
        val shortScreen = maxHeight < 430.dp
        val touchZoneHeight = maxHeight * .5f
        Column(Modifier.fillMaxSize().testTag("prayer-screen").padding(horizontal = 24.dp)) {
            Row(Modifier.fillMaxWidth().padding(vertical = if (compact) 4.dp else 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                SacredIconButton(SacredIcon.CLOSE, "Voltar ao santuário e pausar", model::returnHome, Modifier.testTag("pause-prayer"))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SacredText("Minha oração", style = type.micro, color = colors.secondary)
                    SacredText("Mistérios ${state.sessionSet.label.lowercase()}", style = type.section.copy(fontSize = 17.sp, lineHeight = 23.sp), color = colors.ink)
                }
                SacredIconButton(SacredIcon.HAPTIC, if (state.hapticsEnabled) "Desativar vibração" else "Ativar vibração", model::toggleHaptics, active = state.hapticsEnabled)
            }
            if (!state.eyesClosed) {
                if (compact) CompactMysteries(state.completedDecades, step.decadeIndex, model::jumpToDecade)
                else FiveMysteriesArc(state.completedDecades, step.decadeIndex, model::jumpToDecade)
                Spacer(Modifier.height(if (compact) 0.dp else 4.dp))
            }
            key(state.stepIndex, state.eyesClosed) {
                PrayerTextReader(Modifier.weight(1f).fillMaxWidth()) {
                    SacredText(chapter, style = type.micro, color = colors.marian, align = TextAlign.Center)
                    Spacer(Modifier.height(if (shortScreen) 2.dp else 8.dp))
                    SacredText(step.title, Modifier.testTag("prayer-title"), if (shortScreen) type.section else type.heading, align = TextAlign.Center)
                    if (step.mystery != null && step.prayer == null) {
                        SacredText(step.mystery.scripture, Modifier.padding(top = 8.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.muted)
                    }
                    if (shortScreen) Spacer(Modifier.height(8.dp)) else {
                        Spacer(Modifier.height(14.dp))
                        SacredFlourish()
                        Spacer(Modifier.height(16.dp))
                    }
                    if (state.eyesClosed) {
                        SacredText("Reze no seu tempo.\nO toque acompanha sua oração.", Modifier.padding(horizontal = 3.dp), type.whisper, colors.secondary, TextAlign.Center)
                    } else {
                        SacredText(step.text, Modifier.padding(horizontal = 3.dp).testTag("prayer-text"), type.prayer, colors.ink, TextAlign.Center)
                    }
                    if (step.intention != null) {
                        SacredText(step.intention, Modifier.padding(top = 16.dp), type.whisper, colors.gold, TextAlign.Center)
                    }
                }
            }
            if (state.eyesClosed) {
                EyesClosedZone(beadLabel, onNext, model::previous, Modifier.height(touchZoneHeight).padding(top = 14.dp, bottom = 12.dp))
            } else {
                if (!shortScreen && step.phase == PrayerPhase.CLOSING) {
                    RoseCrownEmblem(Modifier.align(Alignment.CenterHorizontally).size(104.dp))
                } else if (!shortScreen) {
                    val count = if (step.phase == PrayerPhase.INTRODUCTION) 3 else 10
                    BloomingBeadsArc(count, step.beadIndex, step.prayer == FixedPrayer.GLORY_BE || step.prayer == FixedPrayer.FATIMA, model::jumpToBead)
                }
                SacredText(
                    beadLabel,
                    Modifier.fillMaxWidth().padding(bottom = if (compact) 8.dp else 16.dp).testTag("bead-counter").semantics { liveRegion = LiveRegionMode.Polite },
                    type.micro, colors.secondary, TextAlign.Center,
                )
            }
            Column(Modifier.padding(top = 3.dp, bottom = if (compact) 4.dp else 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SacredIconButton(SacredIcon.BACK, "Oração anterior", model::previous, Modifier.testTag("previous-prayer"), enabled = state.stepIndex > 0)
                    SacredPrimaryButton(
                        if (state.stepIndex == state.sequence.lastIndex) "Entregar minha coroa" else if (step.prayer == FixedPrayer.HAIL_MARY) "Próxima conta" else "Próxima oração",
                        onNext, Modifier.weight(1f), "next-prayer", compact = shortScreen,
                    )
                }
                Row(
                    Modifier.clip(RoundedCornerShape(50)).toggleable(state.eyesClosed, role = Role.Switch, onValueChange = { model.toggleEyesClosed() })
                        .testTag("eyes-closed-toggle").heightIn(min = if (compact) 36.dp else 48.dp).padding(horizontal = 19.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SacredIcon(SacredIcon.EYE, Modifier.size(17.dp), if (state.eyesClosed) colors.marian else colors.muted)
                    SacredText(if (state.eyesClosed) "Rezando de olhos fechados" else "Rezar de olhos fechados", style = type.micro, color = if (state.eyesClosed) colors.marian else colors.secondary)
                }
            }
        }
    }
}

@Composable
private fun EyesClosedZone(label: String, onNext: () -> Unit, onPrevious: () -> Unit, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val breath = rememberGardenBreath("eyes-closed-breath", 6400)
    val next by rememberUpdatedState(onNext)
    val previous by rememberUpdatedState(onPrevious)
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(36.dp))
            .background(Brush.verticalGradient(listOf(colors.surface, lerp(colors.surface, SertumTheme.atmosphere.tint, .6f))))
            .border(1.dp, colors.border, RoundedCornerShape(36.dp))
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
        Box(contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(132.dp)) {
                // Slow rings open from the rose, like breath, to rest the eyes on.
                val swell = .5f + .5f * sin(breath.value)
                drawCircle(colors.ruby.copy(alpha = .05f + .04f * swell), size.minDimension * (.4f + .1f * swell))
                drawCircle(colors.ruby.copy(alpha = .08f), size.minDimension * .3f)
            }
            RoseMark(Modifier.size(64.dp), colors.ruby.copy(alpha = .85f))
        }
        Spacer(Modifier.height(10.dp))
        SacredText("Seu toque acompanha a oração.", style = SertumTheme.typography.section, align = TextAlign.Center)
        SacredText(label, Modifier.padding(top = 6.dp).testTag("bead-counter"), SertumTheme.typography.micro, colors.marian)
        SacredText("Toque para seguir. Deslize para voltar.", Modifier.padding(top = 14.dp), SertumTheme.typography.micro.copy(fontWeight = FontWeight.Normal), colors.secondary, TextAlign.Center)
    }
}
