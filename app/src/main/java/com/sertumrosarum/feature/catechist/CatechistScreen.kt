package com.sertumrosarum.feature.catechist

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sertumrosarum.domain.lumen.CatechistAction
import com.sertumrosarum.domain.lumen.CatechistMessage
import com.sertumrosarum.domain.lumen.CatechistPersona
import com.sertumrosarum.domain.lumen.CatechistPrayer
import com.sertumrosarum.domain.lumen.CatechistProgress
import com.sertumrosarum.domain.lumen.CustomRosarySupplement
import com.sertumrosarum.domain.lumen.LumenMood
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.feature.garden.GardenField
import com.sertumrosarum.ui.canvas.DevotionalArtwork
import com.sertumrosarum.ui.components.SacredBottomSheet
import com.sertumrosarum.ui.components.SacredCard
import com.sertumrosarum.ui.components.SacredDivider
import com.sertumrosarum.ui.components.SacredEyebrow
import com.sertumrosarum.ui.components.SacredFlourish
import com.sertumrosarum.ui.components.SacredIcon
import com.sertumrosarum.ui.components.SacredIconButton
import com.sertumrosarum.ui.components.SacredPrimaryButton
import com.sertumrosarum.ui.components.SacredText
import com.sertumrosarum.ui.components.SacredTextLink
import com.sertumrosarum.ui.components.SacredToggleRow
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.shadow
import com.sertumrosarum.feature.sanctuary.MysterySelector
import com.sertumrosarum.ui.components.SacredSecondaryButton
import com.sertumrosarum.ui.components.ScrollHeaderShadow
import com.sertumrosarum.ui.components.softScrollTop
import com.sertumrosarum.ui.theme.SertumTheme

private val StarterPrompts = listOf(
    "Tô travado pra rezar hoje" to "Secura, distração ou cansaço",
    "Me ajuda a montar o terço de hoje" to "Transformar o que vivo em oração",
    "Tenho uma dúvida sincera de fé" to "Confissão, Maria, sofrimento, Missa…",
    "Me conta a história de um santo" to "Inspiração breve para o meu dia",
)

/**
 * Full-screen correspondence experience for talking with the chosen Catholic Catechist
 * (Mateus, Lucas, Clara, or Bento) inside O Jardim de Maria.
 */
@Composable
fun CatechistScreen(
    state: CatechistUiState,
    selectedMysterySet: MysterySet,
    initialDraft: String = "",
    openPrepareInitially: Boolean = false,
    onBack: () -> Unit,
    onSend: (String) -> Unit,
    onSelectPersona: (CatechistPersona) -> Unit,
    onToggleOnline: (Boolean) -> Unit,
    onSaveApiKey: (String) -> Unit,
    onNewConversation: () -> Unit,
    onPrepareCustomRosary: (intention: String, set: MysterySet, mood: LumenMood?, onReady: (CustomRosarySupplement) -> Unit) -> Unit,
    onExecuteAction: (CatechistAction) -> Unit,
    onStartCustomRosary: (CustomRosarySupplement) -> Unit,
    onClearNotice: () -> Unit,
    savedPrayerIds: Set<String> = emptySet(),
    onReadPrayer: (CatechistPrayer) -> Unit = {},
    onSetPrayerSaved: ((CatechistPrayer, Boolean) -> Unit)? = null,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val page = SertumTheme.spacing.page
    val scroll = rememberScrollState()

    var draft by rememberSaveable(initialDraft) { mutableStateOf(initialDraft) }
    var personaSheetOpen by rememberSaveable { mutableStateOf(false) }
    var prepareSheetOpen by rememberSaveable(openPrepareInitially) { mutableStateOf(openPrepareInitially) }
    var voiceUnavailable by rememberSaveable { mutableStateOf(false) }

    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()?.trim()
            if (!spoken.isNullOrBlank()) {
                draft = if (draft.isBlank()) spoken else "$draft $spoken"
            }
        }
    }

    fun launchVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Converse com ${state.persona.displayName}")
        }
        try {
            voiceUnavailable = false
            speechLauncher.launch(intent)
        } catch (_: Exception) {
            voiceUnavailable = true
        }
    }

    val hasMessages = state.messages.isNotEmpty()
    val scrollThreshold = with(LocalDensity.current) { 48.dp.roundToPx() }
    LaunchedEffect(hasMessages, scrollThreshold) {
        if (!hasMessages) return@LaunchedEffect
        var previousEnd = scroll.value
        snapshotFlow { scroll.maxValue }.collect { end ->
            if (end == Int.MAX_VALUE) return@collect
            val followReply = scroll.value >= previousEnd - scrollThreshold && !scroll.isScrollInProgress
            previousEnd = end
            if (followReply) scroll.scrollTo(end)
        }
    }
    val latestUserMessageId = state.messages.lastOrNull { it.fromUser }?.id
    LaunchedEffect(latestUserMessageId) {
        if (latestUserMessageId != null) scroll.scrollTo(scroll.maxValue)
    }

    Box(Modifier.fillMaxSize().imePadding().testTag("catechist-screen")) {
        Column(Modifier.fillMaxSize()) {
            // Top bar: Back · Active Catechist Medallion (tap to switch) · Prepare Rosary
            Row(
                Modifier.fillMaxWidth().padding(horizontal = page, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SacredIconButton(SacredIcon.BACK, "Voltar", onBack, Modifier.testTag("catechist-back"))
                Row(
                    Modifier.weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .clickable(role = Role.Button, onClickLabel = "Escolher catequista") { personaSheetOpen = true }
                        .testTag("catechist-persona-trigger")
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    PortraitMedallion(state.persona, size = 40)
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            SacredText(state.persona.displayName, style = type.section.copy(fontSize = 18.sp, lineHeight = 22.sp), maxLines = 1)
                            SacredIcon(SacredIcon.CHEVRON, Modifier.size(11.dp), colors.muted)
                        }
                        SacredText(
                            state.activeContextLabel ?: "${state.persona.styleTitle} · ${if (state.onlineEnabled && state.hasEffectiveApiKey) "Mistral" else "Offline"}",
                            style = type.micro.copy(fontSize = 12.sp, lineHeight = 16.sp),
                            color = colors.accentInk,
                            maxLines = 1,
                        )
                    }
                }
                SacredIconButton(
                    SacredIcon.BEADS,
                    "Preparar terço sob medida",
                    { prepareSheetOpen = true },
                    Modifier.testTag("catechist-prepare-rosary"),
                )
            }

            SacredDivider(Modifier.padding(horizontal = page))

            // Main scrollable area: Welcome state or Correspondence thread, with soft top & bottom edge fades
            Box(Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    Modifier.fillMaxSize()
                        .softScrollTop(scroll)
                        .verticalScroll(scroll)
                        .padding(horizontal = page)
                        .padding(top = 14.dp, bottom = 36.dp)
                        .testTag("catechist-thread"),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (state.notice != null) {
                        SacredCard(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                SacredText(state.notice, Modifier.weight(1f), type.micro, colors.accentInk)
                                SacredTextLink("Ok", onClearNotice, "dismiss-notice")
                            }
                        }
                    }

                    if (state.messages.isEmpty()) {
                        CatechistWelcome(
                            persona = state.persona,
                            onPickStarter = { prompt ->
                                if (prompt.contains("montar o terço", ignoreCase = true)) {
                                    prepareSheetOpen = true
                                } else {
                                    onSend(prompt)
                                }
                            },
                            onSwitchPersona = { personaSheetOpen = true },
                            onPrepareRosary = { prepareSheetOpen = true },
                        )
                    } else {
                        state.messages.forEachIndexed { index, msg ->
                            if (index > 0 && msg.fromUser) {
                                SacredFlourish(Modifier.align(Alignment.CenterHorizontally).padding(vertical = 2.dp))
                            }
                            if (msg.fromUser) {
                                UserNoteTurn(msg)
                            } else {
                                CatechistReplyTurn(
                                    message = msg,
                                    fallbackPersona = state.persona,
                                    onAction = onExecuteAction,
                                    onFollowUp = onSend,
                                    canSendFollowUp = !state.isThinking,
                                    savedPrayerIds = savedPrayerIds,
                                    onReadPrayer = onReadPrayer,
                                    onSetPrayerSaved = onSetPrayerSaved,
                                )
                            }
                        }
                        state.progress?.let { progress ->
                            CatechistProgressTurn(progress)
                        }
                        if (!state.isThinking && state.messages.isNotEmpty()) {
                            Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.Center) {
                                SacredTextLink("Começar nova conversa", onNewConversation, "catechist-new-chat")
                            }
                        }
                    }
                }
                ScrollHeaderShadow(scroll, Modifier.align(Alignment.TopCenter))
                Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(28.dp).background(Brush.verticalGradient(listOf(colors.background.copy(alpha = 0f), colors.background))))
            }

            // Quiet floating composer with Voice ("Falar") + Blush Petal Send
            val composerInteraction = remember { MutableInteractionSource() }
            val composerFocused by composerInteraction.collectIsFocusedAsState()
            val composerBorder by animateColorAsState(if (composerFocused) colors.marian else colors.border, label = "composer-border")
            val composerShape = RoundedCornerShape(28.dp)
            Column(
                Modifier.fillMaxWidth()
                    .background(colors.background)
                    .padding(horizontal = page)
                    .padding(top = 4.dp, bottom = 12.dp),
            ) {
                if (voiceUnavailable) {
                    SacredText(
                        "Reconhecimento de voz indisponível neste aparelho — você pode digitar normalmente.",
                        Modifier.padding(bottom = 6.dp, start = 6.dp),
                        type.micro,
                        colors.muted,
                    )
                }
                Row(
                    Modifier.fillMaxWidth()
                        .shadow(if (colors.isDark) 0.dp else 8.dp, composerShape, ambientColor = colors.marian.copy(alpha = .14f), spotColor = colors.marian.copy(alpha = .18f))
                        .clip(composerShape)
                        .background(colors.elevated)
                        .border(if (composerFocused) 1.5.dp else 1.dp, composerBorder, composerShape)
                        .padding(start = 18.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(Modifier.weight(1f).padding(vertical = 6.dp), contentAlignment = Alignment.CenterStart) {
                        if (draft.isEmpty()) {
                            SacredText("Escreva ou fale com ${state.persona.displayName}…", style = type.body, color = colors.muted.copy(alpha = .8f), maxLines = 1)
                        }
                        BasicTextField(
                            value = draft,
                            onValueChange = { draft = it.take(4000) },
                            modifier = Modifier.fillMaxWidth().testTag("catechist-input").semantics {
                                contentDescription = "Mensagem para ${state.persona.displayName}"
                            },
                            textStyle = type.body.copy(color = colors.ink),
                            cursorBrush = SolidColor(colors.marian),
                            interactionSource = composerInteraction,
                            maxLines = 4,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (draft.isNotBlank() && !state.isThinking) {
                                        val toSend = draft
                                        draft = ""
                                        onSend(toSend)
                                    }
                                },
                            ),
                        )
                    }
                    // Voice button
                    Box(
                        Modifier.size(44.dp).clip(CircleShape)
                            .background(colors.surface)
                            .clickable(role = Role.Button) { launchVoiceInput() }
                            .semantics { contentDescription = "Falar por voz" }
                            .testTag("catechist-mic"),
                        contentAlignment = Alignment.Center,
                    ) {
                        SacredIcon(SacredIcon.MIC, Modifier.size(18.dp), colors.ink)
                    }
                    // Blush petal send button
                    val canSend = draft.isNotBlank() && !state.isThinking
                    Box(
                        Modifier.size(44.dp).clip(CircleShape)
                            .background(
                                if (canSend) Brush.verticalGradient(listOf(colors.petal, colors.petalDeep))
                                else Brush.verticalGradient(listOf(colors.surface, colors.surface)),
                            )
                            .clickable(enabled = canSend, role = Role.Button) {
                                val toSend = draft
                                draft = ""
                                onSend(toSend)
                            }
                            .semantics { contentDescription = "Enviar mensagem" }
                            .testTag("catechist-send"),
                        contentAlignment = Alignment.Center,
                    ) {
                        SacredIcon(SacredIcon.SEND, Modifier.size(18.dp), if (canSend) colors.onPetal else colors.muted)
                    }
                }
            }
        }

        // Sheet 1: Choose Catechist Persona and configure online access.
        SacredBottomSheet(
            visible = personaSheetOpen,
            onDismiss = { personaSheetOpen = false },
            label = "Escolher catequista",
            tag = "persona-sheet",
        ) {
            PersonaPickerSheetContent(
                state = state,
                onSelect = { persona ->
                    onSelectPersona(persona)
                    personaSheetOpen = false
                },
                onToggleOnline = onToggleOnline,
                onSaveApiKey = onSaveApiKey,
            )
        }

        // Sheet 2: Prepare Custom Rosary ("Preparar com o coração")
        SacredBottomSheet(
            visible = prepareSheetOpen,
            onDismiss = { if (!state.isPreparingRosary) prepareSheetOpen = false },
            label = "Preparar o terço com o coração",
            tag = "prepare-rosary-sheet",
        ) {
            PrepareCustomRosarySheetContent(
                persona = state.persona,
                defaultSet = selectedMysterySet,
                isPreparing = state.isPreparingRosary,
                initialIntention = draft,
                onPrepare = { intention, set, mood ->
                    onPrepareCustomRosary(intention, set, mood) { supplement ->
                        prepareSheetOpen = false
                        onStartCustomRosary(supplement)
                    }
                },
            )
        }
    }
}

@Composable
private fun CatechistWelcome(
    persona: CatechistPersona,
    onPickStarter: (String) -> Unit,
    onSwitchPersona: () -> Unit,
    onPrepareRosary: () -> Unit,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Column(
        Modifier.fillMaxWidth().padding(top = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        DevotionalArtwork(persona.portrait(), Modifier.size(108.dp))
        SacredText("${persona.displayName} · ${persona.styleTitle}", style = type.micro, color = colors.accentInk, align = TextAlign.Center)
        SacredText(persona.welcomeHeading, Modifier.padding(horizontal = 4.dp), type.heading.copy(fontSize = 23.sp, lineHeight = 30.sp), align = TextAlign.Center)
        SacredText(persona.welcomeSubtitle, Modifier.padding(horizontal = 6.dp), type.body, colors.secondary, TextAlign.Center)
        SacredFlourish(Modifier.padding(vertical = 1.dp))

        // Starters in one clean card separated by hairlines (matches Roteiros)
        SacredCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(vertical = 4.dp)) {
                StarterPrompts.forEachIndexed { index, (title, subtitle) ->
                    if (index > 0) SacredDivider(Modifier.padding(horizontal = 18.dp))
                    Row(
                        Modifier.fillMaxWidth()
                            .clickable(role = Role.Button) { onPickStarter(title) }
                            .testTag("catechist-starter-$index")
                            .heightIn(min = 58.dp)
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Column(Modifier.weight(1f)) {
                            SacredText(title, style = type.label.copy(fontWeight = FontWeight.SemiBold))
                            SacredText(subtitle, Modifier.padding(top = 2.dp), type.micro.copy(fontWeight = FontWeight.Normal), colors.secondary)
                        }
                        SacredIcon(SacredIcon.CHEVRON, Modifier.size(12.dp), colors.muted)
                    }
                }
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SacredTextLink("Trocar catequista", onSwitchPersona, "open-persona-picker")
            SacredTextLink("Preparar terço", onPrepareRosary, "welcome-prepare-rosary")
        }

        SacredText(
            "Fiel ao Catecismo e às Escrituras · Não substitui direção espiritual nem o Sacramento da Confissão.",
            Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp),
            type.micro.copy(fontWeight = FontWeight.Normal),
            colors.muted,
            TextAlign.Center,
        )
    }
}

@Composable
private fun UserNoteTurn(message: CatechistMessage) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        val shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 22.dp, bottomEnd = 6.dp)
        Box(
            Modifier.widthIn(max = 300.dp)
                .clip(shape)
                .background(colors.surface)
                .border(1.dp, colors.border, shape)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("user-message"),
        ) {
            SacredText(message.text, style = type.body, color = colors.ink)
        }
    }
}

@Composable
private fun CatechistReplyTurn(
    message: CatechistMessage,
    fallbackPersona: CatechistPersona,
    onAction: (CatechistAction) -> Unit,
    onFollowUp: (String) -> Unit,
    canSendFollowUp: Boolean,
    savedPrayerIds: Set<String>,
    onReadPrayer: (CatechistPrayer) -> Unit,
    onSetPrayerSaved: ((CatechistPrayer, Boolean) -> Unit)?,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val speaker = message.persona ?: fallbackPersona
    Column(
        Modifier.fillMaxWidth()
            .testTag(if (message.isInterim) "catechist-interim-reply" else "catechist-reply")
            .semantics { if (message.isInterim) liveRegion = LiveRegionMode.Polite },
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PortraitMedallion(speaker, size = 30)
            SacredText(speaker.displayName, style = type.micro, color = colors.gold)
            if (message.isOfflineFallback) {
                SacredText("· modo offline", style = type.micro.copy(fontWeight = FontWeight.Normal), color = colors.muted)
            }
        }
        CatechistToolTrace(message.toolTrace)
        CatechistReplyText(message.text)
        message.prayers.forEach { prayer ->
            val saved = prayer.id in savedPrayerIds
            CatechistPrayerCard(
                prayer = prayer,
                saved = saved,
                onOpen = { onReadPrayer(prayer) },
                onToggleSaved = onSetPrayerSaved?.let { save -> { save(prayer, !saved) } },
            )
        }
        message.reference?.let { ref ->
            SacredText(ref, style = type.micro.copy(fontWeight = FontWeight.Medium), color = colors.accentInk)
        }
        val cardSources = message.prayers.mapNotNull { it.sourceUrl }.toSet()
        val otherSources = message.webSources.filterNot { it.url in cardSources }
        if (otherSources.isNotEmpty()) {
            val uriHandler = LocalUriHandler.current
            Column {
                SacredText("Fontes na web", style = type.micro, color = colors.muted)
                otherSources.forEachIndexed { index, source ->
                    Row(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(role = Role.Button) { runCatching { uriHandler.openUri(source.url) } }
                            .testTag("catechist-web-source-$index")
                            .heightIn(min = 48.dp)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SacredText(source.title, Modifier.weight(1f), type.micro, colors.accentInk)
                        SacredIcon(SacredIcon.CHEVRON, Modifier.size(10.dp), colors.marian)
                    }
                }
            }
        }
        val offeredGuideNames = message.prayers.mapNotNull { it.guideName }.toSet()
        message.actions.filterNot { it is CatechistAction.LaunchGuide && it.guideName in offeredGuideNames }.forEachIndexed { idx, action ->
            EmbeddedActionCard(action, onAction, primaryTag = if (idx == 0) "catechist-action-cta" else "catechist-action-cta-$idx")
        }
        message.suggestedFollowUp?.let { followUp ->
            Row(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(18.dp))
                    .clickable(enabled = canSendFollowUp, role = Role.Button, onClickLabel = "Enviar resposta sugerida") {
                        onFollowUp(followUp)
                    }
                    .testTag("catechist-suggested-reply")
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SacredText(followUp, Modifier.weight(1f), type.micro.copy(fontWeight = FontWeight.Normal), colors.secondary)
                SacredIcon(SacredIcon.CHEVRON, Modifier.size(11.dp), colors.marian)
            }
        }
    }
}

@Composable
private fun CatechistToolTrace(trace: List<String>) {
    if (trace.isEmpty()) return
    SacredText(
        trace.joinToString(" · "),
        Modifier.testTag("catechist-tool-trace"),
        SertumTheme.typography.micro.copy(fontWeight = FontWeight.Normal, fontSize = 12.sp),
        SertumTheme.colors.accentInk,
    )
}

@Composable
private fun CatechistReplyText(text: String) {
    val whisper = SertumTheme.typography.whisper
    CatechistMarkdown(text, style = whisper.copy(fontSize = whisper.fontSize * (17.5f / 17f), lineHeight = whisper.lineHeight * (28f / 27f)))
}

@Composable
private fun EmbeddedActionCard(
    action: CatechistAction,
    onAction: (CatechistAction) -> Unit,
    primaryTag: String = "catechist-action-cta",
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val shape = RoundedCornerShape(24.dp)
    Column(
        Modifier.fillMaxWidth()
            .clip(shape)
            .background(colors.elevated)
            .border(1.dp, colors.gold.copy(alpha = .5f), shape)
            .padding(16.dp)
            .testTag("catechist-action-card"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SacredText(action.label, style = type.section.copy(fontSize = 18.sp, lineHeight = 24.sp), color = colors.ink)
        if (action.subtitle.isNotBlank()) {
            SacredText(action.subtitle, style = type.body, color = colors.secondary)
        }
        SacredPrimaryButton(
            text = when (action) {
                is CatechistAction.StartCustomRosary -> if (action.singleDecadeIndex != null) "Rezar esta dezena agora" else "Rezar este terço agora"
                is CatechistAction.StartQuickDecade -> "Rezar esta dezena agora"
                is CatechistAction.LaunchGuide -> "Rezar este roteiro agora"
                is CatechistAction.SaveIntention -> "Guardar no Meu Jardim"
                is CatechistAction.OpenTreasuryPrayer -> "Abrir no Tesouro"
                is CatechistAction.OpenFormationLesson -> "Abrir na Catequese"
                is CatechistAction.OpenFormationQuestion -> "Ver resposta na Catequese"
                is CatechistAction.OpenPracticeJourney -> "Abrir passo a passo"
                is CatechistAction.OpenMysteryStudy -> "Estudar este mistério"
            },
            onClick = { onAction(action) },
            modifier = Modifier.padding(top = 4.dp),
            tag = primaryTag,
            compact = true,
        )
    }
}

@Composable
private fun CatechistProgressTurn(progress: CatechistProgress) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    val transition = rememberInfiniteTransition(label = "catechist-thinking")
    val pulse by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "pulse",
    )
    Column(
        Modifier.fillMaxWidth().testTag("catechist-progress"),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PortraitMedallion(progress.persona, size = 30, modifier = Modifier.alpha(pulse))
            SacredText(
                progress.status,
                Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                type.micro,
                colors.secondary,
            )
        }
        CatechistToolTrace(progress.toolTrace)
        progress.replyPreview?.let { CatechistReplyText(it) }
    }
}

@Composable
fun PortraitMedallion(persona: CatechistPersona, size: Int = 44, modifier: Modifier = Modifier) {
    val colors = SertumTheme.colors
    Box(
        modifier.size(size.dp)
            .clip(CircleShape)
            .background(colors.surface)
            .border(1.dp, colors.gold.copy(alpha = .45f), CircleShape)
            .padding(2.dp),
        contentAlignment = Alignment.Center,
    ) {
        DevotionalArtwork(persona.portrait(), Modifier.fillMaxSize())
    }
}

@Composable
private fun PersonaPickerSheetContent(
    state: CatechistUiState,
    onSelect: (CatechistPersona) -> Unit,
    onToggleOnline: (Boolean) -> Unit,
    onSaveApiKey: (String) -> Unit,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    var showApiSettings by rememberSaveable { mutableStateOf(false) }
    var apiKeyDraft by rememberSaveable(state.customApiKey) { mutableStateOf(state.customApiKey) }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SacredText("Escolha quem caminha com você", Modifier.fillMaxWidth(), type.heading.copy(fontSize = 24.sp, lineHeight = 30.sp), align = TextAlign.Center)
        SacredText("Cada catequista tem seu jeito de conversar, sempre fiel ao Evangelho e ao Catecismo.", Modifier.fillMaxWidth(), type.body, colors.secondary, TextAlign.Center)
        SacredFlourish(Modifier.align(Alignment.CenterHorizontally).padding(vertical = 2.dp))

        SacredCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(vertical = 4.dp)) {
                CatechistPersona.entries.forEachIndexed { index, persona ->
                    if (index > 0) SacredDivider(Modifier.padding(horizontal = 16.dp))
                    val chosen = persona == state.persona
                    Row(
                        Modifier.fillMaxWidth()
                            .clickable(role = Role.Button) { onSelect(persona) }
                            .testTag("persona-option-${persona.id}")
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        DevotionalArtwork(persona.portrait(), Modifier.size(64.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                SacredText(persona.displayName, style = type.section.copy(fontSize = 19.sp, lineHeight = 24.sp))
                                SacredText(
                                    if (persona == CatechistPersona.DEFAULT) "· ${persona.styleTitle} (padrão)" else "· ${persona.styleTitle}",
                                    style = type.micro,
                                    color = colors.accentInk,
                                )
                            }
                            SacredText(persona.description, style = type.body.copy(fontSize = 13.5.sp, lineHeight = 19.sp), color = colors.secondary)
                            SacredText(persona.inspiration, style = type.micro.copy(fontWeight = FontWeight.Normal), color = colors.muted)
                        }
                        if (chosen) {
                            SacredIcon(SacredIcon.CHECK, Modifier.size(18.dp), colors.gold)
                        }
                    }
                }
            }
        }

        SacredCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SacredEyebrow("Motor contemplativo")
                SacredToggleRow(
                    title = "Usar DeepSeek V4.1 Flash online",
                    caption = if (state.hasEffectiveApiKey) "Conversas ao vivo e roteiros sob medida; cai para o modo offline automaticamente sem internet." else "Sem chave configurada — usando respostas offline.",
                    checked = state.onlineEnabled,
                    onToggle = { onToggleOnline(!state.onlineEnabled) },
                    tag = "lumen-online-toggle",
                )
                SacredTextLink(
                    if (showApiSettings) "Ocultar chave Mistral" else "Configurar chave Mistral própria",
                    { showApiSettings = !showApiSettings },
                    "toggle-mistral-key",
                    align = TextAlign.Start,
                    flush = true,
                )
                if (showApiSettings) {
                    GardenField(
                        label = "Chave da API Mistral (opcional)",
                        value = apiKeyDraft,
                        onChange = { apiKeyDraft = it },
                        tag = "mistral-api-key-input",
                        limit = 256,
                        placeholder = "Cole aqui sua chave da API Mistral…",
                    )
                    SacredSecondaryButton(
                        text = "Salvar chave neste aparelho",
                        onClick = { onSaveApiKey(apiKeyDraft); showApiSettings = false },
                        modifier = Modifier.fillMaxWidth(),
                        tag = "save-mistral-key",
                    )
                }
            }
        }
    }
}

@Composable
private fun PrepareCustomRosarySheetContent(
    persona: CatechistPersona,
    defaultSet: MysterySet,
    isPreparing: Boolean,
    initialIntention: String,
    onPrepare: (intention: String, set: MysterySet, mood: LumenMood?) -> Unit,
) {
    val colors = SertumTheme.colors
    val type = SertumTheme.typography
    var intention by rememberSaveable(initialIntention) { mutableStateOf(initialIntention) }
    var chosenSet by rememberSaveable(defaultSet) { mutableStateOf(defaultSet) }
    var chosenMood by rememberSaveable { mutableStateOf<LumenMood?>(null) }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (isPreparing) {
            Column(
                Modifier.fillMaxWidth().padding(vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                DevotionalArtwork(persona.portrait(), Modifier.size(96.dp))
                SacredText("${persona.displayName} está preparando seu terço…", style = type.heading.copy(fontSize = 22.sp, lineHeight = 29.sp), align = TextAlign.Center)
                SacredText(
                    "As orações da Igreja permanecem íntegras; estamos preparando apenas o oferecimento e as meditações nos Mistérios ${chosenSet.label.lowercase()}.",
                    style = type.body,
                    color = colors.secondary,
                    align = TextAlign.Center,
                )
            }
            return
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PortraitMedallion(persona, size = 48)
            Column(Modifier.weight(1f)) {
                SacredText("O que você traz no coração?", style = type.section)
                SacredText("${persona.displayName} prepara o oferecimento e as meditações nos mistérios de hoje.", style = type.body.copy(fontSize = 13.5.sp), color = colors.secondary)
            }
        }

        GardenField(
            label = "Sua intenção ou momento de hoje",
            value = intention,
            onChange = { intention = it },
            tag = "custom-rosary-intention",
            multiline = true,
            limit = 600,
            placeholder = "Ex.: Pela saúde da minha mãe, por paz nas decisões desta semana…",
        )

        // Mystery set selector (reuses adaptive single/multi-row sanctuary selector)
        SacredText("Mistérios a contemplar", Modifier.padding(start = 4.dp), type.micro, colors.accentInk)
        MysterySelector(chosenSet) { chosenSet = it }

        // Interior state rows (quiet list with honey checkmark, no pill clutter)
        SacredCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
                SacredText("Como está seu espírito agora? (opcional)", style = type.label.copy(fontWeight = FontWeight.SemiBold))
                Spacer(Modifier.height(6.dp))
                LumenMood.entries.forEachIndexed { idx, mood ->
                    if (idx > 0) SacredDivider()
                    val selected = chosenMood == mood
                    Row(
                        Modifier.fillMaxWidth()
                            .clickable(role = Role.Button) { chosenMood = if (selected) null else mood }
                            .testTag("mood-${mood.id}")
                            .heightIn(min = 48.dp)
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(Modifier.weight(1f)) {
                            SacredText(mood.label, style = type.label, color = if (selected) colors.ink else colors.secondary)
                            SacredText(mood.hint, style = type.micro.copy(fontWeight = FontWeight.Normal), color = colors.muted)
                        }
                        if (selected) SacredIcon(SacredIcon.CHECK, Modifier.size(16.dp), colors.gold)
                    }
                }
            }
        }

        SacredText(
            "O Pai-Nosso, a Ave-Maria e todas as orações da Igreja nunca são alterados.",
            Modifier.fillMaxWidth(),
            type.micro.copy(fontWeight = FontWeight.Normal),
            colors.muted,
            TextAlign.Center,
        )

        SacredPrimaryButton(
            text = "Preparar e rezar hoje",
            onClick = { onPrepare(intention, chosenSet, chosenMood) },
            tag = "generate-custom-rosary",
        )
    }
}
