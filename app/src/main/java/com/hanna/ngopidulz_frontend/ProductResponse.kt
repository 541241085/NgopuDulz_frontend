package com.hanna.ngopidulz_frontend

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    // Kita suruh Android buka kardus bernama "data", baru isinya adalah List<Product>
    @SerializedName("data")
    val data: List<Product>
)