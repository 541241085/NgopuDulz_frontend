package com.hanna.ngopidulz_frontend

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Product(
    // Ubah dari Int? menjadi String?
    @SerializedName("id") val id: String? = null,

    @SerializedName("name") val name: String? = null,
    @SerializedName("price") val price: Int? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null
) : Serializable