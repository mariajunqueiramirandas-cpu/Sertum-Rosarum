# Sertum Rosarum — O Jardim de Maria

The current visual direction is a quiet Marian garden: sage, warm cream, rose, soft botanical illustrations, and a personal sense of shelter. This direction was selected by the user and takes precedence over the earlier visual descriptions in the roadmap and product vision.

The experience should feel welcoming, familiar, and unhurried. The garden is part of the prayer: a bud becomes a soft rose as each bead is prayed, and the five mysteries bloom along a gently curved stem.

## Visual language

- Original Canvas artwork shows a small ceramic arch crowned by a climbing vine, Mary holding the Child, a sheltered lawn, swaying roses, drifting pollen, and changing daylight.
- Bundled **Fraunces** is the voice of the garden. Its softness axis is fully open, so every serif ends rounded. It sets the wordmark, headings, and every prayer. **Nunito** carries controls and small supporting copy.
- Type is sentence case, with generous line spacing. Headings are regular weight, never bold.
- Surfaces are soft, generously rounded, and edged with a hairline in place of a heavy shadow. Shadows are tinted sage or rose, never grey.
- Actions are petals: a pale blush fill with a rosewood label. Nothing on screen is a dark slab.
- A small sprig (two leaves around a closed bud) marks each pause in the text.
- Night is the same garden at dusk: deep forest, cream, muted rose, moonlight, and honey accents.
- Scripture and fixed Catholic prayers retain their traditional wording. Friendly supporting copy surrounds the prayer without changing it.

## Color tokens

| Token | Day | Vigil |
| --- | --- | --- |
| Background | `#FBF8F2` | `#162A27` |
| Surface | `#F1F0E6` | `#1F3731` |
| Elevated surface | `#FFFDF9` | `#27433A` |
| Primary text | `#324B40` | `#F3EFE3` |
| Supporting text | `#667A6E` | `#BACBBB` |
| Muted text | `#8A968B` | `#8CA594` |
| Honey accent | `#B8935A` | `#D8BE84` |
| Rose (flowers and accents) | `#C07A72` | `#E0A99C` |
| Petal action fill | `#F8E3DA` to `#EFCDC2` | `#EDC6BA` to `#DFA99C` |
| Petal action label | `#7A433D` | `#4A2925` |
| Botanical accent | `#789A81` | `#9DBE9D` |
| Soft border | `#E6E9DD` | `#314D40` |

Mystery atmospheres gently wash the top of the page and tint the garden's flowers: peach for Gozosos, honey for Luminosos, lavender for Dolorosos, and soft sky for Gloriosos. The selected mystery set always retains its correct five mysteries and day-of-week resolution.

## Typography and shape

| Role | Family | Size / line height |
| --- | --- | --- |
| Wordmark | Fraunces Medium | 24 / 30 sp |
| Welcome and closing title | Fraunces Regular | 31 / 39 sp |
| Prayer heading | Fraunces Regular | 28 / 35 sp |
| Section heading | Fraunces Medium | 20 / 27 sp |
| Prayer text | Fraunces Regular | 21 / 34 sp |
| Quiet lines | Fraunces Regular | 17 / 27 sp |
| Supporting copy | Nunito Regular | 14–15 / 21–23 sp |
| Controls | Nunito Semibold–Bold | 15–16 / 21 sp |
| Small labels | Nunito Semibold | 13 / 18 sp |

Cards and primary actions use 30 dp corners; selectors and icon controls are full pills and circles. All are built from Compose UI and Foundation, with custom Canvas icons, indications, and animations. Material and Material3 groups are excluded from every dependency configuration, including preview tooling.

## Screens

### Sanctuary

The Marian garden opens the screen, under a small floral wordmark and the appearance control. A centered greeting follows. Four soft mystery controls lead to one card that holds the chosen mysteries: their invitation, then five moments joined by a stem. The page dissolves into the primary prayer action, which stays within reach. A paused crown offers a gentle continuation card.

### Prayer

Five buds and roses show mystery progress along a leafed stem that grows as the prayer advances. Full prayer text has generous space and scrolls for longer prayers. The ten beads are separate, directly selectable closed buds on a garland; each opens into a rose with a spring animation. Previous and next actions remain visible on short screens.

Overflowing prayers show a persistent sage reading-position rail. Soft alpha fades appear only at edges with hidden text, revealing the actual garden background. A separate 48 dp touch target says **Continue lendo** and advances by most of a viewport, retaining an overlap for reading continuity. At the end it becomes **Voltar ao início**. These cues disappear when the complete text fits. Each new prayer starts at the top; scrolling never advances the rosary. Native accessibility scrolling remains available, and the reading cue exposes its action and whether more text remains.

Eyes-closed mode replaces the reading area with a concise prayer heading and calm supporting copy. A large tactile garden surface occupies half the available height, with slow rings breathing around a single rose. Tap or swipe left/up to advance; swipe right to return. Its accessibility action also advances the prayer.

### Crown delivered

A crown of fifty roses woven with leaves surrounds a large central blossom. The screen names the delivered crown, offers a gentle closing message, records the time in prayer in one quiet line, and returns to the sanctuary. There are no streaks, scores, or urgency cues.

## Motion, haptics, and accessibility

- The garden moves on one slow breath: roses sway, pollen drifts, the halo warms. Active roses breathe slowly; completed roses bloom with a damped spring.
- Ambient motion stays still when system animations are turned off.
- Prayer routes crossfade; buttons gently compress on press.
- Small beads use a soft single pulse. Prayer and decade boundaries use two pulses; completion uses three growing pulses.
- Every icon control has a Portuguese description. Mystery and bead controls expose their selected state and position.
- The eyes-closed surface exposes a screen-reader action as well as touch gestures.
- Larger text and long prayers scroll. Compact layouts preserve navigation and prayer position through rotation.
- Fonts, artwork, and all prayer content are bundled and available offline.

Implementation lives in `ui/theme`, `ui/components`, `ui/canvas`, `feature/sanctuary`, and `feature/prayer`.
