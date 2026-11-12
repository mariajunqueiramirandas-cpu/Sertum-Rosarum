package com.sertumrosarum.core.haptics

import android.content.Context
import android.os.VibrationEffect
import android.os.VibratorManager
import com.sertumrosarum.feature.prayer.PrayerPulse

class HapticFeedbackController(context: Context) {
    private val vibrator = context.getSystemService(VibratorManager::class.java)?.defaultVibrator

    fun play(pulse: PrayerPulse) {
        if (vibrator?.hasVibrator() != true) return
        val effect = when (pulse) {
            PrayerPulse.SOFT -> VibrationEffect.createOneShot(20, 80)
            PrayerPulse.TRANSITION -> VibrationEffect.createWaveform(longArrayOf(0, 45, 70, 65), intArrayOf(0, 140, 0, 180), -1)
            PrayerPulse.COMPLETION -> VibrationEffect.createWaveform(longArrayOf(0, 45, 90, 65, 100, 100), intArrayOf(0, 100, 0, 160, 0, 220), -1)
        }
        vibrator.vibrate(effect)
    }
}
