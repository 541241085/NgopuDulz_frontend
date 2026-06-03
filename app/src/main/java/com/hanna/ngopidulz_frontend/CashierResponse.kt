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
    // 👇 Tambahkan alternate biar dia bisa nangkep 'id' atau '_id', plus kasih tanda tanya (?)
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
    val product: ProductInfo?
)

// Cetakan nama produk di dalam pesanan
data class ProductInfo(
    val name: String?
)