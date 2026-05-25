package com.hanna.ngopidulz_frontend

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse> // <-- Pastikan ini sudah pakai LoginResponse
    @GET("user") // Sesuaikan dengan rute get profil di Laravel kamu (biasanya bawaan sanctum adalah /api/user)
    fun getUserProfile(
        @Header("Authorization") token: String
    ): Call<UserResponse>
    // Ubah bagian ini ya:
    @GET("products")
    fun getProducts(
        @Header("Authorization") token: String
    ): Call<ProductResponse> // 👈 UBAH BAGIAN INI SAJA
}