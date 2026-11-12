# 🎨 Design System Specification: "Santuário Silencioso"

> **Aesthetic Direction:** Contemporary Sacred (*Sacra Contemporânea*)  
> **UI Toolkit Constraint:** Built **exclusively** on `androidx.compose.ui` and `androidx.compose.foundation`. **Zero Material libraries** (`androidx.compose.material` and `androidx.compose.material3` are strictly prohibited).

---

## 1. Visual Philosophy

Far from cluttered, sterile, or gamified mobile interfaces, the **"Santuário Silencioso"** (*Silent Sanctuary*) aesthetic evokes the visual warmth and stillness of candlelight, warm parchment paper, aged wood, and Gothic stained-glass rose windows (*rosáceas*). Every surface, transition, and haptic pulse is crafted to quiet the mind and foster contemplation.

---

## 2. Color Tokens (`SertumColors`)

All color tokens are provided through a custom `staticCompositionLocalOf<SertumColors>()` inside `SertumTheme`.

### 2.1 Core Palette (Day vs. Vigil Mode)

| Semantic Token | Light Mode (`Dia`) | Hex | Dark Mode (`Vigília`) | Hex | UI Role |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `backgroundPrimary` | **Pergaminho Quente** | `#F9F6F0` | **Azul Vigília** | `#0F172A` | Primary screen canvas; maximum reading comfort without eye strain. |
| `surfaceCard` | **Linho Puro** | `#EFECE6` | **Ardósia Noturna** | `#1E293B` | Mystery cards, explanatory bottom sheets, prayer containers, and menus. |
| `surfaceElevated` | **Marfim Claro** | `#F4F1EA` | **Ardósia Elevada** | `#273549` | Modal sheets, active pills, and floating controls. |
| `textPrimary` | **Tinta Escura** | `#1C1917` | **Marfim Suave** | `#F8FAFC` | Prayer texts, biblical passages, and long-form reading. |
| `textSecondary` | **Sépia Suave** | `#57534E` | **Prata Sereno** | `#CBD5E1` | Subtitles, biblical citations, bead counters, and secondary labels. |
| `textMuted` | **Cinza Pedra** | `#78716C` | **Cinza Neblina** | `#64748B` | Inactive beads, subtle hints, and disabled states. |
| `accentPrimary` | **Ouro Velho** | `#C59B27` | **Ouro Luminoso** | `#EAB308` | Active bead ring, 5-Mystery crown progress arc, and sacred icons. |
| `accentSecondary` | **Rubi Litúrgico** | `#881337` | **Rosa Antigo** | `#FDA4AF` | Primary prayer CTA button (*"Tecer a Coroa"*) and bloomed rose beads. |
| `accentInstitutional` | **Azul Mariano** | `#1E3A8A` | **Azul Celeste** | `#93C5FD` | Solemn titles, headers, and Marian references. |
| `borderSubtle` | **Fio de Ouro / Linho** | `#DFD9CE` | **Borda Noturna** | `#334155` | Hairline 1dp card borders and dividers. |

### 2.2 Dynamic Atmosphere by Mystery (`Atmosfera Dinâmica por Mistério`)

The header aura and subtle accent gradients adapt automatically to the active Mystery set (`MysterySet`):

| Mystery Set | Days of the Week | Liturgical Mood | Light Mode Header Tint / Accent | Dark Mode Header Tint / Accent |
| :--- | :--- | :--- | :--- | :--- |
| **Mistérios Gozosos** (*Joyful*) | Monday & Saturday | *Joy of the Incarnation* (Warm Amber & Soft Gold) | Gradient `#FDF6E2` → `#F9F6F0`, Accent `#D97706` | Gradient `#2A1F0D` → `#0F172A`, Accent `#F59E0B` |
| **Mistérios Luminosos** (*Luminous*) | Thursday | *Light of Christ's Public Life* (Solar Gold & Radiant White) | Gradient `#FFFBEB` → `#F9F6F0`, Accent `#CA8A04` | Gradient `#272312` → `#0F172A`, Accent `#FACC15` |
| **Mistérios Dolorosos** (*Sorrowful*) | Tuesday & Friday | *Passion & Sacrifice* (Deep Violet & Liturgical Wine) | Gradient `#F9EFF3` → `#F9F6F0`, Accent `#881337` | Gradient `#28112B` → `#0F172A`, Accent `#F43F5E` |
| **Mistérios Gloriosos** (*Glorious*) | Wednesday & Sunday | *Resurrection & Glory* (Celestial Blue & Gold) | Gradient `#EFF6FF` → `#F9F6F0`, Accent `#1E3A8A` | Gradient `#112240` → `#0F172A`, Accent `#60A5FA` |

---

## 3. Typography (`SertumTypography`)

Typography is exposed via `staticCompositionLocalOf<SertumTypography>()` using bundled TrueType/OpenType fonts in `app/src/main/res/font`:

### 3.1 Font Families
1. **Display / Solemn Titles (`fontFamilySolemn`)**: `Cinzel` or `Cormorant Garamond`
   * Used for the `SERTUM ROSARUM` wordmark, Mystery titles, and section headers.
   * Uppercase display variants use generous letter-spacing (`0.12.em` to `0.18.em`).
2. **Prayer & Meditation Body (`fontFamilyPrayer`)**: `Lora` or `Cormorant Garamond`
   * Warm, editorial serif optimized for unhurried contemplative reading, Scripture passages, and fixed prayers.
3. **UI, Controls & Counters (`fontFamilySans`)**: `Plus Jakarta Sans` or `Inter`
   * High-legibility humanist sans-serif for navigation tabs, bead counters (`Ave-Maria 4 de 10`), pills, and action buttons.

### 3.2 Type Scale

| Token | Font Family | Size | Line Height | Weight | Letter Spacing | Usage |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `brandTitle` | `Cinzel` | `22.sp` | `28.sp` | SemiBold (`600`) | `0.16.em` | Top app header (`SERTUM`), splash, and final Crown screen. |
| `mysteryHeading` | `Cormorant Garamond` | `28.sp` | `34.sp` | Bold (`700`) | `0.01.em` | Mystery name (e.g., *"1º Mistério Gozoso: A Anunciação"*). |
| `sectionTitle` | `Cormorant Garamond` | `22.sp` | `28.sp` | SemiBold (`600`) | `0.02.em` | Screen titles and card headers. |
| `prayerLarge` | `Lora` | `21.sp` | `33.sp` | Regular (`400`) | `0.01.em` | Primary prayer text on the Immersive Rosary Screen. |
| `prayerBody` | `Lora` | `18.sp` | `28.sp` | Regular (`400`) | `0.01.em` | Biblical narratives, meditations, and Treasury prayers. |
| `meditationItalic` | `Lora` (Italic) | `17.sp` | `26.sp` | Italic (`400`) | `0.01.em` | Bead-by-bead contemplative prompts and Gospel citations. |
| `uiLabelLarge` | `Plus Jakarta Sans` | `16.sp` | `22.sp` | SemiBold (`600`) | `0.02.em` | Primary CTA buttons (*"Tecer a Coroa de Hoje"*). |
| `uiBody` | `Plus Jakarta Sans` | `14.sp` | `20.sp` | Medium (`500`) | `0.01.em` | Metadata, settings labels, and bottom sheet explanations. |
| `counterBadge` | `Plus Jakarta Sans` | `13.sp` | `18.sp` | SemiBold (`600`) | `0.08.em` | Uppercase bead counter (`AVE-MARIA 07 / 10`). |

---

## 4. Custom Compose Component Library (`ui/components`)

Because Material libraries are excluded, Sertum Rosarum defines a bespoke set of foundational Compose components:

### 4.1 Structural & Interactive Primitives
* **`SertumScaffold`**: Edge-to-edge layout container managing system bar insets (`WindowInsets.safeDrawing`), dynamic mystery atmosphere background gradients, and optional bottom navigation bar visibility.
* **`SacredCard`**: Contemplative surface container using `surfaceCard` background, `16.dp` rounded corners, a subtle `1.dp` `borderSubtle` stroke (with optional golden highlight border for active cards), and soft elevation shadow.
* **`SacredPrimaryButton`**: Pill-shaped (`RoundedCornerShape(50)`) button in `accentSecondary` (Liturgical Ruby `#881337` in Day mode / Antique Rose `#FDA4AF` in Vigil mode) with a hairline golden inner border and custom press-scale animation (`animateFloatAsState`).
* **`SacredBottomSheet`**: Custom modal bottom drawer built on `androidx.compose.foundation.gestures.anchoredDraggable` / `detectVerticalDragGestures` for *"Toque para Entender"* term definitions and **Lumen** catechetical explanations.
* **`SertumBottomBar`**: 4-tab navigation bar (`Início`, `Roteiros`, `Tesouro`, `Meu Jardim`) with custom vector icons drawn via Compose `Canvas` / `ImageVector` and golden active indicator dots.

### 4.2 Custom Canvas Prayer Visualizations

```
          ╭───┬───┬───┬───┬───╮
          │ I │II │III│IV │ V │   <-- 1. O Arco dos 5 Mistérios (5-Segment Crown Ring)
          ╰───┴───┴───┴───┴───╯

        "Ave Maria, cheia de graça,
         o Senhor é convosco..."  <-- 2. Editorial Prayer & Meditation Card

            🌺  🌺  🌺  🌼  ○
         🌺                   ○   <-- 3. Contas que Florescem (10-Bead Blooming Arc)
       🌺                       ○
```

1. **`FiveMysteriesArc` (`O Arco dos 5 Mistérios`)**:
   * Rendered at the top of the Immersive Prayer Screen using Compose `Canvas`.
   * Draws a 5-segment arched crown separated by `6°` gaps.
   * **Completed decades** glow in solid `accentPrimary` (`#C59B27` / `#EAB308`) with a central diamond/rose point.
   * **Current decade** pulses gently with an animated golden stroke.
   * **Upcoming decades** are drawn in subtle `borderSubtle`.
   * During closing prayers (*Salve Rainha*), all 5 segments unite into a continuous golden ring.

2. **`BloomingBeadsArc` (`Contas que Florescem`)**:
   * Renders the beads of the current decade (10 beads for standard Rosary decades; dynamically supports 3 introductory beads or 7 beads for the *Coroa das Lágrimas*) along a graceful semi-circular arc.
   * **Unprayed Bead (`Conta Vazada`)**: Hollow circle with `1.5.dp` `textMuted` stroke.
   * **Active Bead (`Conta Ativa`)**: Enlarged circle (`1.3x` scale) with a breathing golden halo (`accentPrimary`) and inner ruby nucleus.
   * **Prayed / Bloomed Bead (`Conta Florescida`)**: Fills with `accentSecondary` (`#881337` / `#FDA4AF`) and `accentPrimary` (`#C59B27`), drawing a delicate 6-petal geometric Gothic rose window (`rosácea`) that blooms via `animateFloatAsState` when the bead completes.
   * Tapping any individual bead on the arc allows jumping directly to that bead if needed, while tapping anywhere in the lower touch zone advances sequentially.

3. **`RoseCrownEmblem` (`A Coroa Completa`)**:
   * Full-circle Gothic rose window surrounded by 50 golden/ruby bloomed roses arranged in 5 decades, used as the centerpiece of the **"Sua Coroa de Rosas foi entregue"** completion screen and on the Sanctuary header.

---

## 5. "Eyes Closed" Mode & Haptic Specification

To allow praying without looking at the screen:

### 5.1 Gesture Mechanics
* In the **Immersive Prayer Screen**, the lower 50% of the screen acts as a unified tactile canvas (`Modifier.pointerInput`):
  * **Single Tap** or **Swipe Left / Up**: Advances to the next bead or prayer step (`onAdvanceStep()`).
  * **Swipe Right**: Steps back one bead (`onPreviousStep()`) to recover from accidental taps.

### 5.2 Haptic Patterns (Android 13+ `Vibrator` / `VibrationEffect`)
Since `minSdk = 33`, the app uses rich `VibrationEffect.Waveform` and primitive compositions:

| Event | Haptic Pattern | Tactile Meaning |
| :--- | :--- | :--- |
| **Ave-Maria / Small Bead Advance** | Single soft tick (`EFFECT_TICK` or `20ms` amplitude `80`) | Confirms one Hail Mary was counted without breaking silence. |
| **Decade Completion / Pai-Nosso / Glória** | Double rich pulse (`45ms` on, `70ms` pause, `65ms` on, amplitude `180`) | Signals by touch alone that the 10 Hail Marys have ended and a new Mystery / Our Father has begun. |
| **Rosary / Crown Completion** | Solemn triple crescendo pulse | Signals that the entire Rosary crown has been completed and offered. |
