package com.sertumrosarum.feature.guides

/** The outline of a crown: how many groups it has, and how many small beads each one carries. */
data class CrownShape(val groups: Int, val beads: Int) {
    val groupNoun: String get() = if (beads == 7) "setenas" else "dezenas"
    val summary: String get() = "${spelled(groups)} $groupNoun de ${spelled(beads).lowercase()} contas"
    /** The shape at a glance, for a small label: "5 × 10". */
    val badge: String get() = "$groups × $beads"

    private fun spelled(number: Int) = when (number) {
        5 -> "Cinco"; 7 -> "Sete"; 10 -> "Dez"
        else -> number.toString()
    }
}

enum class GuideShelf(val title: String, val caption: String) {
    ROSARY("O santo terço", "Três jeitos de rezar os mesmos mistérios."),
    THEMED("Terços temáticos", "Uma intenção para acompanhar cada dezena."),
    CROWN("Outras coroas devocionais", "Orações irmãs do terço, cada uma com o seu ritmo."),
}

/**
 * What the Roteiros tab shows for each way of praying. This is interface copy only:
 * [ready] is false until the guided prayer for that crown exists, and the tab then
 * shows its structure without offering to begin.
 */
enum class PrayerGuide(
    val shelf: GuideShelf,
    val title: String,
    val kicker: String,
    val summary: String,
    val about: String,
    val path: List<String>,
    val shape: CrownShape = CrownShape(5, 10),
    val ready: Boolean = false,
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
        ready = true,
    ),
    CATECHETICAL(
        GuideShelf.ROSARY, "Roteiro catequético", "Para quem está começando",
        "Cada passo vem com um bilhete que explica por que rezamos assim.",
        "O mesmo terço, com pequenas notas ao longo do caminho: o sentido do Sinal da Cruz, as três Ave-Marias pela fé, pela esperança e pela caridade, o porquê de cada oração.",
        listOf("Uma nota breve antes de cada oração", "Sinal da Cruz, oferecimento e Creio", "Cinco dezenas, cada uma com o seu mistério", "Infinitas Graças e Salve Rainha"),
        ready = true, prayerLabel = "Roteiro catequético", catechetical = true,
    ),
    COMPLETE(
        GuideShelf.ROSARY, "Tradicional completo", "Com a Ladainha de Nossa Senhora",
        "Todas as jaculatórias e a Ladainha Lauretana ao final.",
        "O terço com todas as jaculatórias tradicionais, as Infinitas Graças, a Salve Rainha e, ao final, a Ladainha Lauretana, rezada em invocações e respostas.",
        listOf("Sinal da Cruz, oferecimento e Creio", "Cinco dezenas com as jaculatórias tradicionais", "Infinitas Graças e Salve Rainha", "Ladainha Lauretana, em invocações e respostas"),
        prayerLabel = "Terço completo", litany = true,
    ),
    FAMILY(
        GuideShelf.THEMED, "Pela família", "Terço temático",
        "Para colocar a sua casa nas mãos de Maria.",
        "Um terço oferecido por quem mora no seu coração: pais, filhos, irmãos, os que estão perto e os que estão longe.",
        listOf("Um oferecimento pela sua família", "Cinco dezenas, cada uma com o seu mistério", "Infinitas Graças e Salve Rainha"),
        ready = true, prayerLabel = "Terço pela família",
    ),
    SICK(
        GuideShelf.THEMED, "Pelos enfermos", "Terço temático",
        "Para acompanhar quem atravessa a doença.",
        "Um terço oferecido pelos doentes, por quem cuida deles e por quem espera uma notícia.",
        listOf("Um oferecimento pelos enfermos", "Cinco dezenas, cada uma com o seu mistério", "Infinitas Graças e Salve Rainha"),
        ready = true, prayerLabel = "Terço pelos enfermos",
    ),
    PEACE(
        GuideShelf.THEMED, "Pela paz", "Terço temático",
        "Para pedir paz ao coração e ao mundo.",
        "Um terço oferecido pela paz: nas famílias, entre os povos e dentro de cada um de nós.",
        listOf("Um oferecimento pela paz", "Cinco dezenas, cada uma com o seu mistério", "Infinitas Graças e Salve Rainha"),
        ready = true, prayerLabel = "Terço pela paz",
    ),
    DIVINE_MERCY(
        GuideShelf.CROWN, "Terço da Divina Misericórdia", "Coroa devocional",
        "Cinco dezenas confiadas à misericórdia de Jesus.",
        "Rezado nas contas do terço, confia o mundo inteiro à misericórdia de Jesus. Muitos o rezam às três da tarde.",
        listOf("Pai-Nosso, Ave-Maria e Creio", "Em cada conta grande, o oferecimento ao Eterno Pai", "Em cada conta pequena, a súplica pela dolorosa Paixão", "Ao final, três vezes: Deus Santo, Deus Forte, Deus Imortal"),
    ),
    SAINT_JOSEPH(
        GuideShelf.CROWN, "Terço de São José", "Coroa devocional",
        "Cinco mistérios da vida do guardião de Jesus.",
        "Contempla a vida de São José, esposo de Maria e guardião de Jesus, pedindo a sua proteção para a família e o trabalho.",
        listOf("Cinco mistérios da vida de São José", "Em cada conta grande, o Pai-Nosso", "Em cada conta pequena, uma invocação a São José", "Glória ao final de cada dezena"),
    ),
    TEARS(
        GuideShelf.CROWN, "Coroa das Lágrimas", "De Nossa Senhora",
        "Sete setenas pelas lágrimas de Maria.",
        "Uma coroa de sete grupos de sete contas, rezada em união com as lágrimas de Nossa Senhora junto à cruz.",
        listOf("Em cada conta grande, a oferta das lágrimas de Maria", "Em cada conta pequena, a súplica pelas suas lágrimas", "Ao final, três vezes a invocação de encerramento"),
        shape = CrownShape(7, 7),
    ),
    FRANCISCAN(
        GuideShelf.CROWN, "Coroa Franciscana", "As sete alegrias de Nossa Senhora",
        "Sete dezenas pelas alegrias de Maria.",
        "Sete dezenas que contemplam as alegrias de Nossa Senhora, da Anunciação à sua glória no céu. Com duas Ave-Marias finais, somam setenta e duas.",
        listOf("Sete alegrias, uma em cada dezena", "Pai-Nosso, dez Ave-Marias e Glória", "Duas Ave-Marias finais: setenta e duas ao todo"),
        shape = CrownShape(7, 10),
    );

    val isRosary: Boolean get() = shelf != GuideShelf.CROWN

    companion object {
        fun on(shelf: GuideShelf): List<PrayerGuide> = entries.filter { it.shelf == shelf }
    }
}
