package com.sertumrosarum.feature.prayer

import androidx.lifecycle.SavedStateHandle
import com.sertumrosarum.domain.prayer.FixedPrayer
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.prayer.PrayerMode
import com.sertumrosarum.feature.guides.PrayerGuide
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import org.junit.Assert.*
import org.junit.Test

class PrayerSessionViewModelTest {
    private val fixedClock = Clock.fixed(Instant.parse("2026-11-12T12:00:00Z"), ZoneOffset.UTC)
    private fun model(handle: SavedStateHandle = SavedStateHandle(), clock: Clock = fixedClock) = PrayerSessionViewModel(handle, clock)

    @Test fun `Latin choice survives stepping pause restoration and a new crown`() {
        val handle = SavedStateHandle()
        val first = model(handle)
        first.selectLanguage(com.sertumrosarum.domain.treasury.PrayerLanguage.LATIN)
        first.start(); repeat(10) { first.next() }; first.returnHome()
        val restored = model(SavedStateHandle(handle.keys().associateWith { handle.get<Any?>(it) }))
        assertEquals(com.sertumrosarum.domain.treasury.PrayerLanguage.LATIN, restored.state.value.language)
        restored.resume(); restored.next()
        assertEquals(com.sertumrosarum.domain.treasury.PrayerLanguage.LATIN, restored.state.value.language)
        restored.returnHome(); restored.start()
        assertEquals(com.sertumrosarum.domain.treasury.PrayerLanguage.LATIN, restored.state.value.language)
        restored.selectLanguage(com.sertumrosarum.domain.treasury.PrayerLanguage.BILINGUAL)
        assertEquals(com.sertumrosarum.domain.treasury.PrayerLanguage.BILINGUAL, restored.state.value.language)
    }

    @Test fun `today selects luminous mysteries on Thursday`() {
        assertEquals(MysterySet.LUMINOUS, model().state.value.selectedSet)
    }

    @Test fun `new sessions default to solo and use personal opening and closing texts`() {
        val model = model()
        assertEquals(PrayerMode.SOLO, model.state.value.selectedMode)
        model.start()
        assertEquals(PrayerMode.SOLO, model.state.value.sessionMode)
        model.next()
        assertTrue(model.state.value.prayerText.startsWith("Divino Jesus, eu Vos ofereço"))
        repeat(77) { model.next() }
        assertEquals(FixedPrayer.THANKSGIVING, model.state.value.step.prayer)
        assertTrue(model.state.value.prayerText.startsWith("Infinitas graças vos dou"))
        model.next()
        assertEquals(FixedPrayer.HAIL_HOLY_QUEEN.text, model.state.value.prayerText)
        assertEquals(PrayerPulse.COMPLETION, model.next())
    }

    @Test fun `mode selection applies to a new crown without changing a paused crown`() {
        val model = model()
        model.selectPrayerMode(PrayerMode.GROUP)
        model.start()
        model.next()
        val startedAt = model.state.value.startedAt
        model.returnHome()
        model.selectPrayerMode(PrayerMode.SOLO)
        model.resume()
        assertEquals(PrayerMode.GROUP, model.state.value.sessionMode)
        assertEquals(1, model.state.value.stepIndex)
        assertEquals(startedAt, model.state.value.startedAt)
        assertEquals(FixedPrayer.OFFERING.text, model.state.value.prayerText)
        model.returnHome()
        model.start()
        assertEquals(PrayerMode.SOLO, model.state.value.sessionMode)
        assertEquals(0, model.state.value.stepIndex)
    }

    @Test fun `selected and active prayer modes restore independently in either mode`() {
        PrayerMode.entries.forEach { mode ->
            val handle = SavedStateHandle()
            val original = model(handle)
            original.selectPrayerMode(mode)
            original.start()
            original.next()
            original.returnHome()
            original.selectPrayerMode(PrayerMode.entries.first { it != mode })
            val restored = model(SavedStateHandle(handle.keys().associateWith { handle.get<Any?>(it) }))
            assertEquals(original.state.value, restored.state.value)
            restored.resume()
            assertEquals(mode, restored.state.value.sessionMode)
            assertEquals(FixedPrayer.OFFERING.textFor(mode), restored.state.value.prayerText)
        }
    }

    @Test fun `mode choice restores before starting the first session`() {
        val handle = SavedStateHandle()
        model(handle).selectPrayerMode(PrayerMode.GROUP)
        val restored = model(SavedStateHandle(handle.keys().associateWith { handle.get<Any?>(it) }))
        assertEquals(PrayerMode.GROUP, restored.state.value.selectedMode)
        restored.start()
        assertEquals(PrayerMode.GROUP, restored.state.value.sessionMode)
    }

    @Test fun `sessions saved before modes existed preserve their group texts`() {
        val restored = model(SavedStateHandle(mapOf(
            "sessionSet" to MysterySet.JOYFUL.name,
            "startedAt" to fixedClock.millis(),
            "stepIndex" to 1,
        )))
        restored.resume()
        assertEquals(PrayerMode.GROUP, restored.state.value.sessionMode)
        assertEquals(FixedPrayer.OFFERING.text, restored.state.value.prayerText)
    }

    @Test fun `every guide starts in the selected mode`() {
        PrayerGuide.entries.forEach { guide ->
            PrayerMode.entries.forEach { mode ->
                val model = model()
                model.selectPrayerMode(mode)
                model.start(guide)
                assertEquals(guide, model.state.value.sessionGuide)
                assertEquals(mode, model.state.value.sessionMode)
                model.returnHome()
                model.selectPrayerMode(PrayerMode.entries.first { it != mode })
                model.resume()
                assertEquals(mode, model.state.value.sessionMode)
            }
        }
    }

    @Test fun `themed offerings follow the active mode without changing their intentions`() {
        listOf(PrayerGuide.FAMILY, PrayerGuide.SICK, PrayerGuide.PEACE).forEach { guide ->
            val model = model()
            model.start(guide)
            repeat(2) { model.next() }
            assertTrue(model.state.value.prayerText.startsWith("Senhor Jesus, ofereço esta oração"))
            assertEquals(guide.script.offering?.title, model.state.value.step.title)
            model.selectPrayerMode(PrayerMode.GROUP)
            model.start(guide)
            repeat(2) { model.next() }
            assertTrue(model.state.value.prayerText.startsWith("Senhor Jesus, oferecemos esta oração"))
            assertEquals(guide.script.offering?.text, model.state.value.prayerText)
        }
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
        original.toggleMeditated()
        original.returnHome()
        original.selectTab(SanctuaryTab.GUIDES)
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

    @Test fun `tabs change the place without touching the selection or a paused crown`() {
        val model = model()
        model.start()
        model.jumpToDecade(1)
        val index = model.state.value.stepIndex
        model.returnHome()
        SanctuaryTab.entries.forEach { tab ->
            model.selectTab(tab)
            assertEquals(tab, model.state.value.tab)
            assertEquals(SanctuaryScreen.HOME, model.state.value.screen)
        }
        assertEquals(MysterySet.LUMINOUS, model.state.value.selectedSet)
        model.resume()
        assertEquals(index, model.state.value.stepIndex)
    }

    @Test fun `every guide starts its own crown and a fresh crown resets the position`() {
        val model = model()
        model.selectMystery(MysterySet.JOYFUL)
        model.start(PrayerGuide.FAMILY)
        assertEquals(SanctuaryScreen.PRAYER, model.state.value.screen)
        assertEquals(PrayerGuide.FAMILY, model.state.value.sessionGuide)
        assertEquals(MysterySet.JOYFUL, model.state.value.sessionSet)
        PrayerGuide.entries.forEach { guide ->
            model.returnHome()
            model.start(guide)
            assertEquals(SanctuaryScreen.PRAYER, model.state.value.screen)
            assertEquals(guide, model.state.value.sessionGuide)
            assertEquals(0, model.state.value.stepIndex)
            assertEquals(guide.script.groupCount, model.state.value.groupCount)
            model.jumpToDecade(guide.script.groupCount - 1)
        }
        model.start()
        assertEquals(PrayerGuide.TRADITIONAL, model.state.value.sessionGuide)
    }

    @Test fun `bead by bead lines follow the Ave Maria only while the meditated rosary is on`() {
        val model = model()
        model.selectMystery(MysterySet.JOYFUL)
        model.start()
        model.jumpToDecade(0)
        assertNull(model.state.value.beadMeditation)
        model.toggleMeditated()
        assertNull("the announcement has no bead line", model.state.value.beadMeditation)
        val lines = requireNotNull(model.state.value.contemplation).beads
        assertEquals(10, lines.size)
        repeat(10) { bead ->
            model.jumpToBead(bead)
            assertEquals(lines[bead], model.state.value.beadMeditation)
        }
        model.next()
        assertEquals(FixedPrayer.GLORY_BE, model.state.value.step.prayer)
        assertNull(model.state.value.beadMeditation)
        model.toggleMeditated()
        model.jumpToBead(4)
        assertNull(model.state.value.beadMeditation)
    }

    @Test fun `every mystery has its three written layers and introductory prayers have none`() {
        val model = model()
        MysterySet.entries.forEach { set ->
            model.returnHome()
            model.selectMystery(set)
            model.start()
            assertNull(model.state.value.contemplation)
            repeat(5) { decade ->
                model.jumpToDecade(decade)
                val layers = requireNotNull(model.state.value.contemplation)
                assertTrue(layers.scene.isNotBlank() && layers.meaning.isNotBlank() && layers.practice.isNotBlank())
                assertEquals(10, layers.beads.size)
            }
        }
    }

    @Test fun `catechetical notes belong to the catechetical guide and are not repeated on every bead`() {
        val model = model()
        model.start()
        assertNull(model.state.value.catecheticalNote)
        model.returnHome()
        model.start(PrayerGuide.CATECHETICAL)
        repeat(4) { assertNotNull(model.state.value.catecheticalNote); model.next() }
        assertEquals(0, model.state.value.step.beadIndex)
        assertNotNull(model.state.value.catecheticalNote)
        model.next()
        assertNull(model.state.value.catecheticalNote)
        model.jumpToDecade(0)
        assertNotNull(model.state.value.catecheticalNote)
        model.jumpToBead(0)
        val first = model.state.value.catecheticalNote
        assertNotNull(first)
        model.jumpToBead(1)
        assertNull(model.state.value.catecheticalNote)
    }

    @Test fun `crowns describe their own shape`() {
        assertEquals("Cinco dezenas de dez contas", PrayerGuide.TRADITIONAL.shape.summary)
        assertEquals("Sete setenas de sete contas", PrayerGuide.TEARS.shape.summary)
        assertEquals("Sete dezenas de dez contas", PrayerGuide.FRANCISCAN.shape.summary)
        assertEquals("7 × 7", PrayerGuide.TEARS.shape.badge)
        assertTrue(PrayerGuide.entries.all { it.path.isNotEmpty() })
    }

    @Test fun `every script traverses its whole sequence in both directions and delivers only after the last prayer`() {
        PrayerGuide.entries.forEach { guide ->
            val model = model()
            model.start(guide)
            val sequence = model.state.value.sequence
            repeat(sequence.lastIndex) { model.next() }
            assertEquals(sequence.last(), model.state.value.step)
            assertNull(model.state.value.finishedAt)
            repeat(sequence.lastIndex) { model.previous() }
            assertEquals(0, model.state.value.stepIndex)
            repeat(sequence.lastIndex) { model.next() }
            assertEquals(PrayerPulse.COMPLETION, model.next())
            assertEquals(SanctuaryScreen.DELIVERED, model.state.value.screen)
            assertEquals(guide.script.groupCount, model.state.value.completedDecades)
            assertNull(model.next())
        }
    }

    @Test fun `every crown jumps to all its own groups and beads without crossing group boundaries`() {
        PrayerGuide.entries.forEach { guide ->
            val model = model()
            model.start(guide)
            repeat(guide.script.groupCount) { group ->
                model.jumpToDecade(group)
                assertEquals(group, model.state.value.step.decadeIndex)
                repeat(guide.script.beadCount(group)) { bead ->
                    model.jumpToBead(bead)
                    assertEquals(group, model.state.value.step.decadeIndex)
                    assertEquals(bead, model.state.value.step.beadIndex)
                }
                val before = model.state.value.stepIndex
                model.jumpToBead(guide.script.beadCount(group))
                model.jumpToDecade(guide.script.groupCount)
                assertEquals(before, model.state.value.stepIndex)
            }
        }
    }

    @Test fun `saved state restores the late litany and seventh Franciscan decade beyond step eighty`() {
        listOf(PrayerGuide.COMPLETE, PrayerGuide.FRANCISCAN, PrayerGuide.TEARS).forEach { guide ->
            val handle = SavedStateHandle()
            val original = model(handle)
            original.start(guide)
            repeat(original.state.value.sequence.lastIndex) { original.next() }
            original.returnHome()
            original.selectMystery(MysterySet.SORROWFUL)
            val restored = model(SavedStateHandle(handle.keys().associateWith { handle.get<Any?>(it) }))
            assertEquals(original.state.value, restored.state.value)
            restored.resume()
            assertEquals(original.state.value.step, restored.state.value.step)
            assertEquals(PrayerPulse.COMPLETION, restored.next())
        }
    }

    @Test fun `Franciscan final pair is counted and navigable without an eighth group`() {
        val model = model()
        model.start(PrayerGuide.FRANCISCAN)
        model.jumpToDecade(6)
        model.jumpToBead(9)
        model.next() // Glória
        model.next() // first closing Ave-Maria
        assertEquals(7, model.state.value.completedDecades)
        assertEquals(2, model.state.value.groupBeads.size)
        assertEquals("Ave-Maria", model.state.value.beadLabel)
        model.jumpToBead(1)
        assertEquals(1, model.state.value.step.beadIndex)
        assertEquals(72, model.state.value.crownBeadCount)
        assertEquals(PrayerPulse.TRANSITION, model.next())
        assertNull(model.state.value.step.beadIndex)
    }

    @Test fun `short invocations use the same soft and boundary pulses as rosary beads`() {
        val model = model()
        model.start(PrayerGuide.TEARS)
        model.jumpToDecade(6)
        assertEquals(PrayerPulse.TRANSITION, model.next()) // large bead
        assertEquals(PrayerPulse.SOFT, model.next())
        model.jumpToBead(6)
        assertEquals(PrayerPulse.TRANSITION, model.next())
        assertEquals(7, model.state.value.completedDecades)
        assertEquals(49, model.state.value.crownBeadCount)
    }

    @Test fun `meditations index every Ave Maria in all twenty mysteries without leaking into other crowns`() {
        val model = model()
        model.toggleMeditated()
        MysterySet.entries.forEach { set ->
            model.selectMystery(set)
            model.start()
            repeat(5) { group ->
                model.jumpToDecade(group)
                val lines = requireNotNull(model.state.value.contemplation).beads
                repeat(10) { bead ->
                    model.jumpToBead(bead)
                    assertEquals(lines[bead], model.state.value.beadMeditation)
                }
                model.next()
                assertNull(model.state.value.beadMeditation)
            }
        }
        model.start(PrayerGuide.FRANCISCAN)
        model.jumpToDecade(6)
        model.jumpToBead(9)
        assertNull(model.state.value.contemplation)
        assertNull(model.state.value.beadMeditation)
    }

    @Test fun `theme meditations follow both the selected script and the active mystery`() {
        val model = model()
        model.start(PrayerGuide.FAMILY)
        model.jumpToDecade(0)
        val first = model.state.value.themedMeditation
        assertNotNull(first)
        model.jumpToDecade(4)
        assertNotEquals(first, model.state.value.themedMeditation)
        model.start(PrayerGuide.PEACE)
        model.jumpToDecade(0)
        assertNotEquals(first, model.state.value.themedMeditation)
        model.start()
        model.jumpToDecade(0)
        assertNull(model.state.value.themedMeditation)
    }

    @Test fun `stale saved position is clamped to its actual script and unknown guide falls back safely`() {
        val model = model(SavedStateHandle(mapOf("sessionGuide" to "TEARS", "stepIndex" to 500)))
        assertEquals(model.state.value.sequence.lastIndex, model.state.value.stepIndex)
        val fallback = model(SavedStateHandle(mapOf("sessionGuide" to "removed", "sessionSet" to "removed", "stepIndex" to -5)))
        assertEquals(PrayerGuide.TRADITIONAL, fallback.state.value.sessionGuide)
        assertEquals(0, fallback.state.value.stepIndex)
        assertNotNull(fallback.state.value.step)
    }

    private class MutableClock(var instant: Instant) : Clock() {
        override fun getZone(): ZoneId = ZoneOffset.UTC
        override fun withZone(zone: ZoneId): Clock = this
        override fun instant(): Instant = instant
    }
}
