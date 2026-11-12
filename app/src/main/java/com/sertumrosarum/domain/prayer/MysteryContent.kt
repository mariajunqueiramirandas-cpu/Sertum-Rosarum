package com.sertumrosarum.domain.prayer

/** The three written layers of a mystery, plus exactly one line for each of the ten Ave-Marias. */
data class MysteryContemplation(
    val scene: String,
    val meaning: String,
    val practice: String,
    val beads: List<String>,
    val scriptureQuote: String = "",
) {
    init { require(beads.size == 10 && beads.all { it.isNotBlank() }) }
}

/** Complete local contemplations for all twenty mysteries. No network or generated runtime text. */
object MysteryContent {
    private val quotes = mapOf(
        MysterySet.JOYFUL to listOf("Eis aqui a serva do Senhor; faça-se em mim segundo a tua palavra.", "Bendita és tu entre as mulheres e bendito é o fruto do teu ventre.", "Hoje nasceu para vós um Salvador, que é Cristo Senhor.", "Meus olhos viram a tua salvação.", "Não sabíeis que devo estar na casa de meu Pai?"),
        MysterySet.LUMINOUS to listOf("Este é o meu Filho amado, em quem me comprazo.", "Fazei tudo o que Ele vos disser.", "Convertei-vos e crede no Evangelho.", "Este é o meu Filho amado. Escutai-O!", "Isto é o meu corpo, que é dado por vós."),
        MysterySet.SORROWFUL to listOf("Não seja como eu quero, mas como Tu queres.", "Então Pilatos mandou flagelar Jesus.", "Trançaram uma coroa de espinhos e a puseram na Sua cabeça.", "Levando a própria cruz, Jesus saiu para o lugar chamado Calvário.", "Tudo está consumado."),
        MysterySet.GLORIOUS to listOf("Eu vi o Senhor!", "Sereis minhas testemunhas até os confins da terra.", "Todos ficaram cheios do Espírito Santo.", "O Todo-Poderoso fez em mim maravilhas.", "Uma mulher vestida de sol, com a lua debaixo dos pés e uma coroa de doze estrelas na cabeça."),
    )

    fun contemplationFor(set: MysterySet, decade: Int): MysteryContemplation =
        mysteryContemplations.getValue(set to decade).copy(scriptureQuote = quotes.getValue(set)[decade])

    /** A short reason for each prayer, shown by the catechetical guide the first time it appears in a group. */
    fun catecheticalNote(step: PrayerStep): String? = when (step.prayer) {
        null -> "Anunciar o mistério é escolher a cena do Evangelho que vamos contemplar com Maria durante a dezena."
        FixedPrayer.SIGN_OF_CROSS -> "Começamos em nome da Trindade. Traçar a cruz sobre nós é lembrar que pertencemos a Deus e que foi pela cruz que Jesus nos salvou."
        FixedPrayer.OFFERING -> "Antes de rezar, dizemos a Deus por quem e por que rezamos. A oração ganha direção quando é oferecida."
        FixedPrayer.CREED -> "O Creio reúne o essencial da fé que recebemos dos apóstolos. Rezá-lo é dizer: é nesta fé que eu quero rezar."
        FixedPrayer.OUR_FATHER -> "É a oração que o próprio Jesus ensinou (Mt 6, 9–13). Ela abre cada dezena, porque tudo começa no Pai."
        FixedPrayer.HAIL_MARY -> when {
            (step.beadIndex ?: 0) > 0 -> null
            step.decadeIndex == null -> "As três primeiras Ave-Marias pedem as virtudes que sustentam toda a oração: a fé, a esperança e a caridade."
            else -> "As primeiras palavras vêm do Evangelho: a saudação do anjo e a de Isabel (Lc 1, 28.42). Enquanto os lábios repetem, o coração contempla o mistério."
        }
        FixedPrayer.GLORY_BE -> "Cada dezena termina em louvor à Trindade. Tudo o que contemplamos volta para a glória de Deus."
        FixedPrayer.FATIMA -> "Nossa Senhora ensinou esta oração aos pastorinhos de Fátima, em 1917, para rezarmos pelos que mais precisam de misericórdia."
        FixedPrayer.THANKSGIVING -> "Agradecemos a Maria por rezar conosco e nos colocamos sob a sua proteção."
        FixedPrayer.HAIL_HOLY_QUEEN -> "Uma das orações marianas mais antigas da Igreja. Nela pedimos a Maria que nos mostre Jesus."
    }
}
