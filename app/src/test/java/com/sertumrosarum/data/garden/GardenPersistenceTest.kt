package com.sertumrosarum.data.garden

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.sertumrosarum.domain.prayer.PrayerMode
import com.sertumrosarum.domain.treasury.PrayerLanguage
import com.sertumrosarum.feature.garden.GardenViewModel
import com.sertumrosarum.feature.guides.PrayerGuide
import com.sertumrosarum.feature.prayer.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class GardenPersistenceTest {
    private lateinit var db: SertumDatabase
    private lateinit var repository: GardenRepository
    private lateinit var storeScope: CoroutineScope
    @Before fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SertumDatabase::class.java).build()
        storeScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val file = File.createTempFile("garden-test-", ".preferences_pb").apply { delete() }
        repository = GardenRepository(db, PreferenceDataStoreFactory.create(scope = storeScope) { file })
    }
    @After fun cleanup() = runBlocking { repository.flush(); repository.close(); db.close(); storeScope.cancel() }

    @Test fun `every bead and backwards movement restore without Android saved state`() = runBlocking {
        val model = PrayerSessionViewModel(SavedStateHandle())
        model.connect(repository)
        model.setTheme(ThemeMode.VIGIL); model.selectLanguage(PrayerLanguage.BILINGUAL); model.toggleHaptics(); model.selectPrayerMode(PrayerMode.GROUP)
        model.start(PrayerGuide.CATECHETICAL, listOf(OfferedIntention("mother", "Pela minha mãe")))
        repeat(30) { model.next() }
        model.previous()
        val expected = model.state.value
        repository.flush()
        val cold = PrayerSessionViewModel(SavedStateHandle())
        cold.connect(repository)
        assertEquals(expected.stepIndex, cold.state.value.stepIndex)
        assertEquals(expected.sessionId, cold.state.value.sessionId)
        assertEquals(PrayerMode.GROUP, cold.state.value.sessionMode)
        assertEquals(PrayerLanguage.BILINGUAL, cold.state.value.language)
        assertEquals(ThemeMode.VIGIL, cold.state.value.themeMode)
        assertFalse(cold.state.value.hapticsEnabled)
        assertEquals("Pela minha mãe", cold.state.value.offeredIntentions.single().title)
        assertEquals(SanctuaryScreen.HOME, cold.state.value.screen)
        cold.resume(); assertEquals(expected.stepIndex, cold.state.value.stepIndex)
    }
    @Test fun `an offered crown is idempotent retains intention snapshots and does not reopen as active`() = runBlocking {
        val model = PrayerSessionViewModel(SavedStateHandle()); model.connect(repository)
        repository.dao.saveIntention(IntentionEntity("family", "Família", createdAt = 1))
        model.start(PrayerGuide.TRADITIONAL, listOf(OfferedIntention("family", "Família", true)))
        repeat(model.state.value.sequence.size) { model.next() }
        model.returnHome(); model.selectTab(SanctuaryTab.GARDEN)
        repository.flush()
        assertEquals(1, repository.dao.sessions().first().count { it.status == "COMPLETED" })
        assertEquals(1, repository.dao.offerings().first().size)
        repository.dao.saveIntention(IntentionEntity("family", "Título editado", createdAt = 1, archived = true))
        assertEquals("Família", repository.dao.offerings().first().single().title)
        assertNull(repository.dao.activeSession())
        val cold = PrayerSessionViewModel(SavedStateHandle()); cold.connect(repository)
        assertFalse(cold.state.value.hasSession)
    }
    @Test fun `starting a replacement crown preserves history and only one resumable crown`() = runBlocking {
        val model = PrayerSessionViewModel(SavedStateHandle()); model.connect(repository)
        model.start(); repeat(6) { model.next() }; val old = model.state.value.sessionId
        model.returnHome(); model.start(); repository.flush()
        val sessions = repository.dao.sessions().first()
        assertEquals(2, sessions.size)
        assertEquals("REPLACED", sessions.single { it.id == old }.status)
        assertEquals(model.state.value.sessionId, repository.dao.activeSession()?.id)
    }
    @Test fun `disk revision wins over an earlier Android snapshot even after moving backward`() = runBlocking {
        val saved = SavedStateHandle()
        val model = PrayerSessionViewModel(saved); model.connect(repository); model.start(); repeat(20) { model.next() }
        val old = saved.get<String>("durableSnapshot")!!
        repeat(3) { model.previous() }; repository.flush()
        val restored = PrayerSessionViewModel(SavedStateHandle(mapOf("durableSnapshot" to old)))
        restored.connect(repository)
        assertEquals(model.state.value.stepIndex, restored.state.value.stepIndex)
    }
    @Test fun `bookmarks journal lesson reflections and reading preference persist independently`() = runBlocking {
        val model = GardenViewModel(repository)
        model.bookmark("prayer", "ave-maria"); model.journal("Uma gratidão", "crown-1")
        model.lesson("prayer", "Minha descoberta", complete = true, practice = true)
        repository.enqueue { repository.storeReadingLanguage("BILINGUAL") }; repository.flush()
        assertEquals("ave-maria", repository.dao.bookmarks().first().single().itemId)
        assertEquals("crown-1", repository.dao.journal().first().single().sessionId)
        val progress = repository.dao.lesson("prayer")!!
        assertTrue(progress.practiceDone); assertNotNull(progress.completedAt); assertEquals("Minha descoberta", progress.reflection)
        assertEquals("BILINGUAL", repository.preferences().readingLanguage)
        model.bookmark("prayer", "ave-maria"); repository.flush()
        assertTrue(repository.dao.bookmarks().first().isEmpty())
    }
    @Test fun `a newly written intention is offered before the database observer emits`() = runBlocking {
        val model = GardenViewModel(repository)
        model.saveIntention("Une intention nouvelle", "", "Família", select = true)
        assertEquals("Une intention nouvelle", model.selected().single().title)
        repository.flush()
    }

    @Test fun `an invalid persisted snapshot does not crash or invent a prayer position`() = runBlocking {
        repository.dao.saveSession(PrayerSessionEntity("broken", 1, guide = "old", mysterySet = "old", mode = "old", stepIndex = 9999, groupCount = 5, snapshot = "{broken"))
        val model = PrayerSessionViewModel(SavedStateHandle()); model.connect(repository)
        assertFalse(model.state.value.hasSession)
        model.start(); repository.flush()
        assertEquals(model.state.value.sessionId, repository.dao.activeSession()?.id)
    }

    @Test fun `Room records survive closing and reopening the actual database file`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "reopen-${java.util.UUID.randomUUID()}.db"
        var disk = Room.databaseBuilder(context, SertumDatabase::class.java, name).build()
        try {
            disk.garden().saveIntention(IntentionEntity("lasting", "Uma intenção persistente", createdAt = 1, thanksgiving = true))
            disk.garden().saveJournal(JournalEntry("note", 2, "Uma memória persistente"))
            disk.close()
            disk = Room.databaseBuilder(context, SertumDatabase::class.java, name).build()
            assertEquals("Uma intenção persistente", disk.garden().intentions().first().single().title)
            assertTrue(disk.garden().intentions().first().single().thanksgiving)
            assertEquals("Uma memória persistente", disk.garden().journal().first().single().text)
        } finally { disk.close(); context.deleteDatabase(name) }
    }

    @Test fun `failed writes are visible and retried in original order`() = runBlocking {
        var shouldFail = true
        val events = mutableListOf<Int>()
        repository.enqueue { if (shouldFail) error("disk unavailable"); events += 1 }
        repository.enqueue { events += 2 }
        try { repository.flush(); fail("Expected a failed save") } catch (_: IllegalStateException) { }
        assertNotNull(repository.error.value); assertTrue(events.isEmpty())
        shouldFail = false; repository.retry(); repository.flush()
        assertEquals(listOf(1, 2), events); assertNull(repository.error.value)
    }
}
