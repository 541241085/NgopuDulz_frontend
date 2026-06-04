package com.hanna.ngopidulz_frontend

import com.google.gson.annotations.SerializedName

// Cetakan untuk data yang dikirim Android saat ngubah status
data class StatusRequest(
    val status: String,
    val payment_status: String? = null
)

// Cetakan balasan umum (cuma nerima pesan sukses/gagal)
data class GeneralResponse(
    val message: String? = null
)

// Cetakan balasan daftar antrean pesanan dari Laravel
data class CashierOrderResponse(
    val message: String? = null,
    val data: List<CashierOrder>? = null
)

// Cetakan detail 1 pesanan di layar Kasir
data class CashierOrder(
    @SerializedName("id", alternate = ["_id"]) val id: String?,
    @SerializedName("total_price") val totalPrice: Int,
    val status: String,
    @SerializedName("payment_status") val paymentStatus: String,
    val user: UserInfo?,
    val items: List<OrderItemInfo>?
)

// Cetakan nama pelanggan
data class UserInfo(
    val name: String?
)

// Cetakan barang apa saja yang dibeli di pesanan tersebut
data class OrderItemInfo(
    val qty: Int,
    val product: ProductInfo?,

    // 👇 BARIS AJAIB INI SUDAH DITAMBAHKAN 👇
    @SerializedName("subtotal") val subtotal: Int
)

// Cetakan nama produk di dalam pesanan
data class ProductInfo(
    val name: String?
)
// 👇 TARUH INI DI BAGIAN PALING BAWAH FILE CashierResponse.kt KAK 👇


// Cetakan detail produk/kopi untuk kebutuhan CRUD Admin
// Wadah respon list akun dari Laravel
data class AdminUserResponse(
    val message: String? = null,
    val data: List<AdminUserModel>? = null
)

// Detail data tiap akun user/pegawai
data class AdminUserModel(
    @SerializedName("id", alternate = ["_id"]) val id: String,
    val name: String,
    val email: String,
    val role: String,

    // 👇 UBAH JADI SEPERTI INI BIAR AMAN DARI DATA KOSONG/NULL 👇
    val status: String? = "active"
)