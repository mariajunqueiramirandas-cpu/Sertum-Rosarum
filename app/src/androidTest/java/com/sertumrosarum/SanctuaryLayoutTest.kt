package com.sertumrosarum

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.feature.prayer.PrayerSessionViewModel
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SanctuaryLayoutTest {
    @get:Rule val compose = createComposeRule()

    @Test fun changing_mysteries_keeps_the_selector_and_card_in_place() {
        val width = mutableStateOf(412.dp)
        val fontScale = mutableStateOf(1f)
        val model = PrayerSessionViewModel(SavedStateHandle(), Clock.fixed(Instant.parse("2026-11-12T12:00:00Z"), ZoneOffset.UTC))
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale.value)) {
                Box(Modifier.width(width.value).fillMaxHeight()) { SertumApp(model) }
            }
        }
        // The app now opens on the home hub; the mystery selector lives in the rosary setup.
        compose.openRosarySetup()
        for (phoneWidth in listOf(320.dp, 412.dp)) {
            for (scale in listOf(1f, 1.35f)) {
                compose.runOnIdle {
                    width.value = phoneWidth
                    fontScale.value = scale
                    model.selectMystery(MysterySet.LUMINOUS)
                }
                compose.onNodeWithTag("mystery-selector").performScrollTo()
                val selectorTop = compose.onNodeWithTag("mystery-selector").fetchSemanticsNode().boundsInRoot.top
                val cardTop = compose.onNodeWithTag("mystery-card").fetchSemanticsNode().boundsInRoot.top
                for (set in listOf("JOYFUL", "SORROWFUL", "GLORIOUS", "LUMINOUS")) {
                    compose.onNodeWithTag("mystery-$set").performClick().assertIsSelected()
                    assertEquals("Selector shifted at $phoneWidth / $scale", selectorTop, compose.onNodeWithTag("mystery-selector").fetchSemanticsNode().boundsInRoot.top, .5f)
                    assertEquals("Card shifted at $phoneWidth / $scale", cardTop, compose.onNodeWithTag("mystery-card").fetchSemanticsNode().boundsInRoot.top, .5f)
                    if (set == "LUMINOUS") compose.onNodeWithText("Hoje").assertIsDisplayed()
                    else compose.onNodeWithText("Hoje").assertDoesNotExist()
                }
            }
        }
    }
}
