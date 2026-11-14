package com.sertumrosarum.domain.formation

import org.junit.Assert.*
import org.junit.Test

class FormationCatalogTest {
    @Test fun `all four pillars provide a complete learning loop and truthful editorial metadata`() {
        assertEquals(16, FormationCatalog.lessons.size)
        assertEquals(FormationCatalog.lessons.size, FormationCatalog.lessons.map { it.id }.distinct().size)
        FormationCatalog.pillars.forEach { pillar -> assertEquals(4, FormationCatalog.lessons.count { it.pillar == pillar }) }
        FormationCatalog.lessons.forEach {
            assertTrue(it.teaching.length > 300)
            assertTrue(it.scripture.isNotBlank() && it.catechism.startsWith("CIC "))
            assertTrue(it.objective.isNotBlank() && it.practice.isNotBlank() && it.prayer.isNotBlank())
            assertEquals(2, it.choices.distinct().size)
            assertTrue(it.correct in it.choices.indices)
            assertTrue(it.explanation.length > 40)
            assertNull(it.theologicalReviewer)
        }
    }
    @Test fun `natural questions retrieve relevant curated answers despite accents and filler words`() {
        listOf("Por que Deus permite o sofrimento?" to "suffering", "Como começar a rezar?" to "start", "A Eucaristia é apenas um símbolo?" to "eucharist", "Por que pedir a intercessão de MARIA?" to "mary").forEach { (query, id) ->
            assertEquals(query, id, FormationCatalog.searchQuestions(query).first().id)
        }
        assertTrue(FormationCatalog.searchQuestions("zxqwypl").isEmpty())
        assertEquals(12, FormationCatalog.searchQuestions("").size)
        assertTrue(FormationCatalog.searchQuestions("a de o").isEmpty())
    }
    @Test fun `practical journeys include preparation participation and daily action`() {
        assertEquals(setOf("mass", "confession", "evening", "mercy"), FormationCatalog.journeys.map { it.id }.toSet())
        FormationCatalog.journeys.forEach { assertTrue(it.steps.size >= 4); assertTrue(it.references.startsWith("CIC")) }
    }
}
