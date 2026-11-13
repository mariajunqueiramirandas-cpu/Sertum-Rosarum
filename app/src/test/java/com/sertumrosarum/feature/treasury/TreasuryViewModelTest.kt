package com.sertumrosarum.feature.treasury

import androidx.lifecycle.SavedStateHandle
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.treasury.*
import org.junit.Assert.*
import org.junit.Test

class TreasuryViewModelTest {
    private fun savedCopy(handle: SavedStateHandle) = SavedStateHandle(handle.keys().associateWith { handle.get<Any?>(it) })
    private val ave = TreasuryCatalog.find("ave-maria")!!
    @Test fun `full reading state restores from a small identifier bundle`() {
        val saved = SavedStateHandle(); val original = TreasuryViewModel(saved)
        original.search("maria"); original.selectCategory(PrayerCategory.MARIAN); original.openPrayer(ave)
        original.selectLanguage(PrayerLanguage.BILINGUAL); original.toggleVerse(0); original.openTerm(ave.annotations[0])
        val restored = TreasuryViewModel(savedCopy(saved))
        assertEquals(original.state.value, restored.state.value)
        assertTrue(saved.keys().all { saved.get<Any?>(it) !is TreasuryPrayer })
        restored.closeTerm(); restored.backToLibrary()
        assertEquals("maria", restored.state.value.query)
        assertEquals(PrayerCategory.MARIAN, restored.state.value.category)
        restored.openPrayer(TreasuryCatalog.find("ato-de-fe")!!)
        assertEquals(PrayerLanguage.BILINGUAL, restored.state.value.language)
        assertNull(restored.state.value.term)
    }
    @Test fun `study selection restores and switching sets closes stale mystery`() {
        val saved = SavedStateHandle(); val original = TreasuryViewModel(saved)
        original.openMysteries(); original.selectStudySet(MysterySet.GLORIOUS); original.openStudyMystery(4)
        val restored = TreasuryViewModel(savedCopy(saved))
        assertEquals(original.state.value, restored.state.value)
        restored.selectStudySet(MysterySet.JOYFUL)
        assertNull(restored.state.value.studyMystery)
        restored.openStudyMystery(999)
        assertNull(restored.state.value.studyMystery)
    }
    @Test fun `invalid restored ids enums and indexes safely fall back`() {
        val model = TreasuryViewModel(SavedStateHandle(mapOf("place" to "READING", "reading" to "deleted", "language" to "unknown", "studyMystery" to -1, "openVerse" to Int.MAX_VALUE)))
        assertEquals(TreasuryPlace.LIBRARY, model.state.value.place)
        assertEquals(PrayerLanguage.PORTUGUESE, model.state.value.language)
        assertNull(model.state.value.openVerse); assertNull(model.state.value.studyMystery)
        model.openPrayer(ave); model.toggleVerse(-1); model.openTerm(PrayerTermAnnotation("foreign", "", ""))
        assertNull(model.state.value.openVerse); assertNull(model.state.value.term)
    }
}
