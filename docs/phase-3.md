# Phase 3: the offline Treasury

Tesouro contains **61 prayers**, **28 Portuguese/Latin readings** (149 aligned passages and 340 Latin glosses), and **78 explained expressions** across all six moments of life. All twenty mysteries remain available for study, including the biblical scene, meaning, spiritual fruit and ten bead meditations. Prayer, search, explanations and study work without network access. The app has no Internet permission and adds no production dependency.

## Reading and finding a prayer

The library uses a lazy list with stable prayer identifiers. Search looks through titles in both languages, prayer text, summaries, annotated expressions and keywords. It ignores case, accents, punctuation and Latin ligatures (`æ`/`œ`); every search word must match. Category and search filters intersect. Title matches precede body matches within each shelf. A count and an explicit empty state explain the result. Formation is near the search, before the long prayer shelves.

Every prayer has a reading context and an attribution. These identify traditional formulas, Portuguese variants, original pastoral prayers and editorial translations. Available primary-source links open only when the reader chooses them. The app never downloads content as part of prayer.

The bilingual reader aligns complete phrases or stanzas, with Latin glosses. It stacks the two languages on narrow screens or with larger text. Glosses explain literal meanings and grammar where the usual Portuguese differs. The Portuguese columns retain the same prayer, rather than silently replacing it with a literal translation. A prayer without a corresponding Latin version displays Portuguese while preserving the reader's language choice for the next bilingual prayer.

## Original Treasury artwork

Seven [new original sacred paintings](artwork/treasury/README.md) give Tesouro its own visual identity: a chapel-garden entrance and distinct morning, night, consolation, thanksgiving, communion and Marian illustrations. They appear in the category openings, shelf headings, prayer cards and reading pages. Search brings results forward by hiding the large introduction. Transparent WebP exports are bundled and decoded lazily through the existing shared cache; the unmodified masters and generation record are archived separately.

## Touch to understand

`PrayerAnnotationParser` is independent of Compose. It matches every occurrence of an expression at word boundaries, ignoring case and allowing whitespace and line breaks. It recognizes canonically equivalent Unicode accents while returning UTF-16 offsets into the untouched prayer. Different accents remain distinct. Longer expressions win overlaps; editorial order breaks ties. Blank terms and regex metacharacters are safe.

Annotated expressions are clickable in Portuguese and in the Portuguese column of the bilingual view. The same expressions have accessible links with 48 dp targets. Each opens the custom `SacredBottomSheet`, with origin, biblical references, present-day meaning and Catechism paragraphs. The drawer supports its drag handle, backdrop dismissal and system Back. Explanations distinguish doctrine, biblical imagery and devotional metaphor; they do not turn prayer into a promise of a particular outcome.

## Latin throughout the rosary

All ten `FixedPrayer` values have Latin text: the Sign of the Cross, offering, Apostles' Creed, Our Father, Hail Mary, Glory Be, Fatima prayer, thanksgiving and Salve Regina. The major inherited formulas are distinguished from **editorial Latin translations of the Brazilian offering and thanksgiving**. Those two also have personal and group variants.

The PT/LAT choice belongs to `PrayerSessionViewModel`, survives navigation and Android saved-state restoration, and remains selected through the session. The Latin text resets the reading scroll on a language switch so the new version starts visibly. Mystery announcements, meditations, catechesis and the additional prayers in extended guides remain in Portuguese; this control selects the language of the available prayer formula, rather than translating the entire interface or the chaplet catalog.

`TreasuryViewModel` saves the query, category, open prayer identifier, language, glossary verse, explanation identifier, study set and mystery index through `SavedStateHandle`. It rejects obsolete identifiers and out-of-range indexes. It does not serialize the large content catalog into the saved-state bundle. Library scroll position is saveable and is preserved when returning from a prayer. Long-term preferences, history and the intention notebook are now delivered by [Phase 4](phase-4.md).

## Content and provenance

The editable source is [`content/treasury/catalog.json`](../content/treasury/catalog.json). Its [inventory and editorial notes](../content/treasury/README.md) list every prayer. `GeneratedTreasury.kt` is deterministically compiled from this JSON; runtime code never relies on an online parser. Unit tests enforce exact agreement with every Portuguese fixed prayer and with the full existing Loreto litany and its closing collect.

The source importer fetches the Holy See's Portuguese Compendium, whose common-prayer appendix includes Portuguese and Latin. It preserves the source separately from published edits:

- HTTPS host restriction, identifiable user agent and `robots.txt` check;
- 30-second request timeout, 2 MiB response limit and bounded retries; numeric and HTTP-date `Retry-After` values are honored, with long deferrals returned to the editor instead of retrying early;
- conditional requests from a verified local cache;
- pinned source SHA-256 and extraction hash;
- explicit appendix boundaries and minimum expected sections, so layout drift fails;
- atomic snapshot writes; fetching never overwrites the edited prayer catalog;
- offline snapshot verification and generated-code drift checks in CI.

The source has genuine transcription errors. The catalog documents corrections (`Sancii` → `Sancti`, `quelli` → `quem`, `posi` → `post`, `curo sanctis` → `cum sanctis`, `Parác1ito` → `Paráclito`, `sáueium` → `sáucium`). Its Portuguese hymn labeled *Veni Creator* actually resembles the Pentecost sequence. The library keeps these two works separate, with explicitly editorial Portuguese translations aligned to their respective Latin hymns. The imported snapshot remains unchanged for comparison.

Portuguese variants in longer imported prayers are retained and identified. The two psalms are complete editorial renderings for personal prayer, with both numbering systems; they are not presented as an approved lectionary translation. Original prayers and the prose explanations are identified as editorial content. Traditional attributions with uncertain authorship (including the Memorare and Anima Christi) are not presented as established facts.

### Maintain the library

From the repository root, using Python 3 and its standard library:

```sh
# Fetch and extract the pinned primary source; does not change published prayers.
python3 tools/treasury/treasury.py fetch

# Edit content/treasury/catalog.json; then validate and regenerate app content.
python3 tools/treasury/treasury.py generate

# Reproducibility and importer checks, without network access.
python3 tools/treasury/treasury.py fetch --offline
python3 tools/treasury/treasury.py generate --check
python3 -m unittest discover -s tools/treasury/tests -v
```

If the source changes, the importer fails before overwriting the snapshot. Review the upstream page, update the manifest digest intentionally, then fetch and compare the extracted paragraphs. An import is evidence for editorial work, not automatic publication. New bilingual entries must reconstruct both full texts; every explained expression and Latin gloss must occur in its corresponding text.

## Verification

The JVM suite covers catalog integrity and full-text search; Unicode boundaries, repetitions and overlapping annotations; saved-state recovery and corrupt-state fallbacks; and actual Compose navigation, language changes and explanation sheets. Device tests exercise inline links, drag dismissal, full-text search, glossary restoration, all twenty study sheets and every fixed prayer in Latin. Screenshot tests cover Day, Vigil, a narrow phone and the bilingual glossary. CI also checks the importer, source snapshots and generated output before building the app.

```sh
./gradlew assembleDebug assembleDebugAndroidTest assembleRelease testDebugUnitTest lintDebug
./gradlew connectedDebugAndroidTest
./gradlew recordRoborazziDebug --tests 'com.sertumrosarum.screenshots.TesouroScreenshotTest'
```
