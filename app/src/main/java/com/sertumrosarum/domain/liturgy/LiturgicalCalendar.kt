package com.sertumrosarum.domain.liturgy

import com.sertumrosarum.domain.prayer.MysterySet
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit

enum class LiturgicalSeason(val label: String, val invitation: String) {
    ADVENT("Advento", "Tempo de espera vigilante: prepare o coração para a vinda do Senhor."),
    CHRISTMAS("Tempo do Natal", "O Verbo se fez carne e habitou entre nós. Contemple o Menino em Belém."),
    LENT("Quaresma", "Tempo de conversão, silêncio e caridade no caminho rumo à Páscoa."),
    TRIDUUM("Tríduo Pascal", "A Igreja acompanha a Paixão, a morte e a vigília da Ressurreição de Cristo."),
    EASTER("Tempo Pascal", "Cristo ressuscitou, aleluia! Tempo de alegria, esperança e dom do Espírito."),
    ORDINARY("Tempo Comum", "Caminhe com Jesus no cotidiano, deixando o Evangelho iluminar cada escolha."),
}

enum class LiturgicalColor(val label: String) {
    GREEN("Verde"),
    PURPLE("Roxo"),
    WHITE("Branco"),
    RED("Vermelho"),
    ROSE("Rosa"),
}

/**
 * Offline Roman & Brazilian (CNBB) liturgical awareness for a given calendar date.
 * Computes movable feasts via Gregorian Computus and recognizes major universal
 * and Brazilian solemnities without requiring a network connection.
 */
data class LiturgicalDay(
    val date: LocalDate,
    val season: LiturgicalSeason,
    val color: LiturgicalColor,
    val celebration: String?,
    val marianAntiphonId: String,
    val marianAntiphonTitle: String,
    val recommendedMysterySet: MysterySet,
    /** Name of a PrayerGuide enum entry when the day invites a specific devotion, or null. */
    val recommendedGuideName: String?,
    val recommendedGuideReason: String?,
    val invitation: String,
) {
    val isEastertide: Boolean get() = season == LiturgicalSeason.EASTER
    val headerLabel: String get() = celebration?.let { "${season.label} · $it" } ?: season.label
}

object LiturgicalCalendar {

    /** Anonymous Gregorian algorithm (Meeus/Jones/Butcher) for Easter Sunday. */
    fun easterSunday(year: Int): LocalDate {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31
        val day = ((h + l - 7 * m + 114) % 31) + 1
        return LocalDate.of(year, month, day)
    }

    /** First Sunday of Advent: the Sunday nearest to St. Andrew's Day (Nov 30), i.e., 4th Sunday before Dec 25. */
    fun firstSundayOfAdvent(year: Int): LocalDate {
        val christmas = LocalDate.of(year, Month.DECEMBER, 25)
        val daysSinceSunday = christmas.dayOfWeek.value % 7 // Sunday -> 0, Monday -> 1 ... Saturday -> 6
        val fourthSundayOfAdvent = if (daysSinceSunday == 0) christmas.minusDays(7) else christmas.minusDays(daysSinceSunday.toLong())
        return fourthSundayOfAdvent.minusWeeks(3)
    }

    /** In Brazil (CNBB), Epiphany is celebrated on the Sunday between Jan 2 and Jan 8; Baptism of the Lord is the following Sunday. */
    fun baptismOfTheLord(year: Int): LocalDate {
        val epiphany = (2..8).map { LocalDate.of(year, Month.JANUARY, it) }.first { it.dayOfWeek == DayOfWeek.SUNDAY }
        return epiphany.plusWeeks(1)
    }

    fun forDate(date: LocalDate): LiturgicalDay {
        val year = date.year
        val easter = easterSunday(year)
        val ashWednesday = easter.minusDays(46)
        val palmSunday = easter.minusDays(7)
        val holyThursday = easter.minusDays(3)
        val goodFriday = easter.minusDays(2)
        val holySaturday = easter.minusDays(1)
        val divineMercySunday = easter.plusDays(7)
        val ascension = easter.plusDays(42) // Sunday celebration in Brazil (CNBB)
        val pentecost = easter.plusDays(49)
        val holyTrinity = pentecost.plusDays(7)
        val corpusChristi = holyTrinity.plusDays(4)
        val sacredHeart = corpusChristi.plusDays(8)
        val immaculateHeart = sacredHeart.plusDays(1)
        val adventStart = firstSundayOfAdvent(year)
        val gaudeteSunday = adventStart.plusWeeks(2)
        val laetareSunday = easter.minusWeeks(3)
        val christTheKing = adventStart.minusWeeks(1)
        val christmas = LocalDate.of(year, Month.DECEMBER, 25)
        val baptism = baptismOfTheLord(year)
        val epiphany = baptism.minusWeeks(1)

        val season = when {
            date >= christmas || date <= baptism -> LiturgicalSeason.CHRISTMAS
            date >= adventStart -> LiturgicalSeason.ADVENT
            date in holyThursday..holySaturday -> LiturgicalSeason.TRIDUUM
            date >= ashWednesday && date < holyThursday -> LiturgicalSeason.LENT
            date in easter..pentecost -> LiturgicalSeason.EASTER
            else -> LiturgicalSeason.ORDINARY
        }

        val movableFeast: FeastInfo? = when (date) {
            ashWednesday -> FeastInfo("Quarta-feira de Cinzas", LiturgicalColor.PURPLE, MysterySet.SORROWFUL, "VIA_SACRA", "A Quaresma começa: a Via-Sacra acompanha o caminho de conversão.")
            palmSunday -> FeastInfo("Domingo de Ramos", LiturgicalColor.RED, MysterySet.SORROWFUL, "VIA_SACRA", "Entramos na Semana Santa com Jesus rumo a Jerusalém.")
            holyThursday -> FeastInfo("Quinta-feira Santa", LiturgicalColor.WHITE, MysterySet.LUMINOUS, null, null)
            goodFriday -> FeastInfo("Sexta-feira da Paixão", LiturgicalColor.RED, MysterySet.SORROWFUL, "VIA_SACRA", "Dia de silêncio e contemplação da Cruz do Senhor.")
            holySaturday -> FeastInfo("Sábado Santo", LiturgicalColor.PURPLE, MysterySet.SORROWFUL, "SEVEN_SORROWS", "Em vigília com Maria junto ao sepulcro, na espera da Ressurreição.")
            easter -> FeastInfo("Domingo de Páscoa", LiturgicalColor.WHITE, MysterySet.GLORIOUS, null, null)
            divineMercySunday -> FeastInfo("Domingo da Divina Misericórdia", LiturgicalColor.WHITE, MysterySet.GLORIOUS, "DIVINE_MERCY", "Hoje a Igreja celebra a misericórdia de Cristo: reze o Terço da Divina Misericórdia.")
            ascension -> FeastInfo("Ascensão do Senhor", LiturgicalColor.WHITE, MysterySet.GLORIOUS, null, null)
            pentecost -> FeastInfo("Domingo de Pentecostes", LiturgicalColor.RED, MysterySet.GLORIOUS, null, null)
            holyTrinity -> FeastInfo("Santíssima Trindade", LiturgicalColor.WHITE, MysterySet.GLORIOUS, null, null)
            corpusChristi -> FeastInfo("Corpus Christi", LiturgicalColor.WHITE, MysterySet.LUMINOUS, null, null)
            sacredHeart -> FeastInfo("Sagrado Coração de Jesus", LiturgicalColor.WHITE, MysterySet.SORROWFUL, "HOLY_WOUNDS", "Contemple o amor redentor do Coração de Jesus no Terço das Santas Chagas.")
            immaculateHeart -> FeastInfo("Imaculado Coração de Maria", LiturgicalColor.WHITE, MysterySet.JOYFUL, "COMPLETE", "Um dia mariano para oferecer o terço completo com a Ladainha.")
            christTheKing -> FeastInfo("Cristo Rei do Universo", LiturgicalColor.WHITE, MysterySet.GLORIOUS, null, null)
            epiphany -> FeastInfo("Epifania do Senhor", LiturgicalColor.WHITE, MysterySet.JOYFUL, "FRANCISCAN", "A Coroa Franciscana contempla também a Adoração dos Magos.")
            baptism -> FeastInfo("Batismo do Senhor", LiturgicalColor.WHITE, MysterySet.LUMINOUS, null, null)
            else -> null
        }

        val fixedFeast: FeastInfo? = fixedCelebration(date)
        val feast = movableFeast ?: fixedFeast

        val seasonColor = when (season) {
            LiturgicalSeason.ADVENT -> if (date == gaudeteSunday) LiturgicalColor.ROSE else LiturgicalColor.PURPLE
            LiturgicalSeason.LENT -> if (date == laetareSunday) LiturgicalColor.ROSE else LiturgicalColor.PURPLE
            LiturgicalSeason.CHRISTMAS, LiturgicalSeason.EASTER -> LiturgicalColor.WHITE
            LiturgicalSeason.TRIDUUM -> if (date == goodFriday) LiturgicalColor.RED else LiturgicalColor.WHITE
            LiturgicalSeason.ORDINARY -> LiturgicalColor.GREEN
        }

        val color = feast?.color ?: seasonColor
        val weekdayMystery = MysterySet.forDay(date.dayOfWeek)
        val recommendedMystery = feast?.mysterySet ?: when {
            season == LiturgicalSeason.ADVENT && date.dayOfWeek == DayOfWeek.SUNDAY -> MysterySet.JOYFUL
            season == LiturgicalSeason.LENT && date.dayOfWeek == DayOfWeek.SUNDAY -> MysterySet.SORROWFUL
            else -> weekdayMystery
        }

        val (guideName, guideReason) = when {
            feast?.guideName != null -> feast.guideName to feast.guideReason
            season == LiturgicalSeason.LENT && date.dayOfWeek == DayOfWeek.FRIDAY ->
                "VIA_SACRA" to "Nas sextas-feiras da Quaresma, a Via-Sacra acompanha Jesus no caminho do Calvário."
            date.month == Month.MAY || date.month == Month.OCTOBER ->
                "COMPLETE" to if (date.month == Month.MAY) "Maio, mês mariano: um convite para rezar o terço com calma." else "Outubro, mês do Rosário: ofereça hoje uma coroa com a Ladainha."
            else -> null to null
        }

        val isEaster = season == LiturgicalSeason.EASTER
        val antiphonId = if (isEaster) "regina-caeli" else "angelus"
        val antiphonTitle = if (isEaster) "Regina Caeli" else "Angelus"
        val invitation = guideReason ?: feast?.let { "${it.name}: ${season.invitation}" } ?: season.invitation

        return LiturgicalDay(
            date = date,
            season = season,
            color = color,
            celebration = feast?.name ?: specialSundayName(date, adventStart, ashWednesday, easter),
            marianAntiphonId = antiphonId,
            marianAntiphonTitle = antiphonTitle,
            recommendedMysterySet = recommendedMystery,
            recommendedGuideName = guideName,
            recommendedGuideReason = guideReason,
            invitation = invitation,
        )
    }

    private fun specialSundayName(date: LocalDate, adventStart: LocalDate, ashWednesday: LocalDate, easter: LocalDate): String? {
        if (date.dayOfWeek != DayOfWeek.SUNDAY) return null
        return when {
            date == adventStart.plusWeeks(2) -> "3º Domingo do Advento (Gaudete)"
            date == easter.minusWeeks(3) -> "4º Domingo da Quaresma (Laetare)"
            date > ashWednesday && date < easter.minusDays(7) -> {
                val week = ChronoUnit.WEEKS.between(ashWednesday.plusDays(4), date).toInt() + 1
                "${week}º Domingo da Quaresma"
            }
            date >= adventStart && date < LocalDate.of(date.year, Month.DECEMBER, 25) -> {
                val week = ChronoUnit.WEEKS.between(adventStart, date).toInt() + 1
                "${week}º Domingo do Advento"
            }
            date > easter && date < easter.plusDays(49) -> {
                val week = ChronoUnit.WEEKS.between(easter, date).toInt() + 1
                "${week}º Domingo da Páscoa"
            }
            else -> null
        }
    }

    private fun fixedCelebration(date: LocalDate): FeastInfo? = when (date.month to date.dayOfMonth) {
        Month.JANUARY to 1 -> FeastInfo("Santa Maria, Mãe de Deus", LiturgicalColor.WHITE, MysterySet.JOYFUL, "COMPLETE", "Comece o ano confiando seus caminhos à Mãe de Deus.")
        Month.FEBRUARY to 2 -> FeastInfo("Apresentação do Senhor", LiturgicalColor.WHITE, MysterySet.JOYFUL, null, null)
        Month.FEBRUARY to 11 -> FeastInfo("Nossa Senhora de Lourdes", LiturgicalColor.WHITE, MysterySet.JOYFUL, "SICK", "Dia Mundial do Enfermo: reze o terço pelos doentes e por quem cuida deles.")
        Month.MARCH to 19 -> FeastInfo("Solenidade de São José", LiturgicalColor.WHITE, MysterySet.JOYFUL, "SAINT_JOSEPH", "Dia de São José, esposo de Maria e guardião da Sagrada Família.")
        Month.MARCH to 25 -> FeastInfo("Anunciação do Senhor", LiturgicalColor.WHITE, MysterySet.JOYFUL, null, null)
        Month.MAY to 13 -> FeastInfo("Nossa Senhora de Fátima", LiturgicalColor.WHITE, MysterySet.JOYFUL, "PEACE", "Nossa Senhora em Fátima convidou à oração do terço pela paz no mundo.")
        Month.MAY to 31 -> FeastInfo("Visitação de Nossa Senhora", LiturgicalColor.WHITE, MysterySet.JOYFUL, null, null)
        Month.JUNE to 29 -> FeastInfo("São Pedro e São Paulo", LiturgicalColor.RED, MysterySet.GLORIOUS, "VOCATIONS", "Reze pela Igreja e pelas vocações ao serviço do Evangelho.")
        Month.JULY to 16 -> FeastInfo("Nossa Senhora do Carmo", LiturgicalColor.WHITE, MysterySet.JOYFUL, "COMPLETE", "Sob o manto de Nossa Senhora do Carmo, ofereça uma coroa de oração.")
        Month.AUGUST to 6 -> FeastInfo("Transfiguração do Senhor", LiturgicalColor.WHITE, MysterySet.LUMINOUS, null, null)
        Month.AUGUST to 15 -> FeastInfo("Assunção de Nossa Senhora", LiturgicalColor.WHITE, MysterySet.GLORIOUS, "FRANCISCAN", "A Coroa Franciscana celebra as sete alegrias de Maria até sua glória no Céu.")
        Month.AUGUST to 22 -> FeastInfo("Nossa Senhora Rainha", LiturgicalColor.WHITE, MysterySet.GLORIOUS, "COMPLETE", "Reze com a Ladainha de Nossa Senhora, Rainha da paz e das famílias.")
        Month.SEPTEMBER to 8 -> FeastInfo("Natividade de Nossa Senhora", LiturgicalColor.WHITE, MysterySet.JOYFUL, null, null)
        Month.SEPTEMBER to 14 -> FeastInfo("Exaltação da Santa Cruz", LiturgicalColor.RED, MysterySet.SORROWFUL, "VIA_SACRA", "Contemple o amor de Cristo entregue na Cruz pela Via-Sacra.")
        Month.SEPTEMBER to 15 -> FeastInfo("Nossa Senhora das Dores", LiturgicalColor.WHITE, MysterySet.SORROWFUL, "SEVEN_SORROWS", "Acompanhe as sete dores de Maria junto à missão do Filho.")
        Month.SEPTEMBER to 29 -> FeastInfo("Santos Arcanjos Miguel, Gabriel e Rafael", LiturgicalColor.WHITE, MysterySet.GLORIOUS, "SAINT_MICHAEL", "Dia dos Santos Arcanjos: reze o Terço de São Miguel.")
        Month.OCTOBER to 7 -> FeastInfo("Nossa Senhora do Rosário", LiturgicalColor.WHITE, MysterySet.JOYFUL, "FULL_ROSARY", "Festa de Nossa Senhora do Rosário: contemple os mistérios com Maria.")
        Month.OCTOBER to 12 -> FeastInfo("Nossa Senhora Aparecida · Padroeira do Brasil", LiturgicalColor.WHITE, MysterySet.JOYFUL, "COMPLETE", "Solenidade da Padroeira do Brasil: ofereça o terço pelo nosso povo e famílias.")
        Month.NOVEMBER to 1 -> FeastInfo("Todos os Santos", LiturgicalColor.WHITE, MysterySet.GLORIOUS, null, null)
        Month.NOVEMBER to 2 -> FeastInfo("Finados · Todos os Fiéis Defuntos", LiturgicalColor.PURPLE, MysterySet.SORROWFUL, "DECEASED", "Dia de rezar pelos falecidos e confiar o luto à esperança da Ressurreição.")
        Month.DECEMBER to 8 -> FeastInfo("Imaculada Conceição de Maria", LiturgicalColor.WHITE, MysterySet.JOYFUL, "COMPLETE", "Solenidade da Imaculada Conceição: reze com Maria, cheia de graça.")
        Month.DECEMBER to 12 -> FeastInfo("Nossa Senhora de Guadalupe", LiturgicalColor.WHITE, MysterySet.JOYFUL, "FAMILY", "Nossa Senhora de Guadalupe, Mãe da América: confie a ela sua família.")
        Month.DECEMBER to 25 -> FeastInfo("Natal do Senhor", LiturgicalColor.WHITE, MysterySet.JOYFUL, "TRADITIONAL", "Hoje nasceu para nós um Salvador: contemple os Mistérios Gozosos.")
        else -> null
    }

    private data class FeastInfo(
        val name: String,
        val color: LiturgicalColor,
        val mysterySet: MysterySet,
        val guideName: String?,
        val guideReason: String?,
    )
}
