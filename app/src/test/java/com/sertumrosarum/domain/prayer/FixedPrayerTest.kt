package com.sertumrosarum.domain.prayer

import org.junit.Assert.*
import org.junit.Test

class FixedPrayerTest {
    @Test fun `solo offering and thanksgiving address the prayer in the first person`() {
        assertEquals(
            "Divino Jesus, eu Vos ofereço este terço que vou rezar, contemplando os mistérios da nossa Redenção. Concedei-me, por intercessão da Virgem Maria, Mãe de Deus e minha Mãe, as virtudes que me são necessárias para bem rezá-lo e a graça de ganhar as indulgências desta santa devoção.",
            FixedPrayer.OFFERING.textFor(PrayerMode.SOLO),
        )
        assertEquals(
            "Infinitas graças vos dou, soberana Rainha, pelos benefícios que todos os dias recebo de vossas mãos liberais. Dignai-vos, agora e para sempre, tomar-me debaixo do vosso poderoso amparo e, para mais vos obrigar, vos saúdo com uma Salve Rainha.",
            FixedPrayer.THANKSGIVING.textFor(PrayerMode.SOLO),
        )
    }

    @Test fun `common traditional prayers retain their text in both modes`() {
        FixedPrayer.entries.filter { it != FixedPrayer.OFFERING && it != FixedPrayer.THANKSGIVING }.forEach { prayer ->
            PrayerMode.entries.forEach { mode -> assertEquals(prayer.text, prayer.textFor(mode)) }
        }
    }

    @Test fun `group mode keeps every original prayer text`() {
        FixedPrayer.entries.forEach { prayer -> assertEquals(prayer.text, prayer.textFor(PrayerMode.GROUP)) }
    }
}
