package com.sertumrosarum.domain.lumen

import com.sertumrosarum.domain.prayer.ChapletContemplations
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.prayer.PrayerStep
import com.sertumrosarum.domain.treasury.TreasuryCatalog
import com.sertumrosarum.domain.treasury.TreasurySearch
import com.sertumrosarum.feature.guides.PrayerGuide
import java.net.URI
import java.util.UUID

/** A complete prayer that can be read and saved independently of its conversation. */
data class CatechistPrayer(
    val id: String,
    val title: String,
    val description: String,
    val body: String,
    val attribution: String,
    val sourceUrl: String? = null,
    val guideName: String? = null,
)

internal fun webUriOrNull(value: String): URI? = runCatching { URI(value) }.getOrNull()?.takeIf {
    !it.host.isNullOrEmpty() && it.userInfo == null &&
        (it.scheme.equals("https", ignoreCase = true) || it.scheme.equals("http", ignoreCase = true))
}

internal object CatechistPrayerCatalog {
    private val chaplets = PrayerGuide.entries.filter { !it.isRosary }
    private val treasuryTitles by lazy { TreasuryCatalog.prayers.associateBy { TreasurySearch.fold(it.title) } }
    private val aliases = mapOf(
        PrayerGuide.TEARS to listOf("coroa das lagrimas", "terco das lagrimas", "coroa de nossa senhora das lagrimas"),
        PrayerGuide.SAINT_MICHAEL to listOf("coroa angelica", "terco de sao miguel arcanjo"),
        PrayerGuide.SEVEN_SORROWS to listOf("coroa das sete dores", "terco de nossa senhora das dores"),
        PrayerGuide.FRANCISCAN to listOf("coroa das sete alegrias"),
    )
    private val requestPrefix = Regex(
        "^(?:(?:eu )?(?:quero|queria|prefiro|gostaria de) )?" +
            "(?:(?:como rezar|rezar|orar|ler|me (?:mostra|mostre|manda|mande|da|de|envia|envie)|(?:o )?texto (?:da|do|de)) )?" +
            "(?:(?:a|o|uma|um) )?",
    )
    private val requestSuffix = Regex("(?: (?:complet[ao]|inteir[ao]|por favor|hoje))+$")
    private val prayerWords = Regex("""\b(orar|rezar|oracao|oracoes|prece|preces|devocao|devocoes)\b""")
    private val discoveryWords = Regex("""\b(diferent\w*|inusua\w*|rar[ao]s?|especi(?:al|ais|fic\w*)|nov[ao]s?)\b""")
    private val otherLanguageRequest = Regex("""\b(em|versao|texto) (latim|ingles|espanhol|frances|italiano|alemao|grego|outra lingua|outro idioma)\b""")

    fun requestedPrayer(text: String): CatechistPrayer? {
        val query = TreasurySearch.fold(text).replace(requestSuffix, "")
        for (name in listOf(query, query.replaceFirst(requestPrefix, ""))) {
            val guide = chaplets.firstOrNull { name == TreasurySearch.fold(it.title) || name in aliases[it].orEmpty() }
            if (guide != null) return fromGuide(guide.name)
            treasuryTitles[name]?.let { return fromTreasury(it.id) }
        }
        return null
    }

    fun curatedOptions(text: String): List<CatechistPrayer> {
        val query = TreasurySearch.fold(text)
        if (!prayerWords.containsMatchIn(query) || !discoveryWords.containsMatchIn(query) || otherLanguageRequest.containsMatchIn(query)) {
            return emptyList()
        }
        return listOfNotNull(fromGuide(PrayerGuide.TEARS.name), fromTreasury("oracao-do-incenso"), fromGuide(PrayerGuide.VIA_SACRA.name))
    }

    fun fromTreasury(id: String): CatechistPrayer? = TreasuryCatalog.find(id)?.let { prayer ->
        CatechistPrayer(
            id = "treasury:${prayer.id}",
            title = prayer.title,
            description = prayer.summary,
            body = prayer.portuguese,
            attribution = prayer.source ?: "Do Tesouro",
            sourceUrl = prayer.sourceUrls.firstOrNull { webUriOrNull(it) != null },
        )
    }

    fun fromGuide(name: String): CatechistPrayer? {
        val guide = PrayerGuide.byNameOrNull(name) ?: return null
        val chaplet = guide.script.chaplet ?: return null
        val body = buildString {
            appendLine("## Abertura")
            chaplet.introduction.forEach { appendPrayer(it) }
            chaplet.groups.forEachIndexed { index, group ->
                val mystery = group.mystery ?: ChapletContemplations.mysteryFor(guide.script, index)
                val title = mystery?.title ?: "${index + 1}ª ${chaplet.groupLabel}"
                appendLine("## $title")
                val scene = ChapletContemplations.forStep(guide.script, MysterySet.JOYFUL, index)?.scene
                if (scene != null) appendLine("\n> $scene\n")
                group.beforeLarge.forEach { appendPrayer(it.title, it.text, it.response) }
                appendPrayer(group.largeBead.title, group.largeBead.text, group.largeBead.response)
                val smallTitle = group.smallBead.title + if (group.beadCount > 1) " · ${group.beadCount} vezes" else ""
                appendPrayer(smallTitle, group.smallBead.text, group.smallBead.response)
                group.after.forEach { appendPrayer(it.title, it.text) }
            }
            appendLine("## Encerramento")
            chaplet.closing.forEach { appendPrayer(it) }
        }.trimEnd()
        return CatechistPrayer("guide:${guide.name}", guide.title, guide.summary, body, "Roteiro do aplicativo", guideName = guide.name)
    }

    private fun StringBuilder.appendPrayer(step: PrayerStep) {
        appendPrayer(step.intention?.let { "${step.title} · $it" } ?: step.title, step.text, step.reading?.response)
    }

    private fun StringBuilder.appendPrayer(title: String, text: String, response: String? = null) {
        appendLine("\n### $title\n\n$text")
        response?.let { appendLine("\n**R.** $it") }
        appendLine()
    }

    fun fromModel(fields: Map<*, *>, sources: List<CatechistSource>): CatechistPrayer? {
        val kind = fields["kind"] as? String ?: return null
        val id = (fields["id"] as? String)?.trim().orEmpty()
        if (kind == "treasury") return fromTreasury(id)
        if (kind == "guide") return fromGuide(id)

        val title = fields.text("title", 160) ?: return null
        val body = fields.text("body", 48_000) ?: return null
        val requestedSourceUrl = fields.text("sourceUrl", 2_048)
        val source = sources.firstOrNull { it.url == requestedSourceUrl && webUriOrNull(it.url) != null }
        val attribution = when (kind) {
            "published" -> source?.title ?: return null
            "translation" -> "Tradução pelo Lumen · ${source?.title ?: return null}"
            "adaptation" -> source?.let { "Adaptação pelo Lumen · ${it.title}" } ?: "Adaptação pelo Lumen"
            "original" -> "Composta pelo Lumen"
            else -> return null
        }
        val language = fields.text("language", 40)
        val version = listOfNotNull(language, attribution).joinToString(" · ")
        val key = UUID.nameUUIDFromBytes("$kind\n$title\n${source?.url.orEmpty()}\n$body".toByteArray(Charsets.UTF_8))
        return CatechistPrayer("lumen:$key", title, fields.text("description", 220).orEmpty(), body, version, source?.url)
    }

    private fun Map<*, *>.text(key: String, maxLength: Int): String? =
        (get(key) as? String)?.trim()?.takeIf { it.length in 1..maxLength }
}
