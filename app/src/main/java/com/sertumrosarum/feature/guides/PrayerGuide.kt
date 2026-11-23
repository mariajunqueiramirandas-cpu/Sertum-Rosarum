package com.sertumrosarum.feature.guides

import com.sertumrosarum.domain.prayer.PrayerScript

/** The outline of a crown: how many groups it has, and how many small beads each one carries. */
data class CrownShape(val groups: Int, val beads: Int, val groupLabel: String = "mistério") {
    val groupNoun: String get() = when (groupLabel) {
        "setena" -> if (groups == 1) "setena" else "setenas"
        "estação" -> if (groups == 1) "estação" else "estações"
        "saudação" -> if (groups == 1) "saudação" else "saudações"
        "dor" -> if (groups == 1) "dor" else "dores"
        "chaga" -> if (groups == 1) "chaga" else "chagas"
        else -> if (groups == 1) "dezena" else "dezenas"
    }
    val summary: String get() = when {
        groupLabel == "estação" -> "Quatorze estações com Pai-Nosso, Ave-Maria e Glória"
        groups == 1 -> "Uma dezena de dez contas"
        groups == 20 -> "Vinte mistérios · quatro terços completos"
        else -> "${spelled(groups)} $groupNoun de ${spelled(beads).lowercase()} contas"
    }
    /** The shape at a glance, for a small label: "5 × 10". */
    val badge: String get() = if (groupLabel == "estação") "14 estações" else "$groups × $beads"

    private fun spelled(number: Int) = when (number) {
        1 -> "Uma"; 3 -> "Três"; 5 -> "Cinco"; 7 -> "Sete"; 9 -> "Nove"; 10 -> "Dez"; 14 -> "Quatorze"; 20 -> "Vinte"
        else -> number.toString()
    }
}

enum class GuideShelf(val title: String, val caption: String) {
    ROSARY("O santo terço", "No ritmo do seu dia: uma dezena, o terço ou o rosário inteiro."),
    THEMED("Terços temáticos", "Seis intenções com reflexões e preces próprias para cada mistério."),
    CROWN("Devoções e coroas brasileiras", "Coroas irmãs do terço e a Via-Sacra, cada uma com sua contemplação."),
}

/** The presentation of each bundled, playable prayer script. */
enum class PrayerGuide(
    val shelf: GuideShelf,
    val title: String,
    val kicker: String,
    val summary: String,
    val about: String,
    val path: List<String>,
    /** The small line above the mystery set while this guide is being prayed. */
    val prayerLabel: String = "Minha oração",
    val catechetical: Boolean = false,
    val litany: Boolean = false,
) {
    TRADITIONAL(
        GuideShelf.ROSARY, "Terço tradicional", "O caminho de sempre",
        "As orações de sempre, uma conta de cada vez.",
        "O terço como a Igreja reza há séculos: cinco mistérios contemplados com Maria, do Sinal da Cruz à Salve Rainha.",
        listOf("Sinal da Cruz, oferecimento e Creio", "Pai-Nosso, três Ave-Marias e Glória", "Cinco dezenas, cada uma com o seu mistério", "Infinitas Graças e Salve Rainha"),
    ),
    CATECHETICAL(
        GuideShelf.ROSARY, "Roteiro catequético", "Para quem está começando",
        "Cada passo vem com um bilhete que explica por que rezamos assim.",
        "O mesmo terço, com pequenas notas ao longo do caminho: o sentido do Sinal da Cruz, as três Ave-Marias pela fé, pela esperança e pela caridade, o porquê de cada oração.",
        listOf("Uma nota breve antes de cada oração", "Sinal da Cruz, oferecimento e Creio", "Cinco dezenas, cada uma com o seu mistério", "Infinitas Graças e Salve Rainha"),
        prayerLabel = "Roteiro catequético", catechetical = true,
    ),
    COMPLETE(
        GuideShelf.ROSARY, "Tradicional completo", "Com a Ladainha de Nossa Senhora",
        "Todas as jaculatórias e a Ladainha Lauretana ao final.",
        "O terço com todas as jaculatórias tradicionais, as Infinitas Graças, a Salve Rainha e, ao final, a Ladainha Lauretana, rezada em invocações e respostas.",
        listOf("Sinal da Cruz, oferecimento e Creio", "Cinco dezenas com as jaculatórias tradicionais", "Infinitas Graças e Salve Rainha", "Ladainha Lauretana, em invocações e respostas"),
        prayerLabel = "Terço completo", litany = true,
    ),
    SINGLE_DECADE(
        GuideShelf.ROSARY, "Uma dezena", "Terço curto · 1 mistério",
        "Um mistério contemplado sem pressa, para dias corridos ou para começar.",
        "Escolha um dos cinco mistérios e reze uma dezena inteira com calma: Sinal da Cruz, oferecimento, contemplação, Pai-Nosso, dez Ave-Marias, Glória, Fátima e Salve Rainha.",
        listOf("Sinal da Cruz e oferecimento", "Um mistério escolhido para contemplar", "Pai-Nosso, dez Ave-Marias, Glória e Fátima", "Salve Rainha de encerramento"),
        prayerLabel = "Uma dezena",
    ),
    FULL_ROSARY(
        GuideShelf.ROSARY, "Rosário completo", "Os vinte mistérios",
        "Gozosos, Luminosos, Dolorosos e Gloriosos numa única coroa.",
        "O Rosário inteiro como o propôs São João Paulo II: os vinte mistérios da vida de Cristo contemplados em sequência, com pausa e retomada preservadas no aparelho.",
        listOf("Abertura tradicional do terço", "Gozosos e Luminosos · dez dezenas", "Dolorosos e Gloriosos · dez dezenas", "Infinitas Graças e Salve Rainha"),
        prayerLabel = "Rosário completo",
    ),
    FAMILY(
        GuideShelf.THEMED, "Pela família", "Terço temático",
        "Para colocar a sua casa nas mãos de Maria.",
        "Um terço oferecido por quem mora no seu coração: pais, filhos, irmãos, os que estão perto e os que estão longe, com intenção, reflexão e prece em cada mistério.",
        listOf("Oferecimento pela família", "Cinco dezenas com intenção e prece próprias", "Infinitas Graças, Salve Rainha e bênção da família"),
        prayerLabel = "Terço pela família",
    ),
    SICK(
        GuideShelf.THEMED, "Pelos enfermos", "Terço temático",
        "Para acompanhar quem atravessa a doença.",
        "Um terço oferecido pelos doentes, por quem cuida deles e por quem espera uma notícia, com meditações de consolo e prece final pela saúde.",
        listOf("Oferecimento pelos enfermos e cuidadores", "Cinco dezenas com meditação de esperança", "Infinitas Graças, Salve Rainha e prece pelos doentes"),
        prayerLabel = "Terço pelos enfermos",
    ),
    PEACE(
        GuideShelf.THEMED, "Pela paz", "Terço temático",
        "Para pedir paz ao coração e ao mundo.",
        "Um terço oferecido pela paz nas famílias, entre os povos e dentro de cada um de nós, com reflexões voltadas à justiça e à reconciliação.",
        listOf("Oferecimento pela paz e reconciliação", "Cinco dezenas com prece pela paz", "Infinitas Graças, Salve Rainha e oração pela paz"),
        prayerLabel = "Terço pela paz",
    ),
    DECEASED(
        GuideShelf.THEMED, "Pelos falecidos", "Terço temático · no luto",
        "Para confiar os que partiram à luz da Ressurreição.",
        "Um terço sereno pela alma dos familiares e amigos falecidos e pelo consolo de quem vive a saudade e o luto.",
        listOf("Oferecimento pelos fiéis defuntos", "Cinco dezenas à luz da esperança pascal", "Infinitas Graças, Salve Rainha e prece pelo descanso eterno"),
        prayerLabel = "Terço pelos falecidos",
    ),
    ANXIETY(
        GuideShelf.THEMED, "Na ansiedade", "Terço temático · discernimento",
        "Para entregar medos, pressa e decisões a Deus.",
        "Um terço acolhedor para dias de sobrecarga, inquietação ou escolhas difíceis, aprendendo com Cristo a caminhar um passo de cada vez.",
        listOf("Oferecimento na busca de paz e discernimento", "Cinco dezenas para aquietar o coração", "Infinitas Graças, Salve Rainha e entrega na paz de Cristo"),
        prayerLabel = "Terço na ansiedade",
    ),
    VOCATIONS(
        GuideShelf.THEMED, "Pelas vocações", "Terço temático · pela Igreja",
        "Pelas famílias, sacerdotes, consagrados e jovens.",
        "Um terço em comunhão com toda a Igreja, pedindo corações generosos para o serviço do Evangelho em todas as vocações.",
        listOf("Oferecimento pela Igreja e pelas vocações", "Cinco dezenas pelos chamados de Deus", "Infinitas Graças, Salve Rainha e prece vocacional"),
        prayerLabel = "Terço pelas vocações",
    ),
    DIVINE_MERCY(
        GuideShelf.CROWN, "Terço da Divina Misericórdia", "Coroa devocional",
        "Cinco dezenas com meditações da Paixão e a prece final.",
        "Rezado nas contas do terço — especialmente às três da tarde — confia o mundo inteiro à misericórdia de Jesus, acompanhado de cinco meditações da Paixão e da oração final de Santa Faustina.",
        listOf("Pai-Nosso, Ave-Maria e Creio", "Cinco dezenas com meditações da Misericórdia", "Três vezes: Deus Santo, Deus Forte, Deus Imortal", "Oração final: Ó Deus eterno"),
    ),
    SAINT_JOSEPH(
        GuideShelf.CROWN, "Terço de São José", "Coroa devocional",
        "Cinco mistérios contemplados da vida do guardião de Jesus.",
        "Contempla a vida de São José, esposo de Maria e guardião de Jesus, com cena bíblica, sentido e fruto em cada dezena e a prece final «A vós, São José».",
        listOf("Cinco mistérios contemplados de São José", "Em cada dezena: Pai-Nosso, dez invocações e Glória", "Oração final «A vós, São José»"),
    ),
    VIA_SACRA(
        GuideShelf.CROWN, "Via-Sacra", "As quatorze estações",
        "Caminhe com Jesus do pretório ao sepulcro em 14 estações.",
        "A devoção clássica das quatorze estações da Cruz: em cada estação, cena do Evangelho, reflexão, aclamação «Nós Vos adoramos, ó Cristo», Pai-Nosso, Ave-Maria e Glória.",
        listOf("Oração preparatória", "Quatorze estações com contemplação e fruto", "Aclamação responsorial, Pai-Nosso, Ave-Maria e Glória", "Oração final na esperança da Ressurreição"),
    ),
    HOLY_WOUNDS(
        GuideShelf.CROWN, "Terço das Santas Chagas", "Devoção brasileira",
        "Cinco chagas gloriosas de Jesus por cura e misericórdia.",
        "Devoção muito amada no Brasil: em cinco dezenas contemplamos as cinco chagas de Cristo, oferecendo ao Pai os méritos da Cruz pela cura das almas e das famílias.",
        listOf("Oferecimento inicial às Santas Chagas", "Cinco dezenas contemplando cada chaga de Cristo", "Três invocações finais e consagração"),
    ),
    SEVEN_SORROWS(
        GuideShelf.CROWN, "Terço das Sete Dores", "Coroa Servita de Maria",
        "Sete dores de Nossa Senhora, com sete Ave-Marias em cada.",
        "Contempla as sete dores que atravessaram o coração de Maria, da profecia de Simeão ao sepultamento de Jesus, concluindo com três Ave-Marias pelas suas lágrimas.",
        listOf("Oferecimento à Mãe das Dores", "Sete dores com contemplação, Pai-Nosso e sete Ave-Marias", "Três Ave-Marias finais e oração conclusiva"),
    ),
    TEARS(
        GuideShelf.CROWN, "Coroa das Lágrimas", "De Nossa Senhora",
        "Sete setenas unidas às sete dores e lágrimas de Maria.",
        "Uma coroa de sete grupos de sete contas, agora acompanhada pelas sete contemplações das dores de Maria junto à missão do Filho.",
        listOf("Oração inicial diante de Jesus Crucificado", "Sete setenas com contemplação das dores de Maria", "Três ofertas finais e oração «Ó Maria, Mãe de amor»"),
    ),
    SAINT_MICHAEL(
        GuideShelf.CROWN, "Terço de São Miguel", "Coroa Angélica · 9 coros",
        "Nove saudações aos coros dos anjos por proteção e virtude.",
        "A tradicional Coroa Angélica de São Miguel Arcanjo: nove grupos de um Pai-Nosso e três Ave-Marias pedindo virtudes concretas, seguidos de quatro Pai-Nossos finais.",
        listOf("Invocação inicial", "Nove coros angélicos com Pai-Nosso e três Ave-Marias", "Quatro Pai-Nossos pelos Arcanjos e Anjo da Guarda", "Oração final a São Miguel"),
    ),
    FRANCISCAN(
        GuideShelf.CROWN, "Coroa Franciscana", "As sete alegrias de Nossa Senhora",
        "Sete dezenas contempladas pelas alegrias de Maria.",
        "Sete dezenas que contemplam as alegrias de Nossa Senhora, da Anunciação à sua glória no céu — incluindo a Adoração dos Magos e a Coroação. Com duas Ave-Marias finais, somam setenta e duas.",
        listOf("Sete alegrias com cena, sentido e fruto", "Pai-Nosso, dez Ave-Marias e Glória em cada dezena", "Duas Ave-Marias finais: setenta e duas ao todo"),
    );

    val isRosary: Boolean get() = script.isRosary
    val usesMysterySet: Boolean get() = script.usesMysterySet
    val script: PrayerScript get() = PrayerScript.valueOf(name)
    val shape: CrownShape get() = CrownShape(script.groupCount, script.beadCount(0), script.groupLabel)

    companion object {
        fun on(shelf: GuideShelf): List<PrayerGuide> = entries.filter { it.shelf == shelf }
        fun byNameOrNull(name: String?): PrayerGuide? = entries.firstOrNull { it.name == name }
    }
}
