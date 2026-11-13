package com.sertumrosarum.feature.treasury

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.treasury.PrayerCategory
import com.sertumrosarum.domain.treasury.PrayerLanguage
import com.sertumrosarum.domain.treasury.PrayerTermAnnotation
import com.sertumrosarum.domain.treasury.TreasuryPrayer
import com.sertumrosarum.domain.treasury.TreasurySamples

/** The three pages of the Treasury tab. */
enum class TreasuryPlace { LIBRARY, READING, MYSTERIES }

/**
 * Everything the Treasury draws. The screens only read this and report touches through
 * [TreasuryActions]; they decide nothing themselves.
 */
@Immutable
data class TreasuryUiState(
    val place: TreasuryPlace = TreasuryPlace.LIBRARY,
    val query: String = "",
    /** The chosen moment of life; null shows every shelf. */
    val category: PrayerCategory? = null,
    /** The prayers to list, already searched and filtered. */
    val prayers: List<TreasuryPrayer> = emptyList(),
    val reading: TreasuryPrayer? = null,
    val language: PrayerLanguage = PrayerLanguage.PORTUGUESE,
    /** The verse whose word-by-word Latin is open in the side-by-side reading. */
    val openVerse: Int? = null,
    /** The expression whose explanation sheet is raised. */
    val term: PrayerTermAnnotation? = null,
    val studySet: MysterySet = MysterySet.JOYFUL,
    /** The mystery whose study sheet is raised, as its index within [studySet]. */
    val studyMystery: Int? = null,
)

interface TreasuryActions {
    fun search(query: String)
    fun selectCategory(category: PrayerCategory?)
    fun openPrayer(prayer: TreasuryPrayer)
    fun selectLanguage(language: PrayerLanguage)
    fun toggleVerse(index: Int)
    fun openTerm(annotation: PrayerTermAnnotation)
    fun closeTerm()
    fun openMysteries()
    fun selectStudySet(set: MysterySet)
    fun openStudyMystery(index: Int)
    fun closeStudyMystery()
    fun backToLibrary()
}

/**
 * A stand-in so the screens can be walked through before their logic exists.
 * It keeps state in memory only (nothing survives rotation or process death) and searches
 * titles in the plainest way. Replace it with the real view model and catalog.
 */
class TreasuryStandIn(private val catalog: List<TreasuryPrayer> = TreasurySamples.prayers) : TreasuryActions {
    var state by mutableStateOf(TreasuryUiState(prayers = catalog))
        private set

    private fun list(query: String, category: PrayerCategory?) =
        catalog.filter { (category == null || it.category == category) && it.title.contains(query.trim(), ignoreCase = true) }

    override fun search(query: String) { state = state.copy(query = query, prayers = list(query, state.category)) }
    override fun selectCategory(category: PrayerCategory?) { state = state.copy(category = category, prayers = list(state.query, category)) }
    override fun openPrayer(prayer: TreasuryPrayer) { state = state.copy(place = TreasuryPlace.READING, reading = prayer, language = PrayerLanguage.PORTUGUESE, openVerse = null) }
    override fun selectLanguage(language: PrayerLanguage) { state = state.copy(language = language) }
    override fun toggleVerse(index: Int) { state = state.copy(openVerse = index.takeUnless { it == state.openVerse }) }
    override fun openTerm(annotation: PrayerTermAnnotation) { state = state.copy(term = annotation) }
    override fun closeTerm() { state = state.copy(term = null) }
    override fun openMysteries() { state = state.copy(place = TreasuryPlace.MYSTERIES) }
    override fun selectStudySet(set: MysterySet) { state = state.copy(studySet = set) }
    override fun openStudyMystery(index: Int) { state = state.copy(studyMystery = index) }
    override fun closeStudyMystery() { state = state.copy(studyMystery = null) }
    override fun backToLibrary() { state = state.copy(place = TreasuryPlace.LIBRARY, term = null, studyMystery = null) }
}

@Composable
fun rememberTreasuryStandIn(): TreasuryStandIn = remember { TreasuryStandIn() }
