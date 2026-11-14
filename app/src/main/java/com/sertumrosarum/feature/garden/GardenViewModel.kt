package com.sertumrosarum.feature.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sertumrosarum.data.garden.*
import com.sertumrosarum.feature.prayer.OfferedIntention
import kotlinx.coroutines.flow.*
import java.util.UUID

val intentionCategories = listOf("Família", "Saúde", "Conversão", "Trabalho", "Falecidos", "Ação de graças")

data class GardenState(
    val intentions: List<IntentionEntity> = emptyList(), val sessions: List<PrayerSessionEntity> = emptyList(),
    val offerings: List<SessionIntentionEntity> = emptyList(), val journal: List<JournalEntry> = emptyList(),
    val bookmarks: List<BookmarkEntity> = emptyList(), val learning: List<LearningEntity> = emptyList(),
)

class GardenViewModel(val repository: GardenRepository) : ViewModel() {
    private val dao = repository.dao
    val state = combine(dao.intentions(), dao.sessions(), dao.offerings(), dao.journal(), dao.bookmarks()) { intentions, sessions, offerings, journal, bookmarks ->
        GardenState(intentions, sessions, offerings, journal, bookmarks)
    }.combine(dao.learning()) { state, learning -> state.copy(learning = learning) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GardenState())
    private val pendingIntentions = mutableMapOf<String, IntentionEntity>()
    private val selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selection = selectedIds.asStateFlow()
    fun toggleIntention(id: String) { selectedIds.value = if (id in selectedIds.value) selectedIds.value - id else selectedIds.value + id }
    fun selected(): List<OfferedIntention> = (state.value.intentions + pendingIntentions.values).associateBy { it.id }.values.filter { !it.archived && it.id in selectedIds.value }.map { OfferedIntention(it.id, it.title, it.thanksgiving) }
    fun saveIntention(title: String, description: String, category: String, existing: IntentionEntity? = null, thanksgiving: Boolean = existing?.thanksgiving ?: false, answer: String = existing?.answeredNote.orEmpty(), select: Boolean = false) {
        if (title.isBlank()) return
        val value = (existing ?: IntentionEntity(UUID.randomUUID().toString(), "", createdAt = System.currentTimeMillis())).copy(
            title = title.trim().take(120), description = description.trim().take(4000), category = category.takeIf { it in intentionCategories } ?: "Família", thanksgiving = thanksgiving, answeredNote = answer.trim().take(4000))
        pendingIntentions[value.id] = value
        repository.enqueue { dao.saveIntention(value) }
        if (select) selectedIds.value += value.id
    }
    fun archive(value: IntentionEntity) { pendingIntentions[value.id] = value.copy(archived = !value.archived); repository.enqueue { dao.saveIntention(value.copy(archived = !value.archived)) }; selectedIds.value -= value.id }
    fun journal(text: String, sessionId: String? = null, existing: JournalEntry? = null) {
        if (text.isBlank()) return
        val value = (existing ?: JournalEntry(UUID.randomUUID().toString(), System.currentTimeMillis(), "", sessionId)).copy(text = text.trim().take(8000))
        repository.enqueue { dao.saveJournal(value) }
    }
    fun deleteJournal(id: String) { repository.enqueue { dao.deleteJournal(id) } }
    fun bookmark(kind: String, id: String) {
        repository.enqueue {
            val current = dao.bookmarks().first().any { it.kind == kind && it.itemId == id }
            if (current) dao.deleteBookmark(kind, id) else dao.saveBookmark(BookmarkEntity(kind, id, System.currentTimeMillis()))
        }
    }
    fun lesson(id: String, reflection: String? = null, complete: Boolean = false, practice: Boolean? = null) { repository.enqueue {
        val old = dao.lesson(id) ?: LearningEntity(id, System.currentTimeMillis())
        dao.saveLearning(old.copy(openedAt = System.currentTimeMillis(), completedAt = old.completedAt ?: System.currentTimeMillis().takeIf { complete },
            reflection = reflection?.take(8000) ?: old.reflection, practiceDone = practice ?: old.practiceDone))
    } }
    companion object {
        fun factory(repository: GardenRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST") override fun <T : ViewModel> create(modelClass: Class<T>): T = GardenViewModel(repository) as T
        }
    }
}
