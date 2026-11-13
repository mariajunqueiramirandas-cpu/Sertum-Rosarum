package com.sertumrosarum.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.clearAndSetSemantics

/**
 * Lays out every variant at once and shows only the chosen one, so the slot is always as tall
 * as its tallest variant and changing the choice never moves what sits below it.
 * Hidden variants are invisible to the eye and to screen readers.
 */
@Composable
fun <T> SteadySlot(
    options: List<T>,
    shown: T,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopCenter,
    content: @Composable (option: T, visible: Boolean) -> Unit,
) {
    Box(modifier, contentAlignment = contentAlignment) {
        options.forEach { option ->
            val visible = option == shown
            key(option) {
                Box(if (visible) Modifier else Modifier.alpha(0f).clearAndSetSemantics {}) { content(option, visible) }
            }
        }
    }
}
