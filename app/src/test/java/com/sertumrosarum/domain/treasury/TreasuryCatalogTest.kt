package com.sertumrosarum.domain.treasury

import com.sertumrosarum.domain.prayer.*
import org.junit.Assert.*
import org.junit.Test

class TreasuryCatalogTest {
    private val prayers = TreasuryCatalog.prayers
    private fun String.flowed() = replace(Regex("\\s+"), " ").trim()

    @Test fun `library has substantive unique entries in every required category`() {
        assertTrue(prayers.size >= 60)
        assertEquals(prayers.size, prayers.map { it.id }.distinct().size)
        assertEquals(prayers.size, prayers.map { it.portuguese }.distinct().size)
        PrayerCategory.entries.forEach { category -> assertTrue(category.name, prayers.count { it.category == category } >= 4) }
        prayers.forEach { prayer ->
            assertTrue(prayer.id, prayer.context.isNotBlank() && !prayer.source.isNullOrBlank())
            assertTrue(prayer.id, prayer.portuguese.length >= 30)
            assertFalse(prayer.id, "__LORETO__" in prayer.portuguese || "\$it" in prayer.portuguese)
        }
    }

    @Test fun `all ten fixed rosary prayers share their Portuguese with the treasury and offer Latin`() {
        FixedPrayer.entries.forEach { fixed ->
            val prayer = requireNotNull(TreasuryCatalog.latinFor(fixed)) { fixed.name }
            assertEquals(fixed.text, prayer.portuguese)
            assertFalse(prayer.latin.isNullOrBlank())
        }
        assertTrue(requireNotNull(TreasuryCatalog.find("oferecimento-do-terco")).latinSolo!!.contains("óffero"))
    }

    @Test fun `all bilingual verses reconstruct both texts and every gloss exists in its verse`() {
        assertTrue(prayers.count { it.latin != null } >= 28)
        prayers.filter { it.latin != null }.forEach { prayer ->
            assertEquals(prayer.id, prayer.portuguese.flowed(), prayer.verses.joinToString(" ") { it.portuguese }.flowed())
            assertEquals(prayer.id, prayer.latin!!.flowed(), prayer.verses.joinToString(" ") { it.latin }.flowed())
            assertTrue(prayer.id, prayer.verses.any { it.glosses.isNotEmpty() })
            prayer.verses.forEach { verse -> verse.glosses.forEach { assertTrue(it.latin, it.latin in verse.latin) } }
        }
    }

    @Test fun `all explained terms match and include origins Scripture Catechism and application`() {
        assertTrue(prayers.sumOf { it.annotations.size } >= 75)
        prayers.forEach { prayer ->
            prayer.annotations.forEach { annotation ->
                assertTrue("${prayer.id}: ${annotation.term}", PrayerAnnotationParser.parse(prayer.portuguese, listOf(annotation)).isNotEmpty())
                assertTrue(annotation.origin.length >= 70 && annotation.today.length >= 70)
                assertFalse(annotation.scriptureReference.isNullOrBlank())
                assertFalse(annotation.catechism.isNullOrBlank())
            }
        }
    }

    @Test fun `complete litany exactly matches the existing devotional engine including its final collect`() {
        val expected = LitanyOfLoreto.invocations.joinToString("\n\n") { "${it.text}\n${it.response}" } + "\n\nOremos. " + DevotionalPrayer.LORETO_CLOSING.text
        assertEquals(expected, TreasuryCatalog.find("ladainha-lauretana")!!.portuguese)
    }

    @Test fun `source transcription errors are corrected without conflating the two Spirit hymns`() {
        assertTrue(TreasuryCatalog.find("sinal-da-cruz")!!.latin!!.contains("Sancti"))
        assertFalse(TreasuryCatalog.find("regina-caeli")!!.latin!!.contains("quelli"))
        assertNotEquals(TreasuryCatalog.find("veni-creator")!!.portuguese, TreasuryCatalog.find("veni-sancte-spiritus")!!.portuguese)
        assertTrue(TreasuryCatalog.find("credo-niceno")!!.annotations.any { it.term == "consubstancial ao Pai" })
    }

    @Test fun `search handles accents punctuation ligatures multiple words and category intersection`() {
        fun ids(query: String, category: PrayerCategory? = null) = TreasuryCatalog.search(query, category).map { it.id }
        assertTrue("oracao-da-noite" in ids("ORACAO noite"))
        assertTrue("regina-caeli" in ids("regina caeli"))
        assertTrue("pai-nosso" in ids("pai nosso"))
        assertTrue("salmo-90-91" in ids("salmo 91"))
        assertTrue("salmo-90-91" in ids("refúgio", PrayerCategory.NIGHT))
        assertFalse("salmo-90-91" in ids("refugio", PrayerCategory.WAKING))
        assertTrue(ids("nomequenuncaexiste").isEmpty())
        assertEquals(prayers, TreasuryCatalog.search(" \n\t "))
        assertEquals("ave-maria", ids("ave maria").first())
    }
}
