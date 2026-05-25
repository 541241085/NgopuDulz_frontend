package com.hanna.ngopidulz_frontend

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Tambahkan Serializable agar datanya bisa dilempar ke halaman Detail nanti
data class Product(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("price") val price: Int? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null
) : Serializable