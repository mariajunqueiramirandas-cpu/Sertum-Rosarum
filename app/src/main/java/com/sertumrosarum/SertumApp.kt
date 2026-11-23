package com.sertumrosarum

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sertumrosarum.core.haptics.HapticFeedbackController
import com.sertumrosarum.data.garden.GardenRepository
import com.sertumrosarum.data.lumen.LumenRepository
import com.sertumrosarum.domain.lumen.CatechistAction
import com.sertumrosarum.domain.lumen.CatechistContext
import com.sertumrosarum.domain.lumen.CatechistPrayer
import com.sertumrosarum.domain.lumen.GardenCrownSnapshot
import com.sertumrosarum.domain.lumen.GardenIntentionSnapshot
import com.sertumrosarum.domain.lumen.GardenJournalSnapshot
import com.sertumrosarum.domain.lumen.GardenSnapshot
import com.sertumrosarum.domain.lumen.LumenToolBridge
import com.sertumrosarum.domain.lumen.PrayerSessionSnapshot
import com.sertumrosarum.domain.treasury.TreasuryCatalog
import com.sertumrosarum.feature.catechist.CatechistScreen
import com.sertumrosarum.feature.catechist.CatechistPrayerSheet
import com.sertumrosarum.feature.catechist.CatechistViewModel
import com.sertumrosarum.feature.formation.FormationScreen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sertumrosarum.feature.garden.GardenScreen
import com.sertumrosarum.feature.garden.GardenSheets
import com.sertumrosarum.feature.garden.GardenState
import com.sertumrosarum.feature.garden.GardenViewModel
import com.sertumrosarum.feature.garden.IntentionPicker
import com.sertumrosarum.feature.garden.QuietAction
import com.sertumrosarum.feature.garden.rememberGardenSheetHost
import com.sertumrosarum.feature.guides.GuideSheetContent
import com.sertumrosarum.feature.guides.GuideSheetFooter
import com.sertumrosarum.feature.guides.GuidesScreen
import com.sertumrosarum.feature.guides.PrayerGuide
import com.sertumrosarum.feature.prayer.CrownDeliveredScreen
import com.sertumrosarum.feature.prayer.ImmersivePrayerScreen
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import com.sertumrosarum.feature.prayer.SanctuaryScreen
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.feature.sanctuary.HomeDashboard
import com.sertumrosarum.feature.sanctuary.SanctuaryScreen as SanctuaryHome
import com.sertumrosarum.feature.sanctuary.UnifiedSearchSheetContent
import com.sertumrosarum.feature.treasury.TreasuryScreen
import com.sertumrosarum.feature.treasury.TreasurySheets
import com.sertumrosarum.feature.treasury.TreasuryViewModel
import com.sertumrosarum.ui.canvas.DevotionalArtwork
import com.sertumrosarum.ui.canvas.DevotionalIllustration
import com.sertumrosarum.ui.components.SacredBottomSheet
import com.sertumrosarum.ui.components.SacredDivider
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredKeepToggle
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredSecondaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SertumBottomBar
import com.sertumrosarum.ui.components.SertumScaffold
import com.sertumrosarum.ui.theme.SertumTheme

@Composable
fun SertumApp(
    model: PrayerSessionViewModel = viewModel(),
    repository: GardenRepository? = null,
    lumenRepository: LumenRepository = remember { LumenRepository() },
) {
    val garden: GardenViewModel? = if (repository != null) viewModel(factory = remember(repository) { GardenViewModel.factory(repository) }) else null
    val catechistModel: CatechistViewModel = viewModel(factory = remember(lumenRepository) { CatechistViewModel.factory(lumenRepository) })
    val catechistState by catechistModel.state.collectAsStateWithLifecycle()
    val personal = garden?.state?.collectAsStateWithLifecycle()?.value ?: GardenState()
    val selected = garden?.selection?.collectAsStateWithLifecycle()?.value ?: emptySet()
    val saveError = repository?.error?.collectAsStateWithLifecycle()?.value
    var ready by remember(repository) { mutableStateOf(repository == null) }
    var startupError by remember { mutableStateOf(false) }
    var retry by remember { mutableStateOf(0) }
    var welcomed by remember { mutableStateOf(true) }
    var picker by rememberSaveable { mutableStateOf(false) }
    var rosarySetup by rememberSaveable { mutableStateOf(false) }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var catechistOpen by rememberSaveable { mutableStateOf(false) }
    var catechistPrepareSheet by rememberSaveable { mutableStateOf(false) }
    var catechistDraft by rememberSaveable { mutableStateOf("") }
    var displayedCatechistPrayer by remember { mutableStateOf<CatechistPrayer?>(null) }
    var catechistPrayerOpen by remember { mutableStateOf(false) }
    var formationLesson by rememberSaveable { mutableStateOf<String?>(null) }
    var formationQuestion by rememberSaveable { mutableStateOf<String?>(null) }
    var formationJourney by rememberSaveable { mutableStateOf<String?>(null) }

    val state by model.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val haptics = remember(context) { HapticFeedbackController(context) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { model.refreshDay() }
    val activeSet = if (state.screen == SanctuaryScreen.HOME) state.selectedSet else state.activeMysterySet
    SertumTheme(state.themeMode, activeSet, state.readingScale) {
        val treasury: TreasuryViewModel = viewModel()
        LaunchedEffect(repository, retry) {
            if (repository != null) {
                startupError = false
                try {
                    model.connect(repository); treasury.connect(repository); catechistModel.connect(repository)
                    welcomed = repository.preferences().welcomed
                    ready = true
                } catch (e: Exception) { if (e is kotlinx.coroutines.CancellationException) throw e; startupError = true }
            }
        }
        val dark = SertumTheme.colors.isDark
        SideEffect {
            (context as? ComponentActivity)?.enableEdgeToEdge(
                statusBarStyle = if (dark) SystemBarStyle.dark(Color.TRANSPARENT) else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                navigationBarStyle = if (android.os.Build.VERSION.SDK_INT < 27) SystemBarStyle.dark(Color.rgb(23, 44, 41)) else if (dark) SystemBarStyle.dark(Color.TRANSPARENT) else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            )
        }
        if (!ready) {
            SertumScaffold {
                Column(Modifier.align(Alignment.Center).padding(28.dp)) {
                    SacredText(if (startupError) "Não foi possível abrir seu jardim." else "Preparando seu jardim…", style = SertumTheme.typography.section)
                    if (startupError) SacredPrimaryButton("Tentar novamente", { retry++ })
                }
            }
            return@SertumTheme
        }
        val tabbed = state.screen == SanctuaryScreen.HOME
        // The sheet keeps its guide while it slides away, so its words do not vanish mid-air.
        var openGuide by rememberSaveable { mutableStateOf<PrayerGuide?>(null) }
        var shownGuide by rememberSaveable { mutableStateOf(PrayerGuide.TRADITIONAL) }
        val gardenSheetHost = rememberGardenSheetHost()
        val treasuryState by treasury.state.collectAsStateWithLifecycle()
        val bookmarkedMysteries = remember(personal.bookmarks) {
            personal.bookmarks.filter { it.kind == "mystery" }.map { it.itemId }.toSet()
        }
        val savedPrayerIds = remember(personal.savedCatechistPrayers) {
            personal.savedCatechistPrayers.map { it.id }.toSet()
        }

        fun openCatechistPrayer(prayer: CatechistPrayer) {
            focusManager.clearFocus()
            displayedCatechistPrayer = prayer
            catechistPrayerOpen = true
        }

        fun openMysteryStudy(set: com.sertumrosarum.domain.prayer.MysterySet, index: Int) {
            treasury.openMysteries()
            treasury.selectStudySet(set)
            treasury.openStudyMystery(index)
            model.selectTab(SanctuaryTab.TREASURY)
        }

        fun openTreasuryPrayer(id: String) {
            TreasuryCatalog.find(id)?.let {
                treasury.openPrayer(it)
                model.selectTab(SanctuaryTab.TREASURY)
            }
        }

        fun openFormation(lesson: String? = null, question: String? = null, journey: String? = null) {
            formationLesson = lesson
            formationQuestion = question
            formationJourney = journey
            model.selectTab(SanctuaryTab.CATECHISM)
        }

        fun openCatechist(
            draft: String = "",
            openPrepare: Boolean = false,
            mysterySet: com.sertumrosarum.domain.prayer.MysterySet = activeSet,
            mysteryIndex: Int? = null,
            mysteryTitle: String? = null,
            autoSendQuestion: String? = null,
        ) {
            val ctx = CatechistContext(
                activeMysterySet = mysterySet,
                activeMysteryIndex = mysteryIndex,
                activeMysteryTitle = mysteryTitle,
                liturgicalSummary = state.liturgicalDay.headerLabel,
                recentIntentions = personal.intentions.filter { !it.archived }.take(3).map { it.title },
            )
            catechistModel.updateContext(ctx)
            catechistDraft = draft
            catechistPrepareSheet = openPrepare
            catechistOpen = true
            if (!autoSendQuestion.isNullOrBlank()) {
                catechistModel.sendMessage(autoSendQuestion, ctx)
            }
        }

        BackHandler(catechistOpen) { catechistOpen = false }
        BackHandler(!catechistOpen && !tabbed) { model.returnHome() }
        BackHandler(!catechistOpen && tabbed && state.tab != SanctuaryTab.HOME) { model.selectTab(SanctuaryTab.HOME) }
        BackHandler(!catechistOpen && tabbed && state.tab == SanctuaryTab.GUIDES && rosarySetup) { rosarySetup = false }
        SertumScaffold {
            Box(Modifier.align(Alignment.TopCenter).fillMaxHeight().widthIn(max = 560.dp).fillMaxWidth()) {
                Crossfade(state.screen, animationSpec = tween(450), label = "sanctuary-transition") { screen ->
                    when (screen) {
                        SanctuaryScreen.HOME -> Column(Modifier.fillMaxSize()) {
                            Crossfade(state.tab, Modifier.weight(1f), tween(320), label = "tab-transition") { tab ->
                                when (tab) {
                                    SanctuaryTab.HOME -> HomeDashboard(
                                        state = state,
                                        personal = personal,
                                        welcomed = welcomed,
                                        onTheme = model::setTheme,
                                        onReadingScale = model::setReadingScale,
                                        onToggleVolumeKeys = model::toggleVolumeKeys,
                                        onSearch = { searchOpen = true },
                                        onTab = { nextTab ->
                                            if (nextTab == SanctuaryTab.GUIDES) rosarySetup = false
                                            if (nextTab == SanctuaryTab.CATECHISM) {
                                                formationLesson = null; formationQuestion = null; formationJourney = null
                                            }
                                            model.selectTab(nextTab)
                                        },
                                        onLesson = { id -> openFormation(lesson = id) },
                                        onPray = { rosarySetup = true; model.selectTab(SanctuaryTab.GUIDES) },
                                        onPrayNow = { model.start(PrayerGuide.TRADITIONAL, garden?.selected().orEmpty()) },
                                        onPraySingleDecade = { shownGuide = PrayerGuide.SINGLE_DECADE; openGuide = PrayerGuide.SINGLE_DECADE },
                                        onOpenMysteries = {
                                            treasury.openMysteries()
                                            treasury.selectStudySet(state.selectedSet)
                                            model.selectTab(SanctuaryTab.TREASURY)
                                        },
                                        onOpenPrayer = ::openTreasuryPrayer,
                                        onOpenGuide = { guide -> shownGuide = guide; openGuide = guide },
                                        onResume = model::resume,
                                        onIntentions = { model.selectTab(SanctuaryTab.GARDEN) },
                                        onWelcome = { welcomed = true; repository?.enqueue { repository.welcome() } },
                                        catechist = catechistState.persona,
                                        onOpenCatechist = { openCatechist() },
                                        onPrepareCustomRosary = { openCatechist(openPrepare = true) },
                                        onRecallPrompt = { title -> openCatechist(draft = "Sobre minha intenção anterior: $title", openPrepare = true) },
                                    )
                                    SanctuaryTab.CATECHISM -> FormationScreen(
                                        model = garden,
                                        state = personal,
                                        initialLesson = formationLesson,
                                        initialQuestion = formationQuestion,
                                        initialJourney = formationJourney,
                                        catechist = catechistState.persona,
                                        onAskCatechist = { q -> openCatechist(draft = q, autoSendQuestion = q.takeIf { it.isNotBlank() }) },
                                    ) { rosarySetup = true; model.selectTab(SanctuaryTab.GUIDES) }
                                    SanctuaryTab.GUIDES -> if (rosarySetup) SanctuaryHome(
                                        state, model,
                                        onStart = { model.start(PrayerGuide.TRADITIONAL, garden?.selected().orEmpty()) },
                                        personal = {
                                            if (garden != null) {
                                                SacredSecondaryButton(
                                                    text = if (selected.isEmpty()) "Oferecer por uma intenção (opcional)" else "${garden.selected().size} ${if (garden.selected().size == 1) "intenção" else "intenções"} para a próxima coroa",
                                                    onClick = { picker = true },
                                                    modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                                                    tag = "choose-intentions",
                                                    icon = SacredIcon.PEN,
                                                )
                                            }
                                        },
                                        onOtherGuides = { rosarySetup = false },
                                    ) else GuidesScreen(onSetup = { rosarySetup = true }) { shownGuide = it; openGuide = it }
                                    SanctuaryTab.TREASURY -> TreasuryScreen(
                                        state = treasuryState,
                                        actions = treasury,
                                        formation = {
                                            SacredDivider(Modifier.padding(start = 80.dp, end = 16.dp))
                                            Row(
                                                Modifier.fillMaxWidth()
                                                    .clickable(role = Role.Button, onClickLabel = "Abrir a Catequese") { openFormation() }
                                                    .testTag("open-formation")
                                                    .heightIn(min = 80.dp)
                                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            ) {
                                                DevotionalArtwork(DevotionalIllustration.CATECHESIS, Modifier.size(48.dp))
                                                Column(Modifier.weight(1f)) {
                                                    SacredText("Centro de catequese", style = SertumTheme.typography.label.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold))
                                                    SacredText("Aprender, perguntar e levar à vida.", Modifier.padding(top = 3.dp), SertumTheme.typography.body.copy(fontSize = 14.sp, lineHeight = 20.sp), SertumTheme.colors.secondary)
                                                }
                                                SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), SertumTheme.colors.muted)
                                            }
                                        },
                                        bookmark = {
                                            treasuryState.reading?.let { prayer ->
                                                if (garden != null) {
                                                    val kept = personal.bookmarks.any { it.kind == "prayer" && it.itemId == prayer.id }
                                                    SacredKeepToggle(
                                                        kept = kept,
                                                        tag = "bookmark-prayer",
                                                        onToggle = { garden.bookmark("prayer", prayer.id) },
                                                        modifier = Modifier.padding(top = 10.dp),
                                                        keptLabel = "Oração guardada",
                                                        unkeptLabel = "Guardar esta oração",
                                                    )
                                                }
                                            }
                                        },
                                        bookmarkedMysteries = bookmarkedMysteries,
                                        onPraySet = { set -> model.startRosarySet(set, garden?.selected().orEmpty()) },
                                    )
                                    SanctuaryTab.GARDEN -> GardenScreen(
                                        model = garden,
                                        state = personal,
                                        onPrayer = ::openTreasuryPrayer,
                                        onLesson = { id -> openFormation(lesson = id) },
                                        onMystery = ::openMysteryStudy,
                                        onQuestion = { id -> openFormation(question = id) },
                                        onJourney = { id -> openFormation(journey = id) },
                                        onResume = model::resume,
                                        sheetHost = gardenSheetHost,
                                        onCatechistPrayer = ::openCatechistPrayer,
                                    )
                                }
                            }
                            SertumBottomBar(state.tab, onSelect = { tab ->
                                if (tab == SanctuaryTab.GUIDES) rosarySetup = false
                                if (tab == SanctuaryTab.CATECHISM) {
                                    formationLesson = null; formationQuestion = null; formationJourney = null
                                }
                                model.selectTab(tab)
                            })
                        }
                        SanctuaryScreen.PRAYER -> ImmersivePrayerScreen(
                            state = state,
                            model = model,
                            language = state.language,
                            onLanguage = model::selectLanguage,
                            latin = TreasuryCatalog.latinFor(state.step.prayer),
                            catechistName = catechistState.persona.displayName,
                            onAskCatechistAboutMystery = { set, idx, title ->
                                openCatechist(
                                    mysterySet = set,
                                    mysteryIndex = idx,
                                    mysteryTitle = title,
                                )
                            },
                        ) {
                            model.next()?.let { if (state.hapticsEnabled) haptics.play(it) }
                        }
                        SanctuaryScreen.DELIVERED -> CrownDeliveredScreen(state, model::returnHome, onReflect = garden?.let { g -> { text -> g.journal(text, state.sessionId) } })
                    }
                }
                val liveToolBridge = remember(garden, model) {
                    object : LumenToolBridge {
                        override fun readGarden(): GardenSnapshot {
                            val g = garden?.state?.value ?: GardenState()
                            return GardenSnapshot(
                                intentions = g.intentions.map {
                                    GardenIntentionSnapshot(
                                        id = it.id,
                                        title = it.title,
                                        description = it.description,
                                        category = it.category,
                                        thanksgiving = it.thanksgiving,
                                        answeredNote = it.answeredNote,
                                        archived = it.archived,
                                        createdAtMillis = it.createdAt,
                                    )
                                },
                                recentCrowns = g.sessions.map {
                                    val finished = it.finishedAt
                                    GardenCrownSnapshot(
                                        id = it.id,
                                        guideName = it.guide,
                                        mysterySet = it.mysterySet,
                                        completedAtMillis = finished ?: it.startedAt,
                                        durationSeconds = if (finished != null) {
                                            ((finished - it.startedAt) / 1000L).coerceAtLeast(0L)
                                        } else {
                                            0L
                                        },
                                        status = it.status,
                                    )
                                },
                                recentJournal = g.journal.map {
                                    GardenJournalSnapshot(it.id, it.text, it.createdAt)
                                },
                                bookmarkedKeys = g.bookmarks.map { "${it.kind}:${it.itemId}" },
                                completedLessonIds = g.learning.filter { it.completedAt != null }.map { it.lessonId },
                                inProgressLessonIds = g.learning.filter { it.completedAt == null }.map { it.lessonId },
                            )
                        }

                        override fun readPrayerSession(): PrayerSessionSnapshot {
                            val s = model.state.value
                            return PrayerSessionSnapshot(
                                hasActiveSession = s.hasSession,
                                guideName = s.sessionGuide.name,
                                guideTitle = s.sessionGuide.title,
                                mysterySet = s.activeMysterySet,
                                currentStepTitle = s.step.title,
                                currentMysteryTitle = s.activeMystery?.title,
                                currentDecadeNumber = s.step.decadeIndex?.plus(1),
                                currentBeadNumber = s.step.beadIndex?.plus(1),
                                meditated = s.meditated,
                                language = s.language.name,
                                offeredIntentions = s.offeredIntentions.map { it.title },
                                today = s.today,
                            )
                        }

                        override fun createIntention(
                            title: String,
                            description: String,
                            category: String,
                            thanksgiving: Boolean,
                            selectForNextRosary: Boolean,
                        ): String? = garden?.createIntentionReturningId(title, description, category, thanksgiving, selectForNextRosary)

                        override fun markIntentionThanksgiving(intentionId: String, note: String): Boolean =
                            garden?.markThanksgiving(intentionId, note) ?: false

                        override fun selectIntentionForNextRosary(intentionId: String): Boolean =
                            garden?.selectIntention(intentionId) ?: false

                        override fun saveJournalReflection(text: String): Boolean {
                            val g = garden ?: return false
                            g.journal(text, model.state.value.sessionId)
                            return true
                        }

                        override fun bookmarkContent(kind: String, itemId: String): Boolean {
                            val g = garden ?: return false
                            val already = g.state.value.bookmarks.any { it.kind == kind && it.itemId == itemId }
                            if (!already) g.bookmark(kind, itemId)
                            return true
                        }
                    }
                }
                LaunchedEffect(liveToolBridge) { catechistModel.attachToolBridge(liveToolBridge) }
                if (catechistOpen) {
                    Box(Modifier.fillMaxSize().background(SertumTheme.colors.background)) {
                        CatechistScreen(
                            state = catechistState,
                            selectedMysterySet = activeSet,
                            initialDraft = catechistDraft,
                            openPrepareInitially = catechistPrepareSheet,
                            onBack = { catechistOpen = false; catechistPrepareSheet = false },
                            onSend = { msg -> catechistModel.sendMessage(msg) },
                            onSelectPersona = catechistModel::selectPersona,
                            onToggleOnline = catechistModel::setOnlineEnabled,
                            onSaveApiKey = catechistModel::setCustomApiKey,
                            onNewConversation = catechistModel::newConversation,
                            onPrepareCustomRosary = catechistModel::prepareCustomRosary,
                            onExecuteAction = { action ->
                                when (action) {
                                    is CatechistAction.StartCustomRosary -> {
                                        catechistOpen = false
                                        model.startCustomRosary(action.supplement, garden?.selected().orEmpty(), action.singleDecadeIndex)
                                    }
                                    is CatechistAction.StartQuickDecade -> {
                                        catechistOpen = false
                                        model.startSingleDecade(action.mysterySet, action.mysteryIndex, garden?.selected().orEmpty(), action.meditated)
                                    }
                                    is CatechistAction.LaunchGuide -> {
                                        val guide = PrayerGuide.byNameOrNull(action.guideName)
                                        if (guide == null) catechistModel.showNotice("Este roteiro não está disponível no aplicativo.")
                                        else {
                                            catechistOpen = false
                                            model.startGuideWithOptions(
                                                guide = guide,
                                                mysterySet = action.mysterySet,
                                                focusMysteryIndex = action.focusMysteryIndex,
                                                meditatedOverride = action.meditated,
                                                intentions = garden?.selected().orEmpty(),
                                            )
                                        }
                                    }
                                    is CatechistAction.SaveIntention -> {
                                        val normalizedCat = if (action.category.equals("Ação de Graças", ignoreCase = true)) "Ação de graças" else action.category
                                        garden?.saveIntention(
                                            title = action.intentionTitle,
                                            description = action.intentionDescription,
                                            category = normalizedCat,
                                            thanksgiving = normalizedCat == "Ação de graças",
                                            select = true,
                                        )
                                        catechistModel.showNotice("Intenção guardada no Meu Jardim e pronta para o próximo terço.")
                                    }
                                    is CatechistAction.OpenTreasuryPrayer -> {
                                        catechistOpen = false
                                        openTreasuryPrayer(action.prayerId)
                                    }
                                    is CatechistAction.OpenFormationLesson -> {
                                        catechistOpen = false
                                        openFormation(lesson = action.lessonId)
                                    }
                                    is CatechistAction.OpenFormationQuestion -> {
                                        catechistOpen = false
                                        openFormation(question = action.questionId)
                                    }
                                    is CatechistAction.OpenPracticeJourney -> {
                                        catechistOpen = false
                                        openFormation(journey = action.journeyId)
                                    }
                                    is CatechistAction.OpenMysteryStudy -> {
                                        catechistOpen = false
                                        openMysteryStudy(action.mysterySet, action.mysteryIndex)
                                    }
                                }
                            },
                            onStartCustomRosary = { supplement ->
                                catechistOpen = false
                                catechistPrepareSheet = false
                                model.startCustomRosary(supplement, garden?.selected().orEmpty())
                            },
                            onClearNotice = catechistModel::clearNotice,
                            savedPrayerIds = savedPrayerIds,
                            onReadPrayer = ::openCatechistPrayer,
                            onSetPrayerSaved = garden?.let { it::setCatechistPrayerSaved },
                        )
                    }
                }
                SacredBottomSheet(
                    openGuide != null && tabbed && !catechistOpen, { openGuide = null }, shownGuide.title, tag = "guide-sheet",
                    footer = { GuideSheetFooter { openGuide = null; model.start(shownGuide, garden?.selected().orEmpty()) } },
                ) {
                    GuideSheetContent(
                        guide = shownGuide,
                        selectedSet = state.selectedSet,
                        onSelectSet = model::selectMystery,
                        meditated = state.meditated,
                        onToggleMeditated = model::toggleMeditated,
                        mode = state.selectedMode,
                        onSelectMode = model::selectPrayerMode,
                        focusMystery = state.selectedFocusMystery,
                        onSelectFocusMystery = model::selectFocusMystery,
                    )
                    if (garden != null) {
                        var inlinePicker by rememberSaveable { mutableStateOf(false) }
                        SacredDivider(Modifier.padding(top = 12.dp, bottom = 6.dp))
                        QuietAction(
                            if (selected.isEmpty()) "Oferecer por uma intenção (opcional)" else "${garden.selected().size} ${if (garden.selected().size == 1) "intenção escolhida" else "intenções escolhidas"}",
                            { inlinePicker = !inlinePicker },
                            "guide-intentions",
                        )
                        if (inlinePicker) IntentionPicker(garden, personal, selected, inline = true) { inlinePicker = false }
                    }
                }
                displayedCatechistPrayer?.let { prayer ->
                    key(prayer.id) {
                        val saved = prayer.id in savedPrayerIds
                        val guide = PrayerGuide.byNameOrNull(prayer.guideName)
                        CatechistPrayerSheet(
                            prayer = prayer,
                            visible = catechistPrayerOpen,
                            saved = saved,
                            onDismiss = { catechistPrayerOpen = false },
                            onToggleSaved = garden?.let { g -> { g.setCatechistPrayerSaved(prayer, !saved) } },
                            onStartGuide = guide?.let {
                                {
                                    catechistPrayerOpen = false
                                    catechistOpen = false
                                    model.start(it, garden?.selected().orEmpty())
                                }
                            },
                        )
                    }
                }
                if (saveError != null) Column(Modifier.align(Alignment.TopCenter).fillMaxWidth().background(SertumTheme.colors.background).padding(horizontal = SertumTheme.spacing.page, vertical = 12.dp)) {
                    SacredText(saveError, style = SertumTheme.typography.micro)
                    QuietAction("Tentar guardar novamente", { repository?.retry() }, "retry-garden-save")
                }
                TreasurySheets(
                    state = treasuryState,
                    actions = treasury,
                    visible = tabbed && !catechistOpen && state.tab == SanctuaryTab.TREASURY,
                    bookmarkedMysteries = bookmarkedMysteries,
                    onToggleMysteryBookmark = garden?.let { g -> { key -> g.bookmark("mystery", key) } },
                    onPraySingleDecade = { set, index -> model.startSingleDecade(set, index, garden?.selected().orEmpty()) },
                    onPraySet = { set -> model.startRosarySet(set, garden?.selected().orEmpty()) },
                )
                GardenSheets(
                    host = gardenSheetHost,
                    model = garden,
                    state = personal,
                    visible = tabbed && !catechistOpen && state.tab == SanctuaryTab.GARDEN,
                )
                SacredBottomSheet(picker && tabbed && !catechistOpen, { picker = false }, "Intenções para esta coroa", tag = "intention-picker") {
                    if (garden != null) IntentionPicker(garden, personal, selected) { picker = false }
                }
                SacredBottomSheet(searchOpen && tabbed && !catechistOpen, { searchOpen = false }, "Buscar no Sertum", tag = "unified-search-sheet") {
                    UnifiedSearchSheetContent(
                        onOpenMystery = { set, idx -> searchOpen = false; openMysteryStudy(set, idx) },
                        onOpenGuide = { guide -> searchOpen = false; shownGuide = guide; openGuide = guide },
                        onOpenPrayer = { id -> searchOpen = false; openTreasuryPrayer(id) },
                        onOpenLesson = { id -> searchOpen = false; openFormation(lesson = id) },
                        onOpenQuestion = { id -> searchOpen = false; openFormation(question = id) },
                        onOpenJourney = { id -> searchOpen = false; openFormation(journey = id) },
                    )
                }
            }
        }
    }
}
