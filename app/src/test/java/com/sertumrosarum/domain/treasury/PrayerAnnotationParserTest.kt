package com.sertumrosarum.domain.treasury

import org.junit.Assert.*
import org.junit.Test

class PrayerAnnotationParserTest {
    private fun term(text: String) = PrayerTermAnnotation(text, "origin", "today")
    @Test fun `matches all repetitions whole words and original case across line breaks`() {
        val text = "Graça e GRAÇA; desgraça. Cheia\nde graça."
        val ranges = PrayerAnnotationParser.parse(text, listOf(term("graça"), term("cheia de graça")))
        assertEquals(listOf("Graça", "GRAÇA", "Cheia\nde graça"), ranges.map { text.substring(it.range) })
    }
    @Test fun `longest expression wins overlaps and editorial order breaks ties`() {
        val first = term("Mãe de Deus")
        val ranges = PrayerAnnotationParser.parse("Santa Mãe de Deus", listOf(term("Deus"), first, first.copy(today = "duplicate")))
        assertEquals(listOf(first), ranges.map { it.annotation })
    }
    @Test fun `canonical accents preserve UTF16 offsets and do not conflate distinct accents`() {
        val text = "🌹 grac\u0327a, avó e avô."
        val ranges = PrayerAnnotationParser.parse(text, listOf(term("graça"), term("avó")))
        assertEquals(listOf("grac\u0327a", "avó"), ranges.map { text.substring(it.range) })
    }
    @Test fun `blank absent and regex metacharacters are safe`() {
        assertTrue(PrayerAnnotationParser.parse("Ave Maria", listOf(term(""), term("  "), term(".*"))).isEmpty())
        assertEquals(1, PrayerAnnotationParser.parse("(Amém.)", listOf(term("Amém."))).size)
        assertTrue(PrayerAnnotationParser.parse("graças", listOf(term("graça"))).isEmpty())
    }
}
