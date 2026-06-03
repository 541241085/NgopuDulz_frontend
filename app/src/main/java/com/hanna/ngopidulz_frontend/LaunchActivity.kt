package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menyambungkan file Kotlin ini dengan desain UI milik temanmu
        setContentView(R.layout.activity_launch)

        val sessionManager = SessionManager(this)

        // Loading 2 detik saat buka aplikasi
        // Contoh kalau pakai delay Handler (sesuaikan dengan kodingan aslimu)
        Handler(Looper.getMainLooper()).postDelayed({

            val token = sessionManager.fetchAuthToken()

            if (token != null) {
                // KALAU SUDAH LOGIN, CEK ROLENYA
                val role = sessionManager.fetchRole() ?: "customer"

                val intent = when (role.lowercase()) {
                    "admin" -> Intent(this, AdminDashboardActivity::class.java)
                    "kasir" -> Intent(this, DashboardKasir::class.java)
                    else -> Intent(this, MainActivity::class.java)
                }
                startActivity(intent)

            } else {
                // KALAU BELUM LOGIN, LEMPAR KE HALAMAN DAFTAR / MASUK
                startActivity(Intent(this, DaftarActivity::class.java)) // Sesuaikan
            }
            finish()

        }, 2000) // Waktu tunda splash screen
    }
}