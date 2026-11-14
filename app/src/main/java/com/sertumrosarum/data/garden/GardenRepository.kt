package com.sertumrosarum.data.garden

import android.content.Context
import androidx.datastore.preferences.core.*
import java.io.File
import androidx.room.Room
import androidx.room.withTransaction
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.UUID


data class PersonalPreferences(
    val theme: String = "SYSTEM", val prayerLanguage: String = "PORTUGUESE", val readingLanguage: String = "PORTUGUESE",
    val meditated: Boolean = false, val haptics: Boolean = true, val mode: String = "SOLO", val welcomed: Boolean = false,
)

/** One ordered writer, owned by the application rather than an activity or a disappearing screen. */
class GardenRepository(val database: SertumDatabase, private val store: androidx.datastore.core.DataStore<Preferences>, private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)) {
    val dao = database.garden()
    private sealed interface Work {
        data class Write(val action: suspend () -> Unit) : Work
        data class Barrier(val done: CompletableDeferred<Boolean>) : Work
        data object Retry : Work
    }
    private val writes = Channel<Work>(Channel.UNLIMITED)
    private val mutableError = MutableStateFlow<String?>(null)
    val error = mutableError.asStateFlow()
    init { scope.launch {
        val pending = ArrayDeque<suspend () -> Unit>()
        suspend fun drain() {
            while (pending.isNotEmpty()) {
                try { pending.first().invoke(); pending.removeFirst() }
                catch (e: Exception) { if (e is CancellationException) throw e; break }
            }
            mutableError.value = if (pending.isEmpty()) null else "Uma alteração ainda não foi guardada. Tente novamente antes de fechar o aplicativo."
        }
        for (work in writes) when (work) {
            is Work.Write -> { val blocked = pending.isNotEmpty(); pending.addLast(work.action); if (!blocked) drain() }
            is Work.Barrier -> work.done.complete(pending.isEmpty())
            Work.Retry -> drain()
        }
    } }
    fun enqueue(write: suspend () -> Unit) { check(writes.trySend(Work.Write(write)).isSuccess) }
    fun retry() { check(writes.trySend(Work.Retry).isSuccess) }
    suspend fun flush() {
        val done = CompletableDeferred<Boolean>()
        check(writes.trySend(Work.Barrier(done)).isSuccess)
        check(done.await()) { "There are unsaved garden changes" }
    }
    fun close() { writes.close(); scope.cancel() }
    suspend fun preferences(): PersonalPreferences {
        val p = store.data.first()
        return PersonalPreferences(p[stringPreferencesKey("theme")] ?: "SYSTEM", p[stringPreferencesKey("prayerLanguage")] ?: "PORTUGUESE",
            p[stringPreferencesKey("readingLanguage")] ?: "PORTUGUESE", p[booleanPreferencesKey("meditated")] ?: false,
            p[booleanPreferencesKey("haptics")] ?: true, p[stringPreferencesKey("mode")] ?: "SOLO", p[booleanPreferencesKey("welcomed")] ?: false)
    }
    suspend fun storePrayerPreferences(theme: String, language: String, meditated: Boolean, haptics: Boolean, mode: String) { store.edit {
        it[stringPreferencesKey("theme")] = theme; it[stringPreferencesKey("prayerLanguage")] = language
        it[booleanPreferencesKey("meditated")] = meditated; it[booleanPreferencesKey("haptics")] = haptics; it[stringPreferencesKey("mode")] = mode
    } }
    suspend fun storeReadingLanguage(value: String) { store.edit { it[stringPreferencesKey("readingLanguage")] = value } }
    suspend fun welcome() { store.edit { it[booleanPreferencesKey("welcomed")] = true } }
    suspend fun savePreferences(value: PersonalPreferences) { store.edit {
        it[stringPreferencesKey("theme")] = value.theme; it[stringPreferencesKey("prayerLanguage")] = value.prayerLanguage
        it[stringPreferencesKey("readingLanguage")] = value.readingLanguage; it[booleanPreferencesKey("meditated")] = value.meditated
        it[booleanPreferencesKey("haptics")] = value.haptics; it[stringPreferencesKey("mode")] = value.mode; it[booleanPreferencesKey("welcomed")] = value.welcomed
    } }
    suspend fun saveCrown(session: PrayerSessionEntity, intentions: List<SessionIntentionEntity>) = database.withTransaction {
        dao.retireOtherSessions(session.id); dao.saveSession(session); dao.saveOfferings(intentions)
    }
    fun feedback(id: String, text: String) { if (text.isNotBlank()) enqueue { dao.saveFeedback(ContentFeedback(UUID.randomUUID().toString(), id, text.trim().take(4000), System.currentTimeMillis())) } }
    companion object {
        internal fun create(context: Context): GardenRepository {
            val app = context.applicationContext
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            val store = PreferenceDataStoreFactory.create(scope = scope, corruptionHandler = androidx.datastore.core.handlers.ReplaceFileCorruptionHandler { emptyPreferences() }) { File(app.filesDir, "datastore/sertum_preferences.preferences_pb") }
            return GardenRepository(Room.databaseBuilder(app, SertumDatabase::class.java, "sertum-garden.db").build(), store, scope)
        }
    }
}
