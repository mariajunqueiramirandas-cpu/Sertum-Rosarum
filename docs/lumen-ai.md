# ✨ Lumen — Contemplative AI Engine Specification

> **Engine Name:** `Lumen` (*Luz para contemplação*)  
> **Core Principle:** Invisible, reverent integration into the sacred editorial UI—never a generic chat bubble interface.

---

## 1. Role & Philosophy

**Lumen** is a specialist Catholic spirituality AI assistant integrated into **Sertum Rosarum**. Its purpose is not to replace human prayer or alter traditional liturgical formulas, but to act as a **quiet spiritual companion and catechist** that bridges the eternal Mysteries of the Gospel with the concrete joys, anxieties, and questions the user carries into prayer today.

### Why No Chat Bubble UI?
Traditional chat interfaces encourage rapid back-and-forth messaging and break the stillness of prayer. Instead:
* User inputs are collected through calm, reflective prompts (e.g., *"O que você traz no coração para este terço?"*) or contextual single-tap contemplation lenses.
* **Lumen** returns structured JSON that renders directly inside the app's native `SacredCard` and `SacredBottomSheet` components using the `Lora` / `Cormorant Garamond` editorial typography.

---

## 2. The Four Core Capabilities of Lumen

### 2.1 Custom Rosary Script Generation (`Geração de Roteiros Sob Medida — Intenção Viva`)
* **Trigger:** On the `Início (O Santuário do Dia)` screen, the user shares what they are living before starting the Rosary (e.g., *"Estou aflito com uma cirurgia da minha mãe e cansado no trabalho"*).
* **Generated Output (`CustomRosarySupplement`)**:
  1. **Personalized Initial Offering (`Oferecimento Inicial Personalizado`)**: Elevates the user's raw intention into solemn, reverent prayer language while preserving the traditional Rosary offering structure.
  2. **5 Adapted Mystery Reflections (`Reflexões nos 5 Mistérios`)**: For each of the 5 Mysteries of the day, generates a contemplative paragraph showing how Christ's life in that specific mystery speaks to the user's pain, fatigue, or gratitude.
  3. **50 Bead-by-Bead Meditations (`10 Meditações por Dezena`)**: 10 short, one-sentence contemplative prompts per decade connecting the Gospel scene with the specific grace the user needs today.

### 2.2 Contextual Catechist (`Catequista Contextual — "Compreender este Mistério"`)
* **Trigger:** Tapping the discreet *"Compreender este mistério"* button in the top corner of the **Immersive Prayer Screen** or inside the **Tesouro** study guide.
* **Three Contemplative Lenses**:
  1. **Beginner Lens (`"Explique de forma simples para quem está começando"`)**: Clear, warm, jargon-free explanation of the mystery and why we meditate on it.
  2. **Biblical & Historical Lens (`"Qual o contexto histórico e judaico desta passagem?"`)**: Explains first-century Jewish customs, geography (Nazareth, Ein Karem, Jerusalem, Galilee), and Old Testament prophecies fulfilled in the scene.
  3. **Wisdom of the Saints (`"O que os Santos ensinam sobre este mistério?"`)**: Curates authentic insights and spiritual commentary from **St. Louis Marie Grignion de Montfort** (*Tratado da Verdadeira Devoção* / *Segredo Admirável do Santíssimo Rosário*), **St. Thomas Aquinas** (*Summa Theologiae*), **St. Teresa of Ávila**, and **St. John Paul II** (*Rosarium Virginis Mariae*).

### 2.3 Mood & Interior State Curation (`Curadoria por Estado de Espírito`)
* **Trigger:** User selects or types their interior state before prayer (e.g., *"Estou seco espiritualmente e sem foco"*, *"Estou ansioso"*, *"Estou cansado ao fim do dia"*).
* **Behavior**:
  * Generates or selects a brief **Preparatory Prayer to the Holy Spirit** tailored to that interior state.
  * Automatically adjusts the Rosary session configuration: for spiritual dryness or fatigue, switches to concise, single-breath bead meditations and enables the **"Eyes Closed"** tactile mode reminder.

### 2.4 Contemplative Memory (`Memória Contemplativa — Diário de Intenções`)
* **Trigger:** On opening `Início`, `LumenMemoryUseCase` inspects recent entries in `Meu Jardim` (e.g., an intention prayed for 5–14 days ago).
* **Behavior**:
  * Displays a gentle, non-intrusive card on the Sanctuary screen:
    > *"Na semana passada você rezou pela cirurgia de sua mãe. Deseja incluir uma ação de graças ou renovar esta intenção na coroa de hoje?"*
  * With one tap, weaves that thanksgiving or renewed petition into today's Rosary offering.

---

## 3. Doctrinal Guardrails & Safety Architecture (`LumenGuardrails`)

```mermaid
flowchart LR
    Input["User Intention / Question"] --> Sanitizer["Input Sanitizer"]
    Sanitizer --> Prompt["Doctrinal System Prompt\n(CIC + Scripture + Rosarium Virginis Mariae)"]
    Prompt --> LLM["LLM Provider / Offline Fallback"]
    LLM --> Parser["Strict JSON Schema Validator"]
    Parser --> Guard["Immutable Prayer Guardrail\n(Fixed Prayers Loaded from Local DB Only)"]
    Guard --> UI["Native SacredCard UI"]
```

### 3.1 Hard Architectural Separation of Fixed Prayers
* **Rule:** The LLM **never** outputs, modifies, or supplies the text of the *Sinal da Cruz*, *Pai-Nosso*, *Ave-Maria*, *Glória ao Pai*, *Credo*, *Jaculatória de Fátima*, *Salve Rainha*, or *Ladainha Lauretana*.
* **Enforcement:** In `PrayerStateMachine`, every `PrayerStep.fixedPrayer` reference resolves directly against `ImmutablePrayerCatalog` compiled into the app binary. `CustomRosarySupplement` can only populate `customOfferingAddition`, `decadeReflectionOverride`, and `beadMeditationOverrides`.

### 3.2 Theological Anchoring (`System Prompt Specification`)
All calls to the AI engine include a non-overridable system instruction enforcing:
1. Strict fidelity to the **Magisterium of the Catholic Church**, the **Catechism of the Catholic Church (CIC)**, Sacred Scripture, and Pope St. John Paul II's Apostolic Letter ***Rosarium Virginis Mariae***.
2. A **reverent, serene, pastoral tone** in Brazilian Portuguese (`pt-BR`)—free of colloquial slang, secular self-help clichés, or superstitious promises.
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
