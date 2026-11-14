package com.sertumrosarum.feature.treasury

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.treasury.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Save identifiers and choices, never large prayer texts, to the Android saved-state bundle. */
class TreasuryViewModel(private val saved: SavedStateHandle) : ViewModel(), TreasuryActions {
    private inline fun <reified T : Enum<T>> enum(key: String, fallback: T): T =
        enumValues<T>().firstOrNull { it.name == saved.get<String>(key) } ?: fallback
    private val reading = TreasuryCatalog.find(saved["reading"])
    private val category = saved.get<String>("category")?.let { name -> PrayerCategory.entries.find { it.name == name } }
    private val query = saved.get<String>("query").orEmpty().take(300)
    private val set = enum("studySet", MysterySet.JOYFUL)
    private val mutableState = MutableStateFlow(TreasuryUiState(
        place = enum("place", TreasuryPlace.LIBRARY).let { if (it == TreasuryPlace.READING && reading == null) TreasuryPlace.LIBRARY else it },
        query = query, category = category, prayers = TreasuryCatalog.search(query, category), reading = reading,
        language = enum("language", PrayerLanguage.PORTUGUESE),
        openVerse = saved.get<Int>("openVerse")?.takeIf { reading?.verses?.getOrNull(it)?.glosses?.isNotEmpty() == true },
        term = saved.get<String>("term")?.let { term -> reading?.annotations?.find { it.term == term } },
        studySet = set, studyMystery = saved.get<Int>("studyMystery")?.takeIf { it in set.mysteries.indices },
    ))
    val state = mutableState.asStateFlow()
    private var repository: com.sertumrosarum.data.garden.GardenRepository? = null
    suspend fun connect(repo: com.sertumrosarum.data.garden.GardenRepository) {
        if (repository != null) return
        val prefs = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { repo.preferences() }
        if (!saved.contains("language")) mutableState.value = mutableState.value.copy(language = enumValues<PrayerLanguage>().firstOrNull { it.name == prefs.readingLanguage } ?: PrayerLanguage.PORTUGUESE)
        repository = repo
    }


    private fun update(transform: (TreasuryUiState) -> TreasuryUiState) {
        val state = transform(mutableState.value)
        mutableState.value = state
        saved["place"] = state.place.name; saved["query"] = state.query; saved["category"] = state.category?.name
        saved["reading"] = state.reading?.id; saved["language"] = state.language.name; saved["openVerse"] = state.openVerse
        saved["term"] = state.term?.term; saved["studySet"] = state.studySet.name; saved["studyMystery"] = state.studyMystery
    }
    override fun search(query: String) = update { it.copy(query = query.take(300), prayers = TreasuryCatalog.search(query, it.category)) }
    override fun selectCategory(category: PrayerCategory?) = update { it.copy(category = category, prayers = TreasuryCatalog.search(it.query, category)) }
    override fun openPrayer(prayer: TreasuryPrayer) {
        val canonical = TreasuryCatalog.find(prayer.id) ?: return
        update { it.copy(place = TreasuryPlace.READING, reading = canonical, openVerse = null, term = null, studyMystery = null) }
    }
    override fun selectLanguage(language: PrayerLanguage) {
        update { it.copy(language = language, openVerse = null, term = null) }
        repository?.let { repo -> repo.enqueue { repo.storeReadingLanguage(language.name) } }
    }
    override fun toggleVerse(index: Int) = update {
        if (it.reading?.verses?.getOrNull(index)?.glosses?.isNotEmpty() == true) it.copy(openVerse = index.takeUnless { n -> n == it.openVerse }) else it
    }
    override fun openTerm(annotation: PrayerTermAnnotation) = update { if (annotation in it.reading?.annotations.orEmpty()) it.copy(term = annotation) else it }
    override fun closeTerm() = update { it.copy(term = null) }
    override fun openMysteries() = update { it.copy(place = TreasuryPlace.MYSTERIES, term = null, studyMystery = null) }
    override fun selectStudySet(set: MysterySet) = update { it.copy(studySet = set, studyMystery = null) }
    override fun openStudyMystery(index: Int) = update { if (index in it.studySet.mysteries.indices) it.copy(studyMystery = index) else it }
    override fun closeStudyMystery() = update { it.copy(studyMystery = null) }
    override fun backToLibrary() = update { it.copy(place = TreasuryPlace.LIBRARY, term = null, studyMystery = null) }
}
