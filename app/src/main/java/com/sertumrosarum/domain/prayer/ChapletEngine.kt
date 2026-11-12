package com.sertumrosarum.domain.prayer

/** One group can have its own count, announcement, large bead and closing prayers. */
data class ChapletGroup(
    val largeBead: PrayerReading,
    val smallBead: PrayerReading,
    val beadCount: Int,
    val mystery: Mystery? = null,
    val largePrayer: FixedPrayer? = null,
    val smallPrayer: FixedPrayer? = null,
    val after: List<FixedPrayer> = emptyList(),
) {
    init { require(beadCount > 0) }
}

data class ChapletDefinition(
    val id: String,
    val title: String,
    val introduction: List<PrayerStep>,
    val groups: List<ChapletGroup>,
    val closing: List<PrayerStep>,
    val beadLabel: String = "Conta",
    val groupLabel: String = "dezena",
) {
    init {
        require(id.isNotBlank() && title.isNotBlank())
        require(groups.isNotEmpty())
        require(introduction.all { it.phase == PrayerPhase.INTRODUCTION && it.decadeIndex == null })
        require(closing.all { it.phase == PrayerPhase.CLOSING && it.decadeIndex == null })
    }
}

object ChapletEngine {
    fun build(definition: ChapletDefinition): List<PrayerStep> = buildList {
        addAll(definition.introduction)
        definition.groups.forEachIndexed { index, group ->
            group.mystery?.let { add(PrayerStep(phase = PrayerPhase.DECADE, decadeIndex = index, mystery = it)) }
            add(PrayerStep(group.largePrayer, PrayerPhase.DECADE, index, beadCount = group.beadCount,
                mystery = group.mystery, reading = group.largeBead, physicalBead = true))
            repeat(group.beadCount) { bead ->
                add(PrayerStep(group.smallPrayer, PrayerPhase.DECADE, index, bead, group.beadCount,
                    group.mystery, reading = group.smallBead, physicalBead = true))
            }
            group.after.forEach { add(PrayerStep(it, PrayerPhase.DECADE, index, beadCount = group.beadCount, mystery = group.mystery)) }
        }
        addAll(definition.closing)
    }
}

private fun FixedPrayer.reading() = PrayerReading(title, text)
private fun opening(prayer: FixedPrayer) = PrayerStep(prayer, PrayerPhase.INTRODUCTION)
private fun closing(prayer: FixedPrayer, bead: Int? = null, count: Int = 0) =
    PrayerStep(prayer, PrayerPhase.CLOSING, beadIndex = bead, beadCount = count)
private fun closing(reading: PrayerReading) = PrayerStep(phase = PrayerPhase.CLOSING, reading = reading)

/** Concrete local devotions; these do not depend on the sanctuary's mystery selection. */
object Chaplets {
    val divineMercy = ChapletDefinition(
        "divine_mercy", "Terço da Divina Misericórdia",
        listOf(FixedPrayer.SIGN_OF_CROSS, FixedPrayer.OUR_FATHER, FixedPrayer.HAIL_MARY, FixedPrayer.CREED).map(::opening),
        List(5) { ChapletGroup(DevotionalPrayer.ETERNAL_FATHER, DevotionalPrayer.SORROWFUL_PASSION, 10) },
        List(3) { closing(DevotionalPrayer.HOLY_GOD).copy(intention = "${it + 1} de 3") },
    )

    val saintJoseph = ChapletDefinition(
        "saint_joseph", "Terço de São José",
        listOf(opening(FixedPrayer.SIGN_OF_CROSS)),
        listOf(
            Mystery("O sonho de São José", "Mt 1, 18–24", "Confiança em Deus"),
            Mystery("O nascimento de Jesus em Belém", "Lc 2, 1–20", "Acolhimento"),
            Mystery("A apresentação de Jesus no Templo", "Lc 2, 22–38", "Fidelidade"),
            Mystery("A fuga para o Egito", "Mt 2, 13–15", "Proteção da vida"),
            Mystery("O encontro de Jesus no Templo", "Lc 2, 41–52", "Busca de Deus"),
        ).map { ChapletGroup(FixedPrayer.OUR_FATHER.reading(), DevotionalPrayer.JOSEPH, 10, it,
            largePrayer = FixedPrayer.OUR_FATHER, after = listOf(FixedPrayer.GLORY_BE)) },
        listOf(closing(FixedPrayer.SIGN_OF_CROSS)),
    )

    val tears = ChapletDefinition(
        "tears", "Coroa das Lágrimas",
        listOf(opening(FixedPrayer.SIGN_OF_CROSS), PrayerStep(phase = PrayerPhase.INTRODUCTION, reading = DevotionalPrayer.TEARS_OPENING)),
        List(7) { ChapletGroup(DevotionalPrayer.TEARS_OFFERING, DevotionalPrayer.TEARS_PLEA, 7) },
        List(3) { closing(DevotionalPrayer.TEARS_OFFERING).copy(intention = "${it + 1} de 3") } + closing(DevotionalPrayer.TEARS_CLOSING),
        groupLabel = "setena",
    )

    val franciscan = ChapletDefinition(
        "franciscan", "Coroa Franciscana",
        listOf(opening(FixedPrayer.SIGN_OF_CROSS)),
        listOf(
            MysterySet.JOYFUL.mysteries[0], MysterySet.JOYFUL.mysteries[1], MysterySet.JOYFUL.mysteries[2],
            Mystery("A adoração dos Magos", "Mt 2, 1–12", "Adoração"),
            MysterySet.JOYFUL.mysteries[4], MysterySet.GLORIOUS.mysteries[0],
            Mystery("A Assunção e Coroação de Maria", "Lc 1, 46–55; Ap 12, 1", "Esperança na vida eterna"),
        ).map { ChapletGroup(FixedPrayer.OUR_FATHER.reading(), FixedPrayer.HAIL_MARY.reading(), 10, it,
            FixedPrayer.OUR_FATHER, FixedPrayer.HAIL_MARY, listOf(FixedPrayer.GLORY_BE)) },
        List(2) { closing(FixedPrayer.HAIL_MARY, it, 2).copy(intention = "Pelas alegrias de Maria · ${it + 71}ª Ave-Maria") } + closing(FixedPrayer.SIGN_OF_CROSS),
        beadLabel = "Ave-Maria",
    )
}
