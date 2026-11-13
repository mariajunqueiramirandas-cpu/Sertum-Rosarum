package com.sertumrosarum.domain.treasury

import java.text.Normalizer

/** Original UTF-16 offsets, safe for Compose links, including every occurrence of an expression. */
data class PrayerTermRange(val annotation: PrayerTermAnnotation, val range: IntRange)

object PrayerAnnotationParser {
    private val combiningMarks = setOf(Character.NON_SPACING_MARK.toInt(), Character.COMBINING_SPACING_MARK.toInt(), Character.ENCLOSING_MARK.toInt())
    /**
     * Prefer the longest expression at an overlap; ties follow editorial order. Match complete words
     * case-insensitively across spaces/newlines, while preserving the original visible text.
     * Accents remain significant: “avó” must never annotate “avô”. No markup is executed or removed.
     */
    fun parse(text: String, annotations: List<PrayerTermAnnotation>): List<PrayerTermRange> {
        // Normalize canonical equivalents while retaining offsets into the untouched prayer.
        val starts = mutableListOf<Int>()
        val ends = mutableListOf<Int>()
        val canonical = buildString {
            var index = 0
            while (index < text.length) {
                val start = index
                index += Character.charCount(text.codePointAt(index))
                while (index < text.length && Character.getType(text.codePointAt(index)) in combiningMarks) {
                    index += Character.charCount(text.codePointAt(index))
                }
                val cluster = Normalizer.normalize(text.substring(start, index), Normalizer.Form.NFC)
                append(cluster)
                repeat(cluster.length) { starts += start; ends += index }
            }
        }
        val candidates = annotations.flatMapIndexed { order, annotation ->
            val words = Normalizer.normalize(annotation.term, Normalizer.Form.NFC).trim().split(Regex("[\\s\\p{Zs}]+"))
            if (words.all { it.isEmpty() }) emptyList() else {
                val expression = words.joinToString("[\\s\\p{Zs}]+") { Regex.escape(it) }
                Regex("(?<![\\p{L}\\p{N}\\p{M}])$expression(?![\\p{L}\\p{N}\\p{M}])", RegexOption.IGNORE_CASE)
                    .findAll(canonical).map { Triple(annotation, starts[it.range.first] until ends[it.range.last], order) }.toList()
            }
        }.sortedWith(compareByDescending<Triple<PrayerTermAnnotation, IntRange, Int>> { it.second.count() }.thenBy { it.third }.thenBy { it.second.first })
        val chosen = mutableListOf<PrayerTermRange>()
        candidates.forEach { (annotation, range) ->
            if (chosen.none { range.first <= it.range.last && it.range.first <= range.last }) chosen += PrayerTermRange(annotation, range)
        }
        return chosen.sortedBy { it.range.first }
    }
}
