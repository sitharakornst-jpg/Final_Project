package com.example.afinal.data.remote

import retrofit2.http.GET

data class BoredActivityResponse(
    val activity: String,
    val type: String?,
    val participants: Int?,
    val price: Double?
)

interface BoredApiService {
    @GET("random")
    suspend fun getRandomActivity(): BoredActivityResponse

    companion object {
        const val BASE_URL = "https://bored-api.appbrewery.com/"
    }
}
