package com.sertumrosarum.feature.treasury

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.sertumrosarum.ui.canvas.DevotionalArtwork
import com.sertumrosarum.ui.canvas.DevotionalIllustration
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.ui.components.BuddingBed
import com.sertumrosarum.ui.components.SanctuaryPage

/** The Treasury's place in the garden, kept ready for the prayers and formation that open in Phase 3. */
@Composable
fun TreasuryScreen() {
    SanctuaryPage(SanctuaryTab.TREASURY.label, "treasury") {
        BuddingBed(
            title = "Orações e formação",
            lead = "Um lugar para rezar e compreender, sem pressa.",
            promiseTitle = "O que vai florescer aqui",
            promises = listOf(
                "Orações para cada momento" to "Ao acordar, antes de dormir, na angústia e na gratidão.",
                "Toque para entender" to "O sentido de cada expressão, ao alcance de um toque.",
                "Português e latim" to "As grandes orações da Igreja, lado a lado.",
                "Os vinte mistérios" to "Um guia para contemplar cada cena com calma.",
            ),
            closing = "Enquanto este canteiro cresce, o terço de hoje espera por você.",
            hero = { DevotionalArtwork(DevotionalIllustration.PRAYER_BOOK, Modifier.fillMaxSize()) },
        )
    }
}
