# Roses throughout the prayer

The botanical artwork extends the approved classical [Marian garden](../garden/README.md) through the whole app. Four original model-generated paintings share its species, brush texture, warm blush and ivory petals, and sage foliage. The rose's layered spiral replaces the former geometric five-petal flower.

## Source paintings

- [Blush rose](masters/rose.png): the mark, prayed beads, and central offered flower.
- [Ivory rose](masters/ivory-rose.png): the current expanded mystery and five mystery markers within the finished crown.
- [Closed bud](masters/bud.png): prayers ahead and the small prayer divider.
- [Sage leaf](masters/leaf.png): growing stems, ornaments, and the woven crown.

The [exact generation prompts and reference relationships](prompts.json) are archived here. The rose and leaf reference the approved day garden; the bud and ivory flower also reference the new blush rose. These source PNGs are preserved at full resolution for future image edits.

## Composition and motion

The ten prayer beads blend from a closed bud into an opening rose, with a soft spring and restrained gold selection ring. The five mysteries use the same paintings along a growing stem; compact layouts keep small botanical marks beside their numbers. Individual beads and mysteries retain their existing actions and accessible descriptions.

The delivered crown uses the active script's small-bead count: fifty roses for the ordinary rosary, forty-nine for Tears, or seventy-two for the Franciscan crown. Ivory flowers mark the group boundaries. Two fine stems weave between alternating painted leaves. A larger offered rose or the [devotion's painting](../devotions/README.md) sits inside a quiet gold setting at the center, outside the counted ring. Its flowers have subtle variations in rotation, size, and position so the crown reads as a woven garden. Blossoms and leaf spacing contract for denser rings.

The small dividers combine a painted bud, two sage leaves, and a fading gold thread. The header, pause/resume card, mystery rose symbols, eyes-closed surface, and adaptive launcher use the same blush rose. The launcher retains its ten golden beads and has a separate line-drawn monochrome rose for Android themed icons.

Ambient breathing and the barely perceptible leaf movement hold still when system animations are disabled. Color is supplemented by size, a selection ring, and accessibility state to identify the current bead.

The animation setting is observed while the app is open, so switching system animations off stops ambient movement without restarting. The [bloom animation](demos/bloom.gif) ([MP4](demos/bloom.mp4)) is rendered from the actual Compose UI at controlled frame times; it shows one bud becoming the next active rose. It is a UI rendering, not an emulator recording.

## Scale

At normal phone sizes, open prayer beads use 24 dp exports and the active bead 29 dp, with transparent margins retained. Closed buds use 23 dp exports and a naturally narrower silhouette. The expanded mystery flowers use 26 dp, or 33 dp for the current ivory bloom. The 286 dp delivered emblem keeps the fifty-rose ring about 232 dp across; the offered central rose is about 88 dp. The mark uses 26–30 dp, and the painted divider stays 18 dp high. These sizes remain fixed in dp rather than stretching the paintings to fill wider layouts. Compact mystery controls retain 48 dp touch areas.

## Runtime assets

The four [WebP textures](../../../app/src/main/res/drawable-nodpi) are exported at 384 × 384 pixels and quality 94, retaining alpha transparency. Together they occupy about 111 KiB. `SertumTheme` decodes one shared set, about 2.25 MiB in memory, so repeating flowers never loads a bitmap per bead. The full-resolution masters are documentation assets and are not included in the APK. Everything remains available offline.

Compose positions the flowers, constructs the stems and crown, and handles touch, progress, and motion. This preserves the exact count and interactions while using the painted art at every scale.

## Verification and preview provenance

Previews are rendered from the actual Compose screens using Robolectric native graphics. Current screenshots are reproducible with `./gradlew recordRoborazziDebug`, including the 49- and 72-flower offerings. The original botanical review harness additionally exercised live reduced motion, compact and landscape navigation, and reading cues; that harness remains outside the repository. The bloom demo uses the same renderer with a controlled clock.

Run the standard build and test checks described in [Phase 2](../../phase-2.md#validation). A full instrumented run requires an attached device or emulator.
