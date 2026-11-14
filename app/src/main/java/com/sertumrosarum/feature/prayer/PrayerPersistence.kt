package com.sertumrosarum.feature.prayer

import com.sertumrosarum.data.garden.*
import com.sertumrosarum.domain.prayer.*
import com.sertumrosarum.feature.guides.PrayerGuide
import java.io.StringReader
import java.io.StringWriter
import java.util.Properties

data class OfferedIntention(val id: String, val title: String, val thanksgiving: Boolean = false)

/** A Java-standard, escaped text format keeps session serialization independent of Android APIs. */
internal fun PrayerUiState.snapshot(): String = StringWriter().also { writer ->
    Properties().apply {
        setProperty("formatVersion", "1"); setProperty("revision", revision.toString())
        setProperty("guide", sessionGuide.name); setProperty("set", sessionSet.name); setProperty("mode", sessionMode.name)
        setProperty("index", stepIndex.toString()); setProperty("started", requireNotNull(startedAt).toString())
        finishedAt?.let { setProperty("finished", it.toString()) }; setProperty("id", requireNotNull(sessionId))
        setProperty("eyes", eyesClosed.toString()); setProperty("meditated", meditated.toString())
        setProperty("count", offeredIntentions.size.toString())
        offeredIntentions.forEachIndexed { i, item -> setProperty("id.$i", item.id); setProperty("title.$i", item.title); setProperty("thanks.$i", item.thanksgiving.toString()) }
    }.store(writer, null)
}.toString()

internal inline fun <reified T : Enum<T>> enumOr(value: String?, fallback: T): T = enumValues<T>().firstOrNull { it.name == value } ?: fallback

internal fun restorePrayer(snapshot: String, base: PrayerUiState): PrayerUiState? = runCatching {
    val p = Properties().apply { load(StringReader(snapshot)) }
    require(p.getProperty("formatVersion") == "1")
    val guide = enumOr(p.getProperty("guide"), PrayerGuide.TRADITIONAL)
    val set = enumOr(p.getProperty("set"), base.selectedSet)
    val count = p.getProperty("count").toInt().also { require(it in 0..10000) }
    base.copy(sessionId = requireNotNull(p.getProperty("id")).also { require(it.isNotBlank()) }, revision = p.getProperty("revision").toLong().coerceAtLeast(0), sessionGuide = guide, sessionSet = set,
        sessionMode = enumOr(p.getProperty("mode"), base.selectedMode),
        stepIndex = p.getProperty("index").toInt().coerceIn(0, guide.script.build(set).lastIndex),
        startedAt = p.getProperty("started").toLong(), finishedAt = p.getProperty("finished")?.toLong(),
        eyesClosed = p.getProperty("eyes").toBoolean(), meditated = p.getProperty("meditated").toBoolean(),
        offeredIntentions = (0 until count).map { i -> OfferedIntention(requireNotNull(p.getProperty("id.$i")), requireNotNull(p.getProperty("title.$i")), p.getProperty("thanks.$i").toBoolean()) })
}.getOrNull()

internal suspend fun GardenRepository.persistPrayer(state: PrayerUiState) {
    storePrayerPreferences(state.themeMode.name, state.language.name, state.meditated, state.hapticsEnabled, state.selectedMode.name)
    val id = state.sessionId ?: return
    val started = state.startedAt ?: return
    saveCrown(PrayerSessionEntity(id, started, state.finishedAt, state.sessionGuide.name, state.sessionSet.name, state.sessionMode.name,
        state.stepIndex, state.groupCount, if (state.finishedAt == null) "ACTIVE" else "COMPLETED", state.snapshot()),
        state.offeredIntentions.map { SessionIntentionEntity(id, it.id, it.title, it.thanksgiving) })
}
