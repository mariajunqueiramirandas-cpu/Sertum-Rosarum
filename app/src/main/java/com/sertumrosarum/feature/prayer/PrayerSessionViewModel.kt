package com.sertumrosarum.feature.prayer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sertumrosarum.domain.liturgy.LiturgicalCalendar
import com.sertumrosarum.domain.liturgy.LiturgicalDay
import com.sertumrosarum.domain.lumen.CustomRosarySupplement
import com.sertumrosarum.domain.treasury.PrayerLanguage
import com.sertumrosarum.domain.prayer.ChapletContemplations
import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.domain.prayer.Mystery
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.prayer.MysteryContent
import com.sertumrosarum.domain.prayer.MysteryStudy
import com.sertumrosarum.domain.prayer.MysteryStudyCatalog
import com.sertumrosarum.domain.prayer.PrayerMode
import com.sertumrosarum.domain.prayer.PrayerPhase
import com.sertumrosarum.domain.prayer.PrayerScript
import com.sertumrosarum.domain.prayer.ThemedMeditations
import com.sertumrosarum.domain.prayer.ThemedMysteryDetail
import com.sertumrosarum.feature.guides.PrayerGuide
import com.sertumrosarum.data.garden.GardenRepository
import com.sertumrosarum.ui.theme.ReadingScale
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(val label: String) { SYSTEM("Sistema"), DAY("Dia"), VIGIL("Vigília") }
/** [HOME] is the tabbed sanctuary; the other two are full-screen and hide the tabs. */
enum class SanctuaryScreen { HOME, PRAYER, DELIVERED }
enum class SanctuaryTab(val label: String, val caption: String) {
    CATECHISM("Catequese", "Aprender, perguntar e viver a fé"),
    GUIDES("Roteiros", "Trilhas e terços"),
    HOME("Início", "O centro do seu jardim"),
    TREASURY("Tesouro", "Orações e formação"),
    GARDEN("Meu Jardim", "Diário e intenções"),
}
enum class PrayerPulse { SOFT, TRANSITION, COMPLETION }

data class PrayerUiState(
    val today: LocalDate,
    val selectedSet: MysterySet,
    val sessionSet: MysterySet = selectedSet,
    val selectedFocusMystery: Int = 0,
    val sessionFocusMystery: Int = 0,
    val selectedMode: PrayerMode = PrayerMode.SOLO,
    val sessionMode: PrayerMode = selectedMode,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val readingScale: ReadingScale = ReadingScale.STANDARD,
    val volumeKeysEnabled: Boolean = true,
    val screen: SanctuaryScreen = SanctuaryScreen.HOME,
    val tab: SanctuaryTab = SanctuaryTab.HOME,
    val sessionGuide: PrayerGuide = PrayerGuide.TRADITIONAL,
    val meditated: Boolean = false,
    val stepIndex: Int = 0,
    val startedAt: Long? = null,
    val finishedAt: Long? = null,
    val eyesClosed: Boolean = false,
    val hapticsEnabled: Boolean = true,
    val language: PrayerLanguage = PrayerLanguage.PORTUGUESE,
    val sessionId: String? = null,
    val revision: Long = 0,
    val offeredIntentions: List<OfferedIntention> = emptyList(),
    val customSupplement: CustomRosarySupplement? = null,
) {
    val liturgicalDay: LiturgicalDay get() = LiturgicalCalendar.forDate(today)
    val script get() = sessionGuide.script
    val sequence = script.build(sessionSet, sessionFocusMystery)
    val step get() = sequence[stepIndex]
    /** Fixed liturgical prayers ALWAYS resolve from [FixedPrayer] and can never be replaced by Lumen. */
    val prayerText get() = step.prayer?.textFor(sessionMode) ?: step.reading?.textFor(sessionMode) ?: step.text
    val groupCount get() = script.groupCount
    /** Active mystery set for the current step (transitions automatically across sets in the Full Rosary). */
    val activeMysterySet: MysterySet get() = step.mysterySet ?: sessionSet
    /** Index 0..4 within [activeMysterySet] when praying any Rosary script (including single decade and full rosary). */
    val effectiveMysteryIndex: Int? get() = when {
        step.decadeIndex == null -> null
        script == PrayerScript.SINGLE_DECADE -> sessionFocusMystery.coerceIn(0, 4)
        script == PrayerScript.FULL_ROSARY -> requireNotNull(step.decadeIndex) % 5
        script.isRosary -> step.decadeIndex
        else -> step.decadeIndex
    }
    val sessionTitle get() = when {
        script == PrayerScript.FULL_ROSARY -> "Mistérios ${activeMysterySet.label.lowercase()}"
        script.isRosary -> "Mistérios ${sessionSet.label.lowercase()}"
        else -> sessionGuide.title
    }
    val groupLabel get() = script.groupLabel
    val crownBeadCount get() = when (script) {
        PrayerScript.SINGLE_DECADE -> 10
        PrayerScript.FULL_ROSARY -> 200
        else -> script.chaplet?.let { crown ->
            crown.groups.sumOf { it.beadCount } + crown.closing.count { it.isSmallBead }
        } ?: 50
    }
    /** The small beads of this actual group, including the Franciscan closing pair. */
    val groupBeads get() = sequence.filter { it.phase == step.phase && it.decadeIndex == step.decadeIndex && it.isSmallBead }
    val beadLabel get() = if (groupBeads.firstOrNull()?.prayer == FixedPrayer.HAIL_MARY) "Ave-Maria" else script.beadLabel
    val beadsComplete get() = groupBeads.lastOrNull()?.let { stepIndex > sequence.indexOf(it) } ?: false
    val hasSession get() = startedAt != null && finishedAt == null
    val completedDecades get() = when (step.phase) {
        PrayerPhase.INTRODUCTION -> 0
        PrayerPhase.DECADE -> requireNotNull(step.decadeIndex)
        PrayerPhase.CLOSING -> groupCount
    }
    val progress get() = if (finishedAt != null) 1f else stepIndex.toFloat() / sequence.size
    /** Mystery metadata for the active step, including chaplets whose steps carry contextual meditations. */
    val activeMystery: Mystery? get() = step.mystery ?: ChapletContemplations.mysteryFor(script, step.decadeIndex)
    /** The written layers of the mystery or devotional group being prayed. */
    val contemplation get() = ChapletContemplations.forStep(script, activeMysterySet, effectiveMysteryIndex)
    /** Deep historical, biblical, and patristic study layer when praying a Rosary mystery. */
    val mysteryStudy: MysteryStudy? get() = effectiveMysteryIndex?.takeIf { script.isRosary }?.let { MysteryStudyCatalog.studyFor(activeMysterySet, it) }
    val themedMeditation get() = effectiveMysteryIndex?.let { ThemedMeditations.forMystery(script, activeMysterySet, it) }
    val themedDetail: ThemedMysteryDetail? get() = effectiveMysteryIndex?.let { ThemedMeditations.detailFor(script, activeMysterySet, it) }
    /** Personalized reflection for the active mystery when praying with a Lumen custom supplement. */
    val customMysteryDecade get() = customSupplement?.takeIf { script.isRosary }?.let { sup ->
        effectiveMysteryIndex?.let { sup.decadeFor(it) }
    }
    /** One line for the current Ave-Maria, shown only while the bead-by-bead rosary is on. */
    val beadMeditation get() = if (meditated && script.isRosary && step.prayer == FixedPrayer.HAIL_MARY) {
        val customLine = customSupplement?.let { sup ->
            val mIdx = effectiveMysteryIndex
            val bIdx = step.beadIndex
            if (mIdx != null && bIdx != null) sup.beadMeditationFor(mIdx, bIdx) else null
        }
        customLine ?: contemplation?.beads?.getOrNull(step.beadIndex ?: -1)
    } else null
    val catecheticalNote get() = if (sessionGuide.catechetical) MysteryContent.catecheticalNote(step) else null
    val resumeDescription get() = buildString {
        append(sessionGuide.title); append(" · ")
        if (step.decadeIndex != null) append("${(step.decadeIndex ?: 0) + 1}º $groupLabel · ")
        if (step.isSmallBead) append("$beadLabel ${(step.beadIndex ?: 0) + 1}/${groupBeads.size}") else append(step.title)
    }
    val durationSeconds get() = ((finishedAt ?: startedAt ?: 0L) - (startedAt ?: 0L)).coerceAtLeast(0L) / 1000L
}

class PrayerSessionViewModel(
    private val savedState: SavedStateHandle,
    private val clock: Clock,
) : ViewModel() {
    constructor(savedState: SavedStateHandle) : this(savedState, Clock.systemDefaultZone())

    private val today = LocalDate.now(clock)
    private val initialSet = if (savedState.get<String>("selectionDate") == today.toString()) {
        savedEnum<MysterySet>("selectedSet") ?: LiturgicalCalendar.forDate(today).recommendedMysterySet
    } else LiturgicalCalendar.forDate(today).recommendedMysterySet
    private val initialGuide = savedEnum<PrayerGuide>("sessionGuide") ?: PrayerGuide.TRADITIONAL
    private val initialSessionSet = savedEnum<MysterySet>("sessionSet") ?: initialSet
    private val initialFocus = (savedState.get<Int>("sessionFocusMystery") ?: 0).coerceIn(0, 4)
    private val initialMode = savedEnum<PrayerMode>("selectedMode") ?: PrayerMode.SOLO
    private inline fun <reified T : Enum<T>> savedEnum(key: String): T? =
        savedState.get<String>(key)?.let { name -> enumValues<T>().firstOrNull { it.name == name } }
    private val mutableState = MutableStateFlow(
        PrayerUiState(
            today = today,
            selectedSet = initialSet,
            sessionSet = initialSessionSet,
            selectedFocusMystery = (savedState.get<Int>("selectedFocusMystery") ?: 0).coerceIn(0, 4),
            sessionFocusMystery = initialFocus,
            selectedMode = initialMode,
            // Sessions saved before modes existed used the group prayer texts.
            sessionMode = savedEnum<PrayerMode>("sessionMode")
                ?: if (savedState.get<Long>("startedAt") != null) PrayerMode.GROUP else initialMode,
            themeMode = savedEnum<ThemeMode>("themeMode") ?: ThemeMode.SYSTEM,
            readingScale = savedEnum<ReadingScale>("readingScale") ?: ReadingScale.STANDARD,
            volumeKeysEnabled = savedState["volumeKeysEnabled"] ?: true,
            screen = savedEnum<SanctuaryScreen>("screen") ?: SanctuaryScreen.HOME,
            tab = savedEnum<SanctuaryTab>("tab") ?: SanctuaryTab.HOME,
            sessionGuide = initialGuide,
            meditated = savedState["meditated"] ?: false,
            stepIndex = (savedState.get<Int>("stepIndex") ?: 0).coerceIn(0, initialGuide.script.build(initialSessionSet, initialFocus).lastIndex),
            startedAt = savedState["startedAt"],
            finishedAt = savedState["finishedAt"],
            eyesClosed = savedState["eyesClosed"] ?: false,
            hapticsEnabled = savedState["hapticsEnabled"] ?: true,
            sessionId = savedState["sessionId"], revision = savedState["revision"] ?: 0,
            language = savedEnum<PrayerLanguage>("language") ?: PrayerLanguage.PORTUGUESE,
        ),
    )
    val state = mutableState.asStateFlow()
    private var repository: GardenRepository? = null
    suspend fun connect(repository: GardenRepository) {
        if (this.repository != null) return
        val (prefs, active) = withContext(Dispatchers.IO) { repository.preferences() to repository.dao.activeSession() }
        var restored = mutableState.value.copy(
            themeMode = if (savedState.contains("themeMode")) mutableState.value.themeMode else enumOr(prefs.theme, ThemeMode.SYSTEM),
            readingScale = if (savedState.contains("readingScale")) mutableState.value.readingScale else enumOr(prefs.readingScale, ReadingScale.STANDARD),
            volumeKeysEnabled = if (savedState.contains("volumeKeysEnabled")) mutableState.value.volumeKeysEnabled else prefs.volumeKeys,
            language = if (savedState.contains("language")) mutableState.value.language else enumOr(prefs.prayerLanguage, PrayerLanguage.PORTUGUESE),
            selectedMode = if (savedState.contains("selectedMode")) mutableState.value.selectedMode else enumOr(prefs.mode, PrayerMode.SOLO),
            hapticsEnabled = if (savedState.contains("hapticsEnabled")) mutableState.value.hapticsEnabled else prefs.haptics,
            meditated = if (savedState.contains("meditated")) mutableState.value.meditated else prefs.meditated,
        )
        // The durable snapshot is authoritative on a cold launch; Android state keeps an open screen on rotation.
        val androidSaved = savedState.get<String>("durableSnapshot")?.let { restorePrayer(it, restored) }
        val diskSaved = active?.snapshot?.let { restorePrayer(it, restored) }
        val latest = when {
            androidSaved == null -> diskSaved
            diskSaved == null -> androidSaved
            androidSaved.sessionId == diskSaved.sessionId -> if (androidSaved.revision >= diskSaved.revision) androidSaved else diskSaved
            (androidSaved.startedAt ?: 0L) > (diskSaved.startedAt ?: 0L) -> androidSaved
            else -> diskSaved
        }
        if (latest != null) restored = latest.copy(screen = if (latest.sessionId == androidSaved?.sessionId) restored.screen else SanctuaryScreen.HOME)
        if (restored.startedAt != null && restored.sessionId == null) restored = restored.copy(sessionId = UUID.randomUUID().toString())
        mutableState.value = restored
        this.repository = repository
        if (restored.sessionId != null) repository.enqueue { repository.persistPrayer(restored) }
    }

    private fun update(transform: (PrayerUiState) -> PrayerUiState) {
        val next = transform(mutableState.value).copy(revision = mutableState.value.revision + 1)
        mutableState.value = next
        savedState["selectedSet"] = next.selectedSet.name
        savedState["selectionDate"] = next.today.toString()
        savedState["sessionSet"] = next.sessionSet.name
        savedState["selectedFocusMystery"] = next.selectedFocusMystery
        savedState["sessionFocusMystery"] = next.sessionFocusMystery
        savedState["selectedMode"] = next.selectedMode.name
        savedState["sessionMode"] = next.sessionMode.name
        savedState["themeMode"] = next.themeMode.name
        savedState["readingScale"] = next.readingScale.name
        savedState["volumeKeysEnabled"] = next.volumeKeysEnabled
        savedState["screen"] = next.screen.name
        savedState["tab"] = next.tab.name
        savedState["sessionGuide"] = next.sessionGuide.name
        savedState["meditated"] = next.meditated
        savedState["stepIndex"] = next.stepIndex
        savedState["startedAt"] = next.startedAt
        savedState["finishedAt"] = next.finishedAt
        savedState["eyesClosed"] = next.eyesClosed
        savedState["hapticsEnabled"] = next.hapticsEnabled
        savedState["language"] = next.language.name
        savedState["sessionId"] = next.sessionId; savedState["revision"] = next.revision
        if (next.sessionId != null) savedState["durableSnapshot"] = next.snapshot()
        repository?.let { repo -> repo.enqueue { repo.persistPrayer(next) } }
    }

    fun selectLanguage(language: PrayerLanguage) = update { it.copy(language = language) }
    fun selectMystery(set: MysterySet) = update { it.copy(selectedSet = set) }
    fun selectFocusMystery(index: Int) = update { it.copy(selectedFocusMystery = index.coerceIn(0, 4)) }
    fun selectPrayerMode(mode: PrayerMode) = update { it.copy(selectedMode = mode) }
    fun refreshDay() {
        val currentDay = LocalDate.now(clock)
        if (state.value.today != currentDay) update { it.copy(today = currentDay, selectedSet = LiturgicalCalendar.forDate(currentDay).recommendedMysterySet) }
    }
    fun setTheme(mode: ThemeMode) = update { it.copy(themeMode = mode) }
    fun setReadingScale(scale: ReadingScale) = update { it.copy(readingScale = scale) }
    fun toggleVolumeKeys() = update { it.copy(volumeKeysEnabled = !it.volumeKeysEnabled) }
    fun toggleEyesClosed() = update { it.copy(eyesClosed = !it.eyesClosed) }
    fun toggleHaptics() = update { it.copy(hapticsEnabled = !it.hapticsEnabled) }
    fun selectTab(tab: SanctuaryTab) = update { it.copy(tab = tab) }
    fun toggleMeditated() = update { it.copy(meditated = !it.meditated) }
    fun start() = start(PrayerGuide.TRADITIONAL)

    /** Begins a crown with its own sequence and keeps the previous tab for the return. */
    fun start(guide: PrayerGuide) = start(guide, emptyList())

    fun start(guide: PrayerGuide, intentions: List<OfferedIntention>) {
        startGuideWithOptions(guide, intentions = intentions)
    }

    /** Launches any bundled PrayerGuide (Rosary, themed rosary, devotional crown, or Via-Sacra) with optional overrides. */
    fun startGuideWithOptions(
        guide: PrayerGuide,
        mysterySet: MysterySet? = null,
        focusMysteryIndex: Int? = null,
        meditatedOverride: Boolean? = null,
        intentions: List<OfferedIntention> = emptyList(),
    ) {
        update {
            val targetSet = mysterySet ?: it.selectedSet
            val targetFocus = (focusMysteryIndex ?: it.selectedFocusMystery).coerceIn(0, 4)
            it.copy(
                sessionId = UUID.randomUUID().toString(),
                offeredIntentions = intentions.distinctBy { item -> item.id },
                screen = SanctuaryScreen.PRAYER,
                selectedSet = targetSet,
                sessionSet = targetSet,
                selectedFocusMystery = targetFocus,
                sessionFocusMystery = targetFocus,
                sessionMode = it.selectedMode,
                sessionGuide = guide,
                meditated = meditatedOverride ?: it.meditated,
                customSupplement = null,
                stepIndex = 0,
                startedAt = clock.millis(),
                finishedAt = null,
            )
        }
    }

    /** Starts a single-decade Rosary focused on a specific mystery (e.g., from Mystery Study or the Catechist). */
    fun startSingleDecade(
        set: MysterySet,
        mysteryIndex: Int,
        intentions: List<OfferedIntention> = emptyList(),
        meditatedOverride: Boolean? = null,
    ) {
        val focus = mysteryIndex.coerceIn(0, 4)
        update {
            it.copy(
                sessionId = UUID.randomUUID().toString(),
                offeredIntentions = intentions.distinctBy { item -> item.id },
                screen = SanctuaryScreen.PRAYER,
                selectedSet = set,
                sessionSet = set,
                selectedFocusMystery = focus,
                sessionFocusMystery = focus,
                sessionMode = it.selectedMode,
                sessionGuide = PrayerGuide.SINGLE_DECADE,
                meditated = meditatedOverride ?: it.meditated,
                customSupplement = null,
                stepIndex = 0,
                startedAt = clock.millis(),
                finishedAt = null,
            )
        }
    }

    /** Starts a Rosary woven with a Lumen [CustomRosarySupplement] (fixed Church prayers remain immutable). */
    fun startCustomRosary(
        supplement: CustomRosarySupplement,
        intentions: List<OfferedIntention> = emptyList(),
        singleDecadeIndex: Int? = null,
    ) {
        val focus = (singleDecadeIndex ?: 0).coerceIn(0, 4)
        val guide = if (singleDecadeIndex != null) PrayerGuide.SINGLE_DECADE else PrayerGuide.TRADITIONAL
        update {
            it.copy(
                sessionId = UUID.randomUUID().toString(),
                offeredIntentions = intentions.distinctBy { item -> item.id },
                screen = SanctuaryScreen.PRAYER,
                selectedSet = supplement.mysterySet,
                sessionSet = supplement.mysterySet,
                selectedFocusMystery = focus,
                sessionFocusMystery = focus,
                sessionMode = it.selectedMode,
                sessionGuide = guide,
                meditated = true,
                customSupplement = supplement,
                stepIndex = 0,
                startedAt = clock.millis(),
                finishedAt = null,
            )
        }
    }

    /** Starts a traditional 5-decade Rosary for a chosen mystery set directly (e.g., from Mystery Study). */
    fun startRosarySet(set: MysterySet, intentions: List<OfferedIntention> = emptyList()) {
        update {
            it.copy(
                sessionId = UUID.randomUUID().toString(),
                offeredIntentions = intentions.distinctBy { item -> item.id },
                screen = SanctuaryScreen.PRAYER,
                selectedSet = set,
                sessionSet = set,
                sessionMode = it.selectedMode,
                sessionGuide = PrayerGuide.TRADITIONAL,
                customSupplement = null,
                stepIndex = 0,
                startedAt = clock.millis(),
                finishedAt = null,
            )
        }
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
        val pulse = if (current.step.isSmallBead && current.step.beadIndex != current.step.beadCount - 1) {
            PrayerPulse.SOFT
        } else PrayerPulse.TRANSITION
        update { it.copy(stepIndex = it.stepIndex + 1) }
        return pulse
    }

    fun previous() = update {
        if (it.screen == SanctuaryScreen.PRAYER) it.copy(stepIndex = (it.stepIndex - 1).coerceAtLeast(0)) else it
    }

    fun jumpToDecade(decade: Int) {
        if (decade !in 0 until state.value.groupCount || state.value.screen != SanctuaryScreen.PRAYER) return
        update { state -> state.copy(stepIndex = state.sequence.indexOfFirst { it.decadeIndex == decade }) }
    }

    fun jumpToBead(bead: Int) {
        val current = state.value
        if (current.screen != SanctuaryScreen.PRAYER) return
        val targetStep = current.groupBeads.firstOrNull { it.beadIndex == bead } ?: return
        val target = current.sequence.indexOf(targetStep)
        if (target >= 0) update { it.copy(stepIndex = target) }
    }
}
