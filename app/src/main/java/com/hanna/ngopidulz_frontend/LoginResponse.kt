package com.hanna.ngopidulz_frontend

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val message: String? = null,
    val token: String? = null,
    val user: UserLoginData? = null
)

data class UserLoginData(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    // 👇 TAMBAHKAN BARIS INI 👇
    val role: String? = null
)