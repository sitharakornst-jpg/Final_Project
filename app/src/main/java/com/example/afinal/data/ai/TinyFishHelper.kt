package com.example.afinal.data.ai

import com.example.afinal.data.remote.RetrofitClient
import com.example.afinal.data.remote.TinyFishChatRequest
import com.example.afinal.data.remote.TinyFishMessage

class TinyFishHelper(private val apiKey: String) {

    suspend fun analyzePasswordSecurity(promptText: String): String {
        return try {
            val authHeader = if (apiKey.startsWith("Bearer ", ignoreCase = true)) apiKey else "Bearer $apiKey"
            val request = TinyFishChatRequest(
                model = "tinyfish",
                messages = listOf(
                    TinyFishMessage(
                        role = "system",
                        content = "คุณคือผู้เชี่ยวชาญด้านความปลอดภัยไซเบอร์ (Cybersecurity Expert & Password Analyst) ให้คำตอบเป็นภาษาไทยที่กระชับ สดใส และตรงประเด็น"
                    ),
                    TinyFishMessage(
                        role = "user",
                        content = promptText
                    )
                )
            )

            val response = RetrofitClient.tinyFishApiService.createChatCompletion(authHeader, request)
            if (response.isSuccessful) {
                val choices = response.body()?.choices
                if (!choices.isNullOrEmpty()) {
                    return choices.first().message.content
                }
            }
            throw Exception("Response error code: ${response.code()}")
        } catch (e: Exception) {
            throw e
        }
    }
}
