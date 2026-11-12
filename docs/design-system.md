# Sertum Rosarum — O Jardim de Maria

The current visual direction is a quiet Marian garden: sage, warm cream, rose, soft botanical illustrations, and a personal sense of shelter. This direction was selected by the user and takes precedence over the earlier visual descriptions in the roadmap and product vision.

The experience should feel welcoming, familiar, and unhurried. The garden is part of the prayer: a bud becomes a soft rose as each bead is prayed, and the five mysteries bloom along a gently curved stem.

## Visual language

- The home garden is a bundled, model-generated sacred painting: an ivory arch, climbing roses, Mary holding the Child, and a stone path. Classical naturalistic faces, a flowing blue mantle, fine gold embroidery, and a radiant halo convey Mary's holiness and grace. Matching day and moonlit paintings share the same composition. Canvas adds restrained moving light and pollen; the painted figures stay still. The [artwork notes, masters, and prompts](artwork/garden/README.md) preserve the chosen direction.
- Bundled **Fraunces** is the voice of the garden. Its softness axis is fully open, so every serif ends rounded. It sets the wordmark, headings, and every prayer. **Nunito** carries controls and small supporting copy.
- Type is sentence case, with generous line spacing. Headings are regular weight, never bold.
- Surfaces are soft, generously rounded, and edged with a hairline in place of a heavy shadow. Shadows are tinted sage or rose, never grey.
- Actions are petals: a pale blush fill with a rosewood label. Nothing on screen is a dark slab.
- A small sprig (two leaves around a closed bud) marks each pause in the text.
- Night is the same garden at dusk: deep forest, cream, muted rose, moonlight, and honey accents.
- All botanical details share four painted textures derived from the garden: a layered blush rose, an ivory rose, a closed bud, and a sage leaf. They appear in the mark, prayer beads, mystery stem, dividers, eyes-closed surface, and delivered crown. The [botanical masters and prompts](artwork/botanical/README.md) preserve the family.
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

Mystery atmospheres gently wash the top of the page and the light around the garden: peach for Gozosos, honey for Luminosos, lavender for Dolorosos, and soft sky for Gloriosos. The painting retains its original rose colors. The selected mystery set always retains its correct five mysteries and day-of-week resolution.

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

### The four places

A soft floating shelf at the foot of the screen holds four places: **Início**, **Roteiros**, **Tesouro**, and **Meu Jardim**. Each has a custom line icon (the garden's niche, a small open rosary, an open book, a sprout) and a label. The chosen place rests on a sage tint; nothing is underlined or badged. Pages leave room for the shelf and fade into it, so it never sits on their words. When prayer begins the shelf slips away below the screen, and returns with the sanctuary. Back returns from any place to Início.

Outside Início, the rose and the place's name sit where the wordmark sits at home.

### Sanctuary

The Marian garden opens the screen, under a small floral wordmark and the appearance control. A centered greeting follows. Four soft mystery controls lead to one card that holds the chosen mysteries: their invitation, then five moments joined by a stem. A quiet link beneath it leads to Roteiros. The page dissolves into the primary prayer action, which stays within reach above the shelf. A paused crown offers a gentle continuation card.

### Roteiros

Every way of praying sits on one of three shelves. **O santo terço** and **Outras coroas devocionais** are single cards whose rows are separated by a hairline; **Terços temáticos** is a row of small painted cards that runs from edge to edge, each washed in the colour of its intention. A devotional crown is introduced by a small glyph of its own shape, seen from above: one dot for each small bead and a honey dot where each group begins, so five decades, seven septets, and seven decades differ at a glance. Small sage pills carry what distinguishes a guide (*Iniciante*, *Ladainha*, its shape).

Touching a guide raises its sheet: what it is, the crown's shape, and the path of the prayer as buds on a stem ending in a rose. The shape is drawn with the same stem and garland as the prayer screen and answers to touch, so a septet of seven can be felt before it is prayed. The complete rosary also shows the litany's two voices. Each guide has one petal action that stays in view and begins its own prayer sequence. Rosaries also offer the mystery selector and bead-by-bead switch. The stem, garland, and delivered crown use the active script’s actual group and bead counts.

### Bottom sheet

A sheet is a rounded page of the background colour that rises over a dusk-green veil, never a grey one. It closes by dragging its handle down, touching the veil, or going back. Its content scrolls beneath the handle, and an action, when there is one, stays at its foot.

### Tesouro and Meu Jardim

Until their own phases, these two places are flowerbeds in bud: a single painted bud in slow rings of light, the place's purpose in one line, and a card naming what will grow there, joined by the same stem as the mysteries. They promise nothing with a date, and Meu Jardim says outright that it will keep no goals or streaks.

### Prayer

Five painted buds and roses show mystery progress along a leafed stem that grows as the prayer advances. The current mystery is an ivory bloom in a fine gold setting. Compact layouts pair the botanical marks with mystery numbers. Full prayer text has generous space and scrolls for longer prayers. The ten beads are separate, directly selectable closed buds on a garland; each blends into a layered blush rose with a spring animation and a fine selection ring. Previous and next actions remain visible on short screens.

Overflowing prayers show a persistent sage reading-position rail. Soft alpha fades appear only at edges with hidden text, revealing the actual garden background. A separate 48 dp touch target says **Continue lendo** and advances by most of a viewport, retaining an overlap for reading continuity. At the end it becomes **Voltar ao início**. These cues disappear when the complete text fits. Each new prayer starts at the top; scrolling never advances the rosary. Native accessibility scrolling remains available, and the reading cue exposes its action and whether more text remains.

A mystery is announced in its dimensions. Its card holds three layers joined by a stem (**A cena bíblica**, **O sentido da contemplação**, **O fruto do mistério**) which open one at a time, the scene first. Layer text is set in the prayer serif and aligned left for reading; the fruit is named in rose above its practice. Beneath a hairline, a switch turns on the fourth dimension, the **terço meditado conta a conta**. The switch is a bud that blooms into a rose as it crosses.

During the decade, the chapter line becomes a small sage pill naming the mystery. It opens the contemplation sheet, where all three layers are laid out to read and, when the bead-by-bead rosary is on, the ten lines of the decade are listed with the current bead resting on a petal. With the bead-by-bead rosary on, each Ave-Maria is led by a soft card with a fine honey edge that carries the mystery's name and the line to contemplate; it takes the heading's place, so the whole prayer still fits beneath it.

The catechetical guide adds one honey-tinted note above a prayer, titled **Por que rezamos assim?**. A note appears the first time a prayer comes in a group, never on every bead.

The mystery stem and the bead garland draw any crown: five or seven flowers on the stem, and three, seven, or ten beads on the garland, with flowers a little smaller when seven share the stem.

Eyes-closed mode replaces the reading area with a concise prayer heading and calm supporting copy. A large tactile garden surface occupies half the available height, with slow rings breathing around a single rose. Tap or swipe left/up to advance; swipe right to return. Its accessibility action also advances the prayer.

### Crown delivered

A crown of fifty roses woven with leaves surrounds a large central blossom. The screen names the delivered crown, offers a gentle closing message, records the time in prayer in one quiet line, and returns to the sanctuary. There are no streaks, scores, or urgency cues.

## Motion, haptics, and accessibility

- The home garden's surrounding light and pollen move on one slow breath; the sacred painting stays at rest. Active prayer roses have a slow surrounding glow; completed roses open with a damped spring. Leaves move by less than a dp along the mystery stem and subtly around the crown.
- Ambient motion stays still when system animations are turned off, including when the setting changes while the app is open.
- Prayer routes and tabs crossfade; buttons gently compress on press. Sheets rise on a damped spring and their veil fades with them.
- Small beads use a soft single pulse. Prayer and decade boundaries use two pulses; completion uses three growing pulses.
- Every icon control has a Portuguese description. Mystery and bead controls expose their selected state and position. Tabs are exposed as tabs with their purpose, switches as switches, each mystery layer as open or closed, and a sheet as a named pane that can be dismissed.
- The eyes-closed surface exposes a screen-reader action as well as touch gestures.
- Larger text and long prayers scroll. Compact layouts preserve navigation and prayer position through rotation.
- Fonts, artwork, and all prayer content are bundled and available offline.

Implementation lives in `ui/theme`, `ui/components`, `ui/canvas`, `feature/sanctuary`, `feature/guides`, `feature/treasury`, `feature/garden`, and `feature/prayer`.
