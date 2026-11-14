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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.domain.treasury.PrayerLanguage
import com.sertumrosarum.domain.treasury.TreasuryPrayer
import com.sertumrosarum.feature.treasury.inLatin
import com.sertumrosarum.domain.prayer.PrayerMode
import com.sertumrosarum.domain.prayer.PrayerPhase
import com.sertumrosarum.feature.guides.PrayerGuide
import com.sertumrosarum.ui.canvas.BloomingBeadsArc
import com.sertumrosarum.ui.canvas.CompactMysteries
import com.sertumrosarum.ui.canvas.FiveMysteriesArc
import com.sertumrosarum.ui.canvas.RoseMark
import com.sertumrosarum.ui.canvas.rememberGardenBreath
import com.sertumrosarum.ui.components.SacredBottomSheet
import com.sertumrosarum.ui.components.SacredFlourish
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredIconButton
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun ImmersivePrayerScreen(
    state: PrayerUiState,
    model: PrayerSessionViewModel,
    language: PrayerLanguage = PrayerLanguage.PORTUGUESE,
    onLanguage: (PrayerLanguage) -> Unit = {},
    /** The Treasury's entry for the prayer being prayed, when it carries a Latin text. */
    latin: TreasuryPrayer? = null,
    onNext: () -> Unit,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val page = SertumTheme.spacing.page
    val largeText = LocalDensity.current.fontScale > 1.3f
    val step = state.step
    val chapter = when (step.phase) {
        PrayerPhase.INTRODUCTION -> "Para aquietar o coração"
        PrayerPhase.DECADE -> "${ordinal(requireNotNull(step.decadeIndex)).let { if (state.groupLabel == "mistério") it else it.removeSuffix("o") + "a" }} ${state.groupLabel}"
        PrayerPhase.CLOSING -> "Sua coroa está florescendo"
    }
    val beadLabel = if (step.isSmallBead) "${state.beadLabel} ${requireNotNull(step.beadIndex) + 1} de ${step.beadCount}" else "Passo ${state.stepIndex + 1} de ${state.sequence.size}"
    val groups = state.groupCount
    val announcing = step.isAnnouncement
    val latinText = if (state.sessionMode == com.sertumrosarum.domain.prayer.PrayerMode.SOLO) latin?.latinSolo ?: latin?.latin else latin?.latin
    val inLatin = language == PrayerLanguage.LATIN && latinText != null
    var contemplating by rememberSaveable(state.stepIndex) { mutableStateOf(false) }
    var readingSection by rememberSaveable(state.stepIndex) { mutableIntStateOf(0) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val shortScreen = maxHeight < 430.dp
        val touchZoneHeight = maxHeight * if (shortScreen) .32f else .5f
        Column(Modifier.fillMaxSize().testTag("prayer-screen")) {
            Row(Modifier.fillMaxWidth().padding(horizontal = page, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                SacredIconButton(SacredIcon.CLOSE, "Voltar ao santuário e pausar", model::returnHome, Modifier.testTag("pause-prayer"))
                Column(Modifier.weight(1f).padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    SacredText(state.sessionTitle, Modifier.fillMaxWidth().testTag("prayer-session-title"), style = type.section.copy(fontSize = 17.sp, lineHeight = 23.sp), color = colors.ink, align = TextAlign.Center)
                    // A generic "Minha oração" repeats the screen's purpose. Keep
                    // meaningful guide names on their own line so large type can wrap.
                    if (state.sessionGuide.prayerLabel != PrayerGuide.TRADITIONAL.prayerLabel) {
                        SacredText(state.sessionGuide.prayerLabel, Modifier.fillMaxWidth().testTag("prayer-guide"), type.micro.copy(fontSize = 12.sp, lineHeight = 16.sp), colors.secondary, TextAlign.Center)
                    }
                    SacredText(if (state.sessionMode == PrayerMode.SOLO) "Oração a sós" else "Oração em grupo", Modifier.fillMaxWidth().testTag("session-mode"), type.micro.copy(fontSize = 12.sp, lineHeight = 16.sp), colors.secondary, TextAlign.Center)
                }
                SacredIconButton(SacredIcon.HAPTIC, if (state.hapticsEnabled) "Desativar vibração" else "Ativar vibração", model::toggleHaptics, active = state.hapticsEnabled)
            }
            if (!state.eyesClosed) {
                if (shortScreen) Box(Modifier.padding(horizontal = 20.dp)) { CompactMysteries(state.completedDecades, step.decadeIndex, model::jumpToDecade, groups, state.groupLabel) }
                else FiveMysteriesArc(state.completedDecades, step.decadeIndex, model::jumpToDecade, Modifier.padding(horizontal = 16.dp), groups = groups, groupLabel = state.groupLabel, compact = true)
            }
            key(state.stepIndex, state.eyesClosed, language) {
                PrayerTextReader(
                    Modifier.weight(1f).fillMaxWidth().padding(horizontal = page - 6.dp),
                    // A scroll cue must not recenter the heading or first line.
                    // Only the concise eyes-closed introduction is centered vertically.
                    centerContent = state.eyesClosed,
                    readingKey = readingSection,
                ) {
                    if (state.eyesClosed) {
                        val titleStyle = if (shortScreen || largeText) type.section else type.heading
                        SacredText(if (inLatin) latin?.latinTitle ?: step.title else step.title, Modifier.fillMaxWidth().testTag("prayer-title"), if (inLatin) titleStyle.inLatin else titleStyle, align = TextAlign.Center)
                        if (!shortScreen && !largeText) {
                            SacredText("Reze no seu tempo.\nO toque acompanha sua oração.", Modifier.padding(top = 10.dp), type.whisper, colors.secondary, TextAlign.Center)
                        }
                    } else {
                        val meditation = state.beadMeditation
                        if (meditation != null && step.mystery != null) {
                            // In the bead-by-bead rosary the line to contemplate leads, and the prayer follows it.
                            MeditatedBead(requireNotNull(step.decadeIndex), step.mystery, meditation, { contemplating = true }, Modifier.padding(bottom = 12.dp))
                        } else {
                            // Within a decade the chapter line becomes the way back into its mystery.
                            if (step.mystery != null && state.contemplation != null && !announcing) {
                                MysteryLink(requireNotNull(step.decadeIndex), step.mystery, { contemplating = true })
                                Spacer(Modifier.height(if (shortScreen) 0.dp else 2.dp))
                            } else {
                                SacredText(chapter, style = type.micro, color = colors.accentInk, align = TextAlign.Center)
                                Spacer(Modifier.height(4.dp))
                            }
                            val titleStyle = if (shortScreen) type.section else type.heading
                            SacredText(if (inLatin) latin?.latinTitle ?: step.title else step.title, Modifier.testTag("prayer-title"), if (inLatin) titleStyle.inLatin else titleStyle, align = TextAlign.Center)
                            if (!shortScreen) {
                                SacredFlourish(Modifier.padding(top = 6.dp, bottom = 8.dp))
                            } else Spacer(Modifier.height(8.dp))
                        }
                        state.catecheticalNote?.let { CatecheticalNote(it, Modifier.padding(bottom = 16.dp)) }
                        val contemplation = state.contemplation
                        if (announcing) state.themedMeditation?.let {
                            SacredText(it, Modifier.padding(bottom = 16.dp).testTag("themed-meditation"), type.whisper, colors.accentInk, TextAlign.Center)
                        }
                        if (step.mystery != null && announcing && contemplation != null) {
                            // The announcement is the mystery itself, layer by layer, before the Pai-Nosso.
                            MysteryDimensions(step.mystery, contemplation, state.meditated, model::toggleMeditated, onLayerSelected = { readingSection = it })
                        } else if (step.reading?.response != null) {
                            LitanyVerses(listOf(state.prayerText to step.reading.response))
                        } else {
                            if (announcing) SacredText(requireNotNull(step.mystery).scripture, Modifier.padding(bottom = 10.dp), type.micro, colors.accentInk, TextAlign.Center)
                            val alignment = when (step.prayer) {
                                FixedPrayer.SIGN_OF_CROSS, FixedPrayer.GLORY_BE -> TextAlign.Center
                                else -> TextAlign.Start
                            }
                            if (latinText != null) PrayerLanguageToggle(language, onLanguage, Modifier.padding(bottom = 4.dp))
                            if (language == PrayerLanguage.BILINGUAL && latinText != null) {
                                Column(Modifier.widthIn(max = 480.dp).fillMaxWidth().testTag("prayer-bilingual"), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    SacredText("Português", style = type.micro, color = colors.accentInk)
                                    SacredText(state.prayerText, Modifier.testTag("prayer-text"), type.prayer, colors.ink, alignment)
                                    SacredText("Latim", style = type.micro, color = colors.accentInk)
                                    SacredText(latinText, Modifier.testTag("prayer-latin-text"), type.prayer.inLatin, colors.secondary, alignment)
                                }
                            } else SacredText(if (inLatin) latinText.orEmpty() else state.prayerText, Modifier.widthIn(max = 480.dp).fillMaxWidth().testTag("prayer-text"), if (inLatin) type.prayer.inLatin else type.prayer, colors.ink, alignment)
                        }
                        if (step.intention != null) {
                            SacredText(step.intention, Modifier.padding(top = 16.dp), type.whisper, colors.gold, TextAlign.Center)
                        }
                    }
                }
            }
            if (state.eyesClosed) {
                EyesClosedZone(beadLabel, onNext, model::previous, Modifier.padding(horizontal = page).height(touchZoneHeight).padding(top = 14.dp, bottom = 12.dp), compact = shortScreen)
            } else {
                if (!shortScreen && state.groupBeads.isNotEmpty()) {
                    BloomingBeadsArc(state.groupBeads.size, step.beadIndex, state.beadsComplete, model::jumpToBead, Modifier.padding(horizontal = 14.dp), beadLabel = state.beadLabel, compact = true)
                }
            }
            Column(Modifier.padding(horizontal = page).padding(bottom = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), horizontalArrangement = Arrangement.spacedBy(8.dp, if (state.eyesClosed) Alignment.End else Alignment.Start), verticalAlignment = Alignment.CenterVertically) {
                    if (!state.eyesClosed) {
                        SacredText(
                            beadLabel,
                            Modifier.weight(1f).testTag("bead-counter").semantics { liveRegion = LiveRegionMode.Polite },
                            type.micro, colors.secondary,
                        )
                    }
                    Row(
                        Modifier.weight(1f, fill = false).clip(RoundedCornerShape(16.dp))
                            .semantics { contentDescription = if (state.eyesClosed) "Voltar à leitura" else "Rezar de olhos fechados" }
                            .toggleable(state.eyesClosed, role = Role.Switch, onValueChange = { model.toggleEyesClosed() })
                            .testTag("eyes-closed-toggle").heightIn(min = 48.dp).padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SacredIcon(SacredIcon.EYE, Modifier.size(17.dp), if (state.eyesClosed) colors.marian else colors.secondary)
                        SacredText(if (state.eyesClosed) "Voltar à leitura" else "Olhos fechados", Modifier.weight(1f, fill = false), type.micro, if (state.eyesClosed) colors.accentInk else colors.secondary)
                    }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SacredIconButton(SacredIcon.BACK, "Oração anterior", model::previous, Modifier.testTag("previous-prayer"), enabled = state.stepIndex > 0)
                    SacredPrimaryButton(
                        if (state.stepIndex == state.sequence.lastIndex) {
                            if (state.sessionMode == PrayerMode.SOLO) "Entregar minha coroa" else "Entregar nossa coroa"
                        } else if (step.isSmallBead) "Próxima conta" else "Próxima oração",
                        onNext, Modifier.weight(1f), "next-prayer", compact = shortScreen,
                    )
                }
            }
        }
        val mystery = step.mystery
        val contemplation = state.contemplation
        SacredBottomSheet(contemplating && mystery != null, { contemplating = false }, "Contemplar o mistério", tag = "mystery-sheet") {
            if (mystery != null && contemplation != null) {
                SacredText("${ordinal(requireNotNull(step.decadeIndex))} mistério ${state.sessionSet.singular.lowercase()}", Modifier.fillMaxWidth(), type.micro, colors.accentInk, TextAlign.Center)
                SacredText(mystery.title, Modifier.fillMaxWidth().padding(top = 6.dp).testTag("mystery-sheet-title"), type.heading, align = TextAlign.Center)
                SacredFlourish(Modifier.align(Alignment.CenterHorizontally).padding(top = 14.dp, bottom = 18.dp))
                state.themedMeditation?.let { SacredText(it, Modifier.padding(bottom = 16.dp).testTag("themed-meditation"), type.whisper, colors.accentInk) }
                MysteryDimensions(mystery, contemplation, state.meditated, model::toggleMeditated, unfolded = true)
                if (state.meditated && contemplation.beads.isNotEmpty()) {
                    SacredText("Conta a conta", Modifier.padding(start = 6.dp, top = 24.dp, bottom = 8.dp), type.section)
                    BeadByBeadList(contemplation.beads, step.beadIndex.takeIf { step.prayer == FixedPrayer.HAIL_MARY })
                }
            }
        }
    }
}

@Composable
private fun EyesClosedZone(label: String, onNext: () -> Unit, onPrevious: () -> Unit, modifier: Modifier = Modifier, compact: Boolean = false) {
    val colors = SertumTheme.colors
    val breath = rememberGardenBreath("eyes-closed-breath", 6400)
    val largeText = LocalDensity.current.fontScale > 1.3f
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
            .padding(if (compact) 8.dp else 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(if (compact) 40.dp else if (largeText) 88.dp else 132.dp)) {
                // Slow rings open from the rose, like breath, to rest the eyes on.
                val swell = .5f + .5f * sin(breath.value)
                drawCircle(colors.ruby.copy(alpha = .05f + .04f * swell), size.minDimension * (.4f + .1f * swell))
                drawCircle(colors.ruby.copy(alpha = .08f), size.minDimension * .3f)
            }
            RoseMark(Modifier.size(if (compact) 32.dp else if (largeText) 56.dp else 72.dp), alpha = .95f)
        }
        if (!compact && !largeText) {
            Spacer(Modifier.height(10.dp))
            SacredText("Seu toque acompanha a oração.", style = SertumTheme.typography.section, align = TextAlign.Center)
        }
        SacredText(label, Modifier.padding(top = 6.dp).testTag("bead-counter"), SertumTheme.typography.micro, colors.accentInk)
        if (!compact) SacredText("Toque para seguir. Deslize para voltar.", Modifier.fillMaxWidth().padding(top = 14.dp), SertumTheme.typography.micro.copy(fontWeight = FontWeight.Normal), colors.secondary, TextAlign.Center)
    }
}
