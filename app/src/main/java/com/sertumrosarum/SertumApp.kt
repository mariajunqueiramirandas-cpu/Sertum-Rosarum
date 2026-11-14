package com.sertumrosarum

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import com.sertumrosarum.data.garden.GardenRepository
import com.sertumrosarum.feature.garden.*
import com.sertumrosarum.feature.sanctuary.HomeDashboard
import com.sertumrosarum.feature.formation.FormationScreen
import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SacredPrimaryButton
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sertumrosarum.core.haptics.HapticFeedbackController
import com.sertumrosarum.feature.garden.GardenScreen
import com.sertumrosarum.feature.guides.GuideSheetContent
import com.sertumrosarum.feature.guides.GuideSheetFooter
import com.sertumrosarum.feature.guides.GuidesScreen
import com.sertumrosarum.feature.guides.PrayerGuide
import com.sertumrosarum.feature.prayer.CrownDeliveredScreen
import com.sertumrosarum.feature.prayer.ImmersivePrayerScreen
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import com.sertumrosarum.feature.prayer.SanctuaryScreen
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.feature.sanctuary.SanctuaryScreen as SanctuaryHome
import com.sertumrosarum.domain.treasury.TreasuryCatalog
import com.sertumrosarum.feature.treasury.TreasuryScreen
import com.sertumrosarum.feature.treasury.TreasurySheets
import com.sertumrosarum.feature.treasury.TreasuryViewModel
import com.sertumrosarum.ui.components.SacredBottomSheet
import com.sertumrosarum.ui.components.SertumBottomBar
import com.sertumrosarum.ui.components.SertumScaffold
import com.sertumrosarum.ui.theme.SertumTheme

@Composable
fun SertumApp(model: PrayerSessionViewModel = viewModel(), repository: GardenRepository? = null) {
    val garden: GardenViewModel? = if (repository != null) viewModel(factory = remember(repository) { GardenViewModel.factory(repository) }) else null
    val personal = garden?.state?.collectAsStateWithLifecycle()?.value ?: GardenState()
    val selected = garden?.selection?.collectAsStateWithLifecycle()?.value ?: emptySet()
    val saveError = repository?.error?.collectAsStateWithLifecycle()?.value
    var ready by remember(repository) { mutableStateOf(repository == null) }
    var startupError by remember { mutableStateOf(false) }
    var retry by remember { mutableStateOf(0) }
    var welcomed by remember { mutableStateOf(true) }
    var picker by rememberSaveable { mutableStateOf(false) }
    var rosarySetup by rememberSaveable { mutableStateOf(false) }
    var formationLesson by rememberSaveable { mutableStateOf<String?>(null) }

    val state by model.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptics = remember(context) { HapticFeedbackController(context) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { model.refreshDay() }
    val activeSet = if (state.screen == SanctuaryScreen.HOME) state.selectedSet else state.sessionSet
    SertumTheme(state.themeMode, activeSet) {
        val treasury: TreasuryViewModel = viewModel()
        LaunchedEffect(repository, retry) {
            if (repository != null) {
                startupError = false
                try {
                    model.connect(repository); treasury.connect(repository)
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
        val treasuryState by treasury.state.collectAsStateWithLifecycle()
        BackHandler(!tabbed) { model.returnHome() }
        BackHandler(tabbed && state.tab != SanctuaryTab.HOME) { model.selectTab(SanctuaryTab.HOME) }
        BackHandler(tabbed && state.tab == SanctuaryTab.GUIDES && rosarySetup) { rosarySetup = false }
        SertumScaffold {
            Box(Modifier.align(Alignment.TopCenter).fillMaxHeight().widthIn(max = 560.dp).fillMaxWidth()) {
                Crossfade(state.screen, animationSpec = tween(450), label = "sanctuary-transition") { screen ->
                    when (screen) {
                        SanctuaryScreen.HOME -> Column(Modifier.fillMaxSize()) {
                            Crossfade(state.tab, Modifier.weight(1f), tween(320), label = "tab-transition") { tab ->
                                when (tab) {
                                    SanctuaryTab.HOME -> HomeDashboard(state, personal, welcomed, model::setTheme,
                                        onTab = { tab -> if (tab == SanctuaryTab.GUIDES) rosarySetup = false; if (tab == SanctuaryTab.CATECHISM) formationLesson = null; model.selectTab(tab) },
                                        onLesson = { id -> formationLesson = id; model.selectTab(SanctuaryTab.CATECHISM) },
                                        onPray = { rosarySetup = true; model.selectTab(SanctuaryTab.GUIDES) }, onResume = model::resume,
                                        onIntentions = { model.selectTab(SanctuaryTab.GARDEN) },
                                        onWelcome = { welcomed = true; repository?.enqueue { repository.welcome() } })
                                    SanctuaryTab.CATECHISM -> FormationScreen(garden, personal, formationLesson) { rosarySetup = true; model.selectTab(SanctuaryTab.GUIDES) }
                                    SanctuaryTab.GUIDES -> if (rosarySetup) SanctuaryHome(state, model,
                                        onStart = { model.start(PrayerGuide.TRADITIONAL, garden?.selected().orEmpty()) },
                                        personal = { if (garden != null) QuietAction(if (selected.isEmpty()) "Oferecer por uma intenção (opcional)" else "${garden.selected().size} intenções para a próxima coroa", { picker = true }, "choose-intentions") },
                                        onOtherGuides = { rosarySetup = false })
                                    else GuidesScreen(onSetup = { rosarySetup = true }) { shownGuide = it; openGuide = it }
                                    SanctuaryTab.TREASURY -> TreasuryScreen(treasuryState, treasury,
                                        formation = { Column(Modifier.padding(horizontal = 22.dp)) { PersonalCard("Centro de catequese", "Aprender, perguntar e levar à vida.", { formationLesson = null; model.selectTab(SanctuaryTab.CATECHISM) }, "open-formation") } },
                                        bookmark = { treasuryState.reading?.let { prayer -> if (garden != null) QuietAction(if (personal.bookmarks.any { it.kind == "prayer" && it.itemId == prayer.id }) "✓ Oração guardada" else "Guardar esta oração", { garden.bookmark("prayer", prayer.id) }, "bookmark-prayer") } })
                                    SanctuaryTab.GARDEN -> GardenScreen(garden, personal,
                                        onPrayer = { id -> TreasuryCatalog.find(id)?.let { treasury.openPrayer(it); model.selectTab(SanctuaryTab.TREASURY) } },
                                        onLesson = { id -> formationLesson = id; model.selectTab(SanctuaryTab.CATECHISM) },
                                        onResume = model::resume)
                                }
                            }
                            // Measure the shelf with the page: wrapped labels at larger
                            // font sizes reserve their actual height above the system bar.
                            SertumBottomBar(state.tab, onSelect = { tab -> if (tab == SanctuaryTab.GUIDES) rosarySetup = false; if (tab == SanctuaryTab.CATECHISM) formationLesson = null; model.selectTab(tab) })
                        }
                        SanctuaryScreen.PRAYER -> ImmersivePrayerScreen(state, model, state.language, model::selectLanguage, TreasuryCatalog.latinFor(state.step.prayer)) {
                            model.next()?.let { if (state.hapticsEnabled) haptics.play(it) }
                        }
                        SanctuaryScreen.DELIVERED -> CrownDeliveredScreen(state, model::returnHome, onReflect = garden?.let { g -> { text -> g.journal(text, state.sessionId) } })
                    }
                }
                SacredBottomSheet(
                    openGuide != null && tabbed, { openGuide = null }, shownGuide.title, tag = "guide-sheet",
                    footer = { GuideSheetFooter { openGuide = null; model.start(shownGuide, garden?.selected().orEmpty()) } },
                ) {
                    GuideSheetContent(shownGuide, state.selectedSet, model::selectMystery, state.meditated, model::toggleMeditated, state.selectedMode, model::selectPrayerMode)
                    if (garden != null) {
                        var inlinePicker by rememberSaveable { mutableStateOf(false) }
                        QuietAction(if (selected.isEmpty()) "Oferecer por uma intenção (opcional)" else "${garden.selected().size} intenções escolhidas", { inlinePicker = !inlinePicker }, "guide-intentions")
                        if (inlinePicker) IntentionPicker(garden, personal, selected) { inlinePicker = false }
                    }
                }
                if (saveError != null) Column(Modifier.align(Alignment.TopCenter).fillMaxWidth().background(SertumTheme.colors.background).padding(12.dp)) {
                    SacredText(saveError, style = SertumTheme.typography.micro)
                    QuietAction("Tentar guardar novamente", { repository?.retry() }, "retry-garden-save")
                }
                TreasurySheets(treasuryState, treasury, visible = tabbed && state.tab == SanctuaryTab.TREASURY )
                SacredBottomSheet(picker && tabbed, { picker = false }, "Intenções para esta coroa", tag = "intention-picker") {
                    if (garden != null) IntentionPicker(garden, personal, selected) { picker = false }
                }
            }
        }
    }
}
