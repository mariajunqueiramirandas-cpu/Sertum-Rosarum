package com.sertumrosarum.data.lumen

import com.sertumrosarum.domain.lumen.LumenGuardrails
import com.sertumrosarum.domain.lumen.LumenJsonParser
import com.sertumrosarum.domain.lumen.LumenTools
import com.sertumrosarum.domain.lumen.LumenWebSearchResult
import com.sertumrosarum.domain.lumen.MistralAgentStream
import com.sertumrosarum.domain.lumen.MistralStepResponse
import com.sertumrosarum.domain.lumen.MistralStepUpdate
import com.sertumrosarum.domain.lumen.MistralWireMessage
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible

/** Abstraction over the LLM transport so unit and UI tests stay deterministic and offline. */
interface LumenApiClient {
    suspend fun completeJson(
        apiKey: String,
        systemPrompt: String,
        messages: List<Pair<String, String>>,
        model: String = LumenGuardrails.DEEPSEEK_FLASH_MODEL,
        temperature: Double = 0.7,
    ): Result<String>

    suspend fun completeAgentStep(
        apiKey: String,
        systemPrompt: String,
        messages: List<MistralWireMessage>,
        toolsJson: String = LumenTools.mistralToolsJson,
        model: String = LumenGuardrails.DEEPSEEK_FLASH_MODEL,
        temperature: Double = 0.72,
        onUpdate: (MistralStepUpdate) -> Unit = {},
    ): Result<MistralStepResponse> = Result.failure(UnsupportedOperationException("Agent tool loop not implemented"))

    suspend fun searchWebPrayers(apiKey: String, query: String): Result<LumenWebSearchResult> =
        Result.failure(UnsupportedOperationException("Web search not implemented"))
}

/**
 * HTTPS client for Mistral's Chat Completions API with streaming and function calling.
 */
class MistralLumenClient(
    private val endpointUrl: String = DEFAULT_ENDPOINT,
    private val connectTimeoutMs: Int = 15_000,
    private val readTimeoutMs: Int = 120_000,
    private val webSearchEndpointUrl: String = DEFAULT_WEB_SEARCH_ENDPOINT,
) : LumenApiClient {

    override suspend fun completeJson(
        apiKey: String,
        systemPrompt: String,
        messages: List<Pair<String, String>>,
        model: String,
        temperature: Double,
    ): Result<String> = runInterruptible(Dispatchers.IO) {
        val payload = LumenJsonParser.buildMistralChatRequest(
            model = model,
            systemPrompt = systemPrompt,
            messages = messages,
            temperature = temperature,
        )
        post(apiKey, payload, accept = "application/json") { _, reader ->
            LumenJsonParser.extractMistralContent(reader.readText())
                ?: error("Resposta vazia do modelo $model.")
        }
    }

    override suspend fun completeAgentStep(
        apiKey: String,
        systemPrompt: String,
        messages: List<MistralWireMessage>,
        toolsJson: String,
        model: String,
        temperature: Double,
        onUpdate: (MistralStepUpdate) -> Unit,
    ): Result<MistralStepResponse> = runInterruptible(Dispatchers.IO) {
        val payload = LumenJsonParser.buildMistralAgentRequest(
            model = model,
            systemPrompt = systemPrompt,
            messages = messages,
            toolsJson = toolsJson,
            temperature = temperature,
            stream = true,
        )
        post(apiKey, payload, accept = "text/event-stream, application/json") { contentType, reader ->
            if (contentType?.substringBefore(';')?.trim().equals("text/event-stream", ignoreCase = true)) {
                MistralAgentStream(onUpdate).read(reader)
            } else {
                LumenJsonParser.parseMistralStep(reader.readText()) ?: error("Resposta inválida do modelo $model.")
            }
        }
    }

    override suspend fun searchWebPrayers(apiKey: String, query: String): Result<LumenWebSearchResult> =
        runInterruptible(Dispatchers.IO) {
            val payload = LumenJsonParser.buildMistralWebSearchRequest(query)
            post(apiKey, payload, accept = "application/json", url = webSearchEndpointUrl) { _, reader ->
                LumenJsonParser.parseMistralWebSearch(reader.readText())
                    ?: error("A busca na web não retornou fontes.")
            }
        }

    private fun <T> post(
        apiKey: String,
        payload: String,
        accept: String,
        url: String = endpointUrl,
        read: (String?, BufferedReader) -> T,
    ): Result<T> {
        if (apiKey.isBlank()) {
            return Result.failure(IllegalStateException("Chave da API Mistral não configurada."))
        }
        var connection: HttpURLConnection? = null
        return try {
            connection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = connectTimeoutMs
                readTimeout = readTimeoutMs
                doOutput = true
                setRequestProperty("Authorization", "Bearer ${apiKey.trim()}")
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", accept)
            }
            connection.outputStream.use { out ->
                out.write(payload.toByteArray(StandardCharsets.UTF_8))
                out.flush()
            }
            val code = connection.responseCode
            if (code !in 200..299) {
                val body = connection.errorStream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }.orEmpty()
                Result.failure(IllegalStateException("Mistral HTTP $code: ${body.take(260)}"))
            } else {
                val contentType = connection.contentType
                val result = connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
                    read(contentType, reader)
                }
                Result.success(result)
            }
        } catch (e: Exception) {
            if (e is CancellationException || e is InterruptedException) throw e
            Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }

    companion object {
        const val DEFAULT_ENDPOINT = "https://api.mistral.ai/v1/chat/completions"
        const val DEFAULT_WEB_SEARCH_ENDPOINT = "https://api.mistral.ai/v1/conversations"
    }
}
