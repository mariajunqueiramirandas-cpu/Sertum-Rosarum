package com.sertumrosarum.domain.lumen

import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.domain.liturgy.LiturgicalCalendar
import com.sertumrosarum.domain.prayer.MysteryContent
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.domain.prayer.MysteryStudyCatalog
import com.sertumrosarum.domain.treasury.PrayerCategory
import com.sertumrosarum.domain.treasury.TreasuryCatalog
import com.sertumrosarum.feature.guides.PrayerGuide
import java.time.LocalDate

/** Lightweight domain view of the user's Meu Jardim data for the agent's read tools. */
data class GardenIntentionSnapshot(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val thanksgiving: Boolean,
    val answeredNote: String,
    val archived: Boolean,
    val createdAtMillis: Long,
)

data class GardenCrownSnapshot(
    val id: String,
    val guideName: String,
    val mysterySet: String,
    val completedAtMillis: Long?,
    val durationSeconds: Long,
    val status: String,
)

data class GardenJournalSnapshot(
    val id: String,
    val text: String,
    val createdAtMillis: Long,
)

data class GardenSnapshot(
    val intentions: List<GardenIntentionSnapshot> = emptyList(),
    val recentCrowns: List<GardenCrownSnapshot> = emptyList(),
    val recentJournal: List<GardenJournalSnapshot> = emptyList(),
    val bookmarkedKeys: List<String> = emptyList(),
    val completedLessonIds: List<String> = emptyList(),
    val inProgressLessonIds: List<String> = emptyList(),
)

data class PrayerSessionSnapshot(
    val hasActiveSession: Boolean = false,
    val guideName: String? = null,
    val guideTitle: String? = null,
    val mysterySet: MysterySet = MysterySet.JOYFUL,
    val currentStepTitle: String? = null,
    val currentMysteryTitle: String? = null,
    val currentDecadeNumber: Int? = null,
    val currentBeadNumber: Int? = null,
    val meditated: Boolean = false,
    val language: String = "PORTUGUESE",
    val offeredIntentions: List<String> = emptyList(),
    val today: LocalDate = LocalDate.now(),
)

/**
 * Live bridge between the Lumen tool executor and the app's Room persistence + active prayer state.
 * Default no-op implementation keeps unit tests and headless contexts effortless.
 */
interface LumenToolBridge {
    fun readGarden(): GardenSnapshot = GardenSnapshot()
    fun readPrayerSession(): PrayerSessionSnapshot = PrayerSessionSnapshot()
    fun createIntention(title: String, description: String, category: String, thanksgiving: Boolean, selectForNextRosary: Boolean): String? = null
    fun markIntentionThanksgiving(intentionId: String, note: String): Boolean = false
    fun selectIntentionForNextRosary(intentionId: String): Boolean = false
    fun saveJournalReflection(text: String): Boolean = false
    fun bookmarkContent(kind: String, itemId: String): Boolean = false

    companion object {
        val EMPTY: LumenToolBridge = object : LumenToolBridge {}
    }
}

data class ToolExecutionOutput(
    val resultJson: String,
    val emittedAction: CatechistAction? = null,
    val traceLabel: String? = null,
    val interimReply: String? = null,
    val webSources: List<CatechistSource> = emptyList(),
)

/**
 * The function-calling tools that give the Catechist full
 * read/write grounding across Sertum Rosarum while keeping fixed Church prayers immutable.
 */
object LumenTools {
    internal const val SEND_INTERIM_REPLY = "send_interim_reply"
    internal const val SEARCH_WEB_PRAYERS = "search_web_prayers"

    internal fun parseInterimReply(argumentsJson: String): String? {
        if (!argumentsJson.trimEnd().endsWith('}')) return null
        val args = LumenJsonParser.parseValue(argumentsJson) as? Map<*, *> ?: return null
        val message = args["message"] as? String ?: return null
        return LumenGuardrails.sanitizeInput(message).takeIf { it.isNotEmpty() && it.length <= 300 }
    }

    fun liveProgressLabel(toolName: String, persona: CatechistPersona): String = when (toolName) {
        SEARCH_WEB_PRAYERS -> "${persona.displayName} está procurando orações na web…"
        "search_treasury", "read_treasury_prayer" -> "${persona.displayName} está consultando o Tesouro de Orações…"
        "search_catechism", "read_catechism_lesson" -> "${persona.displayName} está abrindo a Catequese…"
        "read_mystery_study" -> "${persona.displayName} está meditando o Mistério nas Escrituras…"
        "read_liturgy_and_guides" -> "${persona.displayName} está olhando a liturgia e os roteiros…"
        "read_my_garden" -> "${persona.displayName} está olhando o seu Jardim…"
        "read_current_prayer_session" -> "${persona.displayName} está vendo onde você parou no terço…"
        "build_custom_rosary" -> "${persona.displayName} está tecendo seu terço sob medida…"
        "suggest_prayer_guide" -> "${persona.displayName} está separando um roteiro para você…"
        "manage_intention" -> "${persona.displayName} está cuidando das suas intenções…"
        "save_journal_reflection" -> "${persona.displayName} está guardando no seu Diário…"
        "bookmark_content" -> "${persona.displayName} está salvando em Guardados…"
        "open_app_content" -> "${persona.displayName} está abrindo a leitura certa…"
        else -> persona.thinkingLine
    }

    /** Exact JSON array of tool definitions passed to Mistral `/v1/chat/completions` `"tools"`. */
    val mistralToolsJson: String = """
[
  {
    "type": "function",
    "function": {
      "name": "$SEND_INTERIM_REPLY",
      "description": "Post a short catechist message to the chat while you keep working. Call this first before a web search, a personalized Rosary, or a long sequence of tool calls. Describe the specific next step. Send another message only when you have a useful confirmed discovery to share.",
      "parameters": {
        "type": "object",
        "properties": {
          "message": { "type": "string", "description": "One or two short sentences in your catechist voice, in Brazilian Portuguese, up to 300 characters. Share a useful next step or a confirmed finding." }
        },
        "required": ["message"]
      }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "$SEARCH_WEB_PRAYERS",
      "description": "Find prayers, litanies, novenas, devotions, and published translations on the live web. Call send_interim_reply first. Search by public title, occasion, tradition, or language. The results include cited sources for the final reply.",
      "parameters": {
        "type": "object",
        "properties": {
          "query": { "type": "string", "description": "Public search terms only, such as a prayer title, occasion, tradition, or language. Omit personal names and details from the conversation or Meu Jardim." }
        },
        "required": ["query"]
      }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "search_treasury",
      "description": "Search all 61 bundled Catholic prayers (Portuguese & Latin) and 78 explained theological expressions in the app's Treasury.",
      "parameters": {
        "type": "object",
        "properties": {
          "query": { "type": "string", "description": "Keyword, prayer name, saint, Latin term, or life moment (e.g. 'angustia', 'Santa Teresa', 'Salve Rainha', 'comunhao', 'noite')" },
          "category": { "type": "string", "description": "Optional category filter: WAKING, NIGHT, DISTRESS, THANKSGIVING, COMMUNION, MARIAN" }
        },
        "required": ["query"]
      }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "read_treasury_prayer",
      "description": "Read the full Portuguese text, Latin text (when available), and 'Toque para Entender' theological explanations of a specific prayer from the Treasury.",
      "parameters": {
        "type": "object",
        "properties": {
          "prayerId": { "type": "string", "description": "Exact prayer id returned by search_treasury" }
        },
        "required": ["prayerId"]
      }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "search_catechism",
      "description": "Search all 16 full Catechism study chapters, 12 curated faith Q&As, and 4 practical journeys (Mass, Confession, Evening Review, Works of Mercy).",
      "parameters": {
        "type": "object",
        "properties": {
          "query": { "type": "string", "description": "Topic, question, sacrament, or keyword (e.g. 'confissao', 'eucaristia', 'sofrimento', 'maria', 'missa', 'perdao')" }
        },
        "required": ["query"]
      }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "read_catechism_lesson",
      "description": "Read the full teaching, Scripture references, Catechism (CIC) citations, and daily practice of one of the 16 Catechism chapters.",
      "parameters": {
        "type": "object",
        "properties": {
          "lessonId": { "type": "string", "description": "Exact lesson id from search_catechism (e.g. 'reconciliation', 'eucharist', 'prayer', 'rosary', 'church-mary')" }
        },
        "required": ["lessonId"]
      }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "read_mystery_study",
      "description": "Read the complete 4 dimensions (scene, meaning, fruit, practice, 10 bead lines) AND deep historical/Jewish context, Church Fathers & Saints voices, symbols, and CIC references for any of the 20 Rosary Mysteries.",
      "parameters": {
        "type": "object",
        "properties": {
          "mysterySet": { "type": "string", "enum": ["JOYFUL", "LUMINOUS", "SORROWFUL", "GLORIOUS"] },
          "mysteryNumber": { "type": "integer", "description": "1 through 5 within the mystery set" }
        },
        "required": ["mysterySet", "mysteryNumber"]
      }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "read_liturgy_and_guides",
      "description": "Read today's exact Catholic liturgical day (season, feast, color, Marian antiphon, recommended mystery set) and list every playable prayer guide, themed rosary, devotional crown, and Via-Sacra in Roteiros.",
      "parameters": { "type": "object", "properties": {} }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "read_my_garden",
      "description": "Read the user's real local Meu Jardim records: saved intentions (active & thanksgiving), recent completed crowns, recent journal reflections, bookmarks, and Catechism lesson progress.",
      "parameters": { "type": "object", "properties": {} }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "read_current_prayer_session",
      "description": "Check whether the user currently has an active or paused Rosary/chaplet session, which mystery and bead they are on, and which intentions are attached.",
      "parameters": { "type": "object", "properties": {} }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "build_custom_rosary",
      "description": "Craft a complete personalized Rosary (5 decades or 1 focused decade) anchored in the user's real-life intention and interior state, and present a 1-tap 'Rezar este terço agora' launch card. NEVER alter or include fixed Church prayers (Pai-Nosso, Ave-Maria, Glória, Credo, Salve Rainha).",
      "parameters": {
        "type": "object",
        "properties": {
          "mysterySet": { "type": "string", "enum": ["JOYFUL", "LUMINOUS", "SORROWFUL", "GLORIOUS"] },
          "intentionSummary": { "type": "string", "description": "Respectful 1-sentence summary of the user's intention" },
          "personalizedOffering": { "type": "string", "description": "2-3 reverent sentences elevating the intention to Christ through Mary" },
          "preparatoryPrayer": { "type": "string", "description": "Optional brief 1-2 sentence prayer to the Holy Spirit adapted to the user's state" },
          "suggestEyesClosed": { "type": "boolean", "description": "True when the user is tired, overwhelmed, or praying at night" },
          "singleDecadeIndex": { "type": "integer", "description": "Optional 0..4 index if building a focused single decade instead of all 5 decades" },
          "cardLabel": { "type": "string", "description": "Short warm title for the launch card (e.g. 'Seu terço pela recuperação da sua mãe')" },
          "cardSubtitle": { "type": "string", "description": "Brief description shown on the card" },
          "decades": {
            "type": "array",
            "description": "1 decade (when singleDecadeIndex is set) or all 5 decades (mysteryNumber 1..5), each with adaptedReflection, spiritualFruitApplication, and 10 short beadMeditations.",
            "items": {
              "type": "object",
              "properties": {
                "mysteryNumber": { "type": "integer" },
                "adaptedReflection": { "type": "string" },
                "spiritualFruitApplication": { "type": "string" },
                "beadMeditations": { "type": "array", "items": { "type": "string" } }
              },
              "required": ["mysteryNumber", "adaptedReflection", "beadMeditations"]
            }
          }
        },
        "required": ["mysterySet", "intentionSummary", "personalizedOffering", "decades"]
      }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "suggest_prayer_guide",
      "description": "Attach a 1-tap launch card for a bundled guide the user wants to start. Call read_liturgy_and_guides first. To offer the complete prayer for reading and saving, use the final reply's prayers field instead.",
      "parameters": {
        "type": "object",
        "properties": {
          "guideName": { "type": "string", "enum": ${LumenJsonParser.serializeSimple(PrayerGuide.entries.map { it.name })}, "description": "Exact guideName from read_liturgy_and_guides" },
          "mysterySet": { "type": "string", "enum": ["JOYFUL", "LUMINOUS", "SORROWFUL", "GLORIOUS"], "description": "Optional mystery set override for Rosary scripts" },
          "focusMysteryIndex": { "type": "integer", "description": "Optional 0..4 mystery index when launching SINGLE_DECADE" },
          "meditated": { "type": "boolean", "description": "Optional bead-by-bead meditation toggle" },
          "label": { "type": "string", "description": "Short inviting card title" },
          "subtitle": { "type": "string", "description": "Short explanation of why this guide fits right now" }
        },
        "required": ["guideName", "label"]
      }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "manage_intention",
      "description": "Directly create a new intention in Meu Jardim, mark an existing intention as answered/thanksgiving ('Em ação de graças'), or select an existing intention for the next Rosary.",
      "parameters": {
        "type": "object",
        "properties": {
          "operation": { "type": "string", "enum": ["create", "mark_thanksgiving", "select_for_next_rosary"] },
          "intentionId": { "type": "string", "description": "Required for mark_thanksgiving or select_for_next_rosary (get exact id from read_my_garden)" },
          "title": { "type": "string", "description": "Short intention title (required for create)" },
          "description": { "type": "string", "description": "Optional detail for create" },
          "category": { "type": "string", "enum": ["Família", "Saúde", "Conversão", "Trabalho", "Falecidos", "Ação de graças"] },
          "thanksgivingNote": { "type": "string", "description": "Optional gratitude note when marking thanksgiving" },
          "selectForNextRosary": { "type": "boolean", "description": "Whether to attach the created intention to the next Rosary (default true)" }
        },
        "required": ["operation"]
      }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "save_journal_reflection",
      "description": "Save a concise spiritual reflection, prayer insight, or concrete resolution directly into the user's Meu Jardim spiritual journal.",
      "parameters": {
        "type": "object",
        "properties": {
          "text": { "type": "string", "description": "The reflection text to store in Meu Jardim" }
        },
        "required": ["text"]
      }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "bookmark_content",
      "description": "Save a prayer, catechism lesson, faith Q&A, or Rosary mystery directly to the user's Guardados bookmarks in Meu Jardim.",
      "parameters": {
        "type": "object",
        "properties": {
          "kind": { "type": "string", "enum": ["prayer", "lesson", "question", "mystery"] },
          "itemId": { "type": "string", "description": "Exact verified ID (for mystery use format 'JOYFUL:0'..'GLORIOUS:4')" }
        },
        "required": ["kind", "itemId"]
      }
    }
  },
  {
    "type": "function",
    "function": {
      "name": "open_app_content",
      "description": "Attach a 1-tap native card to open a specific prayer in Tesouro, a Catechism chapter, a curated Q&A, a practical journey (mass, confession, evening, mercy), or a deep Mystery Study view.",
      "parameters": {
        "type": "object",
        "properties": {
          "targetType": { "type": "string", "enum": ["prayer", "lesson", "question", "journey", "mystery_study"] },
          "itemId": { "type": "string", "description": "Verified ID for prayer, lesson, question, or journey ('mass', 'confession', 'evening', 'mercy')" },
          "mysterySet": { "type": "string", "enum": ["JOYFUL", "LUMINOUS", "SORROWFUL", "GLORIOUS"] },
          "mysteryIndex": { "type": "integer", "description": "0..4 index when targetType is mystery_study" },
          "label": { "type": "string", "description": "Short inviting card title" },
          "subtitle": { "type": "string", "description": "Brief card subtitle" }
        },
        "required": ["targetType", "label"]
      }
    }
  }
]
""".trimIndent()

    fun execute(
        toolName: String,
        argumentsJson: String,
        bridge: LumenToolBridge,
        persona: CatechistPersona,
        fallbackMysterySet: MysterySet,
    ): ToolExecutionOutput {
        if (toolName == SEND_INTERIM_REPLY) {
            val message = parseInterimReply(argumentsJson)
                ?: return ToolExecutionOutput(jsonObj("error" to "Escreva uma mensagem breve de até 300 caracteres."))
            return ToolExecutionOutput(jsonObj("ok" to true), interimReply = message)
        }
        val args = (LumenJsonParser.parseValue(argumentsJson) as? Map<*, *>) ?: emptyMap<String, Any?>()
        return when (toolName) {
            "search_treasury" -> execSearchTreasury(args)
            "read_treasury_prayer" -> execReadTreasuryPrayer(args)
            "search_catechism" -> execSearchCatechism(args)
            "read_catechism_lesson" -> execReadCatechismLesson(args)
            "read_mystery_study" -> execReadMysteryStudy(args, fallbackMysterySet)
            "read_liturgy_and_guides" -> execReadLiturgyAndGuides(bridge)
            "read_my_garden" -> execReadMyGarden(bridge)
            "read_current_prayer_session" -> execReadCurrentPrayerSession(bridge)
            "build_custom_rosary" -> execBuildCustomRosary(args, persona, fallbackMysterySet)
            "suggest_prayer_guide" -> execSuggestPrayerGuide(args)
            "manage_intention" -> execManageIntention(args, bridge)
            "save_journal_reflection" -> execSaveJournal(args, bridge)
            "bookmark_content" -> execBookmark(args, bridge)
            "open_app_content" -> execOpenAppContent(args, fallbackMysterySet)
            else -> ToolExecutionOutput(jsonObj("error" to "Ferramenta desconhecida: $toolName"))
        }
    }

    private fun execSearchTreasury(args: Map<*, *>): ToolExecutionOutput {
        val query = (args["query"] as? String).orEmpty().trim()
        val catName = (args["category"] as? String)?.trim()
        val category = PrayerCategory.entries.firstOrNull {
            it.name.equals(catName, true) || it.label.equals(catName, true)
        }
        val hits = TreasuryCatalog.search(query, category).take(8)
        val items = hits.map { p ->
            jsonObj(
                "id" to p.id,
                "title" to p.title,
                "latinTitle" to p.latinTitle,
                "category" to p.category.label,
                "summary" to p.summary,
                "hasLatin" to (p.latin != null),
                "explainedTerms" to p.annotations.map { it.term },
            )
        }
        return ToolExecutionOutput(
            resultJson = jsonObj("totalMatched" to hits.size, "prayers" to rawArray(items)),
            traceLabel = "Consultou o Tesouro",
        )
    }

    private fun execReadTreasuryPrayer(args: Map<*, *>): ToolExecutionOutput {
        val id = (args["prayerId"] as? String)?.trim()
        val p = TreasuryCatalog.find(id)
            ?: return ToolExecutionOutput(jsonObj("error" to "Oração não encontrada para id=$id"))
        val annotations = p.annotations.map { a ->
            jsonObj(
                "term" to a.term,
                "origin" to a.origin,
                "scripture" to a.scriptureReference,
                "today" to a.today,
                "catechism" to a.catechism,
            )
        }
        return ToolExecutionOutput(
            resultJson = jsonObj(
                "id" to p.id,
                "title" to p.title,
                "latinTitle" to p.latinTitle,
                "category" to p.category.label,
                "context" to p.context,
                "portuguese" to p.portuguese,
                "latin" to p.latin,
                "explainedTerms" to rawArray(annotations),
            ),
            traceLabel = "Leu ${p.title}",
        )
    }

    private fun execSearchCatechism(args: Map<*, *>): ToolExecutionOutput {
        val query = (args["query"] as? String).orEmpty().trim()
        val lessons = FormationCatalog.lessons
            .map { it to FormationCatalog.matches(query, it.searchableText) }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(5)
            .map { (l, _) ->
                jsonObj(
                    "id" to l.id,
                    "pillar" to l.pillar,
                    "title" to l.title,
                    "objective" to l.objective,
                    "catechism" to l.catechism,
                    "scripture" to l.scripture,
                )
            }
        val questions = FormationCatalog.searchQuestions(query).take(4).map { q ->
            jsonObj(
                "id" to q.id,
                "question" to q.question,
                "answer" to q.answer,
                "references" to q.references,
            )
        }
        val journeys = FormationCatalog.journeys
            .filter { query.isBlank() || FormationCatalog.matches(query, "${it.title} ${it.introduction} ${it.steps.joinToString(" ")}") > 0 }
            .map { j ->
                jsonObj(
                    "id" to j.id,
                    "title" to j.title,
                    "introduction" to j.introduction,
                    "steps" to j.steps,
                    "references" to j.references,
                )
            }
        return ToolExecutionOutput(
            resultJson = jsonObj(
                "lessons" to rawArray(lessons),
                "questions" to rawArray(questions),
                "practicalJourneys" to rawArray(journeys),
            ),
            traceLabel = "Consultou a Catequese",
        )
    }

    private fun execReadCatechismLesson(args: Map<*, *>): ToolExecutionOutput {
        val id = (args["lessonId"] as? String)?.trim()
        val l = FormationCatalog.lesson(id)
            ?: return ToolExecutionOutput(jsonObj("error" to "Lição não encontrada para id=$id"))
        return ToolExecutionOutput(
            resultJson = jsonObj(
                "id" to l.id,
                "pillar" to l.pillar,
                "title" to l.title,
                "objective" to l.objective,
                "teaching" to l.teaching,
                "scripture" to l.scripture,
                "catechism" to l.catechism,
                "practice" to l.practice,
                "prayer" to l.prayer,
            ),
            traceLabel = "Leu lição: ${l.title}",
        )
    }

    private fun execReadMysteryStudy(args: Map<*, *>, fallbackSet: MysterySet): ToolExecutionOutput {
        val setName = (args["mysterySet"] as? String)?.trim()
        val set = MysterySet.entries.firstOrNull { it.name.equals(setName, true) } ?: fallbackSet
        val number = ((args["mysteryNumber"] as? Number)?.toInt() ?: 1).coerceIn(1, 5)
        val idx = number - 1
        val m = set.mysteries[idx]
        val c = MysteryContent.contemplationFor(set, idx)
        val s = MysteryStudyCatalog.studyFor(set, idx)
        val saints = s.saintsVoices.map { v ->
            jsonObj("saint" to v.saint, "work" to v.work, "reflection" to v.reflection)
        }
        val symbols = s.symbols.map { sym ->
            jsonObj("name" to sym.name, "meaning" to sym.meaning)
        }
        return ToolExecutionOutput(
            resultJson = jsonObj(
                "mysterySet" to set.name,
                "mysteryNumber" to number,
                "title" to m.title,
                "scripture" to m.scripture,
                "fruit" to m.fruit,
                "scene" to c.scene,
                "meaning" to c.meaning,
                "practice" to c.practice,
                "historicalContext" to s.historicalContext,
                "theologicalDepth" to s.theologicalDepth,
                "catechismRefs" to s.catechismRefs,
                "saintsVoices" to rawArray(saints),
                "symbols" to rawArray(symbols),
                "beads" to c.beads,
            ),
            traceLabel = "Meditou ${m.title}",
        )
    }

    private fun execReadLiturgyAndGuides(bridge: LumenToolBridge): ToolExecutionOutput {
        val today = bridge.readPrayerSession().today
        val liturgy = LiturgicalCalendar.forDate(today)
        val guides = PrayerGuide.entries.map { g ->
            jsonObj(
                "guideName" to g.name,
                "title" to g.title,
                "shelf" to g.shelf.title,
                "summary" to g.summary,
                "shape" to g.shape.badge,
                "isRosary" to g.script.isRosary,
            )
        }
        return ToolExecutionOutput(
            resultJson = jsonObj(
                "date" to today.toString(),
                "season" to liturgy.season.label,
                "celebration" to liturgy.celebration,
                "headerLabel" to liturgy.headerLabel,
                "invitation" to liturgy.invitation,
                "marianAntiphonTitle" to liturgy.marianAntiphonTitle,
                "marianAntiphonId" to liturgy.marianAntiphonId,
                "recommendedMysterySet" to liturgy.recommendedMysterySet.name,
                "recommendedGuideName" to liturgy.recommendedGuideName,
                "guides" to rawArray(guides),
            ),
            traceLabel = "Consultou a liturgia e roteiros",
        )
    }

    private fun execReadMyGarden(bridge: LumenToolBridge): ToolExecutionOutput {
        val g = bridge.readGarden()
        val now = System.currentTimeMillis()
        val dayMs = 86_400_000L
        val intentions = g.intentions.filter { !it.archived }.take(15).map { i ->
            jsonObj(
                "id" to i.id,
                "title" to i.title,
                "description" to i.description,
                "category" to i.category,
                "thanksgiving" to i.thanksgiving,
                "answeredNote" to i.answeredNote,
                "daysAgo" to ((now - i.createdAtMillis).coerceAtLeast(0L) / dayMs).toInt(),
            )
        }
        val crowns = g.recentCrowns.take(10).map { c ->
            jsonObj(
                "guideName" to c.guideName,
                "mysterySet" to c.mysterySet,
                "status" to c.status,
                "durationSeconds" to c.durationSeconds,
            )
        }
        val journal = g.recentJournal.take(5).map { j ->
            jsonObj(
                "text" to j.text.take(260),
                "daysAgo" to ((now - j.createdAtMillis).coerceAtLeast(0L) / dayMs).toInt(),
            )
        }
        return ToolExecutionOutput(
            resultJson = jsonObj(
                "activeIntentions" to rawArray(intentions),
                "completedCrownsCount" to g.recentCrowns.count { it.status == "COMPLETED" },
                "recentCrowns" to rawArray(crowns),
                "recentJournal" to rawArray(journal),
                "bookmarks" to g.bookmarkedKeys.take(15),
                "completedLessons" to g.completedLessonIds,
                "inProgressLessons" to g.inProgressLessonIds,
            ),
            traceLabel = "Consultou o Meu Jardim",
        )
    }

    private fun execReadCurrentPrayerSession(bridge: LumenToolBridge): ToolExecutionOutput {
        val s = bridge.readPrayerSession()
        return ToolExecutionOutput(
            resultJson = jsonObj(
                "hasActiveSession" to s.hasActiveSession,
                "guideName" to s.guideName,
                "guideTitle" to s.guideTitle,
                "mysterySet" to s.mysterySet.name,
                "currentStepTitle" to s.currentStepTitle,
                "currentMysteryTitle" to s.currentMysteryTitle,
                "currentDecadeNumber" to s.currentDecadeNumber,
                "currentBeadNumber" to s.currentBeadNumber,
                "meditated" to s.meditated,
                "language" to s.language,
                "offeredIntentions" to s.offeredIntentions,
            ),
            traceLabel = "Viu seu terço em andamento",
        )
    }

    private fun execBuildCustomRosary(
        args: Map<*, *>,
        persona: CatechistPersona,
        fallbackSet: MysterySet,
    ): ToolExecutionOutput {
        val setName = (args["mysterySet"] as? String)?.trim()
        val set = MysterySet.entries.firstOrNull { it.name.equals(setName, true) } ?: fallbackSet
        val summary = (args["intentionSummary"] as? String)?.trim().orEmpty().ifBlank { "Intenção do coração" }
        val offering = (args["personalizedOffering"] as? String)?.trim().orEmpty()
        if (offering.isBlank()) {
            return ToolExecutionOutput(jsonObj("error" to "personalizedOffering é obrigatório."))
        }
        val prep = (args["preparatoryPrayer"] as? String)?.trim()?.takeIf { it.isNotBlank() && !it.equals("null", true) }
        val suggestEyesClosed = (args["suggestEyesClosed"] as? Boolean) ?: false
        val singleDecade = (args["singleDecadeIndex"] as? Number)?.toInt()?.coerceIn(0, 4)
        val rawDecades = args["decades"] as? List<*> ?: emptyList<Any>()
        val decades = rawDecades.mapIndexedNotNull { idx, item ->
            val m = item as? Map<*, *> ?: return@mapIndexedNotNull null
            val num = (m["mysteryNumber"] as? Number)?.toInt()?.coerceIn(1, 5) ?: (idx + 1)
            val reflection = (m["adaptedReflection"] as? String)?.trim()?.takeIf { it.isNotBlank() } ?: return@mapIndexedNotNull null
            val fruit = (m["spiritualFruitApplication"] as? String)?.trim().orEmpty()
            val beads = (m["beadMeditations"] as? List<*>)
                ?.mapNotNull { (it as? String)?.trim()?.takeIf { s -> s.isNotBlank() } }
                ?.take(10)
                .orEmpty()
            CustomRosaryDecade(num, reflection, fruit, beads)
        }.sortedBy { it.mysteryNumber }

        if (decades.isEmpty()) {
            return ToolExecutionOutput(jsonObj("error" to "Inclua ao menos 1 dezena em 'decades' com 10 meditações."))
        }

        val supplement = CustomRosarySupplement(
            mysterySet = set,
            userIntentionSummary = summary,
            personalizedOffering = offering,
            preparatoryPrayer = prep,
            suggestEyesClosed = suggestEyesClosed,
            decades = decades,
            personaId = persona.id,
            isOfflineFallback = false,
        )
        val cardLabel = (args["cardLabel"] as? String)?.trim()?.takeIf { it.isNotBlank() }
            ?: if (singleDecade != null) "Sua dezena personalizada" else "Seu terço sob medida"
        val cardSubtitle = (args["cardSubtitle"] as? String)?.trim()?.takeIf { it.isNotBlank() }
            ?: "Mistérios ${set.label} · ${summary.take(90)}"

        val action = CatechistAction.StartCustomRosary(
            label = cardLabel,
            subtitle = cardSubtitle,
            supplement = supplement,
            singleDecadeIndex = singleDecade,
        )
        return ToolExecutionOutput(
            resultJson = jsonObj(
                "ok" to true,
                "decadesPrepared" to decades.size,
                "mysterySet" to set.name,
                "note" to "Cartão de 1 toque pronto para o usuário iniciar. Orações fixas da Igreja permanecem íntegras.",
            ),
            emittedAction = action,
            traceLabel = "Preparou terço sob medida",
        )
    }

    private fun execSuggestPrayerGuide(args: Map<*, *>): ToolExecutionOutput {
        val rawName = (args["guideName"] as? String)?.trim()
        val guide = PrayerGuide.byNameOrNull(rawName)
            ?: PrayerGuide.entries.firstOrNull { it.title.equals(rawName, true) }
            ?: return ToolExecutionOutput(jsonObj("error" to "Roteiro inválido: $rawName. Chame read_liturgy_and_guides para ver os nomes exatos."))
        val setName = (args["mysterySet"] as? String)?.trim()
        val set = MysterySet.entries.firstOrNull { it.name.equals(setName, true) }
        val focus = (args["focusMysteryIndex"] as? Number)?.toInt()?.coerceIn(0, 4)
        val meditated = args["meditated"] as? Boolean
        val label = (args["label"] as? String)?.trim()?.takeIf { it.isNotBlank() } ?: guide.title
        val subtitle = (args["subtitle"] as? String)?.trim().orEmpty().ifBlank { guide.summary }

        return ToolExecutionOutput(
            resultJson = jsonObj("ok" to true, "guideName" to guide.name, "title" to guide.title),
            emittedAction = CatechistAction.LaunchGuide(
                label = label,
                subtitle = subtitle,
                guideName = guide.name,
                mysterySet = set,
                focusMysteryIndex = focus,
                meditated = meditated,
            ),
            traceLabel = "Separou ${guide.title}",
        )
    }

    private fun execManageIntention(args: Map<*, *>, bridge: LumenToolBridge): ToolExecutionOutput {
        val op = (args["operation"] as? String)?.trim()?.lowercase().orEmpty()
        return when (op) {
            "create" -> {
                val title = (args["title"] as? String)?.trim().orEmpty()
                if (title.isBlank()) return ToolExecutionOutput(jsonObj("error" to "title é obrigatório para create."))
                val desc = (args["description"] as? String)?.trim().orEmpty()
                val rawCat = (args["category"] as? String)?.trim().orEmpty()
                val category = if (rawCat.equals("Ação de Graças", true)) "Ação de graças" else rawCat.ifBlank { "Família" }
                val thanksgiving = category == "Ação de graças"
                val select = (args["selectForNextRosary"] as? Boolean) ?: true
                val id = bridge.createIntention(title, desc, category, thanksgiving, select)
                if (id != null) {
                    ToolExecutionOutput(
                        resultJson = jsonObj("ok" to true, "intentionId" to id, "saved" to true, "selectedForNextRosary" to select),
                        traceLabel = "Guardou intenção no Meu Jardim",
                    )
                } else {
                    ToolExecutionOutput(
                        resultJson = jsonObj("ok" to true, "saved" to false, "note" to "Apresentado cartão para o usuário confirmar e salvar."),
                        emittedAction = CatechistAction.SaveIntention(
                            label = "Guardar intenção: ${title.take(48)}",
                            subtitle = desc.ifBlank { "Categoria: $category" },
                            intentionTitle = title,
                            intentionDescription = desc,
                            category = category,
                        ),
                        traceLabel = "Preparou intenção",
                    )
                }
            }
            "mark_thanksgiving" -> {
                val id = (args["intentionId"] as? String)?.trim().orEmpty()
                val note = (args["thanksgivingNote"] as? String)?.trim().orEmpty()
                val ok = id.isNotBlank() && bridge.markIntentionThanksgiving(id, note)
                ToolExecutionOutput(
                    resultJson = jsonObj("ok" to ok, "intentionId" to id),
                    traceLabel = if (ok) "Marcou em ação de graças" else null,
                )
            }
            "select_for_next_rosary" -> {
                val id = (args["intentionId"] as? String)?.trim().orEmpty()
                val ok = id.isNotBlank() && bridge.selectIntentionForNextRosary(id)
                ToolExecutionOutput(
                    resultJson = jsonObj("ok" to ok, "intentionId" to id),
                    traceLabel = if (ok) "Selecionou intenção para o terço" else null,
                )
            }
            else -> ToolExecutionOutput(jsonObj("error" to "Operação inválida: $op"))
        }
    }

    private fun execSaveJournal(args: Map<*, *>, bridge: LumenToolBridge): ToolExecutionOutput {
        val text = (args["text"] as? String)?.trim().orEmpty()
        if (text.isBlank()) return ToolExecutionOutput(jsonObj("error" to "text não pode ser vazio."))
        val ok = bridge.saveJournalReflection(text)
        return ToolExecutionOutput(
            resultJson = jsonObj("ok" to ok),
            traceLabel = if (ok) "Anotou no Diário" else null,
        )
    }

    private fun execBookmark(args: Map<*, *>, bridge: LumenToolBridge): ToolExecutionOutput {
        val kind = (args["kind"] as? String)?.trim()?.lowercase().orEmpty()
        val itemId = (args["itemId"] as? String)?.trim().orEmpty()
        val valid = when (kind) {
            "prayer" -> TreasuryCatalog.find(itemId) != null
            "lesson" -> FormationCatalog.lesson(itemId) != null
            "question" -> FormationCatalog.questions.any { it.id == itemId }
            "mystery" -> itemId.contains(':')
            else -> false
        }
        if (!valid) return ToolExecutionOutput(jsonObj("error" to "Conteúdo não encontrado: $kind/$itemId"))
        val ok = bridge.bookmarkContent(kind, itemId)
        return ToolExecutionOutput(
            resultJson = jsonObj("ok" to ok, "kind" to kind, "itemId" to itemId),
            traceLabel = if (ok) "Salvou em Guardados" else null,
        )
    }

    private fun execOpenAppContent(args: Map<*, *>, fallbackSet: MysterySet): ToolExecutionOutput {
        val target = (args["targetType"] as? String)?.trim()?.lowercase().orEmpty()
        val itemId = (args["itemId"] as? String)?.trim().orEmpty()
        val label = (args["label"] as? String)?.trim().orEmpty()
        val subtitle = (args["subtitle"] as? String)?.trim().orEmpty()

        val action: CatechistAction? = when (target) {
            "prayer" -> TreasuryCatalog.find(itemId)?.let { p ->
                CatechistAction.OpenTreasuryPrayer(label.ifBlank { p.title }, subtitle.ifBlank { p.summary }, p.id)
            }
            "lesson" -> FormationCatalog.lesson(itemId)?.let { l ->
                CatechistAction.OpenFormationLesson(label.ifBlank { l.title }, subtitle.ifBlank { l.objective }, l.id)
            }
            "question" -> FormationCatalog.questions.find { it.id == itemId }?.let { q ->
                CatechistAction.OpenFormationQuestion(label.ifBlank { q.question }, subtitle.ifBlank { q.references }, q.id)
            }
            "journey" -> FormationCatalog.journeys.find { it.id == itemId }?.let { j ->
                CatechistAction.OpenPracticeJourney(label.ifBlank { j.title }, subtitle.ifBlank { j.introduction }, j.id)
            }
            "mystery_study" -> {
                val setName = (args["mysterySet"] as? String)?.trim()
                val set = MysterySet.entries.firstOrNull { it.name.equals(setName, true) } ?: fallbackSet
                val idx = ((args["mysteryIndex"] as? Number)?.toInt() ?: 0).coerceIn(0, 4)
                val m = set.mysteries[idx]
                CatechistAction.OpenMysteryStudy(label.ifBlank { m.title }, subtitle.ifBlank { "Estudo completo · ${m.scripture}" }, set, idx)
            }
            else -> null
        }

        if (action == null) {
            return ToolExecutionOutput(jsonObj("error" to "Destino inválido ou ID não encontrado ($target/$itemId). Use as ferramentas de busca primeiro."))
        }
        return ToolExecutionOutput(
            resultJson = jsonObj("ok" to true, "targetType" to target, "label" to action.label),
            emittedAction = action,
            traceLabel = "Preparou um atalho no aplicativo",
        )
    }

    // Minimal pure-Kotlin JSON builders for tool results
    private data class RawJson(val raw: String)
    private fun rawArray(items: List<String>): RawJson = RawJson("[" + items.joinToString(",") + "]")

    private fun jsonObj(vararg pairs: Pair<String, Any?>): String = buildString {
        append('{')
        var first = true
        for ((k, v) in pairs) {
            if (!first) append(',')
            first = false
            append('"').append(LumenJsonParser.escape(k)).append("\":")
            appendJsonValue(v)
        }
        append('}')
    }

    private fun StringBuilder.appendJsonValue(v: Any?) {
        when (v) {
            null -> append("null")
            is RawJson -> append(v.raw)
            is Boolean -> append(v)
            is Number -> append(v)
            is String -> append('"').append(LumenJsonParser.escape(v)).append('"')
            is List<*> -> {
                append('[')
                v.forEachIndexed { i, item ->
                    if (i > 0) append(',')
                    appendJsonValue(item)
                }
                append(']')
            }
            else -> append('"').append(LumenJsonParser.escape(v.toString())).append('"')
        }
    }
}
