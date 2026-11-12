# Sertum Rosarum — O Jardim de Maria

The current visual direction is a quiet Marian garden: sage, warm cream, rose, soft botanical illustrations, and a personal sense of shelter. This direction was selected by the user and takes precedence over the earlier visual descriptions in the roadmap and product vision.

The experience should feel welcoming, familiar, and unhurried. The garden is part of the prayer: a bud becomes a soft rose as each bead is prayed, and the five mysteries bloom along a gently curved stem.

## Visual language

- Original Canvas artwork shows a small ceramic arch, Mary holding the Child, a sheltered garden, leaves, roses, and changing daylight.
- Bundled **Nunito** provides rounded, friendly titles and prayer text. **Plus Jakarta Sans** provides concise control labels.
- Type is mostly sentence case, with comfortable line spacing and modest headings.
- Surfaces use soft, generously rounded shapes and quiet color fields.
- Controls use matte rose and sage, custom press feedback, and gentle scale animations.
- Night is the same garden at dusk: deep forest, cream, muted rose, moonlight, and honey accents.
- Scripture and fixed Catholic prayers retain their traditional wording. Friendly supporting copy surrounds the prayer without changing it.

## Color tokens

| Token | Day | Vigil |
| --- | --- | --- |
| Background | `#FAF8F1` | `#172C29` |
| Surface | `#EEF0E6` | `#233D35` |
| Elevated surface | `#FFFEFA` | `#2C493E` |
| Primary text | `#2F493E` | `#F4F1E5` |
| Supporting text | `#6A7C70` | `#BDCEBD` |
| Muted text | `#839084` | `#8EA896` |
| Honey accent | `#BA9655` | `#D4B879` |
| Rose action | `#AB625C` | `#DFA79A` |
| Botanical accent | `#789A81` | `#9DBE9D` |
| Soft border | `#DFE5D8` | `#365246` |

Mystery atmospheres gently tint the opaque garden background and its flowers: peach for Gozosos, honey for Luminosos, lavender for Dolorosos, and soft sky for Gloriosos. The selected mystery set always retains its correct five mysteries and day-of-week resolution.

## Typography and shape

| Role | Family | Size / line height |
| --- | --- | --- |
| Wordmark | Nunito Bold | 25 / 30 sp |
| Welcome | Nunito Bold | 27 / 34 sp |
| Prayer heading | Nunito Bold | 26 / 32 sp |
| Section heading | Nunito Bold | 17–19 / 26 sp |
| Prayer text | Nunito Regular | 20 / 31 sp |
| Supporting copy | Nunito Regular | 14–16 / 24 sp |
| Controls | Plus Jakarta Sans Semibold | 13–14 / 20 sp |
| Small labels | Nunito Semibold | 11 / 16 sp |

Cards use 24–28 dp corners; primary actions use 22 dp corners. Icon controls are soft filled circles. All are built from Compose UI and Foundation, with custom Canvas icons, indications, and animations. Material and Material3 groups are excluded from every dependency configuration, including preview tooling.

## Screens

### Sanctuary

A small floral wordmark and appearance control introduce a personal greeting. The original Marian garden is the visual center. Below it, four soft mystery controls and a concise mystery card lead into the five moments of contemplation. The primary prayer action remains easy to reach. A paused crown offers a gentle continuation card.

### Prayer

Five buds and roses show mystery progress along a botanical arc. Full prayer text has generous space and scrolls for longer prayers. The ten beads are separate, directly selectable buds, blooming with a spring animation. Previous and next actions remain visible on short screens.

Overflowing prayers show a persistent sage reading-position rail. Soft alpha fades appear only at edges with hidden text, revealing the actual garden background. A separate 48 dp touch target says **Continue lendo** and advances by most of a viewport, retaining an overlap for reading continuity. At the end it becomes **Voltar ao início**. These cues disappear when the complete text fits. Each new prayer starts at the top; scrolling never advances the rosary. Native accessibility scrolling remains available, and the reading cue exposes its action and whether more text remains.

Eyes-closed mode replaces the reading area with a concise prayer heading and calm supporting copy. A large tactile garden surface occupies half the available height. Tap or swipe left/up to advance; swipe right to return. Its accessibility action also advances the prayer.

### Crown delivered

A floral crown of fifty roses surrounds a large central blossom. The screen names the delivered crown, offers a gentle closing message, shows prayer duration, and returns to the sanctuary. There are no streaks, scores, or urgency cues.

## Motion, haptics, and accessibility

- Active roses breathe slowly; completed roses bloom with a damped spring.
- Prayer routes crossfade; buttons gently compress on press.
- Small beads use a soft single pulse. Prayer and decade boundaries use two pulses; completion uses three growing pulses.
- Every icon control has a Portuguese description. Mystery and bead controls expose their selected state and position.
- The eyes-closed surface exposes a screen-reader action as well as touch gestures.
- Larger text and long prayers scroll. Compact layouts preserve navigation and prayer position through rotation.
- Fonts, artwork, and all prayer content are bundled and available offline.

Implementation lives in `ui/theme`, `ui/components`, `ui/canvas`, `feature/sanctuary`, and `feature/prayer`.
