package com.sertumrosarum.domain.prayer

import org.junit.Assert.*
import org.junit.Test

class PrayerScriptsTest {
    @Test fun `all twenty mysteries have scripture three distinct layers and two hundred bead lines`() {
        val content = MysterySet.entries.flatMap { set -> (0..4).map { MysteryContent.contemplationFor(set, it) } }
        assertEquals(20, content.size)
        assertEquals(200, content.sumOf { it.beads.size })
        content.forEach {
            assertTrue(it.scriptureQuote.isNotBlank())
            assertTrue(it.scene.length > 100 && it.meaning.length > 100 && it.practice.isNotBlank())
            assertEquals(10, it.beads.distinct().size)
        }
        assertEquals(20, content.map { it.scene }.distinct().size)
        assertTrue(MysteryContent.contemplationFor(MysterySet.GLORIOUS, 3).scene.contains("não narra diretamente"))
    }

    @Test fun `ordinary and catechetical scripts preserve the complete rosary`() {
        MysterySet.entries.forEach { set ->
            val traditional = RosarySequenceBuilder.build(set)
            assertEquals(traditional, PrayerScript.TRADITIONAL.build(set))
            assertEquals(traditional, PrayerScript.CATECHETICAL.build(set))
        }
    }

    @Test fun `complete rosary prays every invocation and collect after the Salve Rainha`() {
        val steps = PrayerScript.COMPLETE.build(MysterySet.SORROWFUL)
        assertEquals(59, steps.count { it.isPhysicalBead })
        assertEquals(5, steps.count { it.reading == DevotionalPrayer.MARY_MOTHER_OF_GRACE })
        steps.filter { it.prayer == FixedPrayer.FATIMA }.forEach {
            assertEquals(DevotionalPrayer.MARY_MOTHER_OF_GRACE, steps[steps.indexOf(it) + 1].reading)
        }
        val salve = steps.indexOfFirst { it.prayer == FixedPrayer.HAIL_HOLY_QUEEN }
        assertEquals(LitanyOfLoreto.invocations, steps.drop(salve + 1).dropLast(1).map { it.reading })
        assertEquals(DevotionalPrayer.LORETO_CLOSING, steps.last().reading)
        assertTrue(steps.drop(salve).all { it.phase == PrayerPhase.CLOSING })
    }

    @Test fun `litany includes current Marian invocations and the different responses`() {
        val verses = LitanyOfLoreto.invocations
        assertEquals(67, verses.size)
        assertEquals(54, verses.count { it.response == "rogai por nós." })
        listOf("Mãe de misericórdia,", "Mãe da esperança,", "Conforto dos migrantes,", "Rainha da família,", "Rainha da paz,").forEach { call ->
            assertTrue(verses.any { it.text == call && it.response == "rogai por nós." })
        }
        assertEquals(listOf("perdoai-nos, Senhor.", "ouvi-nos, Senhor.", "tende piedade de nós."), verses.takeLast(4).take(3).map { it.response })
    }

    @Test fun `thematic scripts have an offering and a specific reflection for each mystery`() {
        val scripts = listOf(PrayerScript.FAMILY, PrayerScript.SICK, PrayerScript.PEACE)
        val reflections = scripts.flatMap { script ->
            MysterySet.entries.flatMap { set ->
                val sequence = script.build(set)
                assertEquals(81, sequence.size)
                assertEquals(59, sequence.count { it.isPhysicalBead })
                assertEquals(script.offering, sequence[2].reading)
                (0..4).map { requireNotNull(ThemedMeditations.forMystery(script, set, it)) }
            }
        }
        assertEquals(60, reflections.distinct().size)
        assertTrue(reflections.all { it.length > 60 })
        assertNull(ThemedMeditations.forMystery(PrayerScript.TRADITIONAL, MysterySet.JOYFUL, 0))
    }

    @Test fun `Divine Mercy uses five offerings fifty pleas and three closing Holy Gods`() {
        val steps = PrayerScript.DIVINE_MERCY.build(MysterySet.JOYFUL)
        assertEquals(listOf(FixedPrayer.SIGN_OF_CROSS, FixedPrayer.OUR_FATHER, FixedPrayer.HAIL_MARY, FixedPrayer.CREED), steps.take(4).map { it.prayer })
        assertEquals(5, steps.count { it.reading == DevotionalPrayer.ETERNAL_FATHER })
        assertEquals(50, steps.count { it.reading == DevotionalPrayer.SORROWFUL_PASSION })
        assertEquals(List(3) { DevotionalPrayer.HOLY_GOD }, steps.takeLast(3).map { it.reading })
        assertTrue(steps.all { it.mystery == null })
        assertGroups(steps, 5, 10)
    }

    @Test fun `Saint Joseph has its own five mysteries and fifty invocations`() {
        val steps = PrayerScript.SAINT_JOSEPH.build(MysterySet.LUMINOUS)
        assertEquals(5, steps.count { it.isAnnouncement })
        assertTrue(steps.any { it.mystery?.title == "A fuga para o Egito" })
        assertEquals(50, steps.count { it.reading == DevotionalPrayer.JOSEPH })
        assertEquals(5, steps.count { it.prayer == FixedPrayer.OUR_FATHER })
        assertEquals(5, steps.count { it.prayer == FixedPrayer.GLORY_BE })
        assertGroups(steps, 5, 10)
    }

    @Test fun `Crown of Tears has seven setenas and forty nine pleas`() {
        val steps = PrayerScript.TEARS.build(MysterySet.GLORIOUS)
        assertGroups(steps, 7, 7)
        assertEquals(49, steps.count { it.reading == DevotionalPrayer.TEARS_PLEA })
        assertEquals(10, steps.count { it.reading == DevotionalPrayer.TEARS_OFFERING })
        assertEquals(DevotionalPrayer.TEARS_CLOSING, steps.last().reading)
    }

    @Test fun `Franciscan crown has seven joys seventy two Ave Marias and the final pair`() {
        val steps = PrayerScript.FRANCISCAN.build(MysterySet.SORROWFUL)
        assertGroups(steps, 7, 10)
        assertEquals(7, steps.count { it.isAnnouncement })
        assertEquals(72, steps.count { it.prayer == FixedPrayer.HAIL_MARY })
        assertEquals(7, steps.count { it.prayer == FixedPrayer.OUR_FATHER })
        assertEquals(listOf(0, 1), steps.filter { it.phase == PrayerPhase.CLOSING && it.isSmallBead }.map { it.beadIndex })
        assertEquals(FixedPrayer.SIGN_OF_CROSS, steps.last().prayer)
    }

    @Test fun `chaplets are independent of the rosary mystery selection`() {
        PrayerScript.entries.filterNot { it.isRosary }.forEach { script ->
            MysterySet.entries.forEach { assertEquals(script.build(MysterySet.JOYFUL), script.build(it)) }
        }
    }

    @Test fun `engine supports mixed group counts without rosary assumptions`() {
        val reading = PrayerReading("Invocação", "Senhor, ouvi-nos.")
        val definition = ChapletDefinition("custom", "Coroa", emptyList(),
            listOf(ChapletGroup(reading, reading, 2), ChapletGroup(reading, reading, 4)), emptyList())
        val steps = ChapletEngine.build(definition)
        assertEquals(8, steps.size)
        assertEquals(listOf(0, 1), steps.filter { it.decadeIndex == 0 && it.isSmallBead }.map { it.beadIndex })
        assertEquals(listOf(0, 1, 2, 3), steps.filter { it.decadeIndex == 1 && it.isSmallBead }.map { it.beadIndex })
        assertTrue(steps.all { it.isPhysicalBead && it.text.isNotBlank() })
        assertThrows(IllegalArgumentException::class.java) { ChapletGroup(reading, reading, 0) }
        assertThrows(IllegalArgumentException::class.java) { definition.copy(groups = emptyList()) }
    }

    private fun assertGroups(steps: List<PrayerStep>, groups: Int, beads: Int) {
        assertEquals((0 until groups).toList(), steps.mapNotNull { it.decadeIndex }.distinct())
        repeat(groups) { index ->
            val group = steps.filter { it.decadeIndex == index && it.isSmallBead }
            assertEquals((0 until beads).toList(), group.map { it.beadIndex })
            assertTrue(group.all { it.beadCount == beads })
        }
    }
}
