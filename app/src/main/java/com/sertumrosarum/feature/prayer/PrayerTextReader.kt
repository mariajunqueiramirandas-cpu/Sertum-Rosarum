package com.sertumrosarum.feature.prayer

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.theme.SertumTheme
import kotlinx.coroutines.launch
import kotlin.math.max

/** Keeps the reading cues outside the text, with native scroll semantics intact. */
@Composable
internal fun PrayerTextReader(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scroll = rememberScrollState()
    val hasOverflow by remember { derivedStateOf { scroll.maxValue in 1 until Int.MAX_VALUE } }
    val hasMoreBelow by remember { derivedStateOf { scroll.canScrollForward } }
    val scope = rememberCoroutineScope()
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val lineOverlap = with(LocalDensity.current) { type.prayer.lineHeight.toPx() }

    Column(modifier.testTag("prayer-reader"), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            Column(
                Modifier.fillMaxSize().padding(horizontal = 12.dp)
                    .readingEdgeFades(scroll)
                    .verticalScroll(scroll)
                    .testTag("prayer-scroll")
                    .padding(vertical = 18.dp),
                verticalArrangement = Arrangement.Center,
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

        // A stable slot prevents the cue from covering text or changing the scroll
        // range as it appears. It also leaves a full 48 dp touch target.
        Box(Modifier.fillMaxWidth().heightIn(min = 48.dp), contentAlignment = Alignment.Center) {
            if (hasOverflow) {
                Row(
                    Modifier.testTag("prayer-scroll-cue")
                        .clip(RoundedCornerShape(50))
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
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        Modifier.clip(RoundedCornerShape(50))
                            .background(colors.marian.copy(alpha = .08f))
                            .border(1.dp, colors.marian.copy(alpha = .18f), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SacredText(
                            if (hasMoreBelow) "Continue lendo" else "Voltar ao início",
                            style = type.micro.copy(fontSize = 12.sp),
                            color = colors.secondary,
                        )
                        SacredIcon(
                            SacredIcon.CHEVRON,
                            Modifier.size(13.dp).rotate(if (hasMoreBelow) 90f else -90f),
                            colors.secondary,
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
    graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            val depth = 24.dp.toPx().coerceAtMost(size.height / 2f)
            val above = scroll.value.toFloat().coerceIn(0f, depth)
            val below = (scroll.maxValue.toLong() - scroll.value).toFloat().coerceIn(0f, depth)
            if (above > 0f) {
                drawRect(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black), endY = above),
                    size = Size(size.width, above),
                    blendMode = BlendMode.DstIn,
                )
            }
            if (below > 0f) {
                drawRect(
                    Brush.verticalGradient(listOf(Color.Black, Color.Transparent), startY = size.height - below, endY = size.height),
                    topLeft = Offset(0f, size.height - below),
                    size = Size(size.width, below),
                    blendMode = BlendMode.DstIn,
                )
            }
        }
