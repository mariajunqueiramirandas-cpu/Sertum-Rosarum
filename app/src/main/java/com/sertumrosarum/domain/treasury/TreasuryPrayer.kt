package com.sertumrosarum.domain.treasury

/** The moments of life the Treasury is shelved by. */
enum class PrayerCategory(val label: String, val caption: String) {
    WAKING("Ao acordar", "Para entregar a Deus o dia que começa."),
    NIGHT("Antes de dormir", "Para rever o dia e descansar em Deus."),
    DISTRESS("Na angústia", "Quando o coração precisa de amparo."),
    THANKSGIVING("Ação de graças", "Para agradecer o que foi recebido."),
    COMMUNION("Pós-comunhão", "Para permanecer com Jesus depois da Missa."),
    MARIAN("Marianas e tradicionais", "As orações que a Igreja reza há séculos."),
}

/** How a prayer is read. [BILINGUAL] sets both languages verse by verse. */
enum class PrayerLanguage(val label: String) {
    PORTUGUESE("Português"),
    LATIN("Latim"),
    BILINGUAL("Lado a lado"),
}

/**
 * One prayer of the Treasury, as the screens need it.
 * Everything Latin is optional: a prayer without [latin] simply offers no language choice.
 */
data class TreasuryPrayer(
    val id: String,
    val title: String,
    val category: PrayerCategory,
    /** One line for the library shelf. */
    val summary: String,
    val portuguese: String,
    val latinTitle: String? = null,
    val latin: String? = null,
    /** The same prayer cut into matching lines, for the side-by-side reading. */
    val verses: List<BilingualVerse> = emptyList(),
    val annotations: List<PrayerTermAnnotation> = emptyList(),
    /** Where the wording comes from, shown beneath the prayer. */
    val source: String? = null,
)

data class BilingualVerse(
    val portuguese: String,
    val latin: String,
    /** The Latin of this verse, expression by expression. Empty when it has not been broken down. */
    val glosses: List<LatinGloss> = emptyList(),
)

data class LatinGloss(val latin: String, val portuguese: String, val note: String? = null)

/** "Toque para entender": one expression of a prayer and what stands behind it. */
data class PrayerTermAnnotation(
    /** The exact words as they appear in the Portuguese text. */
    val term: String,
    val origin: String,
    val today: String,
    val scriptureQuote: String? = null,
    val scriptureReference: String? = null,
    val catechism: String? = null,
)
