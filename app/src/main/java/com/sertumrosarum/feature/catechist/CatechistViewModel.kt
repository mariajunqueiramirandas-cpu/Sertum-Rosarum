package com.sertumrosarum.feature.catechist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sertumrosarum.data.garden.GardenRepository
import com.sertumrosarum.data.lumen.LumenRepository
import com.sertumrosarum.domain.lumen.CatechistContext
import com.sertumrosarum.domain.lumen.CatechistMessage
import com.sertumrosarum.domain.lumen.CatechistPersona
import com.sertumrosarum.domain.lumen.CatechistProgress
import com.sertumrosarum.domain.lumen.CustomRosarySupplement
import com.sertumrosarum.domain.lumen.LumenGuardrails
import com.sertumrosarum.domain.lumen.LumenMood
import com.sertumrosarum.domain.lumen.LumenToolBridge
import com.sertumrosarum.domain.prayer.MysterySet
import com.sertumrosarum.ui.canvas.DevotionalIllustration
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun CatechistPersona.portrait(): DevotionalIllustration = when (this) {
    CatechistPersona.MATEUS -> DevotionalIllustration.CATECHIST_MATEUS
    CatechistPersona.LUCAS -> DevotionalIllustration.CATECHIST_LUCAS
    CatechistPersona.CLARA -> DevotionalIllustration.CATECHIST_CLARA
    CatechistPersona.BENTO -> DevotionalIllustration.CATECHIST_BENTO
}

data class CatechistUiState(
    val persona: CatechistPersona = CatechistPersona.DEFAULT,
    val messages: List<CatechistMessage> = emptyList(),
    val progress: CatechistProgress? = null,
    val isPreparingRosary: Boolean = false,
    val onlineEnabled: Boolean = true,
    val customApiKey: String = "",
    val hasEffectiveApiKey: Boolean = false,
    val activeContextLabel: String? = null,
    val notice: String? = null,
) {
    val isThinking: Boolean get() = progress != null
}

class CatechistViewModel(
    private val lumen: LumenRepository = LumenRepository(),
) : ViewModel() {

    private val mutableState = MutableStateFlow(
        CatechistUiState(hasEffectiveApiKey = lumen.hasApiKey()),
    )
    val state = mutableState.asStateFlow()

    private var gardenRepository: GardenRepository? = null
    private var currentContext = CatechistContext(activeMysterySet = MysterySet.JOYFUL)
    private var toolBridge: LumenToolBridge = LumenToolBridge.EMPTY
    private var conversationJob: Job? = null

    suspend fun connect(repository: GardenRepository) {
        if (gardenRepository != null) return
        gardenRepository = repository
        val prefs = withContext(Dispatchers.IO) { repository.preferences() }
        val persona = CatechistPersona.byId(prefs.catechistPersona)
        mutableState.update {
            it.copy(
                persona = persona,
                onlineEnabled = prefs.lumenOnline,
                customApiKey = prefs.mistralApiKey,
                hasEffectiveApiKey = lumen.hasApiKey(prefs.mistralApiKey),
            )
        }
    }

    fun attachToolBridge(bridge: LumenToolBridge) {
        toolBridge = bridge
    }

    fun updateContext(context: CatechistContext) {
        currentContext = context
        val label = if (!context.activeMysteryTitle.isNullOrBlank() && context.activeMysteryIndex != null) {
            "${context.activeMysteryIndex + 1}º Mistério · ${context.activeMysteryTitle}"
        } else null
        mutableState.update { it.copy(activeContextLabel = label) }
    }

    fun selectPersona(persona: CatechistPersona) {
        mutableState.update { it.copy(persona = persona, notice = null) }
        persistPrefs()
    }

    fun setOnlineEnabled(enabled: Boolean) {
        mutableState.update { it.copy(onlineEnabled = enabled) }
        persistPrefs()
    }

    fun setCustomApiKey(apiKey: String) {
        val clean = apiKey.trim()
        mutableState.update {
            it.copy(
                customApiKey = clean,
                hasEffectiveApiKey = lumen.hasApiKey(clean),
            )
        }
        persistPrefs()
    }

    fun clearNotice() {
        mutableState.update { it.copy(notice = null) }
    }

    fun showNotice(message: String) {
        mutableState.update { it.copy(notice = message) }
    }

    fun newConversation() {
        conversationJob?.cancel()
        mutableState.update { it.copy(messages = emptyList(), progress = null, notice = null) }
    }

    fun sendMessage(rawText: String, overrideContext: CatechistContext? = null) {
        val text = LumenGuardrails.sanitizeInput(rawText)
        if (text.isBlank() || mutableState.value.isThinking) return
        val ctx = overrideContext ?: currentContext
        val snapshot = mutableState.value
        val userMsg = CatechistMessage(
            id = UUID.randomUUID().toString(),
            fromUser = true,
            text = text,
        )
        val initialStatus = if (snapshot.onlineEnabled && snapshot.hasEffectiveApiKey) {
            "Conectando com ${snapshot.persona.displayName}…"
        } else {
            snapshot.persona.thinkingLine
        }
        mutableState.update {
            it.copy(
                messages = it.messages + userMsg,
                progress = CatechistProgress(snapshot.persona, initialStatus),
                notice = null,
            )
        }
        fun updateActiveTurn(transform: (CatechistUiState) -> CatechistUiState) {
            mutableState.update { state ->
                if (state.isThinking && state.messages.lastOrNull { it.fromUser }?.id == userMsg.id) {
                    transform(state)
                } else state
            }
        }
        conversationJob = viewModelScope.launch {
            val turn = lumen.sendConversationTurn(
                userMessage = text,
                history = snapshot.messages,
                persona = snapshot.persona,
                context = ctx,
                toolBridge = toolBridge,
                onlineEnabled = snapshot.onlineEnabled,
                userApiKeyOverride = snapshot.customApiKey,
                onInterimReply = { text ->
                    val message = CatechistMessage(
                        id = UUID.randomUUID().toString(),
                        fromUser = false,
                        text = text,
                        persona = snapshot.persona,
                        isInterim = true,
                    )
                    updateActiveTurn { it.copy(messages = it.messages + message) }
                },
                onProgress = { progress ->
                    updateActiveTurn { it.copy(progress = progress) }
                },
            )
            val replyMsg = CatechistMessage(
                id = UUID.randomUUID().toString(),
                fromUser = false,
                text = turn.replyText,
                persona = snapshot.persona,
                reference = turn.reference,
                actions = turn.actions,
                toolTrace = turn.toolTrace,
                suggestedFollowUp = turn.suggestedFollowUp,
                isOfflineFallback = turn.isOfflineFallback,
                webSources = turn.webSources,
                prayers = turn.prayers,
            )
            updateActiveTurn {
                it.copy(messages = it.messages + replyMsg, progress = null)
            }
        }
    }

    fun prepareCustomRosary(
        intention: String,
        mysterySet: MysterySet,
        mood: LumenMood?,
        onReady: (CustomRosarySupplement) -> Unit,
    ) {
        if (mutableState.value.isPreparingRosary) return
        val snapshot = mutableState.value
        mutableState.update { it.copy(isPreparingRosary = true, notice = null) }
        viewModelScope.launch {
            val supplement = lumen.generateCustomRosary(
                intention = intention,
                mysterySet = mysterySet,
                mood = mood,
                persona = snapshot.persona,
                onlineEnabled = snapshot.onlineEnabled,
                userApiKeyOverride = snapshot.customApiKey,
            )
            mutableState.update { it.copy(isPreparingRosary = false) }
            onReady(supplement)
        }
    }

    private fun persistPrefs() {
        val s = mutableState.value
        gardenRepository?.enqueue {
            gardenRepository?.storeLumenPreferences(s.persona.id, s.onlineEnabled, s.customApiKey)
        }
    }

    companion object {
        fun factory(lumen: LumenRepository = LumenRepository()): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    CatechistViewModel(lumen) as T
            }
    }
}
