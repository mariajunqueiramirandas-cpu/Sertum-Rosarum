package com.sertumrosarum.domain.prayer

import java.time.DayOfWeek
import org.junit.Assert.*
import org.junit.Test

class RosarySequenceBuilderTest {
    @Test fun `each mystery set has 59 beads and the complete 80 step sequence`() {
        MysterySet.entries.forEach { set ->
            val steps = RosarySequenceBuilder.build(set)
            assertEquals(80, steps.size)
            assertEquals(59, steps.count { it.isPhysicalBead })
            assertEquals(53, steps.count { it.prayer == FixedPrayer.HAIL_MARY })
            assertEquals(6, steps.count { it.prayer == FixedPrayer.OUR_FATHER })
            assertEquals(6, steps.count { it.prayer == FixedPrayer.GLORY_BE })
            assertEquals(5, steps.count { it.prayer == FixedPrayer.FATIMA })
            assertEquals(FixedPrayer.SIGN_OF_CROSS, steps.first().prayer)
            assertEquals(FixedPrayer.HAIL_HOLY_QUEEN, steps.last().prayer)
        }
    }

    @Test fun `opening prayers are ordered and the three Ave Marias carry the theological virtues`() {
        val steps = RosarySequenceBuilder.build(MysterySet.JOYFUL).take(8)
        assertEquals(listOf(FixedPrayer.SIGN_OF_CROSS, FixedPrayer.OFFERING, FixedPrayer.CREED, FixedPrayer.OUR_FATHER,
            FixedPrayer.HAIL_MARY, FixedPrayer.HAIL_MARY, FixedPrayer.HAIL_MARY, FixedPrayer.GLORY_BE), steps.map { it.prayer })
        assertEquals(listOf("Pela fé", "Pela esperança", "Pela caridade"), steps.filter { it.beadIndex != null }.map { it.intention })
        assertTrue(steps.all { it.phase == PrayerPhase.INTRODUCTION })
    }

    @Test fun `every decade includes its own announcement ten beads and both closing prayers`() {
        MysterySet.entries.forEach { set ->
            val sequence = RosarySequenceBuilder.build(set)
            repeat(5) { decade ->
                val steps = sequence.filter { it.decadeIndex == decade }
                assertEquals(14, steps.size)
                assertNull(steps.first().prayer)
                assertEquals(set.mysteries[decade], steps.first().mystery)
                assertEquals(FixedPrayer.OUR_FATHER, steps[1].prayer)
                assertEquals((0..9).toList(), steps.filter { it.prayer == FixedPrayer.HAIL_MARY }.map { it.beadIndex })
                assertEquals(FixedPrayer.GLORY_BE, steps[12].prayer)
                assertEquals(FixedPrayer.FATIMA, steps[13].prayer)
            }
            assertEquals(listOf(FixedPrayer.THANKSGIVING, FixedPrayer.HAIL_HOLY_QUEEN), sequence.takeLast(2).map { it.prayer })
        }
    }

    @Test fun `automatic mystery resolver covers every day of the week`() {
        val expected = listOf(MysterySet.JOYFUL, MysterySet.SORROWFUL, MysterySet.GLORIOUS, MysterySet.LUMINOUS, MysterySet.SORROWFUL, MysterySet.JOYFUL, MysterySet.GLORIOUS)
        DayOfWeek.entries.forEachIndexed { index, day -> assertEquals(expected[index], MysterySet.forDay(day)) }
    }

    @Test fun `the twenty mysteries have scripture and fruits and all fixed prayers have complete text`() {
        assertEquals(20, MysterySet.entries.flatMap { it.mysteries }.map { it.title }.distinct().size)
        assertTrue(MysterySet.entries.flatMap { it.mysteries }.all { it.scripture.isNotBlank() && it.fruit.isNotBlank() })
        assertTrue(FixedPrayer.entries.all { it.text.length > 50 && it.title.isNotBlank() })
    }
}
