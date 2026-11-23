package com.sertumrosarum.domain.prayer

/** Bundled devotional texts. Variants in popular use are identified in the content notes. */
object DevotionalPrayer {
    val MARY_MOTHER_OF_GRACE = PrayerReading("Maria, Mãe da Graça", "Maria, Mãe da Graça, Mãe de misericórdia, defendei-nos do inimigo e recebei-nos na hora da morte. Amém.")
    val ETERNAL_FATHER = PrayerReading("Eterno Pai", "Eterno Pai, eu Vos ofereço o Corpo e o Sangue, a Alma e a Divindade de Vosso diletíssimo Filho, Nosso Senhor Jesus Cristo, em expiação dos nossos pecados e dos do mundo inteiro.")
    val SORROWFUL_PASSION = PrayerReading("Pela Sua dolorosa Paixão", "Pela Sua dolorosa Paixão, tende misericórdia de nós e do mundo inteiro.")
    val HOLY_GOD = PrayerReading("Deus Santo", "Deus Santo, Deus Forte, Deus Imortal, tende piedade de nós e do mundo inteiro.")
    val DIVINE_MERCY_CLOSING = PrayerReading(
        "Ó Deus eterno",
        "Ó Deus eterno, em quem a misericórdia é insondável e o tesouro da compaixão é inesgotável, olhai propício para nós e aumentai em nós a Vossa misericórdia, para que nos momentos difíceis não nos desesperemos nem nos abatamos, mas com grande confiança nos submetamos à Vossa santa vontade, que é o próprio Amor e a própria Misericórdia. Amém.\n\nJesus, nós confiamos em Vós!",
        soloText = "Ó Deus eterno, em quem a misericórdia é insondável e o tesouro da compaixão é inesgotável, olhai propício para mim e aumentai em mim a Vossa misericórdia, para que nos momentos difíceis eu não me desespere nem me abata, mas com grande confiança me submeta à Vossa santa vontade, que é o próprio Amor e a própria Misericórdia. Amém.\n\nJesus, eu confio em Vós!",
    )
    val JOSEPH = PrayerReading("São José, valei-nos!", "São José, valei-nos!", soloText = "São José, valei-me!")
    val JOSEPH_CLOSING = PrayerReading(
        "Oração final a São José",
        "A vós, São José, recorremos em nossa tribulação e, depois de ter implorado o auxílio de vossa santíssima Esposa, cheios de confiança solicitamos também o vosso patrocínio. Guardai nossas famílias, nosso trabalho e nossa fé em Jesus Cristo. Amém.",
        soloText = "A vós, São José, recorro em minha tribulação e, depois de ter implorado o auxílio de vossa santíssima Esposa, cheio de confiança solicito também o vosso patrocínio. Guardai minha família, meu trabalho e minha fé em Jesus Cristo. Amém.",
    )
    val TEARS_OFFERING = PrayerReading("Vede, ó Jesus", "Vede, ó Jesus, que são as lágrimas d'Aquela que mais Vos amou na terra e que mais Vos ama no Céu.")
    val TEARS_PLEA = PrayerReading("Pelas lágrimas de Vossa Mãe", "Meu Jesus, ouvi os nossos rogos, pelas lágrimas de Vossa Mãe Santíssima.")
    val TEARS_OPENING = PrayerReading("Jesus Crucificado", "Eis-nos aqui aos Vossos pés, ó dulcíssimo Jesus Crucificado, para Vos oferecer as lágrimas d'Aquela que, com tanto amor, Vos acompanhou no caminho doloroso do Calvário. Fazei, ó bom Mestre, que saibamos aproveitar a lição que elas nos dão, para que, na terra, realizando a Vossa santíssima vontade, possamos um dia, nos Céus, Vos louvar por toda a eternidade. Amém.")
    val TEARS_CLOSING = PrayerReading("Ó Maria, Mãe de amor", "Ó Maria, Mãe de amor, das dores e de misericórdia, nós Vos suplicamos: uni as Vossas súplicas às nossas, a fim de que Jesus, Vosso Divino Filho, a quem nos dirigimos em nome das Vossas lágrimas maternais, ouça as nossas súplicas e nos conceda, com as graças que desejamos, a coroa da vida eterna. Amém.")
    val LORETO_CLOSING = PrayerReading("Oremos", "Concedei, nós Vos pedimos, Senhor nosso Deus, que nós, Vossos servos, gozemos sempre de saúde de alma e corpo; e que, pela gloriosa intercessão da bem-aventurada sempre Virgem Maria, sejamos livres da presente tristeza e gozemos da eterna alegria. Por Cristo nosso Senhor. Amém.")

    // Terço das Santas Chagas (devoção tradicional muito difundida no Brasil)
    val HOLY_WOUNDS_OPENING = PrayerReading(
        "Oferecimento às Santas Chagas",
        "Ó Jesus, Divino Redentor, sede misericordioso conosco e com o mundo inteiro. Deus Santo, Deus Forte, Deus Imortal, tende piedade de nós e do mundo inteiro. Graça e misericórdia, meu Jesus, nos perigos presentes; cobri-nos com Vosso preciosíssimo Sangue. Amém.",
        soloText = "Ó Jesus, Divino Redentor, sede misericordioso comigo e com o mundo inteiro. Deus Santo, Deus Forte, Deus Imortal, tende piedade de mim e do mundo inteiro. Graça e misericórdia, meu Jesus, nos perigos presentes; cobri-me com Vosso preciosíssimo Sangue. Amém.",
    )
    val HOLY_WOUNDS_LARGE = PrayerReading(
        "Eterno Pai, pelas Santas Chagas",
        "Eterno Pai, nós Vos oferecemos as Santas Chagas de Nosso Senhor Jesus Cristo, para curar as chagas de nossas almas.",
        soloText = "Eterno Pai, eu Vos ofereço as Santas Chagas de Nosso Senhor Jesus Cristo, para curar as chagas de nossas almas.",
    )
    val HOLY_WOUNDS_SMALL = PrayerReading(
        "Meu Jesus, perdão e misericórdia",
        "Meu Jesus, perdão e misericórdia, pelos méritos de Vossas Santas Chagas.",
    )
    val HOLY_WOUNDS_CLOSING = PrayerReading(
        "Consagração às Santas Chagas",
        "Senhor Jesus, pelas Vossas cinco Chagas gloriosas, curai as feridas do nosso coração, fortalecei os que sofrem e conduzi-nos à paz da Vossa Ressurreição. Amém.",
        soloText = "Senhor Jesus, pelas Vossas cinco Chagas gloriosas, curai as feridas do meu coração, fortalecei os que sofrem e conduzi-me à paz da Vossa Ressurreição. Amém.",
    )

    // Terço das Sete Dores de Nossa Senhora (Coroa Servita)
    val SEVEN_SORROWS_OPENING = PrayerReading(
        "Oferecimento das Sete Dores",
        "Virgem das Dores, unimos o nosso coração ao vosso para contemplar as sete dores que atravessaram a vossa alma no seguimento de Jesus. Ensinai-nos a permanecer fiéis ao amor na provação e a consolar quem sofre. Amém.",
        soloText = "Virgem das Dores, uno o meu coração ao vosso para contemplar as sete dores que atravessaram a vossa alma no seguimento de Jesus. Ensinai-me a permanecer fiel ao amor na provação e a consolar quem sofre. Amém.",
    )
    val SEVEN_SORROWS_CLOSING = PrayerReading(
        "Oração final à Mãe das Dores",
        "Rogai por nós, ó Mãe dolorosíssima, para que sejamos dignos das promessas de Cristo.\n\nSenhor Jesus, que associastes Vossa Mãe ao mistério da Cruz, concedei-nos, por sua intercessão, coragem nas dores da vida e esperança firme na Ressurreição. Amém.",
    )

    // Terço de São Miguel Arcanjo (Coroa Angélica — 9 coros)
    val MICHAEL_OPENING = PrayerReading(
        "Invocação inicial",
        "Deus, vinde em nosso auxílio.\nSenhor, socorrei-nos e salvai-nos.\nGlória ao Pai, ao Filho e ao Espírito Santo, como era no princípio, agora e sempre. Amém.",
        soloText = "Deus, vinde em meu auxílio.\nSenhor, apressai-Vos em socorrer-me.\nGlória ao Pai, ao Filho e ao Espírito Santo, como era no princípio, agora e sempre. Amém.",
    )
    val MICHAEL_CLOSING = PrayerReading(
        "Oração a São Miguel Arcanjo",
        "Ó glorioso Príncipe São Miguel, chefe e comandante das milícias celestes, guardião das almas e defensor do povo de Deus: livrai-nos de todo mal e ajudai-nos a servir fielmente ao Senhor todos os dias da nossa vida.\n\nRogai por nós, ó bem-aventurado São Miguel, Príncipe da Igreja de Cristo, para que sejamos dignos de Suas promessas. Amém.",
        soloText = "Ó glorioso Príncipe São Miguel, chefe e comandante das milícias celestes, guardião das almas e defensor do povo de Deus: livrai-me de todo mal e ajudai-me a servir fielmente ao Senhor todos os dias da minha vida.\n\nRogai por nós, ó bem-aventurado São Miguel, Príncipe da Igreja de Cristo, para que sejamos dignos de Suas promessas. Amém.",
    )

    // Via-Sacra (As 14 Estações da Cruz)
    val VIA_SACRA_OPENING = PrayerReading(
        "Oração preparatória da Via-Sacra",
        "Senhor Jesus, que carregastes a cruz por amor a nós, queremos percorrer convosco este caminho de entrega e misericórdia. Abri nossos olhos para reconhecer a Vossa presença em cada pessoa que sofre e ensinai-nos a amar até o fim. Amém.",
        soloText = "Senhor Jesus, que carregastes a cruz por amor a mim, quero percorrer convosco este caminho de entrega e misericórdia. Abri meus olhos para reconhecer a Vossa presença em cada pessoa que sofre e ensinai-me a amar até o fim. Amém.",
    )
    val VIA_SACRA_ADORATION = PrayerReading(
        "Aclamação da Estação",
        "Nós Vos adoramos, ó Cristo, e Vos bendizemos.",
        response = "Porque pela Vossa santa Cruz remistes o mundo.",
    )
    val VIA_SACRA_CLOSING = PrayerReading(
        "Oração final da Via-Sacra",
        "Senhor Jesus Cristo, que atravessastes a dor e a morte para abrir o caminho da vida nova, gravai em nós o amor que mostrastes na Cruz. Que a esperança da Vossa Ressurreição ilumine nossas dores e nos transforme em instrumentos de justiça, perdão e paz. Amém.",
        soloText = "Senhor Jesus Cristo, que atravessastes a dor e a morte para abrir o caminho da vida nova, gravai em mim o amor que mostrastes na Cruz. Que a esperança da Vossa Ressurreição ilumine minhas dores e me transforme em instrumento de justiça, perdão e paz. Amém.",
    )
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
