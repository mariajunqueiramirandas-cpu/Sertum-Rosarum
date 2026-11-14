package com.sertumrosarum.domain.treasury

import com.sertumrosarum.domain.prayer.FixedPrayer
import java.text.Normalizer
import java.util.Locale

/** Immutable, offline catalog. The importer is an editorial tool; the app never scrapes the web. */
object TreasuryCatalog {
    val prayers: List<TreasuryPrayer> get() = GeneratedTreasury.prayers
    private val byId by lazy { prayers.associateBy { it.id } }
    private val byFixed by lazy { prayers.filter { it.fixedPrayer != null }.associateBy { it.fixedPrayer } }
    private val search by lazy { TreasurySearch(prayers) }
    fun find(id: String?): TreasuryPrayer? = byId[id]
    fun latinFor(prayer: FixedPrayer?): TreasuryPrayer? = byFixed[prayer]?.takeIf { it.latin != null }
    fun search(query: String, category: PrayerCategory? = null): List<TreasuryPrayer> = search.find(query, category)
}

/** Accent/case/ligature insensitive AND search, ordered by title relevance then editorial order. */
class TreasurySearch(catalog: List<TreasuryPrayer>) {
    private data class Entry(val prayer: TreasuryPrayer, val title: String, val body: String)
    private val entries = catalog.map { prayer ->
        Entry(prayer, fold("${prayer.title} ${prayer.latinTitle.orEmpty()}"), fold(buildString {
            append(prayer.title); append(' '); append(prayer.latinTitle.orEmpty()); append(' ')
            append(prayer.summary); append(' '); append(prayer.portuguese); append(' '); append(prayer.latin.orEmpty())
            append(' '); append(prayer.category.label); append(' '); append(prayer.keywords.joinToString(" "))
            append(' '); append(prayer.context)
            prayer.annotations.forEach { append(' '); append(it.term); append(' '); append(it.origin); append(' '); append(it.today); append(' '); append(it.scriptureReference.orEmpty()); append(' '); append(it.catechism.orEmpty()) }
        }))
    }
    fun find(query: String, category: PrayerCategory? = null): List<TreasuryPrayer> {
        val words = fold(query.take(300)).split(' ').filter { it.isNotEmpty() }
        return entries.filter { (category == null || it.prayer.category == category) && words.all { word -> word in it.body } }
            .sortedByDescending { entry -> words.count { it in entry.title } }
            .map { it.prayer }
    }
    companion object {
        internal fun fold(text: String): String = Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "").lowercase(Locale.ROOT).replace("æ", "ae").replace("œ", "oe")
            .replace(Regex("[^\\p{L}\\p{N}]+"), " ").trim()
    }
}
