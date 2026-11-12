package com.sertumrosarum.ui.canvas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.sertumrosarum.R
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

/** One small, shared set of painted textures for every flower in the app. */
@Immutable
internal data class BotanicalArtwork(
    val rose: ImageBitmap,
    val ivoryRose: ImageBitmap,
    val bud: ImageBitmap,
    val leaf: ImageBitmap,
)

internal val LocalBotanicalArtwork = staticCompositionLocalOf<BotanicalArtwork> {
    error("Botanical artwork must be provided by SertumTheme")
}

@Composable
internal fun rememberBotanicalArtwork(): BotanicalArtwork {
    val resources = LocalContext.current.resources
    return remember(resources) {
        BotanicalArtwork(
            ImageBitmap.imageResource(resources, R.drawable.botanical_rose),
            ImageBitmap.imageResource(resources, R.drawable.botanical_ivory_rose),
            ImageBitmap.imageResource(resources, R.drawable.botanical_bud),
            ImageBitmap.imageResource(resources, R.drawable.botanical_leaf),
        )
    }
}

/** The square exports retain their transparent margins; no petal is cropped. */
internal fun DrawScope.paintBotanical(
    image: ImageBitmap,
    at: Offset,
    extent: Float,
    alpha: Float = 1f,
    turn: Float = 0f,
) {
    if (extent <= 0f || alpha <= 0f) return
    val side = extent.roundToInt().coerceAtLeast(1)
    rotate(turn, at) {
        drawImage(
            image,
            dstOffset = IntOffset((at.x - extent / 2f).roundToInt(), (at.y - extent / 2f).roundToInt()),
            dstSize = IntSize(side, side),
            alpha = alpha.coerceIn(0f, 1f),
            filterQuality = FilterQuality.High,
        )
    }
}

/** Orient the painted leaf's petiole along a stem, preserving its natural shape. */
internal fun DrawScope.paintLeaf(image: ImageBitmap, root: Offset, tip: Offset, alpha: Float = 1f) {
    val direction = tip - root
    val turn = (atan2(direction.y, direction.x) * 180f / PI).toFloat() + 70f
    paintBotanical(image, (root + tip) / 2f, direction.getDistance() / .82f, alpha, turn)
}
