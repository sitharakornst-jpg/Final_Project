package com.example.afinal.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class ChatRequest(val message: String)
data class ChatResponse(val reply: String, val status: String)

interface GeminiService {
    @POST("ask")
    suspend fun getGeminiReply(@Body request: ChatRequest): Response<ChatResponse>
}
