package com.example.afinal.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class OpenRouterMessage(
    val role: String,
    val content: String
)

data class OpenRouterReasoning(
    val enabled: Boolean
)

data class OpenRouterChatRequest(
    val model: String = "openrouter/free",
    val messages: List<OpenRouterMessage>,
    val reasoning: OpenRouterReasoning? = null,
    val temperature: Double = 0.7
)

data class OpenRouterChoice(
    val message: OpenRouterMessage
)

data class OpenRouterChatResponse(
    val choices: List<OpenRouterChoice>?
)

interface OpenRouterApiService {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Header("HTTP-Referer") referer: String = "https://example.com",
        @Header("X-Title") title: String = "AI Password Analyzer",
        @Body request: OpenRouterChatRequest
    ): Response<OpenRouterChatResponse>

    companion object {
        const val BASE_URL = "https://openrouter.ai/api/"
    }
}
