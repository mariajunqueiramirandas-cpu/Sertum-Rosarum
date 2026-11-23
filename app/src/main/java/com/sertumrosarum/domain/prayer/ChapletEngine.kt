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
    /** Optional preparatory invocation before the large bead (e.g., Via-Sacra adoration). */
    val beforeLarge: List<PrayerReading> = emptyList(),
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
            group.beforeLarge.forEach {
                add(PrayerStep(phase = PrayerPhase.DECADE, decadeIndex = index, mystery = group.mystery, reading = it, physicalBead = false))
            }
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
private fun opening(reading: PrayerReading) = PrayerStep(phase = PrayerPhase.INTRODUCTION, reading = reading)
private fun closing(prayer: FixedPrayer, bead: Int? = null, count: Int = 0) =
    PrayerStep(prayer, PrayerPhase.CLOSING, beadIndex = bead, beadCount = count)
private fun closing(reading: PrayerReading) = PrayerStep(phase = PrayerPhase.CLOSING, reading = reading)

/** Concrete local devotions; these do not depend on the sanctuary's mystery selection. */
object Chaplets {
    val divineMercy = ChapletDefinition(
        "divine_mercy", "Terço da Divina Misericórdia",
        listOf(FixedPrayer.SIGN_OF_CROSS, FixedPrayer.OUR_FATHER, FixedPrayer.HAIL_MARY, FixedPrayer.CREED).map(::opening),
        List(5) { ChapletGroup(DevotionalPrayer.ETERNAL_FATHER, DevotionalPrayer.SORROWFUL_PASSION, 10) },
        List(3) { closing(DevotionalPrayer.HOLY_GOD).copy(intention = "${it + 1} de 3") } + closing(DevotionalPrayer.DIVINE_MERCY_CLOSING),
    )

    val saintJoseph = ChapletDefinition(
        "saint_joseph", "Terço de São José",
        listOf(opening(FixedPrayer.SIGN_OF_CROSS)),
        ChapletContemplations.saintJoseph.map { item ->
            ChapletGroup(FixedPrayer.OUR_FATHER.reading(), DevotionalPrayer.JOSEPH, 10, item.toMystery(),
                largePrayer = FixedPrayer.OUR_FATHER, after = listOf(FixedPrayer.GLORY_BE))
        },
        listOf(closing(DevotionalPrayer.JOSEPH_CLOSING), closing(FixedPrayer.SIGN_OF_CROSS)),
    )

    val tears = ChapletDefinition(
        "tears", "Coroa das Lágrimas",
        listOf(opening(FixedPrayer.SIGN_OF_CROSS), opening(DevotionalPrayer.TEARS_OPENING)),
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

    /** Classic Servite Chaplet of the Seven Sorrows of Mary: 7 groups of 7 Ave-Marias + 3 final Ave-Marias. */
    val sevenSorrows = ChapletDefinition(
        "seven_sorrows", "Terço das Sete Dores",
        listOf(opening(FixedPrayer.SIGN_OF_CROSS), opening(DevotionalPrayer.SEVEN_SORROWS_OPENING)),
        ChapletContemplations.sevenSorrows.map { sorrow ->
            ChapletGroup(FixedPrayer.OUR_FATHER.reading(), FixedPrayer.HAIL_MARY.reading(), 7, sorrow.toMystery(),
                FixedPrayer.OUR_FATHER, FixedPrayer.HAIL_MARY, listOf(FixedPrayer.GLORY_BE))
        },
        List(3) { closing(FixedPrayer.HAIL_MARY, it, 3).copy(intention = "Em honra das lágrimas de Maria · ${it + 1} de 3") } +
            closing(DevotionalPrayer.SEVEN_SORROWS_CLOSING),
        beadLabel = "Ave-Maria",
        groupLabel = "dor",
    )

    /** Beloved Brazilian devotion: Chaplet of the Five Holy Wounds of Jesus. */
    val holyWounds = ChapletDefinition(
        "holy_wounds", "Terço das Santas Chagas",
        listOf(opening(FixedPrayer.SIGN_OF_CROSS), opening(DevotionalPrayer.HOLY_WOUNDS_OPENING)),
        ChapletContemplations.holyWounds.map { wound ->
            ChapletGroup(DevotionalPrayer.HOLY_WOUNDS_LARGE, DevotionalPrayer.HOLY_WOUNDS_SMALL, 10, wound.toMystery())
        },
        List(3) { closing(DevotionalPrayer.HOLY_WOUNDS_LARGE).copy(intention = "Pela Igreja e pelas famílias · ${it + 1} de 3") } +
            closing(DevotionalPrayer.HOLY_WOUNDS_CLOSING),
        beadLabel = "Invocação",
        groupLabel = "chaga",
    )

    /** Chaplet of St. Michael the Archangel (9 Choirs of Angels, 1 Pai-Nosso + 3 Ave-Marias each + 4 concluding Pai-Nossos). */
    val saintMichael = ChapletDefinition(
        "saint_michael", "Terço de São Miguel",
        listOf(opening(FixedPrayer.SIGN_OF_CROSS), opening(DevotionalPrayer.MICHAEL_OPENING)),
        ChapletContemplations.saintMichaelChoirs.map { choir ->
            ChapletGroup(FixedPrayer.OUR_FATHER.reading(), FixedPrayer.HAIL_MARY.reading(), 3, choir.toMystery(),
                FixedPrayer.OUR_FATHER, FixedPrayer.HAIL_MARY)
        },
        listOf(
            closing(FixedPrayer.OUR_FATHER).copy(intention = "Em honra de São Miguel Arcanjo (1 de 4)"),
            closing(FixedPrayer.OUR_FATHER).copy(intention = "Em honra de São Gabriel Arcanjo (2 de 4)"),
            closing(FixedPrayer.OUR_FATHER).copy(intention = "Em honra de São Rafael Arcanjo (3 de 4)"),
            closing(FixedPrayer.OUR_FATHER).copy(intention = "Em honra do seu Anjo da Guarda (4 de 4)"),
            closing(DevotionalPrayer.MICHAEL_CLOSING),
            closing(FixedPrayer.SIGN_OF_CROSS),
        ),
        beadLabel = "Ave-Maria",
        groupLabel = "saudação",
    )

    /** The 14 Stations of the Cross: meditation, responsive adoration, Pai-Nosso, Ave-Maria, and Glória per station. */
    val viaSacra = ChapletDefinition(
        "via_sacra", "Via-Sacra",
        listOf(opening(FixedPrayer.SIGN_OF_CROSS), opening(DevotionalPrayer.VIA_SACRA_OPENING)),
        ChapletContemplations.viaSacraStations.map { station ->
            ChapletGroup(
                largeBead = FixedPrayer.OUR_FATHER.reading(),
                smallBead = FixedPrayer.HAIL_MARY.reading(),
                beadCount = 1,
                mystery = station.toMystery(),
                largePrayer = FixedPrayer.OUR_FATHER,
                smallPrayer = FixedPrayer.HAIL_MARY,
                after = listOf(FixedPrayer.GLORY_BE),
                beforeLarge = listOf(DevotionalPrayer.VIA_SACRA_ADORATION),
            )
        },
        listOf(closing(DevotionalPrayer.VIA_SACRA_CLOSING), closing(FixedPrayer.SIGN_OF_CROSS)),
        beadLabel = "Ave-Maria",
        groupLabel = "estação",
    )
}
