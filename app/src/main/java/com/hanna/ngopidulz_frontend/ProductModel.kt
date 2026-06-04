package com.hanna.ngopidulz_frontend

import com.google.gson.annotations.SerializedName

data class ProductModel(
    // 👇 JURUS SAKTI: Biar Android bisa ngebaca 'id' biasa maupun '_id' MongoDB 👇
    @SerializedName("id", alternate = ["_id"]) val id: String,

    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Int, // 💡 Wajib Int karena di Laravel tipenya integer
    @SerializedName("category") val category: String,
    @SerializedName("image") val image: String? = "img_default.png"
)