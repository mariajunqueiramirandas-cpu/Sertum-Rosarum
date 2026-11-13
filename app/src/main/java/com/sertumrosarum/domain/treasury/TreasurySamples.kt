package com.sertumrosarum.domain.treasury

import com.sertumrosarum.domain.prayer.FixedPrayer

/**
 * The single handwritten example the Phase 3 screens are drawn with: one prayer, with its Latin,
 * its verses, one verse broken down word by word, and one expression explained.
 * The real catalog replaces this object.
 */
object TreasurySamples {
    val AVE_MARIA = TreasuryPrayer(
        id = "ave-maria",
        title = FixedPrayer.HAIL_MARY.title,
        category = PrayerCategory.MARIAN,
        summary = "A saudação do anjo e o pedido da Igreja.",
        // The rosary's own wording, so the Treasury can never drift from it.
        portuguese = FixedPrayer.HAIL_MARY.text,
        latinTitle = "Ave, Maria",
        latin = "Ave, María, grátia plena, Dóminus tecum. Benedícta tu in muliéribus, et benedíctus fructus ventris tui, Iesus.\n\nSancta María, Mater Dei, ora pro nobis peccatóribus, nunc et in hora mortis nostræ. Amen.",
        verses = listOf(
            BilingualVerse(
                "Ave Maria, cheia de graça,", "Ave, María, grátia plena,",
                listOf(
                    LatinGloss("Ave", "Salve", "A saudação latina de quem chega: ao pé da letra, o desejo de que a pessoa esteja bem."),
                    LatinGloss("grátia", "de graça", "Graça é favor gratuito: o dom que Deus dá sem que seja merecido."),
                    LatinGloss("plena", "cheia", "Concorda com Maria: é ela quem está repleta."),
                ),
            ),
            BilingualVerse("o Senhor é convosco;", "Dóminus tecum."),
            BilingualVerse("bendita sois vós entre as mulheres,", "Benedícta tu in muliéribus,"),
            BilingualVerse("e bendito é o fruto do vosso ventre, Jesus.", "et benedíctus fructus ventris tui, Iesus."),
            BilingualVerse("Santa Maria, Mãe de Deus,", "Sancta María, Mater Dei,"),
            BilingualVerse("rogai por nós, pecadores,", "ora pro nobis peccatóribus,"),
            BilingualVerse("agora e na hora de nossa morte. Amém.", "nunc et in hora mortis nostræ. Amen."),
        ),
        annotations = listOf(
            PrayerTermAnnotation(
                term = "cheia de graça",
                origin = "Traduz o latim gratia plena, que por sua vez verte uma só palavra grega do Evangelho: kecharitōménē, “aquela que foi agraciada e assim permanece”. Na saudação do anjo, a expressão ocupa o lugar do nome de Maria.",
                today = "Ao repetir estas palavras, não elogiamos um mérito de Maria: reconhecemos o que Deus fez nela. E pedimos que a mesma graça encontre espaço em nós.",
                scriptureQuote = "Alegra-te, cheia de graça, o Senhor está contigo.",
                scriptureReference = "Lucas 1, 28",
                catechism = "Catecismo da Igreja Católica, 490–491",
            ),
        ),
        source = "Texto latino conforme o apêndice de orações comuns do Compêndio do Catecismo da Igreja Católica.",
    )

    val prayers = listOf(AVE_MARIA)

    /** The Treasury entry that carries the Latin of a rosary prayer, when there is one. */
    fun latinFor(prayer: FixedPrayer?): TreasuryPrayer? = if (prayer == FixedPrayer.HAIL_MARY) AVE_MARIA else null
}
