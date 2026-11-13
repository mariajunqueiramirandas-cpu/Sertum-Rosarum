# Phase 3: the interface

The Tesouro tab now has every Phase 3 screen, drawn with **one handwritten example prayer**. The catalog, the search, the saved language, and everything else that decides something are still to be written. No dependency, permission, or network access was added.

## What is built

| Screen | File | What it draws |
| --- | --- | --- |
| Library | [`PrayerLibrary.kt`](../app/src/main/java/com/sertumrosarum/feature/treasury/PrayerLibrary.kt) | Search field, the six moments of life, one shelf per moment that has prayers, the empty and no-result states, and the Formação shelf |
| Reading | [`PrayerReadingPage.kt`](../app/src/main/java/com/sertumrosarum/feature/treasury/PrayerReadingPage.kt) | One prayer in Portuguese, in Latin, or side by side verse by verse, with a verse opening word by word |
| Touch to understand | [`AnnotatedPrayerText.kt`](../app/src/main/java/com/sertumrosarum/feature/treasury/AnnotatedPrayerText.kt) | The marked expressions in the text and the sheet that explains one |
| Mysteries to study | [`MysteriesStudyPage.kt`](../app/src/main/java/com/sertumrosarum/feature/treasury/MysteriesStudyPage.kt) | The four sets, their five mysteries, and each mystery's contemplation sheet |
| Latin in the rosary | [`PrayerLanguageToggle.kt`](../app/src/main/java/com/sertumrosarum/feature/prayer/PrayerLanguageToggle.kt) | Português / Latim above a rosary prayer that has a Latin text |

The mysteries guide needs no new content: it reads the twenty mysteries and their contemplations already bundled for Phase 2.

## The one example

[`TreasurySamples.kt`](../app/src/main/java/com/sertumrosarum/domain/treasury/TreasurySamples.kt) holds the Ave-Maria with one of each thing the screens can show:

- its Portuguese text, taken from the rosary's own `FixedPrayer.HAIL_MARY` so the two cannot differ;
- its Latin text, as printed in the appendix of common prayers of the Compendium of the Catechism of the Catholic Church, including its stress accents;
- its seven matching verses;
- one verse broken down word by word;
- one explained expression, *cheia de graça*.

The word-by-word notes and the explanation are a first draft and should be reviewed by someone with catechetical training before they are relied on.

## Where the logic goes

The screens read one immutable [`TreasuryUiState`](../app/src/main/java/com/sertumrosarum/feature/treasury/TreasuryUiState.kt) and report every touch through `TreasuryActions`. They decide nothing.

`TreasuryStandIn`, in the same file, implements those actions so the screens can be walked through today. It is deliberately thin and is meant to be replaced:

- its state lives in memory, so the open prayer, the language, and the search are lost on rotation and process death;
- its search only matches titles;
- `SertumApp` holds the rosary's language in a `rememberSaveable`, and `TreasurySamples.latinFor` decides which rosary prayer has Latin. Both belong in the prayer session's own state.

Because only the Ave-Maria has a Latin text, choosing Latim in the rosary changes the Ave-Marias and leaves every other prayer in Portuguese until the catalog grows.

`termRanges`, in `AnnotatedPrayerText.kt`, finds an expression by its first exact occurrence in the text. It is the place for a real annotation parser.

## Verification

`TreasuryShellTest` walks the screens through the activity on the JVM. `TreasurySamplesTest` checks that the example's verses rejoin into its two texts and that every expression and gloss occurs in its text. `TesouroScreenshotTest` records the screens in Day, Vigil, and on a narrow phone with `./gradlew recordRoborazziDebug`.
