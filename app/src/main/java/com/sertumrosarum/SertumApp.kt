package com.sertumrosarum

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.sertumrosarum.feature.treasury.TreasuryScreen
import com.sertumrosarum.ui.components.SacredBottomSheet
import com.sertumrosarum.ui.components.SertumBottomBar
import com.sertumrosarum.ui.components.SertumBottomBarSpace
import com.sertumrosarum.ui.components.SertumScaffold
import com.sertumrosarum.ui.theme.SertumTheme

@Composable
fun SertumApp(model: PrayerSessionViewModel = viewModel()) {
    val state by model.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptics = remember(context) { HapticFeedbackController(context) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { model.refreshDay() }
    val activeSet = if (state.screen == SanctuaryScreen.HOME) state.selectedSet else state.sessionSet
    SertumTheme(state.themeMode, activeSet) {
        val dark = SertumTheme.colors.isDark
        SideEffect {
            (context as? ComponentActivity)?.enableEdgeToEdge(
                statusBarStyle = if (dark) SystemBarStyle.dark(Color.TRANSPARENT) else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                navigationBarStyle = if (dark) SystemBarStyle.dark(Color.TRANSPARENT) else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            )
        }
        val tabbed = state.screen == SanctuaryScreen.HOME
        // The sheet keeps its guide while it slides away, so its words do not vanish mid-air.
        var openGuide by rememberSaveable { mutableStateOf<PrayerGuide?>(null) }
        var shownGuide by rememberSaveable { mutableStateOf(PrayerGuide.TRADITIONAL) }
        BackHandler(!tabbed) { model.returnHome() }
        BackHandler(tabbed && state.tab != SanctuaryTab.HOME) { model.selectTab(SanctuaryTab.HOME) }
        SertumScaffold {
            Box(Modifier.align(Alignment.TopCenter).fillMaxHeight().widthIn(max = 560.dp).fillMaxWidth()) {
                Crossfade(state.screen, animationSpec = tween(450), label = "sanctuary-transition") { screen ->
                    when (screen) {
                        SanctuaryScreen.HOME -> Column(Modifier.fillMaxSize()) {
                            Crossfade(state.tab, Modifier.weight(1f), tween(320), label = "tab-transition") { tab ->
                                when (tab) {
                                    SanctuaryTab.HOME -> SanctuaryHome(state, model)
                                    SanctuaryTab.GUIDES -> GuidesScreen { shownGuide = it; openGuide = it }
                                    SanctuaryTab.TREASURY -> TreasuryScreen()
                                    SanctuaryTab.GARDEN -> GardenScreen()
                                }
                            }
                            Spacer(Modifier.height(SertumBottomBarSpace))
                        }
                        SanctuaryScreen.PRAYER -> ImmersivePrayerScreen(state, model) {
                            model.next()?.let { if (state.hapticsEnabled) haptics.play(it) }
                        }
                        SanctuaryScreen.DELIVERED -> CrownDeliveredScreen(state, model::returnHome)
                    }
                }
                // The tabs rest over the page and slip away below it when prayer begins.
                AnimatedVisibility(
                    tabbed, Modifier.align(Alignment.BottomCenter),
                    enter = slideInVertically(tween(420, delayMillis = 120)) { it } + fadeIn(tween(300, delayMillis = 120)),
                    exit = slideOutVertically(tween(320)) { it } + fadeOut(tween(240)),
                ) { SertumBottomBar(state.tab, model::selectTab) }
                SacredBottomSheet(
                    openGuide != null && tabbed, { openGuide = null }, shownGuide.title, tag = "guide-sheet",
                    footer = { GuideSheetFooter(shownGuide) { openGuide = null; model.start(shownGuide) } },
                ) { GuideSheetContent(shownGuide, state.selectedSet, model::selectMystery, state.meditated, model::toggleMeditated) }
            }
        }
    }
}
