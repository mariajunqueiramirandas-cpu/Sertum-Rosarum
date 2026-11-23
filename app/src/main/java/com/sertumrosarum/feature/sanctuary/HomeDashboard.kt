package com.sertumrosarum.feature.sanctuary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sertumrosarum.domain.formation.FormationCatalog
import com.sertumrosarum.domain.liturgy.LiturgicalColor
import com.sertumrosarum.domain.lumen.CatechistPersona
import com.sertumrosarum.domain.lumen.OfflineCatechistEngine
import com.sertumrosarum.feature.catechist.portrait
import com.sertumrosarum.feature.garden.GardenState
import com.sertumrosarum.feature.garden.PersonalCard
import com.sertumrosarum.feature.garden.QuietAction
import com.sertumrosarum.feature.guides.PrayerGuide
import com.sertumrosarum.feature.prayer.PrayerUiState
import com.sertumrosarum.feature.prayer.SanctuaryTab
import com.sertumrosarum.feature.prayer.ThemeMode
import com.sertumrosarum.ui.canvas.DevotionalIllustration
import com.sertumrosarum.ui.canvas.SanctuaryGarden
import com.sertumrosarum.ui.canvas.lessonIllustration
import com.sertumrosarum.ui.components.SacredFlourish
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredSecondaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.ScrollHeaderShadow
import com.sertumrosarum.ui.components.softScrollTop
import com.sertumrosarum.ui.theme.ReadingScale
import com.sertumrosarum.ui.theme.SertumTheme
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun LiturgicalColor.gardenTint(dark: Boolean): Color = when (this) {
    LiturgicalColor.GREEN -> if (dark) Color(0xFF9DBE9D) else Color(0xFF5D8066)
    LiturgicalColor.PURPLE -> if (dark) Color(0xFFC5B1D6) else Color(0xFF7B5E8B)
    LiturgicalColor.WHITE -> if (dark) Color(0xFFE5D29A) else Color(0xFFB8935A)
    LiturgicalColor.RED -> if (dark) Color(0xFFE0A098) else Color(0xFFAB524C)
    LiturgicalColor.ROSE -> if (dark) Color(0xFFE6B5BC) else Color(0xFFC47986)
}

/** The whole garden has its home here: liturgy of the day, faith, prayer, a treasury and personal continuity. */
@Composable
fun HomeDashboard(
    state: PrayerUiState,
    personal: GardenState,
    welcomed: Boolean,
    onTheme: (ThemeMode) -> Unit,
    onReadingScale: (ReadingScale) -> Unit = {},
    onToggleVolumeKeys: () -> Unit = {},
    onSearch: () -> Unit = {},
    onTab: (SanctuaryTab) -> Unit,
    onLesson: (String?) -> Unit,
    onPray: () -> Unit,
    onPrayNow: () -> Unit = onPray,
    onPraySingleDecade: () -> Unit = onPray,
    onOpenMysteries: () -> Unit = { onTab(SanctuaryTab.TREASURY) },
    onOpenPrayer: (String) -> Unit = { onTab(SanctuaryTab.TREASURY) },
    onOpenGuide: (PrayerGuide) -> Unit = { onTab(SanctuaryTab.GUIDES) },
    onResume: () -> Unit,
    onIntentions: () -> Unit,
    onWelcome: () -> Unit,
    catechist: CatechistPersona = CatechistPersona.DEFAULT,
    onOpenCatechist: () -> Unit = {},
    onPrepareCustomRosary: () -> Unit = {},
    onRecallPrompt: (String) -> Unit = { onOpenCatechist() },
) {
    val type = SertumTheme.typography
    val colors = SertumTheme.colors
    val scroll = rememberScrollState()
    val liturgy = state.liturgicalDay
    val next = personal.learning.firstOrNull { it.completedAt == null }?.lessonId?.let(FormationCatalog::lesson)
        ?: FormationCatalog.lessons.firstOrNull { lesson -> personal.learning.none { it.lessonId == lesson.id && it.completedAt != null } }
    val memoryRecall = OfflineCatechistEngine.recallIntention(
        personal.intentions
            .filter { !it.archived && !it.thanksgiving }
            .map { Triple(it.id, it.title, it.createdAt) },
    )
    Column(Modifier.fillMaxSize().testTag("sanctuary")) {
        SanctuaryHeader(
            mode = state.themeMode,
            onTheme = onTheme,
            readingScale = state.readingScale,
            onReadingScale = onReadingScale,
            volumeKeysEnabled = state.volumeKeysEnabled,
            onToggleVolumeKeys = onToggleVolumeKeys,
            onSearch = onSearch,
        )
        Box(Modifier.weight(1f)) {
            Column(
                Modifier.fillMaxSize()
                    .softScrollTop(scroll)
                    .verticalScroll(scroll)
                    .testTag("home-scroll")
                    .padding(horizontal = SertumTheme.spacing.page)
                    .padding(top = 4.dp, bottom = 44.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Harmonious centered welcome matching the sanctuary's serene opening.
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SanctuaryGarden(Modifier.fillMaxWidth().height(126.dp))
                    SacredText(
                        state.today.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale.forLanguageTag("pt-BR"))).replaceFirstChar { it.uppercaseChar() },
                        Modifier.padding(top = 8.dp),
                        type.micro,
                        colors.accentInk,
                        TextAlign.Center,
                    )
                    // Quiet liturgical awareness badge: season, color, and celebration.
                    Row(
                        Modifier.padding(top = 4.dp).testTag("liturgical-day-badge"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        Box(Modifier.size(7.dp).background(liturgy.color.gardenTint(colors.isDark), CircleShape))
                        SacredText(liturgy.headerLabel, style = type.micro, color = colors.secondary, align = TextAlign.Center)
                    }
                    SacredText("Que bom ter você aqui.", Modifier.padding(top = 10.dp).testTag("sanctuary-greeting"), type.display, align = TextAlign.Center)
                    SacredText("Aprender a fé, rezar e cultivar a vida com Deus.", Modifier.padding(top = 6.dp), type.body, colors.secondary, TextAlign.Center)
                    SacredFlourish(Modifier.padding(top = 16.dp, bottom = 4.dp))
                }

                if (state.hasSession) {
                    PersonalCard("Sua oração espera por você", state.resumeDescription, onResume, "resume", illustration = DevotionalIllustration.CONTEMPLATIVE_PRAYER)
                }

                SacredText("Por onde quer caminhar?", Modifier.padding(top = 2.dp), type.section)
                val links = listOf(
                    Triple(SanctuaryTab.CATECHISM, "Catequese", "Aprender, perguntar e viver."),
                    Triple(SanctuaryTab.GUIDES, "Roteiros", "O terço e outras coroas."),
                    Triple(SanctuaryTab.TREASURY, "Tesouro", "Orações e suas palavras."),
                    Triple(SanctuaryTab.GARDEN, "Meu Jardim", "Guardar o que floresce."),
                )
                BoxWithConstraints {
                    val columns = if (maxWidth >= 260.dp && LocalDensity.current.fontScale <= 1.3f) 2 else 1
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        links.chunked(columns).forEach { row ->
                            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                row.forEach { (tab, title, detail) ->
                                    val illustration = when (tab) {
                                        SanctuaryTab.CATECHISM -> DevotionalIllustration.CATECHESIS
                                        SanctuaryTab.GUIDES -> DevotionalIllustration.CONTEMPLATIVE_PRAYER
                                        SanctuaryTab.TREASURY -> DevotionalIllustration.BOOKMARKS
                                        else -> DevotionalIllustration.PERSONAL_GARDEN
                                    }
                                    val tag = when (tab) {
                                        SanctuaryTab.CATECHISM -> "home-catechism"
                                        SanctuaryTab.GUIDES -> "other-guides"
                                        SanctuaryTab.TREASURY -> "home-treasury"
                                        else -> "home-garden"
                                    }
                                    DashboardLink(illustration, title, detail, { onTab(tab) }, tag, Modifier.weight(1f).fillMaxHeight())
                                }
                            }
                        }
                    }
                }

                if (!welcomed) {
                    PersonalCard("Comece por onde seu coração está.", "Você pode aprender do início, buscar uma resposta ou simplesmente rezar.") {
                        QuietAction("Quero aprender a rezar", { onWelcome(); onLesson("prayer") }, "welcome-learn", showDivider = true)
                        QuietAction("Já conheço · seguir pelo jardim", onWelcome, "welcome-dismiss", showDivider = true)
                    }
                }

                // Today's Rosary card: the single primary petal action on Home, plus quiet secondary routes.
                PersonalCard(
                    "Uma oração para hoje",
                    "Mistérios ${state.selectedSet.label.lowercase()} · ${state.selectedSet.invitation}",
                    illustration = DevotionalIllustration.CONTEMPLATIVE_PRAYER,
                ) {
                    SacredPrimaryButton("Rezar o terço agora", onPrayNow, Modifier.padding(bottom = 6.dp), tag = "home-pray-now", compact = true)
                    QuietAction("Preparar o terço de hoje", onPray, "home-pray", showDivider = true)
                    QuietAction("Rezar apenas uma dezena", onPraySingleDecade, "home-single-decade", showDivider = true)
                    QuietAction("Estudar os vinte mistérios", onOpenMysteries, "home-mysteries", showDivider = true)
                }

                // Conversational Catechist companion card (Mateus / Lucas / Clara / Bento)
                PersonalCard(
                    title = "Conversar com ${catechist.displayName}",
                    detail = "${catechist.styleTitle} · Tire dúvidas de fé, converse sobre seu dia ou prepare o terço juntos.",
                    tag = "home-catechist-card",
                    illustration = catechist.portrait(),
                ) {
                    SacredSecondaryButton("Abrir conversa", onOpenCatechist, Modifier.fillMaxWidth().padding(bottom = 6.dp), tag = "home-open-catechist")
                    QuietAction("Preparar terço sob medida com ${catechist.displayName}", onPrepareCustomRosary, "home-prepare-custom-rosary", showDivider = true)
                }

                if (memoryRecall != null) {
                    PersonalCard(
                        title = memoryRecall.heading,
                        detail = memoryRecall.body,
                        tag = "home-memory-recall",
                        illustration = DevotionalIllustration.INTENTIONS,
                    ) {
                        QuietAction("Incluir ação de graças ou renovar no terço", { onRecallPrompt(memoryRecall.intentionTitle) }, "home-memory-action", showDivider = true)
                    }
                }

                // Liturgical time card: seasonal Marian antiphon (Angelus vs Regina Caeli) & feast devotion.
                val suggestedGuide = PrayerGuide.byNameOrNull(liturgy.recommendedGuideName)
                PersonalCard(
                    liturgy.celebration ?: liturgy.season.label,
                    liturgy.invitation,
                    tag = "home-liturgy-card",
                    illustration = DevotionalIllustration.LITURGICAL_YEAR,
                ) {
                    QuietAction(
                        if (liturgy.isEastertide) "Rezar o ${liturgy.marianAntiphonTitle} (Tempo Pascal)" else "Rezar o ${liturgy.marianAntiphonTitle}",
                        { onOpenPrayer(liturgy.marianAntiphonId) },
                        "home-marian-antiphon",
                        showDivider = true,
                    )
                    if (suggestedGuide != null) {
                        QuietAction("Abrir: ${suggestedGuide.title}", { onOpenGuide(suggestedGuide) }, "home-liturgical-guide", showDivider = true)
                    }
                }

                if (next != null) {
                    PersonalCard(
                        if (personal.learning.isEmpty()) "Um primeiro passo na fé" else "Continuar aprendendo",
                        next.title,
                        { onLesson(next.id) },
                        "home-learning",
                        illustration = lessonIllustration(next.id),
                    ) {
                        SacredText(next.objective, style = type.body, color = colors.secondary)
                    }
                }

                val intentions = personal.intentions.filter { !it.archived }.take(2)
                PersonalCard(
                    "O que você traz no coração?",
                    if (intentions.isEmpty()) "Uma intenção pode ser o início de uma oração."
                    else intentions.joinToString("\n") { (if (it.thanksgiving) "Em ação de graças: " else "Por ") + it.title },
                    illustration = DevotionalIllustration.INTENTIONS,
                ) {
                    QuietAction("Cuidar das minhas intenções", onIntentions, "home-intentions", showDivider = true)
                }

                SacredText(
                    "Uma oração, uma descoberta, um gesto de cada vez.",
                    Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 6.dp),
                    type.whisper,
                    colors.secondary,
                    TextAlign.Center,
                )
            }
            ScrollHeaderShadow(scroll, Modifier.align(Alignment.TopCenter))
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(28.dp).background(Brush.verticalGradient(listOf(colors.background.copy(alpha = 0f), colors.background))))
        }
    }
}

@Composable
private fun DashboardLink(illustration: DevotionalIllustration, title: String, caption: String, click: () -> Unit, tag: String, modifier: Modifier = Modifier) {
    PersonalCard(title, caption, click, tag, modifier, illustration = illustration, illustrationAbove = true)
}
