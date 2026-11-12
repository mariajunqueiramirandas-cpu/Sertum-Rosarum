package com.sertumrosarum.feature.garden

import androidx.compose.runtime.Composable
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.ui.components.BuddingBed
import com.sertumrosarum.ui.components.SanctuaryPage

/** My Garden's place, kept ready for the intentions and the crowns that take root in Phase 4. */
@Composable
fun GardenScreen() {
    SanctuaryPage(SanctuaryTab.GARDEN.label, "garden") {
        BuddingBed(
            title = "Diário e intenções",
            lead = "Um canto só seu, para guardar o que você reza.",
            promiseTitle = "O que vai florescer aqui",
            promises = listOf(
                "Caderno de intenções" to "Por quem e pelo que você reza, guardado com carinho.",
                "Suas coroas de rosas" to "Cada terço rezado floresce no seu jardim.",
                "Continuar de onde parou" to "Sua oração espera por você, mesmo depois de fechar o aplicativo.",
            ),
            closing = "Sem metas nem cobranças: só a constância de quem volta.",
        )
    }
}
