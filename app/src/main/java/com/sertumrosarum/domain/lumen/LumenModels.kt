package com.sertumrosarum.domain.lumen

import com.sertumrosarum.domain.prayer.MysterySet

enum class LumenMood(
    val id: String,
    val label: String,
    val hint: String,
    val suggestEyesClosed: Boolean,
) {
    DRY_UNFOCUSED("seco", "Seco e sem foco", "Frases curtas de um fôlego", false),
    ANXIOUS("ansioso", "Aflito ou ansioso", "Pausa inicial e ritmo sereno", false),
    TIRED("cansado", "Cansado no fim do dia", "Sugere o modo Olhos fechados", true),
    GRATEFUL("grato", "Em paz e grato", "Tom de ação de graças", false);

    companion object {
        fun byId(id: String?): LumenMood? =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) }
    }
}

data class CustomRosaryDecade(
    val mysteryNumber: Int,
    val adaptedReflection: String,
    val spiritualFruitApplication: String,
    val beadMeditations: List<String>,
)

/**
 * Contemplative layer woven around fixed Rosary prayers.
 *
 * Architecturally, this model NEVER carries or replaces fixed Church prayers
 * (Sign of the Cross, Creed, Our Father, Hail Mary, Glory Be, Fatima Prayer, Hail Holy Queen).
 */
data class CustomRosarySupplement(
    val mysterySet: MysterySet,
    val userIntentionSummary: String,
    val personalizedOffering: String,
    val preparatoryPrayer: String? = null,
    val suggestEyesClosed: Boolean = false,
    val decades: List<CustomRosaryDecade> = emptyList(),
    val personaId: String = CatechistPersona.DEFAULT.id,
    val isOfflineFallback: Boolean = false,
) {
    fun decadeFor(mysteryIndexZeroBased: Int): CustomRosaryDecade? =
        decades.firstOrNull { it.mysteryNumber == mysteryIndexZeroBased + 1 }
            ?: decades.getOrNull(mysteryIndexZeroBased)

    fun beadMeditationFor(mysteryIndexZeroBased: Int, beadIndexZeroBased: Int): String? =
        decadeFor(mysteryIndexZeroBased)?.beadMeditations?.getOrNull(beadIndexZeroBased)
            ?.takeIf { it.isNotBlank() }
}

sealed interface CatechistAction {
    val label: String
    val subtitle: String

    data class StartCustomRosary(
        override val label: String,
        override val subtitle: String,
        val supplement: CustomRosarySupplement,
        val singleDecadeIndex: Int? = null,
    ) : CatechistAction

    data class StartQuickDecade(
        override val label: String,
        override val subtitle: String,
        val mysterySet: MysterySet,
        val mysteryIndex: Int,
        val meditated: Boolean = true,
    ) : CatechistAction

    data class LaunchGuide(
        override val label: String,
        override val subtitle: String,
        val guideName: String,
        val mysterySet: MysterySet? = null,
        val focusMysteryIndex: Int? = null,
        val meditated: Boolean? = null,
    ) : CatechistAction

    data class SaveIntention(
        override val label: String,
        override val subtitle: String,
        val intentionTitle: String,
        val intentionDescription: String,
        val category: String,
    ) : CatechistAction

    data class OpenTreasuryPrayer(
        override val label: String,
        override val subtitle: String,
        val prayerId: String,
    ) : CatechistAction

    data class OpenFormationLesson(
        override val label: String,
        override val subtitle: String,
        val lessonId: String,
    ) : CatechistAction

    data class OpenFormationQuestion(
        override val label: String,
        override val subtitle: String,
        val questionId: String,
    ) : CatechistAction

    data class OpenPracticeJourney(
        override val label: String,
        override val subtitle: String,
        val journeyId: String,
    ) : CatechistAction

    data class OpenMysteryStudy(
        override val label: String,
        override val subtitle: String,
        val mysterySet: MysterySet,
        val mysteryIndex: Int,
    ) : CatechistAction
}

data class CatechistMessage(
    val id: String,
    val fromUser: Boolean,
    val text: String,
    val persona: CatechistPersona? = null,
    val reference: String? = null,
    val actions: List<CatechistAction> = emptyList(),
    val toolTrace: List<String> = emptyList(),
    val suggestedFollowUp: String? = null,
    val isOfflineFallback: Boolean = false,
    val timestampMs: Long = System.currentTimeMillis(),
) {
    /** Convenience primary action for callers expecting a single action. */
    val action: CatechistAction? get() = actions.firstOrNull()
}

data class CatechistContext(
    val activeMysterySet: MysterySet,
    val activeMysteryIndex: Int? = null,
    val activeMysteryTitle: String? = null,
    val liturgicalSummary: String? = null,
    val recentIntentions: List<String> = emptyList(),
)

data class CatechistTurnResult(
    val replyText: String,
    val reference: String?,
    val actions: List<CatechistAction> = emptyList(),
    val toolTrace: List<String> = emptyList(),
    val suggestedFollowUp: String? = null,
    val isOfflineFallback: Boolean = false,
) {
    val action: CatechistAction? get() = actions.firstOrNull()
}

data class MemoryRecallPrompt(
    val intentionId: String,
    val intentionTitle: String,
    val daysAgo: Int,
    val heading: String,
    val body: String,
)
