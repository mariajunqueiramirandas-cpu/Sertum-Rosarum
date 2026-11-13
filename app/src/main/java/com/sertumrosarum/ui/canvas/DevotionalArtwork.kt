package com.sertumrosarum.ui.canvas

import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import com.sertumrosarum.R

enum class DevotionalIllustration(internal val resourceId: Int) {
    PRAYER_BOOK(R.drawable.devotion_prayer_book),
    HOLY_FAMILY(R.drawable.devotion_holy_family),
    DIVINE_MERCY(R.drawable.devotion_divine_mercy),
    SAINT_JOSEPH(R.drawable.devotion_saint_joseph),
    OUR_LADY_OF_TEARS(R.drawable.devotion_our_lady_of_tears),
    CONSOLATION(R.drawable.devotion_consolation),
    PEACE(R.drawable.devotion_peace),
    SEVEN_JOYS(R.drawable.devotion_seven_joys),
}

/** Decode each painting once, only when it is first needed; cards and sheets share it. */
@Stable
internal class DevotionalArtworkCache(private val resources: Resources) {
    private val images = mutableMapOf<DevotionalIllustration, ImageBitmap>()
    fun image(illustration: DevotionalIllustration): ImageBitmap = images.getOrPut(illustration) {
        ImageBitmap.imageResource(resources, illustration.resourceId)
    }
}

internal val LocalDevotionalArtwork = staticCompositionLocalOf<DevotionalArtworkCache> {
    error("Devotional artwork must be provided by SertumTheme")
}

@Composable
internal fun rememberDevotionalArtwork(): DevotionalArtworkCache {
    val resources = LocalContext.current.resources
    return remember(resources) { DevotionalArtworkCache(resources) }
}

/** The adjacent title names the devotion; the illustration is decorative and never cropped. */
@Composable
fun DevotionalArtwork(illustration: DevotionalIllustration, modifier: Modifier = Modifier) {
    val image = LocalDevotionalArtwork.current.image(illustration)
    Image(image, contentDescription = null, modifier = modifier, contentScale = ContentScale.Fit, filterQuality = FilterQuality.High)
}
