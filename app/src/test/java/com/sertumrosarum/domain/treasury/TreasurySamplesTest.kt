package com.sertumrosarum.domain.treasury

import com.sertumrosarum.domain.prayer.FixedPrayer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The example prayer must agree with itself: its verses are its text, and its expressions are in it. */
class TreasurySamplesTest {
    private val ave = TreasurySamples.AVE_MARIA
    private fun String.flowed() = replace(Regex("\\s+"), " ").trim()

    @Test fun `the verses rejoin into the full Portuguese and Latin texts`() {
        assertEquals(FixedPrayer.HAIL_MARY.text.flowed(), ave.verses.joinToString(" ") { it.portuguese })
        assertEquals(requireNotNull(ave.latin).flowed(), ave.verses.joinToString(" ") { it.latin })
    }

    @Test fun `every explained expression and every gloss is found in its text`() {
        ave.annotations.forEach { assertTrue(it.term, it.term in ave.portuguese) }
        ave.verses.forEach { verse -> verse.glosses.forEach { assertTrue(it.latin, it.latin in verse.latin) } }
    }
}
