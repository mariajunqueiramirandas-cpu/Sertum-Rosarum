package com.sertumrosarum.data.garden

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "intentions")
data class IntentionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val category: String = "Família",
    val createdAt: Long,
    val archived: Boolean = false,
    val thanksgiving: Boolean = false,
    val answeredNote: String = "",
)

@Entity(tableName = "sessions", indices = [Index("startedAt")])
data class PrayerSessionEntity(
    @PrimaryKey val id: String,
    val startedAt: Long,
    val finishedAt: Long? = null,
    val guide: String,
    val mysterySet: String,
    val mode: String,
    val stepIndex: Int,
    val groupCount: Int,
    val status: String = "ACTIVE",
    val snapshot: String,
)

/** Titles are snapshots: editing an intention never rewrites an offered crown. */
@Entity(tableName = "session_intentions", primaryKeys = ["sessionId", "intentionId"],
    foreignKeys = [ForeignKey(entity = PrayerSessionEntity::class, parentColumns = ["id"], childColumns = ["sessionId"], onDelete = ForeignKey.CASCADE)])
data class SessionIntentionEntity(val sessionId: String, val intentionId: String, val title: String, val thanksgiving: Boolean)

@Entity(tableName = "journal")
data class JournalEntry(@PrimaryKey val id: String, val createdAt: Long, val text: String, val sessionId: String? = null)

@Entity(tableName = "bookmarks", primaryKeys = ["kind", "itemId"])
data class BookmarkEntity(val kind: String, val itemId: String, val createdAt: Long)

@Entity(tableName = "learning")
data class LearningEntity(@PrimaryKey val lessonId: String, val openedAt: Long, val completedAt: Long? = null, val reflection: String = "", val practiceDone: Boolean = false)

@Entity(tableName = "feedback")
data class ContentFeedback(@PrimaryKey val id: String, val contentId: String, val text: String, val createdAt: Long)

@Dao
interface GardenDao {
    @Query("SELECT * FROM intentions ORDER BY createdAt DESC") fun intentions(): Flow<List<IntentionEntity>>
    @Query("SELECT * FROM sessions ORDER BY startedAt DESC") fun sessions(): Flow<List<PrayerSessionEntity>>
    @Query("SELECT * FROM session_intentions") fun offerings(): Flow<List<SessionIntentionEntity>>
    @Query("SELECT * FROM journal ORDER BY createdAt DESC") fun journal(): Flow<List<JournalEntry>>
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC") fun bookmarks(): Flow<List<BookmarkEntity>>
    @Query("SELECT * FROM learning ORDER BY openedAt DESC") fun learning(): Flow<List<LearningEntity>>
    @Upsert suspend fun saveIntention(value: IntentionEntity)
    @Upsert suspend fun saveSession(value: PrayerSessionEntity)
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun saveOfferings(values: List<SessionIntentionEntity>)
    @Upsert suspend fun saveJournal(value: JournalEntry)
    @Query("DELETE FROM journal WHERE id = :id") suspend fun deleteJournal(id: String)
    @Upsert suspend fun saveBookmark(value: BookmarkEntity)
    @Query("DELETE FROM bookmarks WHERE kind = :kind AND itemId = :id") suspend fun deleteBookmark(kind: String, id: String)
    @Upsert suspend fun saveLearning(value: LearningEntity)
    @Insert suspend fun saveFeedback(value: ContentFeedback)
    @Query("SELECT * FROM learning WHERE lessonId = :id") suspend fun lesson(id: String): LearningEntity?
    @Query("SELECT * FROM sessions WHERE status = 'ACTIVE' ORDER BY startedAt DESC LIMIT 1") suspend fun activeSession(): PrayerSessionEntity?
    @Query("UPDATE sessions SET status = 'REPLACED' WHERE status = 'ACTIVE' AND id != :id") suspend fun retireOtherSessions(id: String)
    @Query("SELECT COUNT(*) FROM feedback") suspend fun feedbackCount(): Int
}

@Database(entities = [IntentionEntity::class, PrayerSessionEntity::class, SessionIntentionEntity::class, JournalEntry::class, BookmarkEntity::class, LearningEntity::class, ContentFeedback::class], version = 1, exportSchema = true)
abstract class SertumDatabase : RoomDatabase() { abstract fun garden(): GardenDao }
