# Sertum Rosarum — O Jardim de Maria

The current visual direction is a quiet Marian garden: sage, warm cream, rose, soft botanical illustrations, and a personal sense of shelter. This direction was selected by the user and takes precedence over the earlier visual descriptions in the roadmap and product vision.

The experience should feel welcoming, familiar, and unhurried. The garden is part of the prayer: a bud becomes a soft rose as each bead is prayed, and the five mysteries bloom along a gently curved stem.

## Visual language

- The home garden is a bundled, model-generated sacred painting: an ivory arch, climbing roses, Mary holding the Child, and a stone path. Classical naturalistic faces, a flowing blue mantle, fine gold embroidery, and a radiant halo convey Mary's holiness and grace. Matching day and moonlit paintings share the same composition. Canvas adds restrained moving light and pollen; the painted figures stay still. The [artwork notes, masters, and prompts](artwork/garden/README.md) preserve the chosen direction.
- Bundled **Fraunces** is the voice of the garden. Its softness axis is baked fully open into fixed-weight font files, so every serif ends rounded consistently across devices. It sets the wordmark, headings, and every prayer. **Nunito** carries controls and small supporting copy, with separate regular, medium, semibold, and bold resources. [Font sources and regeneration](fonts/README.md) preserve the exact cuts.
- Type is sentence case, with generous line spacing. Headings use balanced line breaking; Portuguese paragraphs use high-quality line breaking. Android fallback-font padding is disabled and outer line leading is trimmed so visible letterforms align consistently. Headings are regular weight, never bold.
- Surfaces are soft, generously rounded, and edged with a hairline in place of a heavy shadow. Shadows are tinted sage or rose, never grey.
- Pill shapes are discouraged and used sparingly where they clearly communicate selection or the main action. Status, categories, counts, Scripture citations, and other supporting labels use plain typography. Secondary actions use open text with a small directional icon and a generous touch target. Give every enclosed shape a functional reason to exist.
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
| Supporting text | `#5C6E61` | `#BACBBB` |
| Muted text | `#5E7062` | `#8CA594` |
| Honey accent | `#B8935A` | `#D8BE84` |
| Rose (flowers and accents) | `#C07A72` | `#E0A99C` |
| Petal action fill | `#F6DED5` to `#EDC7BC` | `#EDC6BA` to `#DFA99C` |
| Petal action label | `#713F39` | `#4A2925` |
| Botanical accent | `#789A81` | `#9DBE9D` |
| Soft border | `#E6E9DD` | `#314D40` |

Mystery atmospheres gently wash the top of the page and the light around the garden: peach for Gozosos, honey for Luminosos, lavender for Dolorosos, and soft sky for Gloriosos. The painting retains its original rose colors. The selected mystery set always retains its correct five mysteries and day-of-week resolution. Small sage captions use a darker blend of botanical sage and primary ink in Day; supporting and muted text retain at least 4.5:1 contrast on the three base Day surfaces.

## Typography and shape

| Role | Family | Size / line height |
| --- | --- | --- |
| Wordmark | Fraunces Medium | 24 / 30 sp |
| Welcome and closing title | Fraunces Regular | 31 / 39 sp |
| Prayer and sheet heading | Fraunces Regular | 28 / 35 sp |
| Section heading | Fraunces Medium | 20 / 27 sp |
| Prayer text | Fraunces Regular | 21 / 34 sp |
| Quiet lines | Fraunces Regular | 17 / 27 sp |
| Supporting copy | Nunito Regular | 14–15 / 21–23 sp |
| Controls | Nunito Semibold–Bold | 15–16 / 21 sp |
| Small labels | Nunito Semibold | 13 / 18 sp |

Cards and primary actions use 30 dp corners. The main prayer action, mystery selector, and active bottom-navigation destination retain rounded fills to communicate action or selection; icon controls remain circular. Supporting text stays unenclosed. All are built from Compose UI and Foundation, with custom Canvas icons, indications, and animations. Material and Material3 groups are excluded from every dependency configuration, including preview tooling.

## Screens

### The four places

A soft floating shelf at the foot of the screen holds four places: **Início**, **Roteiros**, **Tesouro**, and **Meu Jardim**. Each has a custom line icon (the garden's niche, a small open rosary, an open book, a sprout) and a label. The chosen place rests on a sage tint; nothing is underlined or badged. Pages reserve the shelf’s measured height and fade into it, so it never sits on their words. Destination labels wrap at word boundaries; when four columns would split a word, they reflow into two rows with icons beside the labels. Every destination in a row grows to the same height. The shelf crossfades away with the sanctuary when prayer begins and returns with it. Back returns from any place to Início.

Outside Início, the rose and the place's name sit where the wordmark sits at home.

Once content scrolls beneath a fixed header, its first 32 dp fade to reveal the actual page wash. A faint sage shadow adds separation without an opaque strip. Both effects disappear at the top of the page; sheets use a shorter 16 dp fade beneath their handle.

### Sanctuary

The Marian garden opens the screen, under a small floral wordmark and the appearance control. A centered greeting follows. The painting scales with the available page height. Four soft mystery controls normally share one row. Each reserves its own label's selected width and modest side padding, keeping normal and modestly enlarged text together without shrinking the type. Larger text can reflow into two columns, or one when necessary. They lead to one card that holds the chosen mysteries: their invitation, then five moments joined by a stem. The section heading always reserves the space for its plain “Hoje” label and small dot, so selecting another set does not shift the controls or card. The hidden label is excluded from accessibility. Below the mysteries, **A sós** and **Em grupo** appear as small text choices with a check beside the selected mode, without a filled track or border. New users begin in **A sós**. The mode choice scrolls with the page and keeps a 48 dp touch target. A quiet link beneath it leads to Roteiros. The page dissolves into the primary prayer action, which stays within reach above the shelf. A faint downward chevron invites scrolling and advances part of a page when touched; it disappears at the end. A paused crown offers a gentle continuation card.

### Roteiros

Every way of praying sits on one of three shelves. **O santo terço** and **Outras coroas devocionais** are single cards whose rows are separated by a hairline; **Terços temáticos** is a row of small painted cards that runs from edge to edge, each washed in the colour of its intention. An open prayer book welcomes the reader. Devotional rows carry their own classical sacred portraits; themed cards carry the Holy Family, a lily and lamp for consolation, or a dove of peace. Plain sage text identifies what distinguishes a guide (*Iniciante*, *Ladainha*, its shape), and guide sheets introduce their category with a simple caption. The traditional rosary retains its small crown glyph. The [paintings, iconography, scales, and prompts](artwork/devotions/README.md) form one family with the home garden.

Touching a guide raises its sheet: what it is, the crown's shape, and the path of the prayer as buds on a stem ending in a rose. The shape is drawn with the same stem and garland as the prayer screen and answers to touch, so a septet of seven can be felt before it is prayed. The complete rosary also shows the litany's two voices. Each guide has one petal action that stays in view and begins its own prayer sequence. Rosaries also offer the mystery selector and bead-by-bead switch. The stem, garland, and delivered crown use the active script’s actual group and bead counts.

### Bottom sheet

A sheet is a rounded page of the background colour that rises over a dusk-green veil, never a grey one. It closes by dragging its handle down, touching the veil, or going back. Its content scrolls beneath the handle, and an action, when there is one, stays at its foot.

### Tesouro

The Treasury opens under its own painted chapel-garden entrance. Six original transparent paintings distinguish its moments of life in shelf headings, prayer rows and reading pages. Choosing a moment brings its painting into the opening; searching hides this large introduction so results come forward. A soft search field sits on the surface colour; its cross appears only when there is something to clear. The moments of life follow as plain words in a row that runs past the edge, with a small sage bead under the chosen one. Each moment that has prayers is a shelf in the Roteiros form: a heading, a caption, and one card whose rows are separated by a hairline. What a prayer offers beyond its text (*Português e latim*, *Toque para entender*) stays typographic. A moment with nothing on it, or a search with no result, says so under a single bud. A **Formação** shelf leads to the twenty mysteries.

A prayer or the mysteries guide opens as a page of the same place: the way back takes the rose's seat beside **Tesouro**, and the shelf of four places stays. The prayer is set exactly as in the rosary, at 21 / 34 sp. When it has a Latin text, one soft track offers **Português**, **Latim**, and **Lado a lado**, in the mystery selector's form.

- An explained expression rests on a short string of honey beads; the wording and its colour are untouched. Beneath the prayer the same expressions are listed as open links with a 48 dp target. Either one raises a sheet with three parts: where the expression comes from, where Scripture says it, and what it asks of us today, with the Catechism reference as a closing line.
- Side by side sets Portuguese beside Latin, verse by verse, with the Latin in the rose of a litany's answer. On a narrow screen or with enlarged text the Latin steps in beneath its verse instead. A verse that has been broken down carries a small chevron and opens a washed panel, **Palavra por palavra**.
- Latin text is marked as Latin for screen readers.

The mysteries guide reuses the mystery selector, then lists the five mysteries of the chosen set on one card, each with its fruit and Scripture citation. Touching one raises the same contemplation sheet used during prayer, with the ten bead lines and without the bead-by-bead switch.

Inside the rosary, a prayer that has a Latin text shows **Português** and **Latim** as two plain words above it, in the form of the choice between praying alone and in a group.

### Meu Jardim

Until its own phase, this place remains in bud: a single painted bud in slow rings of light. “Ainda em botão” is a plain caption beneath the illustration. It states its purpose in one line and names what will grow there on a card joined by the same stem as the mysteries. It promises nothing with a date, and says outright that it will keep no goals or streaks.

### Prayer

Reading occupies most of the prayer screen. The header centers the session title above its prayer mode; meaningful guide names get their own wrapping line. The generic “Minha oração” caption is omitted. Five painted buds and roses show mystery progress along a shallow, curved 66 dp stem; the current mystery is an ivory bloom in a fine gold setting. Landscape layouts pair botanical marks with mystery numbers. The lower bead garland keeps its curve in 58 dp. Guide sheets retain the original larger arcs. Both sizes keep the painted textures, spring blooms, breathing glows, swaying leaves, and growing stem.

Prayer and contemplation text keep their 21 / 34 sp and 17 / 27 sp sizes. Longer passages are aligned to the start with a natural ragged edge, using Android's paragraph line breaking. The Sign of the Cross and Glory Be retain a centered setting. Text columns are capped at 480 dp. Titles and the painted sprig remain horizontally centered, with prayer headings at 28 / 35 sp. Reading starts at a consistent top edge for short and long prayers, so the heading and first line do not move when paging controls appear. The concise eyes-closed introduction retains vertical centering. No prayer wording or paragraph breaks are changed.

The progress counter and a labeled **Olhos fechados** switch share one row above the previous and next actions. The switch, paging action, and navigation targets stay at least 48 dp high. The next action retains its full 60 dp petal form in portrait. The delivered crown has its own screen, leaving closing prayers room to breathe.

Overflowing prayers show a sage reading-position rail and 18 dp alpha fades only at edges with hidden text, revealing the actual garden background. **Continue lendo** has a separate, right-aligned 48 dp touch target and advances by most of a viewport with a line of overlap. At the end it becomes **Voltar ao início**. Readers that fit reserve no paging row. Each prayer starts at the top; scrolling never advances the rosary. Native accessibility scrolling remains available.

A rounded meditation card holds three numbered tabs, **Cena**, **Sentido**, and **Fruto**, joined by a fine stem. Their full names are exposed to accessibility. The active node gains a honey edge as the stem extends to it. Only the newly selected section fades in, at its final height; there is no outgoing paragraph underneath it and no height spring. The surrounding heading and tabs stay anchored to the top of the reading area rather than recentering when the body changes. Choosing a section resets its reading before layout, so switching from the bottom never carries an old offset into the new text. Paging gestures from the previous section are cancelled. Reduced motion removes these transitions.

Inside the card, the small prayer book and Scripture citation share one line above the quote. Quote, scene, meaning, fruit, and practice use the same left edge, with no indented text gutter or repeated section heading. The contemplation sheet lays out all three sections vertically with full-width bodies. Beneath a hairline, the **terço meditado conta a conta** switch retains its explanatory caption and its bud-to-rose animation.

During the decade, an open text link names the mystery beside a small ivory rose and chevron. It opens the contemplation sheet, where all three layers are laid out to read and, when the bead-by-bead rosary is on, the ten lines of the decade are listed with the current bead resting on a petal. Scripture references use plain text. With the bead-by-bead rosary on, each Ave-Maria is led by a soft card with a fine honey edge that carries the mystery's name and the line to contemplate; it takes the heading's place, so the whole prayer still fits beneath it.

The catechetical guide adds one honey-tinted note above a prayer, titled **Por que rezamos assim?**. A note appears the first time a prayer comes in a group, never on every bead.

The mystery stem and the bead garland draw any crown: five or seven flowers on the stem, and three, seven, or ten beads on the garland, with flowers a little smaller when seven share the stem.

Eyes-closed mode replaces the reading area with a concise prayer heading and calm supporting copy. A large tactile garden surface occupies half the available height, with slow rings breathing around a single rose. Tap or swipe left/up to advance; swipe right to return. Its accessibility action also advances the prayer.

### Crown delivered

A crown of roses woven with leaves surrounds a large central blossom or the chosen devotion's sacred painting. The ring uses the script's actual bead and group counts; blossoms and leaves contract for denser rings, keeping all 72 Franciscan flowers distinct. The screen names the delivered crown, offers a gentle closing message, records the time in prayer as a plain line of text, and returns to the sanctuary. There are no streaks, scores, or urgency cues.

## Motion, haptics, and accessibility

- The home garden's surrounding light and pollen move on one slow breath; the sacred painting stays at rest. Active prayer roses have a slow surrounding glow; completed roses open with a damped spring. Leaves move by less than a dp along the mystery stem and subtly around the crown.
- Ambient motion stays still when system animations are turned off, including when the setting changes while the app is open.
- Prayer routes and tabs crossfade; buttons gently compress on press. Sheets rise on a damped spring and their veil fades with them.
- Small beads use a soft single pulse. Prayer and decade boundaries use two pulses; completion uses three growing pulses.
- Every icon control has a Portuguese description. Mystery and bead controls expose their selected state and position. Tabs are exposed as tabs with their purpose, switches as switches, each mystery layer as open or closed, and a sheet as a named pane that can be dismissed.
- The eyes-closed surface exposes a screen-reader action as well as touch gestures.
- Larger text and long prayers scroll. Compact layouts preserve navigation and prayer position through rotation.
- Fonts, artwork, and all prayer content are bundled and available offline.

The [POCO X8 Pro display notes](poco-x8-pro.md) record the device specifications, adaptive margins, and portrait, enlarged-text, and landscape validation profiles.

Implementation lives in `ui/theme`, `ui/components`, `ui/canvas`, `feature/sanctuary`, `feature/guides`, `feature/treasury`, `feature/garden`, and `feature/prayer`.


## Five destinations and the central home

The current app uses Catequese, Roteiros, Início, Tesouro and Meu Jardim, in that order. Início is a distinct rose in a petal-colored circle at the physical center. The four surrounding destinations retain the garden's quiet icon treatment. At larger text scales, show the selected destination label at full width beneath the five accessible icon targets rather than breaking words or moving the center button. Navigation disappears during prayer.

Início is the whole app's dashboard: a compact garden and greeting, responsive destination cards, prayer recovery, continued formation and personal intentions. Rosary preparation and its mystery selector are within Roteiros. The first-visit invitation follows the destination cards so it does not displace the app's primary routes.

The [twelve formation and personal-garden paintings](artwork/formation-and-garden/README.md) give the dashboard, Catequese and Meu Jardim the same watercolor and gouache treatment as prayer and Tesouro. Complete transparent paintings sit above the four home destination labels and beside personal and learning cards. Christ teaching welcomes Catequese; a fountain and open garden gate welcome Meu Jardim. Crer, Celebrar, Viver and Rezar have distinct paintings, as do questions, reconciliation, the liturgical year, petitions, the journal and saved readings. Fixed illustration frames prevent theme or subject changes from changing their height. Search hides the large formation painting; opening a lesson or practical guide returns its content to the beginning. These images remain still, uncropped and decorative, and reuse the shared lazy bitmap cache.
