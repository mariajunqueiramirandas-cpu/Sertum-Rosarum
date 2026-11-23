package com.sertumrosarum.domain.prayer

/** A Saint or Doctor of the Church illuminating the mystery from Catholic tradition. */
data class SaintVoice(
    val saint: String,
    val work: String,
    val reflection: String,
)

/** A biblical or spiritual symbol inside the scene to hold in quiet contemplation. */
data class MysterySymbol(
    val name: String,
    val meaning: String,
)

/**
 * Deep contemplative and catechetical study layer for each of the twenty Mysteries of the Rosary.
 * Complements the concise live-prayer [MysteryContemplation] without cluttering active prayer.
 */
data class MysteryStudy(
    val scriptureTitle: String,
    /** Extended narrative synthesis rooted in the Gospel pericope, clearly marked as editorial study text. */
    val scriptureReading: String,
    /** First-century Jewish customs, geography, and Old Testament typology fulfilled in the mystery. */
    val historicalContext: String,
    /** Catechism-anchored theological reflection. */
    val theologicalDepth: String,
    val catechismRefs: String,
    val saintsVoices: List<SaintVoice>,
    val symbols: List<MysterySymbol>,
    val reflectionQuestions: List<String>,
    val closingPrayer: String,
) {
    val searchableText: String get() = buildString {
        append(scriptureTitle).append(' ').append(scriptureReading).append(' ')
        append(historicalContext).append(' ').append(theologicalDepth).append(' ').append(catechismRefs).append(' ')
        saintsVoices.forEach { append(it.saint).append(' ').append(it.work).append(' ').append(it.reflection).append(' ') }
        symbols.forEach { append(it.name).append(' ').append(it.meaning).append(' ') }
        reflectionQuestions.forEach { append(it).append(' ') }
        append(closingPrayer)
    }
}

object MysteryStudyCatalog {
    private val all: Map<Pair<MysterySet, Int>, MysteryStudy> by lazy {
        joyfulLuminousStudies + sorrowfulGloriousStudies
    }

    fun studyFor(set: MysterySet, decade: Int): MysteryStudy = all.getValue(set to decade)

    fun allStudies(): List<Triple<MysterySet, Int, MysteryStudy>> =
        MysterySet.entries.flatMap { set ->
            set.mysteries.indices.map { index -> Triple(set, index, studyFor(set, index)) }
        }
}
