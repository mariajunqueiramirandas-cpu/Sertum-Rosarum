package com.sertumrosarum.feature.prayer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.prayer.PrayerPhase
import com.sertumrosarum.domain.prayer.RosarySequenceBuilder
import java.time.Clock
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(val label: String) { SYSTEM("Sistema"), DAY("Dia"), VIGIL("Vigília") }
enum class SanctuaryScreen { HOME, PRAYER, DELIVERED }
enum class PrayerPulse { SOFT, TRANSITION, COMPLETION }

data class PrayerUiState(
    val today: LocalDate,
    val selectedSet: MysterySet,
    val sessionSet: MysterySet = selectedSet,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val screen: SanctuaryScreen = SanctuaryScreen.HOME,
    val stepIndex: Int = 0,
    val startedAt: Long? = null,
    val finishedAt: Long? = null,
    val eyesClosed: Boolean = false,
    val hapticsEnabled: Boolean = true,
) {
    val sequence = RosarySequenceBuilder.build(sessionSet)
    val step get() = sequence[stepIndex]
    val hasSession get() = startedAt != null && finishedAt == null
    val completedDecades get() = when (step.phase) {
        PrayerPhase.INTRODUCTION -> 0
        PrayerPhase.DECADE -> requireNotNull(step.decadeIndex)
        PrayerPhase.CLOSING -> 5
    }
    val progress get() = if (finishedAt != null) 1f else stepIndex.toFloat() / sequence.size
    val durationSeconds get() = ((finishedAt ?: startedAt ?: 0L) - (startedAt ?: 0L)).coerceAtLeast(0L) / 1000L
}

class PrayerSessionViewModel(
    private val savedState: SavedStateHandle,
    private val clock: Clock,
) : ViewModel() {
    constructor(savedState: SavedStateHandle) : this(savedState, Clock.systemDefaultZone())

    private val today = LocalDate.now(clock)
    private val initialSet = if (savedState.get<String>("selectionDate") == today.toString()) {
        savedState.get<String>("selectedSet")?.let(MysterySet::valueOf) ?: MysterySet.forDay(today.dayOfWeek)
    } else MysterySet.forDay(today.dayOfWeek)
    private val mutableState = MutableStateFlow(
        PrayerUiState(
            today = today,
            selectedSet = initialSet,
            sessionSet = savedState.get<String>("sessionSet")?.let(MysterySet::valueOf) ?: initialSet,
            themeMode = savedState.get<String>("themeMode")?.let(ThemeMode::valueOf) ?: ThemeMode.SYSTEM,
            screen = savedState.get<String>("screen")?.let(SanctuaryScreen::valueOf) ?: SanctuaryScreen.HOME,
            stepIndex = (savedState.get<Int>("stepIndex") ?: 0).coerceIn(0, 79),
            startedAt = savedState["startedAt"],
            finishedAt = savedState["finishedAt"],
            eyesClosed = savedState["eyesClosed"] ?: false,
            hapticsEnabled = savedState["hapticsEnabled"] ?: true,
        ),
    )
    val state = mutableState.asStateFlow()

    private fun update(transform: (PrayerUiState) -> PrayerUiState) {
        val next = transform(mutableState.value)
        mutableState.value = next
        savedState["selectedSet"] = next.selectedSet.name
        savedState["selectionDate"] = next.today.toString()
        savedState["sessionSet"] = next.sessionSet.name
        savedState["themeMode"] = next.themeMode.name
        savedState["screen"] = next.screen.name
        savedState["stepIndex"] = next.stepIndex
        savedState["startedAt"] = next.startedAt
        savedState["finishedAt"] = next.finishedAt
        savedState["eyesClosed"] = next.eyesClosed
        savedState["hapticsEnabled"] = next.hapticsEnabled
    }

    fun selectMystery(set: MysterySet) = update { it.copy(selectedSet = set) }
    fun refreshDay() {
        val currentDay = LocalDate.now(clock)
        if (state.value.today != currentDay) update { it.copy(today = currentDay, selectedSet = MysterySet.forDay(currentDay.dayOfWeek)) }
    }
    fun setTheme(mode: ThemeMode) = update { it.copy(themeMode = mode) }
    fun toggleEyesClosed() = update { it.copy(eyesClosed = !it.eyesClosed) }
    fun toggleHaptics() = update { it.copy(hapticsEnabled = !it.hapticsEnabled) }
    fun start() = update {
        it.copy(screen = SanctuaryScreen.PRAYER, sessionSet = it.selectedSet, stepIndex = 0, startedAt = clock.millis(), finishedAt = null)
    }
    fun resume() = update { if (it.hasSession) it.copy(screen = SanctuaryScreen.PRAYER) else it }
    fun returnHome() = update { it.copy(screen = SanctuaryScreen.HOME) }

    fun next(): PrayerPulse? {
        val current = state.value
        if (current.screen != SanctuaryScreen.PRAYER) return null
        if (current.stepIndex == current.sequence.lastIndex) {
            update { it.copy(screen = SanctuaryScreen.DELIVERED, finishedAt = clock.millis()) }
            return PrayerPulse.COMPLETION
        }
        val pulse = if (current.step.prayer == FixedPrayer.HAIL_MARY && current.step.beadIndex != current.step.beadCount - 1) {
            PrayerPulse.SOFT
        } else PrayerPulse.TRANSITION
        update { it.copy(stepIndex = it.stepIndex + 1) }
        return pulse
    }

    fun previous() = update {
        if (it.screen == SanctuaryScreen.PRAYER) it.copy(stepIndex = (it.stepIndex - 1).coerceAtLeast(0)) else it
    }

    fun jumpToDecade(decade: Int) {
        if (decade !in 0..4 || state.value.screen != SanctuaryScreen.PRAYER) return
        update { state -> state.copy(stepIndex = state.sequence.indexOfFirst { it.decadeIndex == decade }) }
    }

    fun jumpToBead(bead: Int) {
        val current = state.value
        if (current.screen != SanctuaryScreen.PRAYER || current.step.phase == PrayerPhase.CLOSING) return
        val count = if (current.step.phase == PrayerPhase.INTRODUCTION) 3 else 10
        if (bead !in 0 until count) return
        val target = current.sequence.indexOfFirst {
            it.decadeIndex == current.step.decadeIndex && it.prayer == FixedPrayer.HAIL_MARY && it.beadIndex == bead
        }
        if (target >= 0) update { it.copy(stepIndex = target) }
    }
}
