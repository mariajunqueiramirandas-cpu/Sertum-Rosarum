package com.sertumrosarum.domain.liturgy

import com.sertumrosarum.domain.prayer.MysterySet
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class LiturgicalCalendarTest {

    @Test fun `Computus matches canonical Gregorian Easter dates`() {
        assertEquals(LocalDate.of(2024, 3, 31), LiturgicalCalendar.easterSunday(2024))
        assertEquals(LocalDate.of(2025, 4, 20), LiturgicalCalendar.easterSunday(2025))
        assertEquals(LocalDate.of(2026, 4, 5), LiturgicalCalendar.easterSunday(2026))
        assertEquals(LocalDate.of(2027, 3, 28), LiturgicalCalendar.easterSunday(2027))
    }

    @Test fun `Eastertide recommends Regina Caeli and Ordinary Time recommends Angelus`() {
        val easter = LiturgicalCalendar.forDate(LocalDate.of(2026, 4, 5))
        assertEquals(LiturgicalSeason.EASTER, easter.season)
        assertEquals("regina-caeli", easter.marianAntiphonId)
        assertTrue(easter.isEastertide)
        assertEquals(MysterySet.GLORIOUS, easter.recommendedMysterySet)

        val pentecost = LiturgicalCalendar.forDate(LocalDate.of(2026, 5, 24))
        assertEquals(LiturgicalSeason.EASTER, pentecost.season)
        assertEquals("regina-caeli", pentecost.marianAntiphonId)

        val ordinary = LiturgicalCalendar.forDate(LocalDate.of(2026, 11, 12))
        assertEquals(LiturgicalSeason.ORDINARY, ordinary.season)
        assertEquals("angelus", ordinary.marianAntiphonId)
        assertFalse(ordinary.isEastertide)
        assertEquals(MysterySet.LUMINOUS, ordinary.recommendedMysterySet)
    }

    @Test fun `major Brazilian and universal solemnities are recognized with appropriate devotions`() {
        val aparecida = LiturgicalCalendar.forDate(LocalDate.of(2026, 10, 12))
        assertTrue(aparecida.celebration?.contains("Aparecida") == true)
        assertEquals(LiturgicalColor.WHITE, aparecida.color)

        val finados = LiturgicalCalendar.forDate(LocalDate.of(2026, 11, 2))
        assertEquals("DECEASED", finados.recommendedGuideName)
        assertEquals(MysterySet.SORROWFUL, finados.recommendedMysterySet)

        val saintJoseph = LiturgicalCalendar.forDate(LocalDate.of(2026, 3, 19))
        assertEquals("SAINT_JOSEPH", saintJoseph.recommendedGuideName)

        val divineMercy = LiturgicalCalendar.forDate(LocalDate.of(2026, 4, 12))
        assertEquals("DIVINE_MERCY", divineMercy.recommendedGuideName)

        val goodFriday = LiturgicalCalendar.forDate(LocalDate.of(2026, 4, 3))
        assertEquals(LiturgicalSeason.TRIDUUM, goodFriday.season)
        assertEquals("VIA_SACRA", goodFriday.recommendedGuideName)
        assertEquals(LiturgicalColor.RED, goodFriday.color)

        val lentenFriday = LiturgicalCalendar.forDate(LocalDate.of(2026, 2, 27))
        assertEquals(LiturgicalSeason.LENT, lentenFriday.season)
        assertEquals("VIA_SACRA", lentenFriday.recommendedGuideName)
    }

    @Test fun `Gaudete and Laetare Sundays use rose liturgical color`() {
        // 2026: Easter is Apr 5 -> Laetare (4th Sunday of Lent) is Mar 15.
        val laetare = LiturgicalCalendar.forDate(LocalDate.of(2026, 3, 15))
        assertEquals(LiturgicalColor.ROSE, laetare.color)
        assertTrue(laetare.celebration?.contains("Laetare") == true)

        // 2026: Advent starts Nov 29 -> Gaudete (3rd Sunday of Advent) is Dec 13.
        val gaudete = LiturgicalCalendar.forDate(LocalDate.of(2026, 12, 13))
        assertEquals(LiturgicalColor.ROSE, gaudete.color)
        assertTrue(gaudete.celebration?.contains("Gaudete") == true)
    }
}
