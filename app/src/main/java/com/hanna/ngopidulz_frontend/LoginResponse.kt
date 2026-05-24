package com.hanna.ngopidulz_frontend

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("message")
    val message: String? = null,

    // Ganti "token" dengan "access_token" jika di Laravel kamu menamakannya access_token
    @SerializedName("token")
    val token: String? = null
)