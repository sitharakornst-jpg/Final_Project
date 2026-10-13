package com.example.afinal.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface HibpApiService {
    @Headers("User-Agent: Final-Password-Analyzer")
    @GET("range/{hashPrefix}")
    suspend fun checkPwnedRange(@Path("hashPrefix") hashPrefix: String): Response<ResponseBody>

    companion object {
        const val BASE_URL = "https://api.pwnedpasswords.com/"
    }
}
