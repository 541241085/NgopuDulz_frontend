package com.hanna.ngopidulz_frontend

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    // 👇 TAMBAHKAN RUTE INI DI BAWAH getProducts 👇
    // 👇 TAMBAHKAN BARIS INI 👇
    // 👇 GANTI FUNGSI createOrder SEBELUMNYA DENGAN INI 👇
    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("orders") // Sesuaikan jika rutenya 'order'
    fun createOrder(
        @Header("Authorization") token: String,
        @Body request: OrderRequest // 👈 Pakai @Body, kirim satu paket kardus utuh
    ): Call<OrderResponse>

    // 1. Mengambil daftar antrean pesanan untuk Kasir
    @GET("cashier/orders")
    fun getCashierOrders(
        @Header("Authorization") token: String
    ): Call<CashierOrderResponse>

    // 2. Kasir mengubah status pesanan (misal: dari pending -> diproses)
    @PUT("cashier/orders/{id}/status")
    fun updateOrderStatus(
        @Header("Authorization") token: String,
        @Path("id") orderId: String,
        @Body request: StatusRequest
    ): Call<GeneralResponse>
}