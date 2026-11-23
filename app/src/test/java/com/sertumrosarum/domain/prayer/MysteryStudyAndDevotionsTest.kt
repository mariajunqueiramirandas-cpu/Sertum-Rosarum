package com.sertumrosarum.domain.prayer

import com.sertumrosarum.domain.formation.FormationCatalog
import org.junit.Assert.*
import org.junit.Test

class MysteryStudyAndDevotionsTest {

    @Test fun `all twenty mysteries have rich biblical, historical, patristic, and contemplative study content`() {
        val studies = MysteryStudyCatalog.allStudies()
        assertEquals(20, studies.size)
        studies.forEach { (set, index, study) ->
            assertTrue("Missing scripture title for $set:$index", study.scriptureTitle.isNotBlank())
            assertTrue("Short scripture reading for $set:$index", study.scriptureReading.length > 180)
            assertTrue("Short historical context for $set:$index", study.historicalContext.length > 200)
            assertTrue("Short theological depth for $set:$index", study.theologicalDepth.length > 180)
            assertTrue("Missing CIC refs for $set:$index", study.catechismRefs.startsWith("CIC "))
            assertTrue("Expected 2+ saint voices for $set:$index", study.saintsVoices.size >= 2)
            assertTrue("Expected 2+ symbols for $set:$index", study.symbols.size >= 2)
            assertTrue("Expected 2+ reflection questions for $set:$index", study.reflectionQuestions.size >= 2)
            assertTrue("Missing closing prayer for $set:$index", study.closingPrayer.isNotBlank())
        }
        assertEquals(20, studies.map { it.third.historicalContext }.distinct().size)
        assertEquals(20, studies.map { it.third.closingPrayer }.distinct().size)
    }

    @Test fun `single decade builds any chosen mystery cleanly`() {
        MysterySet.entries.forEach { set ->
            (0..4).forEach { focus ->
                val steps = PrayerScript.SINGLE_DECADE.build(set, focus)
                assertEquals(17, steps.size)
                assertEquals(1, steps.count { it.isAnnouncement })
                assertEquals(set.mysteries[focus], steps.first { it.isAnnouncement }.mystery)
                assertEquals(10, steps.count { it.prayer == FixedPrayer.HAIL_MARY })
                assertEquals(FixedPrayer.HAIL_HOLY_QUEEN, steps.last().prayer)
            }
        }
    }

    @Test fun `full rosary traverses all twenty mysteries across all four sets`() {
        val steps = PrayerScript.FULL_ROSARY.build(MysterySet.JOYFUL)
        assertEquals(20, steps.count { it.isAnnouncement })
        assertEquals(203, steps.count { it.prayer == FixedPrayer.HAIL_MARY })
        assertEquals(MysterySet.entries, steps.filter { it.isAnnouncement }.map { it.mysterySet }.distinct())
        assertEquals(steps, PrayerScript.FULL_ROSARY.build(MysterySet.SORROWFUL))
    }

    @Test fun `classic Brazilian devotions have authentic structures and full contemplations`() {
        val viaSacra = PrayerScript.VIA_SACRA.build(MysterySet.SORROWFUL)
        assertEquals(14, viaSacra.count { it.isAnnouncement })
        assertEquals(14, viaSacra.count { it.reading == DevotionalPrayer.VIA_SACRA_ADORATION })
        assertEquals(14, viaSacra.count { it.prayer == FixedPrayer.OUR_FATHER })
        assertEquals(14, viaSacra.count { it.prayer == FixedPrayer.HAIL_MARY })
        repeat(14) { assertNotNull(ChapletContemplations.forStep(PrayerScript.VIA_SACRA, MysterySet.SORROWFUL, it)) }

        val sevenSorrows = PrayerScript.SEVEN_SORROWS.build(MysterySet.SORROWFUL)
        assertEquals(7, sevenSorrows.count { it.isAnnouncement })
        assertEquals(52, sevenSorrows.count { it.prayer == FixedPrayer.HAIL_MARY }) // 7*7 + 3
        repeat(7) { assertNotNull(ChapletContemplations.forStep(PrayerScript.SEVEN_SORROWS, MysterySet.SORROWFUL, it)) }

        val holyWounds = PrayerScript.HOLY_WOUNDS.build(MysterySet.SORROWFUL)
        assertEquals(5, holyWounds.count { it.isAnnouncement })
        assertEquals(50, holyWounds.count { it.reading == DevotionalPrayer.HOLY_WOUNDS_SMALL })
        assertEquals(8, holyWounds.count { it.reading == DevotionalPrayer.HOLY_WOUNDS_LARGE }) // 5 + 3 closing
        repeat(5) { assertNotNull(ChapletContemplations.forStep(PrayerScript.HOLY_WOUNDS, MysterySet.SORROWFUL, it)) }

        val michael = PrayerScript.SAINT_MICHAEL.build(MysterySet.GLORIOUS)
        assertEquals(9, michael.count { it.isAnnouncement })
        assertEquals(27, michael.count { it.prayer == FixedPrayer.HAIL_MARY }) // 9*3
        assertEquals(13, michael.count { it.prayer == FixedPrayer.OUR_FATHER }) // 9 + 4 concluding
        repeat(9) { assertNotNull(ChapletContemplations.forStep(PrayerScript.SAINT_MICHAEL, MysterySet.GLORIOUS, it)) }

        // Improved existing chaplets also have full contemplations for every group.
        repeat(5) { assertNotNull(ChapletContemplations.forStep(PrayerScript.DIVINE_MERCY, MysterySet.JOYFUL, it)) }
        repeat(5) { assertNotNull(ChapletContemplations.forStep(PrayerScript.SAINT_JOSEPH, MysterySet.JOYFUL, it)) }
        repeat(7) { assertNotNull(ChapletContemplations.forStep(PrayerScript.TEARS, MysterySet.JOYFUL, it)) }
        repeat(7) { assertNotNull(ChapletContemplations.forStep(PrayerScript.FRANCISCAN, MysterySet.JOYFUL, it)) }
    }

    @Test fun `catechesis quiz permutation places the correct answer across all three positions`() {
        val positions = FormationCatalog.lessons.map { lesson ->
            val order = when (Math.floorMod(lesson.id.hashCode(), 3)) {
                0 -> listOf(0, 1, 2)
                1 -> listOf(1, 0, 2)
                else -> listOf(2, 1, 0)
            }
            order.indexOf(lesson.correct)
        }.toSet()
        assertEquals(setOf(0, 1, 2), positions)
    }
}
