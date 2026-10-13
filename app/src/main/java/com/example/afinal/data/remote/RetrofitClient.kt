package com.example.afinal.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val hibpApiService: HibpApiService by lazy {
        Retrofit.Builder()
            .baseUrl(HibpApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HibpApiService::class.java)
    }

    val openRouterApiService: OpenRouterApiService by lazy {
        Retrofit.Builder()
            .baseUrl(OpenRouterApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenRouterApiService::class.java)
    }

    val tinyFishApiService: TinyFishApiService by lazy {
        Retrofit.Builder()
            .baseUrl(TinyFishApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TinyFishApiService::class.java)
    }

    // กำหนด Base URL ของเซิร์ฟเวอร์ที่คุณใช้รับ-ส่ง ChatRequest
    // หากทดสอบรันบน Emulator และเซิร์ฟเวอร์รันอยู่บนเครื่องคอมพิวเตอร์เดียวกัน ให้ใช้ 10.0.2.2
    val customGeminiService: GeminiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/") 
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiService::class.java)
    }
}
