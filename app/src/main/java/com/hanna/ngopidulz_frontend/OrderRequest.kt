package com.hanna.ngopidulz_frontend

import com.google.gson.annotations.SerializedName

// Ini pembungkus luarnya (Total Harga & Daftar Belanjaan)
data class OrderRequest(
    @SerializedName("total_price") val totalPrice: Int,
    @SerializedName("items") val items: List<OrderItemRequest>
)

// Ini isi daftar belanjaannya (Kopi apa, jumlahnya berapa, subtotalnya berapa)
data class OrderItemRequest(
    @SerializedName("product_id") val productId: String,
    @SerializedName("qty") val qty: Int,
    @SerializedName("subtotal") val subtotal: Int
)