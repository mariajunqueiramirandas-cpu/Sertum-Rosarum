package com.sertumrosarum.domain.prayer

/** Bundled devotional texts. Variants in popular use are identified in the content notes. */
object DevotionalPrayer {
    val MARY_MOTHER_OF_GRACE = PrayerReading("Maria, Mãe da Graça", "Maria, Mãe da Graça, Mãe de misericórdia, defendei-nos do inimigo e recebei-nos na hora da morte. Amém.")
    val ETERNAL_FATHER = PrayerReading("Eterno Pai", "Eterno Pai, eu Vos ofereço o Corpo e o Sangue, a Alma e a Divindade de Vosso diletíssimo Filho, Nosso Senhor Jesus Cristo, em expiação dos nossos pecados e dos do mundo inteiro.")
    val SORROWFUL_PASSION = PrayerReading("Pela Sua dolorosa Paixão", "Pela Sua dolorosa Paixão, tende misericórdia de nós e do mundo inteiro.")
    val HOLY_GOD = PrayerReading("Deus Santo", "Deus Santo, Deus Forte, Deus Imortal, tende piedade de nós e do mundo inteiro.")
    val JOSEPH = PrayerReading("São José, valei-nos!", "São José, valei-nos!")
    val TEARS_OFFERING = PrayerReading("Vede, ó Jesus", "Vede, ó Jesus, que são as lágrimas d'Aquela que mais Vos amou na terra e que mais Vos ama no Céu.")
    val TEARS_PLEA = PrayerReading("Pelas lágrimas de Vossa Mãe", "Meu Jesus, ouvi os nossos rogos, pelas lágrimas de Vossa Mãe Santíssima.")
    val TEARS_OPENING = PrayerReading("Jesus Crucificado", "Eis-nos aqui aos Vossos pés, ó dulcíssimo Jesus Crucificado, para Vos oferecer as lágrimas d'Aquela que, com tanto amor, Vos acompanhou no caminho doloroso do Calvário. Fazei, ó bom Mestre, que saibamos aproveitar a lição que elas nos dão, para que, na terra, realizando a Vossa santíssima vontade, possamos um dia, nos Céus, Vos louvar por toda a eternidade. Amém.")
    val TEARS_CLOSING = PrayerReading("Ó Maria, Mãe de amor", "Ó Maria, Mãe de amor, das dores e de misericórdia, nós Vos suplicamos: uni as Vossas súplicas às nossas, a fim de que Jesus, Vosso Divino Filho, a quem nos dirigimos em nome das Vossas lágrimas maternais, ouça as nossas súplicas e nos conceda, com as graças que desejamos, a coroa da vida eterna. Amém.")
    val LORETO_CLOSING = PrayerReading("Oremos", "Concedei, nós Vos pedimos, Senhor nosso Deus, que nós, Vossos servos, gozemos sempre de saúde de alma e corpo; e que, pela gloriosa intercessão da bem-aventurada sempre Virgem Maria, sejamos livres da presente tristeza e gozemos da eterna alegria. Por Cristo nosso Senhor. Amém.")
}

/** The full Litany of Loreto, including the three invocations added in 2020. */
object LitanyOfLoreto {
    val invocations: List<PrayerReading> = buildList {
        fun verse(call: String, response: String) = add(PrayerReading("Ladainha de Nossa Senhora", call, response))
        verse("Senhor, tende piedade de nós.", "Senhor, tende piedade de nós.")
        verse("Cristo, tende piedade de nós.", "Cristo, tende piedade de nós.")
        verse("Senhor, tende piedade de nós.", "Senhor, tende piedade de nós.")
        verse("Cristo, ouvi-nos.", "Cristo, ouvi-nos.")
        verse("Cristo, atendei-nos.", "Cristo, atendei-nos.")
        listOf("Deus Pai do Céu,", "Deus Filho, Redentor do mundo,", "Deus Espírito Santo,", "Santíssima Trindade, que sois um só Deus,").forEach { verse(it, "tende piedade de nós.") }
        listOf(
            "Santa Maria", "Santa Mãe de Deus", "Santa Virgem das virgens",
            "Mãe de Cristo", "Mãe da Igreja", "Mãe de misericórdia", "Mãe da divina graça", "Mãe da esperança",
            "Mãe puríssima", "Mãe castíssima", "Mãe sempre virgem", "Mãe imaculada", "Mãe amável", "Mãe admirável", "Mãe do bom conselho", "Mãe do Criador", "Mãe do Salvador",
            "Virgem prudentíssima", "Virgem venerável", "Virgem louvável", "Virgem poderosa", "Virgem clemente", "Virgem fiel",
            "Espelho de justiça", "Sede da sabedoria", "Causa da nossa alegria", "Vaso espiritual", "Vaso honorífico", "Vaso insigne de devoção", "Rosa mística", "Torre de Davi", "Torre de marfim", "Casa de ouro", "Arca da aliança", "Porta do Céu", "Estrela da manhã",
            "Saúde dos enfermos", "Refúgio dos pecadores", "Conforto dos migrantes", "Consoladora dos aflitos", "Auxílio dos cristãos",
            "Rainha dos anjos", "Rainha dos patriarcas", "Rainha dos profetas", "Rainha dos apóstolos", "Rainha dos mártires", "Rainha dos confessores", "Rainha das virgens", "Rainha de todos os santos", "Rainha concebida sem pecado original", "Rainha assunta ao Céu", "Rainha do santíssimo Rosário", "Rainha da família", "Rainha da paz",
        ).forEach { verse("$it,", "rogai por nós.") }
        verse("Cordeiro de Deus, que tirais o pecado do mundo,", "perdoai-nos, Senhor.")
        verse("Cordeiro de Deus, que tirais o pecado do mundo,", "ouvi-nos, Senhor.")
        verse("Cordeiro de Deus, que tirais o pecado do mundo,", "tende piedade de nós.")
        verse("Rogai por nós, Santa Mãe de Deus,", "para que sejamos dignos das promessas de Cristo.")
    }
}
