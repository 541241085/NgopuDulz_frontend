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
import retrofit2.http.DELETE

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("user")
    fun getUserProfile(
        @Header("Authorization") token: String
    ): Call<UserResponse>

    // Jalur produk khusus Customer biasa
    @GET("products")
    fun getProducts(
        @Header("Authorization") token: String
    ): Call<ProductResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("orders")
    fun createOrder(
        @Header("Authorization") token: String,
        @Body request: OrderRequest
    ): Call<OrderResponse>

    // 1. Mengambil daftar antrean pesanan untuk Kasir
    @GET("cashier/orders")
    fun getCashierOrders(
        @Header("Authorization") token: String
    ): Call<CashierOrderResponse>

    // 2. Kasir mengubah status pesanan (pending -> diproses -> selesai)
    @PUT("cashier/orders/{id}/status")
    fun updateOrderStatus(
        @Header("Authorization") token: String,
        @Path("id") orderId: String,
        @Body request: StatusRequest
    ): Call<GeneralResponse>

    @GET("customer/orders")
    fun getCustomerOrders(
        @Header("Authorization") token: String
    ): Call<CashierOrderResponse>

    // ====================================================
    // 👑 JALUR SINKRON KHUSUS ADMIN NGOPUDULZ (FIXED 100%)
    // ====================================================

    // 1. Manajemen Akun & Suspend User
    @GET("admin/users")
    fun getAllUsers(@Header("Authorization") token: String): Call<AdminUserResponse>

    @PUT("admin/users/{id}")
    fun updateUserStatus(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body body: HashMap<String, String>
    ): Call<GeneralResponse>

    // 2. CRUD Pengelolaan Produk Sisi Admin (Sudah Diarahkan ke Jalur Benar)
    @GET("admin/products") // 🟢 SEKARANG SUDAH BENAR JALUR ADMIN
    fun getAdminProducts(@Header("Authorization") token: String): Call<AdminProductResponse>

    @POST("admin/products") // 🟢 SEKARANG SUDAH BENAR JALUR ADMIN
    fun createProduct(@Header("Authorization") token: String, @Body body: ProductModel): Call<GeneralResponse>
    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String
    ): Call<GeneralResponse> // 💡 Kita pakai GeneralResponse milikmu biar gak usah bikin file baru
    @PUT("admin/products/{id}")
    fun updateProduct(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body body: ProductModel
    ): Call<GeneralResponse>

    @DELETE("admin/products/{id}")
    fun deleteProduct(@Header("Authorization") token: String, @Path("id") id: String): Call<GeneralResponse>

    // 3. Autentikasi & Profil Akun Admin
    @GET("user")
    fun getAdminProfile(@Header("Authorization") token: String): Call<AdminUserModel>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<GeneralResponse>
}