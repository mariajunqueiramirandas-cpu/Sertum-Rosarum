package com.sertumrosarum.feature.treasury

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sertumrosarum.domain.treasury.PrayerCategory
import com.sertumrosarum.ui.canvas.DevotionalArtwork
import com.sertumrosarum.ui.canvas.DevotionalIllustration

/** The moment gives each shelf its own iconography; the shared cache decodes each painting once. */
private val PrayerCategory.illustration: DevotionalIllustration
    get() = when (this) {
        PrayerCategory.WAKING -> DevotionalIllustration.TREASURY_MORNING
        PrayerCategory.NIGHT -> DevotionalIllustration.TREASURY_NIGHT
        PrayerCategory.DISTRESS -> DevotionalIllustration.TREASURY_CONSOLATION
        PrayerCategory.THANKSGIVING -> DevotionalIllustration.TREASURY_THANKSGIVING
        PrayerCategory.COMMUNION -> DevotionalIllustration.TREASURY_COMMUNION
        PrayerCategory.MARIAN -> DevotionalIllustration.TREASURY_MARIAN
    }

@Composable
internal fun TreasuryArtwork(category: PrayerCategory, modifier: Modifier = Modifier) {
    DevotionalArtwork(category.illustration, modifier)
}
