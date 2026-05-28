package com.hanna.ngopidulz_frontend

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("message") val message: String? = null,
    // 👇 TAMBAHKAN BARIS INI 👇
    @SerializedName("payment_url") val paymentUrl: String? = null
)