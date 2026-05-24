package com.hanna.ngopidulz_frontend

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    // Ganti yang 10.0.2.2 (emulator) menjadi IP Wi-Fi aslimu
    private const val BASE_URL = "http://192.168.18.10:8000/api/"

    fun getApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }
}