package com.hanna.ngopidulz_frontend

import com.google.gson.annotations.SerializedName

data class AdminProductResponse(
    @SerializedName("message") val message: String?,

    // 👇 NAMA VARIABEL WAJIB 'data' SESUAI RETURN JSON LARAVEL 👇
    @SerializedName("data") val data: List<ProductModel>?
)