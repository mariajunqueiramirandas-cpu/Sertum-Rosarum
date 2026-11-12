package com.sertumrosarum.feature.prayer

import androidx.lifecycle.SavedStateHandle
import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.domain.prayer.MysterySet
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import org.junit.Assert.*
import org.junit.Test

class PrayerSessionViewModelTest {
    private val fixedClock = Clock.fixed(Instant.parse("2026-11-12T12:00:00Z"), ZoneOffset.UTC)
    private fun model(handle: SavedStateHandle = SavedStateHandle(), clock: Clock = fixedClock) = PrayerSessionViewModel(handle, clock)

    @Test fun `today selects luminous mysteries on Thursday`() {
        assertEquals(MysterySet.LUMINOUS, model().state.value.selectedSet)
    }

    @Test fun `all 80 steps can be traversed forward and backward without skipping boundaries`() {
        val model = model()
        model.start()
        repeat(79) { index ->
            assertEquals(index, model.state.value.stepIndex)
            assertNotNull(model.next())
        }
        assertEquals(SanctuaryScreen.PRAYER, model.state.value.screen)
        assertEquals(FixedPrayer.HAIL_HOLY_QUEEN, model.state.value.step.prayer)
        repeat(79) { model.previous() }
        assertEquals(0, model.state.value.stepIndex)
        model.previous()
        assertEquals(0, model.state.value.stepIndex)
    }

    @Test fun `last prayer must be prayed before delivery and completed session ignores next`() {
        val model = model()
        model.start()
        repeat(79) { model.next() }
        assertNull(model.state.value.finishedAt)
        assertEquals(PrayerPulse.COMPLETION, model.next())
        assertEquals(SanctuaryScreen.DELIVERED, model.state.value.screen)
        assertEquals(1f, model.state.value.progress)
        assertFalse(model.state.value.hasSession)
        assertNull(model.next())
    }

    @Test fun `decade jumps land on the announcement and bead jumps select exact Ave Maria`() {
        val model = model()
        model.start()
        repeat(5) { decade ->
            model.jumpToDecade(decade)
            assertEquals(decade, model.state.value.step.decadeIndex)
            assertNull(model.state.value.step.prayer)
            repeat(10) { bead ->
                model.jumpToBead(bead)
                assertEquals(FixedPrayer.HAIL_MARY, model.state.value.step.prayer)
                assertEquals(bead, model.state.value.step.beadIndex)
                assertEquals(decade, model.state.value.completedDecades)
            }
        }
    }

    @Test fun `invalid decade and bead jumps do not change the session`() {
        val model = model()
        model.start()
        model.jumpToDecade(-1); model.jumpToDecade(5)
        model.jumpToBead(-1); model.jumpToBead(3)
        assertEquals(0, model.state.value.stepIndex)
        model.jumpToDecade(2)
        val index = model.state.value.stepIndex
        model.jumpToBead(10)
        assertEquals(index, model.state.value.stepIndex)
    }

    @Test fun `introductory bead jumps respect their three bead group`() {
        val model = model()
        model.start()
        repeat(3) { bead ->
            model.jumpToBead(bead)
            assertEquals(bead, model.state.value.step.beadIndex)
            assertNull(model.state.value.step.decadeIndex)
            assertEquals(3, model.state.value.step.beadCount)
        }
    }

    @Test fun `closing prayers show the full crown and cannot jump to beads`() {
        val model = model()
        model.start()
        repeat(78) { model.next() }
        assertEquals(5, model.state.value.completedDecades)
        model.jumpToBead(0)
        assertEquals(78, model.state.value.stepIndex)
    }

    @Test fun `home selection never mutates a paused crown and restart uses the new mysteries`() {
        val model = model()
        model.start()
        model.jumpToDecade(2)
        model.jumpToBead(5)
        val index = model.state.value.stepIndex
        model.returnHome()
        model.selectMystery(MysterySet.SORROWFUL)
        assertEquals(MysterySet.LUMINOUS, model.state.value.sessionSet)
        model.resume()
        assertEquals(index, model.state.value.stepIndex)
        model.returnHome()
        model.start()
        assertEquals(MysterySet.SORROWFUL, model.state.value.sessionSet)
        assertEquals(0, model.state.value.stepIndex)
    }

    @Test fun `save state restores exact session mystery theme and tactile preferences`() {
        val handle = SavedStateHandle()
        val original = model(handle)
        original.setTheme(ThemeMode.VIGIL)
        original.selectMystery(MysterySet.GLORIOUS)
        original.start()
        original.jumpToDecade(3)
        original.jumpToBead(8)
        original.toggleEyesClosed()
        original.toggleHaptics()
        val restoredHandle = SavedStateHandle(handle.keys().associateWith { handle.get<Any?>(it) })
        assertEquals(original.state.value, model(restoredHandle).state.value)
    }

    @Test fun `ordinary Ave Marias use one pulse but decade boundaries use a double pulse`() {
        val model = model()
        model.start()
        model.jumpToDecade(0)
        assertEquals(PrayerPulse.TRANSITION, model.next())
        assertEquals(PrayerPulse.TRANSITION, model.next())
        assertEquals(PrayerPulse.SOFT, model.next())
        model.jumpToBead(9)
        assertEquals(PrayerPulse.TRANSITION, model.next())
        assertEquals(FixedPrayer.GLORY_BE, model.state.value.step.prayer)
    }

    @Test fun `duration records elapsed time at completion and remains fixed on returning home`() {
        val clock = MutableClock(fixedClock.instant())
        val model = model(clock = clock)
        model.start()
        clock.instant = clock.instant.plusSeconds(1527)
        repeat(80) { model.next() }
        assertEquals(1527L, model.state.value.durationSeconds)
        clock.instant = clock.instant.plusSeconds(600)
        model.returnHome()
        assertEquals(1527L, model.state.value.durationSeconds)
    }

    @Test fun `resume with no session and navigation on home are harmless`() {
        val model = model()
        model.resume(); model.previous(); model.jumpToDecade(2); model.jumpToBead(1)
        assertNull(model.next())
        assertEquals(SanctuaryScreen.HOME, model.state.value.screen)
        assertEquals(0, model.state.value.stepIndex)
    }

    @Test fun `a new day updates the sanctuary without changing an active crown`() {
        val clock = MutableClock(fixedClock.instant())
        val model = model(clock = clock)
        model.start()
        model.jumpToDecade(2)
        model.jumpToBead(6)
        val position = model.state.value.stepIndex
        clock.instant = clock.instant.plusSeconds(86400)
        model.refreshDay()
        assertEquals(MysterySet.SORROWFUL, model.state.value.selectedSet)
        assertEquals(MysterySet.LUMINOUS, model.state.value.sessionSet)
        assertEquals(position, model.state.value.stepIndex)
    }

    private class MutableClock(var instant: Instant) : Clock() {
        override fun getZone(): ZoneId = ZoneOffset.UTC
        override fun withZone(zone: ZoneId): Clock = this
        override fun instant(): Instant = instant
    }
}
