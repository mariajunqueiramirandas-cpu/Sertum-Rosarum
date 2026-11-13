package com.sertumrosarum.domain.prayer

enum class PrayerScript {
    TRADITIONAL, CATECHETICAL, COMPLETE, FAMILY, SICK, PEACE,
    DIVINE_MERCY, SAINT_JOSEPH, TEARS, FRANCISCAN;

    val chaplet: ChapletDefinition? get() = when (this) {
        DIVINE_MERCY -> Chaplets.divineMercy
        SAINT_JOSEPH -> Chaplets.saintJoseph
        TEARS -> Chaplets.tears
        FRANCISCAN -> Chaplets.franciscan
        else -> null
    }
    val isRosary get() = chaplet == null
    val groupCount get() = chaplet?.groups?.size ?: 5
    val beadLabel get() = chaplet?.beadLabel ?: "Ave-Maria"
    val groupLabel get() = chaplet?.groupLabel ?: "mistério"
    fun beadCount(group: Int): Int = chaplet?.groups?.get(group)?.beadCount ?: 10

    val offering: PrayerReading? get() = when (this) {
        FAMILY -> PrayerReading("Pela família", "Senhor Jesus, oferecemos esta oração pelas nossas famílias. Acolhei os que estão sós, fortalecei os que cuidam e ensinai-nos a escutar, perdoar e servir. Com Maria, entregamos a Vós os que estão perto e os que estão longe. Amém.", soloText = "Senhor Jesus, ofereço esta oração pela minha família. Acolhei os que estão sós, fortalecei os que cuidam e ensinai-me a escutar, perdoar e servir. Com Maria, entrego a Vós os que estão perto e os que estão longe. Amém.")
        SICK -> PrayerReading("Pelos enfermos", "Senhor Jesus, oferecemos esta oração pelos enfermos, por suas famílias e por quem cuida deles. Concedei-lhes alívio, coragem e companhia. Sustentai os profissionais de saúde e ajudai-nos a reconhecer a Vossa presença em quem sofre. Amém.", soloText = "Senhor Jesus, ofereço esta oração pelos enfermos, por suas famílias e por quem cuida deles. Concedei-lhes alívio, coragem e companhia. Sustentai os profissionais de saúde e ajudai-me a reconhecer a Vossa presença em quem sofre. Amém.")
        PEACE -> PrayerReading("Pela paz", "Senhor Jesus, oferecemos esta oração pela paz nas famílias e entre os povos. Protegei as vítimas da violência, acolhei os refugiados e convertei os corações. Fazei de nós servidores da reconciliação, da justiça e da paz. Amém.", soloText = "Senhor Jesus, ofereço esta oração pela paz nas famílias e entre os povos. Protegei as vítimas da violência, acolhei os refugiados e convertei os corações. Fazei de mim instrumento da reconciliação, da justiça e da paz. Amém.")
        else -> null
    }

    /** Cached per script and mystery set; moving a bead does not rebuild the entire sequence. */
    fun build(set: MysterySet): List<PrayerStep> = sequences.getValue(this to set)

    companion object {
        private val sequences by lazy {
            entries.flatMap { script -> MysterySet.entries.map { set -> (script to set) to generate(script, set) } }.toMap()
        }

        private fun generate(script: PrayerScript, set: MysterySet): List<PrayerStep> {
            script.chaplet?.let { return ChapletEngine.build(it) }
            return buildList {
                RosarySequenceBuilder.build(set).forEach { step ->
                    add(step)
                    if (step.prayer == FixedPrayer.OFFERING) script.offering?.let {
                        add(PrayerStep(phase = PrayerPhase.INTRODUCTION, reading = it))
                    }
                    if (script == COMPLETE && step.prayer == FixedPrayer.FATIMA) {
                        add(PrayerStep(phase = PrayerPhase.DECADE, decadeIndex = step.decadeIndex,
                            mystery = step.mystery, reading = DevotionalPrayer.MARY_MOTHER_OF_GRACE))
                    }
                }
                if (script == COMPLETE) {
                    LitanyOfLoreto.invocations.forEach { add(PrayerStep(phase = PrayerPhase.CLOSING, reading = it)) }
                    add(PrayerStep(phase = PrayerPhase.CLOSING, reading = DevotionalPrayer.LORETO_CLOSING))
                }
            }
        }
    }
}
