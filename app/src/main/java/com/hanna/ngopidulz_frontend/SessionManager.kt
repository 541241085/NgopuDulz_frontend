package com.hanna.ngopidulz_frontend

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("NgopuDulzPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
        private const val IS_LOGGED_IN = "is_logged_in"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
    fun saveRole(role: String) {
        val editor = prefs.edit()
        editor.putString("USER_ROLE", role)
        editor.apply()
    }

    // 👇 FUNGSI BARU UNTUK MENGAMBIL ROLE 👇
    fun fetchRole(): String? {
        return prefs.getString("USER_ROLE", "customer") // Defaultnya 'customer' kalau gak ketemu
    }
    fun clearAuthToken() {
        val editor = prefs.edit() // ⚠️ Sesuaikan kata 'prefs' dengan nama SharedPreferences di filemu (bisa 'sharedPreferences' atau 'pref')
        editor.clear() // Menghapus semua session termasuk token login
        editor.apply()
    }
}