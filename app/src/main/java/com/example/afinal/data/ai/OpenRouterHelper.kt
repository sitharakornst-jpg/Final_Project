package com.example.afinal.data.ai

import com.example.afinal.data.remote.OpenRouterChatRequest
import com.example.afinal.data.remote.OpenRouterMessage
import com.example.afinal.data.remote.OpenRouterReasoning
import com.example.afinal.data.remote.RetrofitClient

class OpenRouterHelper(private val apiKey: String) {

    suspend fun analyzePasswordSecurity(promptText: String): String {
        return try {
            val authHeader = if (apiKey.startsWith("Bearer ", ignoreCase = true)) apiKey else "Bearer $apiKey"
            val request = OpenRouterChatRequest(
                model = "openrouter/free",
                reasoning = OpenRouterReasoning(enabled = true),
                messages = listOf(
                    OpenRouterMessage(
                        role = "system",
                        content = "You are a Cybersecurity Expert & Password Analyst. Provide concise, easy-to-read, and highly beneficial advice in English to improve the given password structure."
                    ),
                    OpenRouterMessage(
                        role = "user",
                        content = promptText
                    )
                )
            )

            val response = RetrofitClient.openRouterApiService.createChatCompletion(authorization = authHeader, request = request)
            if (response.isSuccessful) {
                val choices = response.body()?.choices
                if (!choices.isNullOrEmpty()) {
                    return choices.first().message.content
                }
            }
            throw Exception("OpenRouter API Error: ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            throw e
        }
    }
}
