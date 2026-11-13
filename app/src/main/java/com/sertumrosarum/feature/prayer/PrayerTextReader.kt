package com.sertumrosarum.feature.prayer

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineStart
import kotlin.math.max

/** A stable reading column; paging remains separate from advancing the prayer. */
@Composable
internal fun PrayerTextReader(
    modifier: Modifier = Modifier,
    centerContent: Boolean = false,
    readingKey: Int = 0,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scroll = rememberScrollState()
    val hasMoreBelow by remember(scroll) {
        // At the start, never flash the previous section's "end of text" cue
        // while Android is measuring the new scroll range.
        derivedStateOf { scroll.value == 0 || scroll.canScrollForward }
    }
    val scope = rememberCoroutineScope()
    var previousReadingKey by rememberSaveable { mutableIntStateOf(readingKey) }
    SideEffect {
        if (readingKey != previousReadingKey) {
            previousReadingKey = readingKey
            // Run before layout, rather than scheduling the reset for a later
            // frame. Keep the same scroll state for restoration and accessibility.
            scope.launch(start = CoroutineStart.UNDISPATCHED) { scroll.scrollTo(0) }
        }
    }
    val colors = SertumTheme.colors
    val cueInk = lerp(colors.secondary, colors.ink, .3f)
    val type = SertumTheme.typography
    val lineOverlap = with(LocalDensity.current) { type.prayer.lineHeight.toPx() }

    BoxWithConstraints(modifier.testTag("prayer-reader")) {
        val availableHeight = with(LocalDensity.current) { maxHeight.roundToPx() }
        // Compare against the whole reader so paging cannot create its own overflow.
        val hasOverflow by remember(scroll, availableHeight) {
            derivedStateOf {
                scroll.maxValue in 1 until Int.MAX_VALUE &&
                    scroll.viewportSize.toLong() + scroll.maxValue > availableHeight
            }
        }
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    Modifier.fillMaxSize().padding(horizontal = 6.dp)
                        .readingEdgeFades(scroll)
                        .verticalScroll(scroll)
                        .testTag("prayer-scroll")
                        .padding(vertical = 8.dp),
                    verticalArrangement = if (centerContent) Arrangement.Center else Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = content,
                )
                if (hasOverflow) {
                    ReadingPositionRail(
                        scroll,
                        Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(vertical = 12.dp).width(6.dp),
                    )
                }
            }

            // The touch target never overlaps text or reserves an empty row.
            if (hasOverflow) {
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        Modifier.testTag("prayer-scroll-cue")
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                role = Role.Button,
                                onClickLabel = if (hasMoreBelow) "Ler a próxima parte da oração" else "Voltar ao início da oração",
                            ) {
                                scope.launch {
                                    if (scroll.canScrollForward) {
                                        // Retain at least a line where space permits,
                                        // even when the system font size is enlarged.
                                        val viewport = scroll.viewportSize.toFloat()
                                        val overlap = max(viewport * .2f, lineOverlap).coerceAtMost(viewport * .5f)
                                        scroll.animateScrollBy(viewport - overlap, tween(380))
                                    } else {
                                        scroll.animateScrollTo(0, tween(380))
                                    }
                                }
                            }
                            .semantics {
                                stateDescription = if (hasMoreBelow) "Há mais texto abaixo" else "Fim do texto"
                            }
                            .heightIn(min = 48.dp)
                            .padding(horizontal = 8.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SacredText(
                            if (hasMoreBelow) "Continue lendo" else "Voltar ao início",
                            Modifier.weight(1f, fill = false),
                            style = type.micro,
                            color = cueInk,
                        )
                        SacredIcon(
                            SacredIcon.CHEVRON,
                            Modifier.size(13.dp).rotate(if (hasMoreBelow) 90f else -90f),
                            cueInk,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingPositionRail(scroll: ScrollState, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    val thumb = lerp(colors.marian, colors.ink, .25f)
    Canvas(modifier.testTag("prayer-scroll-position")) {
        if (size.height <= 0f || scroll.maxValue <= 0) return@Canvas
        val viewport = scroll.viewportSize.toFloat()
        val fraction = viewport / (viewport + scroll.maxValue).coerceAtLeast(1f)
        val thumbHeight = (size.height * fraction).coerceIn(28.dp.toPx().coerceAtMost(size.height), size.height)
        val thumbY = (size.height - thumbHeight) * (scroll.value.toFloat() / scroll.maxValue).coerceIn(0f, 1f)
        drawRoundRect(
            colors.marian.copy(alpha = .18f),
            topLeft = Offset(center.x - .75.dp.toPx(), 0f),
            size = Size(1.5.dp.toPx(), size.height),
            cornerRadius = CornerRadius(1.dp.toPx()),
        )
        drawRoundRect(
            thumb,
            topLeft = Offset(center.x - 1.75.dp.toPx(), thumbY),
            size = Size(3.5.dp.toPx(), thumbHeight),
            cornerRadius = CornerRadius(2.dp.toPx()),
        )
    }
}

/** Alpha masks reveal the actual garden background, without opaque edge strips. */
private fun Modifier.readingEdgeFades(scroll: ScrollState) =
    drawWithCache {
        val maskPaint = Paint()
        onDrawWithContent {
            val depth = 18.dp.toPx().coerceAtMost(size.height / 2f)
            val above = scroll.value.toFloat().coerceIn(0f, depth)
            val below = (scroll.maxValue.toLong() - scroll.value).toFloat().coerceIn(0f, depth)
            if (above == 0f && below == 0f) {
                drawContent()
                return@onDrawWithContent
            }
            // Keep the mask and its layer bounds together, including on short
            // layouts. Only reading content is faded; the real page shows through.
            val canvas = drawContext.canvas
            canvas.saveLayer(Rect(Offset.Zero, size), maskPaint)
            try {
                drawContent()
                drawRect(
                    Brush.verticalGradient(
                        0f to if (above > 0f) Color.Black else Color.Transparent,
                        above / size.height to Color.Transparent,
                        1f - below / size.height to Color.Transparent,
                        1f to if (below > 0f) Color.Black else Color.Transparent,
                        endY = size.height,
                    ),
                    blendMode = BlendMode.DstOut,
                )
            } finally {
                canvas.restore()
            }
        }
    }
