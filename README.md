# 🌹 Sertum Rosarum: O Santo Terço

> **"Teça a sua coroa diária de contemplação."**  
> *Sertum Rosarum* (*"Crown of Roses"*, the original Latin expression that gave rise to the word *Rosary*) is a contemplative Catholic prayer and Rosary application for Android—designed as a **silent sanctuary in your pocket** and a **school of contemplative prayer**.

---

## 📚 Documentation & Development Plan

All product, design, architectural, liturgical, and AI specifications for the application are documented in the repository:

| Document | Description |
| :--- | :--- |
| **[`phases.md`](phases.md)** | **Phased Development & Milestone Plan** — 5 balanced, end-to-end feature milestones starting with Phase 1 (Silent Sanctuary UI & Interactive Rosary Counter). |
| **[`docs/product-vision.md`](docs/product-vision.md)** | **Product Vision & Experience Architecture** — Brand identity, user personas, "Crown of Roses" metaphor, "Eyes Closed" tactile navigation, and screen inventory. |
| **[`docs/design-system.md`](docs/design-system.md)** | **"Santuário Silencioso" Design System** — Day (`Pergaminho Quente`) & Vigil (`Azul Vigília`) palettes, 4 Dynamic Mystery Atmospheres, typography, custom non-Material Compose primitives, Canvas bead/arc specs, and haptic patterns. |
| **[`docs/architecture.md`](docs/architecture.md)** | **Technical & Software Architecture** — Package structure, Unidirectional Data Flow, Navigation graph, deterministic Prayer Engine State Machine, persistence (`Room` + `DataStore`), and testing strategy. |
| **[`docs/domain-and-liturgical-content.md`](docs/domain-and-liturgical-content.md)** | **Domain Models & Liturgical Content** — The 20 Mysteries in 4 Dimensions, immutable fixed prayers in Portuguese & Latin, *"Toque para Entender"* theological annotations, and Devotional Crowns (*Divina Misericórdia, São José, Coroa das Lágrimas, Coroa Franciscana*). |
| **[`docs/lumen-ai.md`](docs/lumen-ai.md)** | **Lumen Contemplative AI Engine** — Custom Rosary generation (*Intenção Viva*), Contextual Catechist, Mood Curation, Contemplative Memory, doctrinal guardrails, JSON schemas, and offline fallback. |
| **[`docs/visao-de-produto-pt.md`](docs/visao-de-produto-pt.md)** | **Documento de Visão e Produto (PT-BR)** — Original Portuguese product vision and experience architecture specification. |

---

## ✨ Key Highlights

* **"Santuário Silencioso" Custom UI (Zero Material Libraries):** Built purely on `androidx.compose.ui` and `androidx.compose.foundation` with warm parchment (`#F9F6F0`) and vigil blue (`#0F172A`) palettes, dynamic Mystery-of-the-Day atmospheres, and editorial serif typography (`Cinzel`, `Cormorant Garamond`, `Lora`, `Plus Jakarta Sans`).
* **Interactive "Crown of Roses" Counter:** Custom Compose `Canvas` visualizations featuring **10 Blooming Rose Beads (`Contas que Florescem`)** per decade, a **5-Mystery Crown Arc (`O Arco dos 5 Mistérios`)**, and the **"Eyes Closed" Mode (`Modo Olhos Fechados`)** with a wide lower-screen touch zone and differentiated haptic pulses.
* **Progressive Contemplative Formation:** The **20 Mysteries in 4 Dimensions** (Biblical Narrative, Contemplative Meaning, Spiritual Fruit, and optional Bead-by-Bead Meditations), **Portuguese/Latin Bilingual Mode**, **"Touch to Understand" (`Anatomia da Oração`)** bottom sheets, and multiple Devotional Crowns.
* **Lumen Contemplative AI:** Seamlessly integrated into sacred editorial cards to craft custom Rosary offerings and Gospel-anchored meditations for the user's daily intentions while strictly preserving the immutability of fixed Church prayers.

---

## 🛠️ Tech Stack

* **Language:** Kotlin 2.1.20 (JDK 17 toolchain)
* **UI Framework:** Jetpack Compose (Compose BOM `2025.04.00`, Compose Compiler Gradle plugin)
  * **Strict No-Material Policy:** Uses `androidx.compose.ui` and `androidx.compose.foundation` only—no `material` or `material3` dependencies.
* **Build System:** Android Gradle Plugin (AGP) `8.9.1`, Gradle `8.11.1`, Kotlin DSL + Version Catalog (`gradle/libs.versions.toml`)
* **Android SDK:** `compileSdk 35`, `targetSdk 35`, **`minSdk 33`** (Android 13+ only), edge-to-edge enabled

---

## 📁 Project Structure

```text
app/                    # Single application module (namespace com.sertumrosarum)
docs/                   # Comprehensive product, design, liturgical, and technical documentation
gradle/                 # Version catalog + wrapper config
.github/workflows/      # CI (unit tests + lint on push/PR)
phases.md               # Phased implementation roadmap & milestones
```

---

## 🚀 Getting Started

```bash
gradle wrapper          # one-time: generates gradlew and the wrapper jar
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

## 🔄 Continuous Integration

GitHub Actions ([`.github/workflows/android-ci.yml`](.github/workflows/android-ci.yml)) runs unit tests and Android Lint on every push and pull request.
