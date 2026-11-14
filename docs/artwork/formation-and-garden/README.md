# Twelve paintings for formation and the personal garden

These original model-generated watercolor and gouache paintings extend Sertum's sacred botanical family into Catequese, Início and Meu Jardim. Christ welcomes the learner; a fountain and open gate welcome personal prayer. The four pillars have distinct subjects, while intentions, reflections, saved readings, questions, reconciliation and the liturgical year each receive their own painting.

| Catequese | Meu Jardim | Crer |
| --- | --- | --- |
| <img src="masters/catechesis.png" width="220" alt="Christ teaching with an open book, an arch and roses" /> | <img src="masters/garden.png" width="220" alt="A fountain, open garden gate, roses and a rosary" /> | <img src="masters/faith.png" width="220" alt="A standing crucifix, open book and roses" /> |

| Celebrar | Viver | Rezar |
| --- | --- | --- |
| <img src="masters/sacraments.png" width="220" alt="A chalice, plain host, water vessel, candle, wheat and grapes" /> | <img src="masters/charity.png" width="220" alt="Two hands sharing bread above wheat and roses" /> | <img src="masters/prayer.png" width="220" alt="Mary praying in an ivory veil and blue mantle" /> |

| Intenções | Diário | Guardados |
| --- | --- | --- |
| <img src="masters/intentions.png" width="220" alt="Folded blank petitions, a blue ribbon, rosary and rose" /> | <img src="masters/journal.png" width="220" alt="A blank notebook, feather and flowers on a writing board" /> | <img src="masters/bookmarks.png" width="220" alt="Three prayer books with a ribbon bookmark and rose" /> |

| Perguntas | Reconciliação | Ano litúrgico |
| --- | --- | --- |
| <img src="masters/questions.png" width="220" alt="A lamp beside an open book on a stone ledge" /> | <img src="masters/reconciliation.png" width="220" alt="An inviting wooden confessional, kneeler and roses" /> | <img src="masters/liturgical_year.png" width="220" alt="A chapel surrounded by olive, palm, lilies, wheat and roses" /> |

## Placement and behavior

Início's four destination cards use complete paintings above their labels. Its recovery, learning, prayer and intention cards have smaller contextual illustrations. Catequese has a fixed 144 dp opening frame, 44 dp pillar paintings, illustrated lesson/question/journey cards and a 112 dp painting at the beginning of each lesson or practical guide. Search removes the large opening image to bring results into view. Opening a lesson or guide resets its reading position to the top.

Meu Jardim has a compact 112 dp fountain vignette. Its five personal sections, empty states, record cards, intention picker, intention/journal editors and crown details use the corresponding artwork. Crown dates and counts retain their existing dynamic rose rendering. `FormationArtwork.kt` keeps the editorial image mapping outside the domain catalog; all sixteen lessons receive a contextual illustration, with existing Treasury portraits reused where appropriate.

All paintings are decorative, with null content descriptions. Text labels, semantics and actions remain responsible for navigation. They stay still in Day and Vigil and use `ContentScale.Fit`, preserving faces, halos, crosses and margins. No generated words are used as interface text.

## Masters, edits and runtime exports

The twelve final [PNG masters](masters) are preserved at their original generated resolution: two 1536 × 1024 landscapes and ten 1254 × 1254 squares. [prompts.json](prompts.json) records the generator, shared style, subject prompts, edits, source paths, SHA-256 values and runtime dimensions. Two [initial studies](studies) are retained: image-model edits gave the crucifix and sharing-bread compositions complete silhouettes and transparent margins. The final charity painting also removes a distracting gold disc.

The app bundles twelve transparent WebP exports in `drawable-nodpi`: two at 768 × 512 and ten at 512 × 512. They total **1,106,498 bytes (1.06 MiB)**. Their decoded RGBA memory is approximately **13 MiB** if all twelve are visited. The existing shared lazy devotional cache decodes each painting only once, including when the same image appears on multiple cards. Masters and studies stay outside the APK; the app downloads nothing.

Reproduce the proportional exports from the repository root with ImageMagick:

```sh
for subject in catechesis garden; do
  magick "docs/artwork/formation-and-garden/masters/$subject.png" \
    -resize 768x512 -define webp:method=6 -quality 94 \
    "app/src/main/res/drawable-nodpi/formation_$subject.webp"
done
for subject in faith sacraments charity prayer intentions journal bookmarks questions reconciliation liturgical_year; do
  magick "docs/artwork/formation-and-garden/masters/$subject.png" \
    -resize 512x512 -define webp:method=6 -quality 94 \
    "app/src/main/res/drawable-nodpi/formation_$subject.webp"
done
```

## Native interface previews

These are actual Compose renders from Robolectric native graphics. `FormationArtworkFlowTest` opens a lesson from every pillar and all four practical guides, checking their opening paintings, then renders all five personal sections in both themes. `HomeHubTest`, `GardenFormationFlowTest` and `PocoDisplayLayoutTest` cover destination actions, persistence flows, narrow screens and enlarged text.

```sh
./gradlew recordRoborazziDebug --tests '*HomeHubTest' \
  --tests '*GardenFormationFlowTest' --tests '*FormationArtworkFlowTest' \
  --tests '*PocoDisplayLayoutTest'
```

| Início | Catequese | Diário in Vigil |
| --- | --- | --- |
| <img src="../../screenshots/phase-4/home-day.png" width="220" alt="The illustrated whole-app home" /> | <img src="../../screenshots/phase-4/catechism.png" width="220" alt="Christ welcomes the learner to Catequese" /> | <img src="../../screenshots/phase-4/journal-vigil.png" width="220" alt="The garden fountain and notebook in Vigil" /> |

| Crer | Celebrar | Preparar uma confissão |
| --- | --- | --- |
| <img src="../../screenshots/phase-4/lesson-faith.png" width="220" alt="An illustrated lesson in the profession of faith" /> | <img src="../../screenshots/phase-4/lesson-sacraments.png" width="220" alt="An illustrated lesson on the sacraments" /> | <img src="../../screenshots/phase-4/reconciliation.png" width="220" alt="The confessional above its practical guide" /> |

See [Phase 4](../../phase-4.md) for the learning and personal-continuity behavior.

The artwork change passed all 156 JVM/Compose tests, 15 Treasury tool tests, Android lint, and debug, device-test and release APK builds. The previews use the Android native renderer on the JVM; no physical-device or emulator run is claimed.
