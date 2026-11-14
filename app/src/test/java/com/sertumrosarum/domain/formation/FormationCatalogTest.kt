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
            assertEquals(3, it.choices.distinct().size)
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

    @Test fun `every lesson is a substantial referenced study with retrieval and application`() {
        assertEquals(FormationCatalog.lessons.map { it.id }.toSet(), FormationCurriculum.studies.keys)
        FormationCatalog.lessons.forEach { lesson ->
            val study = lesson.study
            val words = (lesson.teaching + " " + study.searchableText).split(Regex("\\s+")).size
            assertTrue("${lesson.id} needs a full chapter, found $words words", words >= 650)
            assertEquals(4, study.sections.size)
            study.sections.forEach { section ->
                assertTrue(section.title.isNotBlank())
                assertTrue(section.reference.startsWith("CIC "))
                assertTrue(section.body.contains("\n\n"))
            }
            assertTrue(study.scripture.reference.isNotBlank() && study.scripture.context.isNotBlank())
            assertTrue(study.scripture.reading.isNotBlank() && study.scripture.notice.isNotBlank())
            assertTrue(study.example.situation.isNotBlank() && study.example.response.isNotBlank())
            assertEquals(3, study.takeaways.distinct().size)
            assertEquals(3, study.terms.map { it.name }.distinct().size)
            assertEquals(2, study.recall.map { it.question }.distinct().size)
            assertTrue(lesson.readingMinutes >= 4)
            assertEquals(2, lesson.revision)
        }
    }

    @Test fun `search includes the expanded teaching instead of only the old introduction`() {
        val searches = mapOf("catecumenato" to "sacraments", "epiclese" to "mass", "bem-aventuranças" to "dignity", "Tríduo" to "year", "Liturgia das Horas" to "daily")
        searches.forEach { (query, id) ->
            assertTrue(query, FormationCatalog.matches(query, FormationCatalog.lesson(id)!!.searchableText) > 0)
        }
    }
}
