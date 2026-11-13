package com.sertumrosarum.feature.treasury

import androidx.compose.runtime.Immutable
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.treasury.PrayerCategory
import com.sertumrosarum.domain.treasury.PrayerLanguage
import com.sertumrosarum.domain.treasury.PrayerTermAnnotation
import com.sertumrosarum.domain.treasury.TreasuryPrayer

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
