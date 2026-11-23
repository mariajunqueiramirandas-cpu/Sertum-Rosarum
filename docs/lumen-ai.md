# ✨ Lumen — Contemplative AI & Conversational Catechist Specification

> **Engine Name:** `Lumen` (*Luz para contemplação*) — powered by **Mistral Large 4** (`mistral-large-4`) with full offline fallback.  
> **Core Principle:** Reverent, correspondence-style integration into **O Jardim de Maria** — Fraunces & Nunito typography, soft sage note-cards, open parchment replies, watercolor portraits (**MeshyAI `gpt-image-2-6-sunburst`**), and embedded native prayer actions — never generic chat bubbles.

---

## 1. Role, Philosophy & The Four Catechist Companions

**Lumen** is a specialist Catholic spirituality companion and catechist integrated into **Sertum Rosarum**. Its purpose is not to replace human prayer, spiritual direction, or the Sacraments, nor to alter traditional liturgical formulas, but to walk beside the believer—answering honest questions, offering warm encouragement, and bridging real life directly into the Rosary, `Tesouro`, `Catequese`, and `Meu Jardim`.

### Four Selectable Named Catechists
Users can talk directly with their companion (by typing or voice input) and switch personalities anytime:
* **Mateus (*Café & Alegria · default*)**: Blends relatable everyday analogies (*Café no Jardim*, inspired by Bl./St. Pier Giorgio Frassati & Carlo Acutis) with joyful, disarming wit (*Alegre & Perspicaz*, inspired by St. Philip Neri & Chesterton).
* **Lucas (*Café no Jardim*)**: Warm, grounded friend for everyday struggles—work, family, fatigue, and distraction.
* **Clara (*Alegre & Perspicaz*)**: Bright, witty, incisive catechist who dissolves anxious scrupulosity with saint stories and Gospel clarity (inspired by St. Teresa of Ávila & St. Philip Neri).
* **Bento (*Mestre Sereno*)**: Gentle, pastoral elder mentor for quiet nights, grief, and unhurried contemplation (inspired by St. Francis de Sales & St. John Paul II).

### Why Correspondence Layout Instead of Generic Chat Bubbles?
Traditional chat bubbles feel noisy and pull users out of stillness. Instead:
* User notes sit right-aligned on soft sage surface cards; the catechist writes openly on the warm parchment canvas in **Fraunces**, headed by their watercolor portrait medallion ([catechists artwork](artwork/catechists/README.md)) and separated by botanical sprigs.
* **Lumen** runs a **multi-step native Mistral Large 4 Function Calling loop** (`mistral-large-4`, `reasoning_effort: low`, **no `max_tokens` cap**, up to 6 parallel tool rounds) that grounds every answer in the app's real catalogs and `Meu Jardim` database and attaches up to multiple **embedded native action cards** (`Rezar este terço agora`, `Rezar esta dezena agora`, `Iniciar roteiro`, `Guardar no Meu Jardim`, `Abrir no Tesouro`, `Abrir na Catequese`, `Abrir dúvida`, `Abrir caminho prático`, `Estudar mistério`).

---

## 2. The Core Capabilities & 16 Agent Tools (`LumenTools`)

Lumen uses **16 Mistral function-calling tools** declared in `LumenTools.mistralToolsJson`. Garden and prayer session tools access app state through `LumenToolBridge` in `SertumApp`.

### 2.1 Read & Grounding Tools (8)
1. **`search_treasury`** — Searches all 61 bundled Catholic prayers (Portuguese & Latin) and 78 explained theological expressions (`Toque para Entender`).
2. **`read_treasury_prayer`** — Reads the full Portuguese & Latin text and annotations of a specific prayer by ID.
3. **`search_catechism`** — Searches all 16 Catechism study chapters, 12 curated faith Q&As, and 4 practical journeys (*Missa*, *Confissão*, *Exame da Noite*, *Obras de Misericórdia*).
4. **`read_catechism_lesson`** — Reads the full teaching, Scripture references, CIC citations, and daily practice of any chapter.
5. **`read_mystery_study`** — Reads the 4 contemplative dimensions (scene, meaning, fruit, practice, 10 bead lines) plus historical/Jewish context, Saints' voices, symbols, and CIC references for all 20 Rosary Mysteries.
6. **`read_liturgy_and_guides`** — Reads today's live liturgical day (season, feast, color, Marian antiphon, recommended mystery set) and lists every playable Rosary, themed Rosary, devotional crown (*Misericórdia*, *São José*, *Lágrimas*, *Sete Alegrias Franciscanas*), and *Via-Sacra*.
7. **`read_my_garden`** — Reads the user's real local `Meu Jardim` records: active & thanksgiving intentions, recent completed crowns, journal reflections, bookmarks, and Catechism lesson progress.
8. **`read_current_prayer_session`** — Reads the live active/paused prayer session (guide, mystery, decade, bead, meditated mode, offered intentions).

### 2.2 Write & Action Tools (6)
9. **`build_custom_rosary`** — Crafts a complete personalized Rosary (all 5 decades + 50 bead meditations, or 1 focused decade + 10 bead meditations) with `personalizedOffering`, `preparatoryPrayer`, and `suggestEyesClosed`, attaching a 1-tap launch card while keeping `FixedPrayer` strictly immutable.
10. **`suggest_prayer_guide`** — Attaches a 1-tap launch card for any bundled guide in `Roteiros` (including mystery set, focus decade, and meditated toggle).
11. **`manage_intention`** — Mutates real Room `Meu Jardim` intentions (`create`, `mark_thanksgiving` with answered note, or `select_for_next_rosary`).
12. **`save_journal_reflection`** — Saves a contemplative reflection directly into the user's `Diário` in Room.
13. **`bookmark_content`** — Bookmarks a Treasury prayer, Catechism lesson, or Mystery study into `Guardados`.
14. **`open_app_content`** — Attaches a 1-tap deep-link card directly into a `prayer`, `lesson`, `question`, `journey`, or `mystery_study`.

### 2.3 Contextual Catechist, Interior State & Contemplative Memory
* **`send_interim_reply`** posts a short message to the chat while the catechist continues a longer task. The message stays in the conversation when the final reply arrives.
* **`search_web_prayers`** finds prayers and devotional sources beyond the Tesouro through Mistral web search in online mode. The chat links to the cited pages.
* **Contextual Mystery Bridge**: From the Immersive Prayer Screen's `mystery-sheet` (*"Conversar com [nome] sobre este mistério"*), opens the catechist pre-seeded with the active mystery and bead context.
* **Interior State Adaptation**: Automatically adapts offering length, bead meditations, and `"Eyes Closed"` mode suggestion for fatigue, anxiety, grief, or spiritual dryness.
* **Contemplative Memory (`LumenMemoryUseCase`)**: On `Início`, inspects `Meu Jardim` intentions prayed 5–14 days ago and gently invites renewal or thanksgiving in today's crown.

---

## 3. Doctrinal Guardrails & Safety Architecture (`LumenGuardrails`)

```mermaid
flowchart LR
    Input["User Note / Voice"] --> Sanitizer["Input Sanitizer"]
    Sanitizer --> Prompt["Persona + Doctrinal System Prompt\n(CIC + Scripture + Rosarium Virginis Mariae)"]
    Prompt --> Loop["Mistral Large 4 Agent Loop\n(No max_tokens · Up to 6 Parallel Tool Rounds)"]
    Loop <--> Tools["16 Agent Tools\n(Catalogs + Liturgy + Meu Jardim + Web Search + Interim Replies)"]
    Loop --> Parser["Structured Reply + Action Cards"]
    Parser --> Guard["Immutable Prayer Guardrail\n(Fixed Prayers Loaded from Local Binary Only)"]
    Guard --> UI["O Jardim de Maria Correspondence UI"]
```

### 3.1 Hard Architectural Separation of Fixed Prayers
* **Rule:** The LLM **never** outputs, modifies, or supplies the text of the *Sinal da Cruz*, *Pai-Nosso*, *Ave-Maria*, *Glória ao Pai*, *Credo*, *Jaculatória de Fátima*, *Salve Rainha*, or *Ladainha Lauretana*.
* **Enforcement:** In `PrayerUiState` (`PrayerSessionViewModel`), every `PrayerStep.prayer` reference resolves directly against `FixedPrayer` compiled into the app binary. `CustomRosarySupplement` can only populate `personalizedOffering`, `preparatoryPrayer`, `adaptedReflection`, `spiritualFruitApplication`, and `beadMeditations`.

### 3.2 Theological Anchoring (`System Prompt Specification`)
All calls to the AI engine include a non-overridable system instruction enforcing:
1. Strict fidelity to the **Magisterium of the Catholic Church**, the **Catechism of the Catholic Church (CIC)**, Sacred Scripture, and Pope St. John Paul II's Apostolic Letter ***Rosarium Virginis Mariae***.
2. Warm, human, pastoral **Brazilian Portuguese (`pt-BR`)** shaped by the user's chosen catechist companion—free of secular self-help clichés or superstitious promises.
3. Refusal to give medical, legal, or psychological diagnoses; when a user expresses severe distress, Lumen responds with gentle spiritual consolation anchored in Christ's compassion while encouraging appropriate human and pastoral support.

---

## 4. Structured Output JSON Schemas

### 4.1 `CustomRosarySupplement` Schema
```json
{
  "mysterySetId": "sorrowful",
  "userIntentionSummary": "Pela recuperação cirúrgica da mãe e fortalecimento no cansaço do trabalho",
  "personalizedOffering": "Senhor Jesus, por mãos de Maria Santíssima, oferecemos esta Coroa de Rosas...",
  "decades": [
    {
      "mysteryNumber": 1,
      "adaptedReflection": "No Horto das Oliveiras, Cristo acolhe a angústia humana e entrega tudo ao Pai...",
      "spiritualFruitApplication": "Confiar a cirurgia de sua mãe às mãos providentes do Pai.",
      "beadMeditations": [
        "1. Jesus ajoelha-se no Getsêmani carregando todas as nossas dores.",
        "2. Entregamos ao Senhor a aflição por esta cirurgia.",
        "3. O suor de sangue de Cristo santifica o nosso cansaço diário.",
        "4. 'Pai, não se faça a minha vontade, mas a Tua.'",
        "5. Que o Espírito Santo guie as mãos dos médicos e enfermeiros.",
        "6. Um anjo do Céu vem consolar e fortalecer o Senhor na agonia.",
        "7. Maria vigia em oração silenciosa junto ao coração do Filho.",
        "8. Descansamos o peso do trabalho no jugo suave de Cristo.",
        "9. A paz de Deus, que supera todo entendimento, guarde a nossa família.",
        "10. Com Cristo, levantamo-nos fortalecidos na esperança."
      ]
    }
  ]
}
```

### 4.2 `MysteryExplanation` Schema
```json
{
  "mysteryId": "joyful_1",
  "lens": "SAINTS_WISDOM",
  "title": "A Anunciação segundo os Santos",
  "bodyParagraphs": [
    "São Luís Maria Grignion de Montfort ensina no 'Segredo Admirável do Santíssimo Rosário' que a Ave-Maria foi o cântico novo pelo qual Deus se fez homem...",
    "Santo Tomás de Aquino destaca que o 'Fiat' (Faça-se) de Maria representou o consentimento livre de toda a humanidade para acolher o Redentor..."
  ],
  "scriptureOrCatechismRef": "CIC §484-494 • Tratado da Verdadeira Devoção, §142"
}
```

---

## 5. Offline Fallback & Resilience (`OfflineLumenFallback`)

Because a believer may pray in a chapel, on an airplane, or without an active internet/API connection, every **Lumen** entry point includes a deterministic **Offline Contemplative Engine**:
* **Custom Intention Rosary Offline Mode**: Combines the user's typed intention into a solemn template offering and pairs it with curated thematic meditations (*Saúde e Enfermos*, *Família*, *Trabalho e Cansaço*, *Paz Interior*, *Ação de Graças*).
* **Contextual Catechist Offline Mode**: Pre-bundles all 3 lenses (*Iniciante*, *Contexto Histórico e Judaico*, and *Sabedoria dos Santos*) for all **20 Mysteries of the Rosary** in the local catalog so *"Compreender este mistério"* works instantaneously even in Airplane Mode.
