# The Marian garden

The home-screen garden uses original model-generated sacred paintings, made with the image generation tool and bundled with the app. The final direction follows the user's preferred classical painted garden: the serene dignity of Raphael's Madonnas and the gentle radiance of Murillo, with soft botanical surroundings. These are new illustrations, not reproductions of an existing painting.

Mary and the Child are the focus. Naturalistic faces, a tender embrace, flowing Marian blue and ivory, narrow gold embroidery, and a radiant nimbus convey holiness and grace. The climbing roses, slender branch, ferns, and stone path give the figures a sheltered garden without competing with them.

## Final artwork and prompts

- [Day master](masters/day.png) — the full-resolution transparent PNG, 1374 × 1145.
- [Vigil master](masters/vigil.png) — the matching moonlit edit, preserving the same figures, anatomy, flowers, and composition.
- [Day generation prompt](day-prompt.txt).
- [Vigil editing prompt](vigil-prompt.txt).
- [All seven generation rounds](prompts.json), including the reference relationship between edits.

For future revisions, use the day master as the image-editing reference and keep the faces, hands, pose, architecture, and garden silhouette consistent. Derive Vigil from the selected daytime image instead of generating a separate scene. The original day prompt refers to the classical garden from round 02 and the user's attached preview of that same candidate; the final master is the reference to use going forward. Prompts record the actual requests, not a promise of identical output from a new generation.

## What the exploration established

| Round | Direction | Finding |
| --- | --- | --- |
| 01 | Close watercolor Madonna and roses | Tender, but the portrait and dense flowers dominated the garden. |
| 02 | Full-length classical Madonna in an airy garden | The user's preferred composition and naturalistic painted treatment. |
| 03–05 | Simplified contemporary gouache, refined day and Vigil | Clear at small sizes, but the user preferred the sacred presence of the classical version. |
| 06 | Classical garden with more luminous sacred figures | Selected day: serene face, sculptural blue mantle, fine gold embroidery and radiant halo. |
| 07 | Moonlit edit of round 06 | Selected Vigil: deeper sage foliage and stone, with warm light retained on the holy figures. |

## App integration

The runtime assets are [day](../../../app/src/main/res/drawable-nodpi/sanctuary_garden_day.webp) and [Vigil](../../../app/src/main/res/drawable-nodpi/sanctuary_garden_vigil.webp). They retain the masters' dimensions and alpha transparency and are encoded as WebP at quality 92. Together they add about 1.1 MB of image resources; the full-resolution PNG masters stay in documentation and are not packaged in the APK. No runtime generation or network access is needed.

`SanctuaryGarden` fits the complete image without cropping, with a small protective inset. Sparse animated pollen, a soft mystery-colored light, and the sun or crescent remain in Compose Canvas. The painted figures stay still. Ambient animation respects the existing system animation setting. The painting's rose colors remain constant; the page wash and surrounding light respond to the selected mystery.

The prayer beads, mystery stem, delivered crown, and small ornaments use a [coordinated family of painted botanical textures](../botanical/README.md), derived from this garden and composed and animated in Canvas.

## Preview provenance

The updated sanctuary screenshots in `docs/screenshots` are rendered from the actual Android Compose screen with Robolectric native graphics at 412 × 915 dp and 2× density. They are not photographs or emulator captures. A temporary review harness checks both appearances, all four mystery selections, and entry into prayer at that size, plus both appearances and entry into prayer at 320 × 640 dp. It is kept outside the application source and introduces no runtime or project dependencies.

The standard project verification passes: `assembleDebug`, all 19 JVM unit tests, `compileDebugAndroidTestKotlin`, and `lintDebug`. Lint reports 30 dependency-update warnings and no errors. The full instrumented device suite was not run; no device or emulator is attached to this environment.
