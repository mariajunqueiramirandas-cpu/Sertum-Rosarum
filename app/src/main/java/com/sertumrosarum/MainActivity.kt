package com.sertumrosarum

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.sertumrosarum.core.haptics.HapticFeedbackController
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import com.sertumrosarum.feature.prayer.SanctuaryScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val haptics by lazy { HapticFeedbackController(this) }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch { runCatching { (application as SertumApplication).garden.flush() } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { SertumApp(repository = (application as SertumApplication).garden) }
    }

    /** Hardware volume buttons advance or step back beads during active prayer when enabled. */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val model = ViewModelProvider(this)[PrayerSessionViewModel::class.java]
        val state = model.state.value
        if (state.screen == SanctuaryScreen.PRAYER && state.volumeKeysEnabled) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    model.next()?.let { if (state.hapticsEnabled) haptics.play(it) }
                    return true
                }
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    model.previous()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
