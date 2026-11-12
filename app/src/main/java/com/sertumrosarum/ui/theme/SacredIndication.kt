package com.sertumrosarum.ui.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import kotlinx.coroutines.launch

/** Quiet sage press feedback replaces Foundation's stock debug indication everywhere. */
internal data class SacredIndication(val color: Color) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = SacredPressNode(color, interactionSource)
}

private class SacredPressNode(private val color: Color, private val source: InteractionSource) : Modifier.Node(), DrawModifierNode {
    private val presses = mutableSetOf<PressInteraction.Press>()

    override fun onAttach() {
        coroutineScope.launch {
            source.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> presses.add(interaction)
                    is PressInteraction.Release -> presses.remove(interaction.press)
                    is PressInteraction.Cancel -> presses.remove(interaction.press)
                }
                invalidateDraw()
            }
        }
    }

    override fun onDetach() { presses.clear() }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (presses.isNotEmpty()) drawRect(color.copy(alpha = .09f))
    }
}
