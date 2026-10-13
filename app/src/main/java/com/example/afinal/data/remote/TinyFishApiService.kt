package com.example.afinal.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class TinyFishMessage(
    val role: String,
    val content: String
)

data class TinyFishChatRequest(
    val model: String = "tinyfish",
    val messages: List<TinyFishMessage>,
    val temperature: Double = 0.7
)

data class TinyFishChoice(
    val message: TinyFishMessage
)

data class TinyFishChatResponse(
    val choices: List<TinyFishChoice>?
)

interface TinyFishApiService {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: TinyFishChatRequest
    ): Response<TinyFishChatResponse>

    companion object {
        const val BASE_URL = "https://api.tinyfish.io/"
    }
}
