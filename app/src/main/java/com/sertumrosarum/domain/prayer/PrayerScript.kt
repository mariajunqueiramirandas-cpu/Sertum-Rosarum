package com.sertumrosarum.domain.prayer

enum class PrayerScript {
    TRADITIONAL, CATECHETICAL, COMPLETE, SINGLE_DECADE, FULL_ROSARY,
    FAMILY, SICK, PEACE, DECEASED, ANXIETY, VOCATIONS,
    DIVINE_MERCY, SAINT_JOSEPH, TEARS, FRANCISCAN,
    SEVEN_SORROWS, HOLY_WOUNDS, SAINT_MICHAEL, VIA_SACRA;

    val chaplet: ChapletDefinition? get() = when (this) {
        DIVINE_MERCY -> Chaplets.divineMercy
        SAINT_JOSEPH -> Chaplets.saintJoseph
        TEARS -> Chaplets.tears
        FRANCISCAN -> Chaplets.franciscan
        SEVEN_SORROWS -> Chaplets.sevenSorrows
        HOLY_WOUNDS -> Chaplets.holyWounds
        SAINT_MICHAEL -> Chaplets.saintMichael
        VIA_SACRA -> Chaplets.viaSacra
        else -> null
    }
    val isRosary get() = chaplet == null
    val isThemed get() = this in setOf(FAMILY, SICK, PEACE, DECEASED, ANXIETY, VOCATIONS)
    /** True when the user picks which MysterySet (Gozosos/Luminosos/Dolorosos/Gloriosos) to pray. */
    val usesMysterySet get() = isRosary && this != FULL_ROSARY
    val groupCount get() = when (this) {
        SINGLE_DECADE -> 1
        FULL_ROSARY -> 20
        else -> chaplet?.groups?.size ?: 5
    }
    val beadLabel get() = chaplet?.beadLabel ?: "Ave-Maria"
    val groupLabel get() = chaplet?.groupLabel ?: "mistério"
    fun beadCount(group: Int): Int = chaplet?.groups?.getOrNull(group)?.beadCount ?: 10

    val offering: PrayerReading? get() = when (this) {
        FAMILY -> PrayerReading(
            "Pela família",
            "Senhor Jesus, oferecemos esta oração pelas nossas famílias. Acolhei os que estão sós, fortalecei os que cuidam e ensinai-nos a escutar, perdoar e servir. Com Maria, entregamos a Vós os que estão perto e os que estão longe. Amém.",
            soloText = "Senhor Jesus, ofereço esta oração pela minha família. Acolhei os que estão sós, fortalecei os que cuidam e ensinai-me a escutar, perdoar e servir. Com Maria, entrego a Vós os que estão perto e os que estão longe. Amém.",
        )
        SICK -> PrayerReading(
            "Pelos enfermos",
            "Senhor Jesus, oferecemos esta oração pelos enfermos, por suas famílias e por quem cuida deles. Concedei-lhes alívio, coragem e companhia. Sustentai os profissionais de saúde e ajudai-nos a reconhecer a Vossa presença em quem sofre. Amém.",
            soloText = "Senhor Jesus, ofereço esta oração pelos enfermos, por suas famílias e por quem cuida deles. Concedei-lhes alívio, coragem e companhia. Sustentai os profissionais de saúde e ajudai-me a reconhecer a Vossa presença em quem sofre. Amém.",
        )
        PEACE -> PrayerReading(
            "Pela paz",
            "Senhor Jesus, oferecemos esta oração pela paz nas famílias e entre os povos. Protegei as vítimas da violência, acolhei os refugiados e convertei os corações. Fazei de nós servidores da reconciliação, da justiça e da paz. Amém.",
            soloText = "Senhor Jesus, ofereço esta oração pela paz nas famílias e entre os povos. Protegei as vítimas da violência, acolhei os refugiados e convertei os corações. Fazei de mim instrumento da reconciliação, da justiça e da paz. Amém.",
        )
        DECEASED -> PrayerReading(
            "Pelos falecidos e no luto",
            "Senhor Jesus, oferecemos esta oração pelos nossos entes queridos que já partiram desta vida e por todas as famílias que atravessam o luto. Concedei-lhes o descanso eterno e sustentai a nossa esperança na Ressurreição. Amém.",
            soloText = "Senhor Jesus, ofereço esta oração pelos meus entes queridos que já partiram desta vida e por todos os que atravessam o luto. Concedei-lhes o descanso eterno e sustentai a minha esperança na Ressurreição. Amém.",
        )
        ANXIETY -> PrayerReading(
            "Na ansiedade e discernimento",
            "Senhor Jesus, oferecemos esta oração confiando a Vós os medos, as preocupações e as decisões que pesam sobre o coração. Dai-nos clareza, serenidade e a liberdade de caminhar um dia de cada vez na Vossa paz. Amém.",
            soloText = "Senhor Jesus, ofereço esta oração confiando a Vós os meus medos, preocupações e decisões. Dai-me clareza, serenidade e a liberdade de caminhar um dia de cada vez na Vossa paz. Amém.",
        )
        VOCATIONS -> PrayerReading(
            "Pelas vocações e pela Igreja",
            "Senhor Jesus, oferecemos esta oração por toda a Igreja, pelas famílias, pelos sacerdotes, pelos consagrados e pelos jovens em discernimento. Despertai corações generosos para o serviço do Evangelho. Amém.",
            soloText = "Senhor Jesus, ofereço esta oração por toda a Igreja, pelas famílias, pelos sacerdotes, pelos consagrados e pelos jovens em discernimento. Despertai corações generosos para o serviço do Evangelho. Amém.",
        )
        else -> null
    }

    val themedClosing: PrayerReading? get() = ThemedMeditations.closingFor(this)

    /** Cached per script and mystery set; moving a bead does not rebuild the entire sequence. */
    fun build(set: MysterySet, focusMystery: Int = 0): List<PrayerStep> =
        if (this == SINGLE_DECADE) singleDecadeSequences.getValue(set to focusMystery.coerceIn(0, 4))
        else sequences.getValue(this to set)

    companion object {
        private val sequences by lazy {
            entries.filter { it != SINGLE_DECADE }
                .flatMap { script -> MysterySet.entries.map { set -> (script to set) to generate(script, set) } }
                .toMap()
        }

        private val singleDecadeSequences by lazy {
            MysterySet.entries.flatMap { set ->
                (0..4).map { index -> (set to index) to RosarySequenceBuilder.buildSingleDecade(set, index) }
            }.toMap()
        }

        private fun generate(script: PrayerScript, set: MysterySet): List<PrayerStep> {
            script.chaplet?.let { return ChapletEngine.build(it) }
            if (script == FULL_ROSARY) return RosarySequenceBuilder.buildFullRosary()
            return buildList {
                RosarySequenceBuilder.build(set).forEach { step ->
                    add(step)
                    if (step.prayer == FixedPrayer.OFFERING) script.offering?.let {
                        add(PrayerStep(phase = PrayerPhase.INTRODUCTION, reading = it))
                    }
                    if (script == COMPLETE && step.prayer == FixedPrayer.FATIMA) {
                        add(PrayerStep(phase = PrayerPhase.DECADE, decadeIndex = step.decadeIndex,
                            mystery = step.mystery, reading = DevotionalPrayer.MARY_MOTHER_OF_GRACE, mysterySet = set))
                    }
                }
                if (script == COMPLETE) {
                    LitanyOfLoreto.invocations.forEach { add(PrayerStep(phase = PrayerPhase.CLOSING, reading = it)) }
                    add(PrayerStep(phase = PrayerPhase.CLOSING, reading = DevotionalPrayer.LORETO_CLOSING))
                }
                script.themedClosing?.let { add(PrayerStep(phase = PrayerPhase.CLOSING, reading = it)) }
            }
        }
    }
}
