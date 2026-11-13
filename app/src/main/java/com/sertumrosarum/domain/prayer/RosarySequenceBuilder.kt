package com.sertumrosarum.domain.prayer

enum class PrayerPhase { INTRODUCTION, DECADE, CLOSING }

/** An immutable reading or a responsive invocation, separate from fixed prayer texts. */
data class PrayerReading(val title: String, val text: String, val response: String? = null, val soloText: String = text) {
    fun textFor(mode: PrayerMode): String = if (mode == PrayerMode.SOLO) soloText else text
}

data class PrayerStep(
    val prayer: FixedPrayer? = null,
    val phase: PrayerPhase,
    val decadeIndex: Int? = null,
    val beadIndex: Int? = null,
    val beadCount: Int = 0,
    val mystery: Mystery? = null,
    val intention: String? = null,
    val reading: PrayerReading? = null,
    val physicalBead: Boolean = prayer == FixedPrayer.OUR_FATHER || prayer == FixedPrayer.HAIL_MARY,
) {
    val title: String get() = prayer?.title ?: reading?.title ?: requireNotNull(mystery).title
    val text: String get() = prayer?.text ?: reading?.text ?: "Contemple ${requireNotNull(mystery).titleInSentence}.\n\nFruto deste mistério: ${mystery.fruitInSentence}."
    val isPhysicalBead: Boolean get() = physicalBead
    val isSmallBead: Boolean get() = beadIndex != null
    val isAnnouncement: Boolean get() = mystery != null && prayer == null && reading == null
}

object RosarySequenceBuilder {
    /** 59 physical beads and 80 prayer/announcement steps, including all transitions. */
    fun build(set: MysterySet): List<PrayerStep> = buildList {
        fun introductory(prayer: FixedPrayer) = add(PrayerStep(prayer, PrayerPhase.INTRODUCTION))
        introductory(FixedPrayer.SIGN_OF_CROSS)
        introductory(FixedPrayer.OFFERING)
        introductory(FixedPrayer.CREED)
        introductory(FixedPrayer.OUR_FATHER)
        listOf("Pela fé", "Pela esperança", "Pela caridade").forEachIndexed { index, virtue ->
            add(PrayerStep(FixedPrayer.HAIL_MARY, PrayerPhase.INTRODUCTION, beadIndex = index, beadCount = 3, intention = virtue))
        }
        introductory(FixedPrayer.GLORY_BE)
        set.mysteries.forEachIndexed { decade, mystery ->
            add(PrayerStep(phase = PrayerPhase.DECADE, decadeIndex = decade, mystery = mystery))
            add(PrayerStep(FixedPrayer.OUR_FATHER, PrayerPhase.DECADE, decade, mystery = mystery))
            repeat(10) { bead ->
                add(PrayerStep(FixedPrayer.HAIL_MARY, PrayerPhase.DECADE, decade, bead, 10, mystery))
            }
            add(PrayerStep(FixedPrayer.GLORY_BE, PrayerPhase.DECADE, decade, mystery = mystery))
            add(PrayerStep(FixedPrayer.FATIMA, PrayerPhase.DECADE, decade, mystery = mystery))
        }
        add(PrayerStep(FixedPrayer.THANKSGIVING, PrayerPhase.CLOSING))
        add(PrayerStep(FixedPrayer.HAIL_HOLY_QUEEN, PrayerPhase.CLOSING))
    }
}
