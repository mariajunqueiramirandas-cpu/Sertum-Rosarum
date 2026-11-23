package com.sertumrosarum.data.lumen

import com.sertumrosarum.domain.lumen.LumenGuardrails
import com.sertumrosarum.domain.lumen.LumenJsonParser
import com.sertumrosarum.domain.lumen.LumenTools
import com.sertumrosarum.domain.lumen.MistralStepResponse
import com.sertumrosarum.domain.lumen.MistralWireMessage
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Abstraction over the LLM transport so unit and UI tests stay deterministic and offline. */
interface LumenApiClient {
    suspend fun completeJson(
        apiKey: String,
        systemPrompt: String,
        messages: List<Pair<String, String>>,
        model: String = LumenGuardrails.MISTRAL_MODEL,
        temperature: Double = 0.7,
    ): Result<String>

    suspend fun completeAgentStep(
        apiKey: String,
        systemPrompt: String,
        messages: List<MistralWireMessage>,
        toolsJson: String = LumenTools.mistralToolsJson,
        model: String = LumenGuardrails.MISTRAL_MODEL,
        temperature: Double = 0.72,
    ): Result<MistralStepResponse> = Result.failure(UnsupportedOperationException("Agent tool loop not implemented"))
}

/**
 * Lightweight zero-dependency HTTPS client for Mistral's Chat Completions API
 * targeting [LumenGuardrails.MISTRAL_MODEL] (`mistral-large-4`) with native Function Calling
 * and NO artificial `max_tokens` limits.
 */
class MistralLumenClient(
    private val endpointUrl: String = DEFAULT_ENDPOINT,
    private val connectTimeoutMs: Int = 15_000,
    private val readTimeoutMs: Int = 120_000,
) : LumenApiClient {

    override suspend fun completeJson(
        apiKey: String,
        systemPrompt: String,
        messages: List<Pair<String, String>>,
        model: String,
        temperature: Double,
    ): Result<String> = withContext(Dispatchers.IO) {
        val payload = LumenJsonParser.buildMistralChatRequest(
            model = model,
            systemPrompt = systemPrompt,
            messages = messages,
            temperature = temperature,
        )
        postJson(apiKey, payload).mapCatching { body ->
            LumenJsonParser.extractMistralContent(body)
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
    ): Result<MistralStepResponse> = withContext(Dispatchers.IO) {
        val payload = LumenJsonParser.buildMistralAgentRequest(
            model = model,
            systemPrompt = systemPrompt,
            messages = messages,
            toolsJson = toolsJson,
            temperature = temperature,
        )
        postJson(apiKey, payload).mapCatching { body ->
            LumenJsonParser.parseMistralStep(body)
                ?: error("Resposta inválida do modelo $model.")
        }
    }

    private fun postJson(apiKey: String, payload: String): Result<String> {
        if (apiKey.isBlank()) {
            return Result.failure(IllegalStateException("Chave da API Mistral não configurada."))
        }
        var connection: HttpURLConnection? = null
        return try {
            connection = (URL(endpointUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = connectTimeoutMs
                readTimeout = readTimeoutMs
                doOutput = true
                setRequestProperty("Authorization", "Bearer ${apiKey.trim()}")
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }
            connection.outputStream.use { out ->
                out.write(payload.toByteArray(StandardCharsets.UTF_8))
                out.flush()
            }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.use { input ->
                BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).readText()
            }.orEmpty()
            if (code !in 200..299) {
                Result.failure(IllegalStateException("Mistral HTTP $code: ${body.take(260)}"))
            } else {
                Result.success(body)
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }

    companion object {
        const val DEFAULT_ENDPOINT = "https://api.mistral.ai/v1/chat/completions"
    }
}
