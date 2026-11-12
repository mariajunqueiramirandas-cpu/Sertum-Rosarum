package com.sertumrosarum

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sertumrosarum.core.haptics.HapticFeedbackController
import com.sertumrosarum.feature.prayer.CrownDeliveredScreen
import com.sertumrosarum.feature.prayer.ImmersivePrayerScreen
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import com.sertumrosarum.feature.prayer.SanctuaryScreen
import com.sertumrosarum.feature.sanctuary.SanctuaryScreen as SanctuaryHome
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
        BackHandler(state.screen != SanctuaryScreen.HOME) { model.returnHome() }
        SertumScaffold {
            Box(Modifier.align(Alignment.TopCenter).fillMaxHeight().widthIn(max = 560.dp).fillMaxWidth()) {
                Crossfade(state.screen, animationSpec = tween(450), label = "sanctuary-transition") { screen ->
                    when (screen) {
                        SanctuaryScreen.HOME -> SanctuaryHome(state, model)
                        SanctuaryScreen.PRAYER -> ImmersivePrayerScreen(state, model) {
                            model.next()?.let { if (state.hapticsEnabled) haptics.play(it) }
                        }
                        SanctuaryScreen.DELIVERED -> CrownDeliveredScreen(state, model::returnHome)
                    }
                }
            }
        }
    }
}
