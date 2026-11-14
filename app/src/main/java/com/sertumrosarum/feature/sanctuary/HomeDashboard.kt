package com.sertumrosarum.feature.sanctuary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.feature.garden.*
import com.sertumrosarum.feature.prayer.*
import com.sertumrosarum.ui.canvas.SanctuaryGarden
import com.sertumrosarum.ui.components.*
import com.sertumrosarum.ui.theme.SertumTheme
import java.time.format.DateTimeFormatter
import java.util.Locale

/** The whole garden has its home here: faith, prayer, a treasury and personal continuity. */
@Composable
fun HomeDashboard(state: PrayerUiState, personal: GardenState, welcomed: Boolean,
    onTheme: (ThemeMode) -> Unit, onTab: (SanctuaryTab) -> Unit, onLesson: (String?) -> Unit,
    onPray: () -> Unit, onResume: () -> Unit, onIntentions: () -> Unit, onWelcome: () -> Unit) {
    val type = SertumTheme.typography
    val colors = SertumTheme.colors
    val next = personal.learning.firstOrNull { it.completedAt == null }?.lessonId?.let(FormationCatalog::lesson)
        ?: FormationCatalog.lessons.firstOrNull { lesson -> personal.learning.none { it.lessonId == lesson.id && it.completedAt != null } }
    Column(Modifier.fillMaxSize().testTag("sanctuary")) {
        SanctuaryHeader(state.themeMode, onTheme)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).testTag("home-scroll").padding(horizontal = SertumTheme.spacing.page).padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SanctuaryGarden(Modifier.align(Alignment.CenterHorizontally).fillMaxWidth().height(128.dp))
            SacredText(state.today.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale.forLanguageTag("pt-BR"))).replaceFirstChar { it.uppercaseChar() }, style = type.micro, color = colors.accentInk)
            SacredText("Que bom ter você aqui.", Modifier.testTag("sanctuary-greeting"), type.display)
            SacredText("Um lugar para aprender a fé, rezar e cuidar do que floresce em você.", style = type.body, color = colors.secondary)
            if (state.hasSession) PersonalCard("Sua oração espera por você", state.resumeDescription, onResume, "resume")
            SacredText("Por onde quer caminhar?", style = type.section)
            val links = listOf(
                Triple(SanctuaryTab.CATECHISM, "Catequese", "Aprender, perguntar e viver."),
                Triple(SanctuaryTab.GUIDES, "Roteiros", "O terço e outras coroas."),
                Triple(SanctuaryTab.TREASURY, "Tesouro", "Orações e suas palavras."),
                Triple(SanctuaryTab.GARDEN, "Meu Jardim", "Guardar o que floresce."),
            )
            BoxWithConstraints {
                val columns = if (maxWidth >= 260.dp && LocalDensity.current.fontScale <= 1.3f) 2 else 1
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    links.chunked(columns).forEach { row -> Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { (tab, title, detail) ->
                            val icon = when (tab) { SanctuaryTab.CATECHISM -> SacredIcon.CROSS; SanctuaryTab.GUIDES -> SacredIcon.BEADS; SanctuaryTab.TREASURY -> SacredIcon.BOOK; else -> SacredIcon.SPROUT }
                            val tag = when (tab) { SanctuaryTab.CATECHISM -> "home-catechism"; SanctuaryTab.GUIDES -> "other-guides"; SanctuaryTab.TREASURY -> "home-treasury"; else -> "home-garden" }
                            DashboardLink(icon, title, detail, { onTab(tab) }, tag, Modifier.weight(1f).fillMaxHeight())
                        }
                    } }
                }
            }
            if (!welcomed) PersonalCard("Comece por onde seu coração está.", "Você pode aprender do início, buscar uma resposta ou simplesmente rezar.") {
                QuietAction("Quero aprender a rezar", { onWelcome(); onLesson("prayer") }, "welcome-learn")
                QuietAction("Já conheço · seguir pelo jardim", onWelcome, "welcome-dismiss")
            }
            if (next != null) PersonalCard(if (personal.learning.isEmpty()) "Um primeiro passo na fé" else "Continuar aprendendo", next.title, { onLesson(next.id) }, "home-learning") {
                SacredText(next.objective, Modifier.padding(top = 8.dp), type.body, colors.secondary)
            }
            PersonalCard("Uma oração para hoje", "Mistérios ${state.selectedSet.label.lowercase()} · ${state.selectedSet.invitation}") {
                QuietAction("Preparar o terço de hoje", onPray, "home-pray")
            }
            val intentions = personal.intentions.filter { !it.archived }.take(2)
            PersonalCard("O que você traz no coração?", if (intentions.isEmpty()) "Uma intenção pode ser o início de uma oração." else intentions.joinToString("\n") { (if (it.thanksgiving) "Em ação de graças: " else "Por ") + it.title }) {
                QuietAction("Cuidar das minhas intenções", onIntentions, "home-intentions")
            }
            SacredText("Uma oração, uma descoberta, um gesto de cada vez.", style = type.whisper, color = colors.secondary)
        }
    }
}

@Composable
private fun DashboardLink(icon: SacredIcon, title: String, caption: String, click: () -> Unit, tag: String, modifier: Modifier = Modifier) {
    PersonalCard(title, caption, click, tag, modifier) {
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            SacredIcon(icon, Modifier.size(23.dp), SertumTheme.colors.accentInk)
            SacredIcon(SacredIcon.CHEVRON, Modifier.size(13.dp), SertumTheme.colors.secondary)
        }
    }
}
