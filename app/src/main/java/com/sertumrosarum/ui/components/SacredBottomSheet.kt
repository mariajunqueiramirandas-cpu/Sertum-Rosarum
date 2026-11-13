package com.sertumrosarum.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sertumrosarum.ui.theme.SertumTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * A page that rises from the foot of the screen, over a dusk-green veil.
 * It closes by dragging its handle down, touching the veil, or going back.
 * Place it last in a full-size [Box] so it covers everything drawn before it.
 */
@Composable
fun SacredBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    tag: String = "sheet",
    /** Stays in view beneath the scrolling page, for the one action the sheet offers. */
    footer: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = SertumTheme.colors
    val dismiss by rememberUpdatedState(onDismiss)
    val veil by animateFloatAsState(if (visible) 1f else 0f, tween(if (visible) 280 else 220), label = "sheet-veil")
    val veilColor = if (colors.isDark) Color(0xFF050D0B).copy(alpha = .62f) else Color(0xFF22352D).copy(alpha = .36f)
    BackHandler(visible) { dismiss() }
    BoxWithConstraints(modifier.fillMaxSize()) {
        val tallest = maxHeight * .9f
        if (veil > 0f) {
            Box(
                // Drawn generously past its own edges so the veil also reaches under the system bars.
                Modifier.fillMaxSize().drawBehind { drawRect(veilColor.copy(alpha = veilColor.alpha * veil), Offset(-size.width, -size.height), Size(size.width * 3f, size.height * 3f)) }
                    .pointerInput(Unit) { detectTapGestures { dismiss() } }
                    .testTag("$tag-veil")
                    .semantics {
                        contentDescription = "Fechar"
                        role = Role.Button
                        onClick { dismiss(); true }
                    },
            )
        }
        AnimatedVisibility(
            visible, Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(spring(dampingRatio = .9f, stiffness = 260f)) { it } + fadeIn(tween(160)),
            exit = slideOutVertically(tween(240)) { it } + fadeOut(tween(200)),
        ) {
            val pull = remember { Animatable(0f) }
            val scope = rememberCoroutineScope()
            val release = with(LocalDensity.current) { 96.dp.toPx() }
            val scroll = rememberScrollState()
            Column(
                Modifier.fillMaxWidth().heightIn(max = tallest).offset { IntOffset(0, pull.value.roundToInt()) }
                    .drawBehind {
                        // One rounded page that continues below the screen's edge.
                        val round = 34.dp.toPx()
                        val page = Path().apply {
                            moveTo(0f, size.height * 2f); lineTo(0f, round)
                            quadraticTo(0f, 0f, round, 0f); lineTo(size.width - round, 0f)
                            quadraticTo(size.width, 0f, size.width, round); lineTo(size.width, size.height * 2f); close()
                        }
                        drawPath(page, colors.background)
                        drawPath(page, colors.border, style = Stroke(1.dp.toPx()))
                    }
                    .pointerInput(Unit) { detectTapGestures { } }
                    .semantics { paneTitle = label; dismiss { dismiss(); true } }
                    .testTag(tag),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier.fillMaxWidth().height(30.dp).testTag("$tag-handle")
                        .draggable(
                            rememberDraggableState { delta -> scope.launch { pull.snapTo((pull.value + delta).coerceAtLeast(0f)) } },
                            Orientation.Vertical,
                            onDragStopped = { velocity ->
                                if (pull.value > release || velocity > 1400f) dismiss() else pull.animateTo(0f, spring(dampingRatio = .8f))
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) { Box(Modifier.width(42.dp).height(4.dp).background(colors.muted.copy(alpha = .38f), CircleShape)) }
                Column(
                    Modifier.weight(1f, fill = false).softScrollTop(scroll, 16.dp).verticalScroll(scroll).testTag("$tag-scroll").padding(horizontal = 24.dp).padding(bottom = 20.dp),
                    content = content,
                )
                if (footer != null) Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 4.dp, bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally, content = footer)
            }
        }
    }
}
