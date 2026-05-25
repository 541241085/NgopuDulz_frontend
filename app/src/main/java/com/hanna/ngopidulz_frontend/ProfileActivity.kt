package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val sessionManager = SessionManager(this)
        val token = sessionManager.fetchAuthToken()

        // Kenalkan ID dari XML
        val tvNama = findViewById<TextView>(R.id.tv_profile_name)
        val tvEmail = findViewById<TextView>(R.id.tv_profile_email)
        val btnLogout = findViewById<CardView>(R.id.btn_logout) // ID ini pakai CardView di desainmu
        val btnBack = findViewById<ImageView>(R.id.btn_back_profile)

        // 1. Fungsi Tombol Keluar (Logout)
        btnLogout.setOnClickListener {
            // Hapus token di memori HP
            sessionManager.clearSession()
            Toast.makeText(this, "Berhasil Keluar Akun", Toast.LENGTH_SHORT).show()

            // Lempar kembali ke halaman Login dan hapus jejak history halaman
            val intent = Intent(this, MasukActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // 2. Fungsi Tombol Panah Kembali
        btnBack.setOnClickListener {
            finish()
        }

        // 3. Tarik Data Profil dari Laravel
        if (token != null) {
            val bearerToken = "Bearer $token"
            ApiConfig.getApiService().getUserProfile(bearerToken).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        // Jika sukses, ubah teks di layar pakai data dari database Laravel
                        val user = response.body()
                        tvNama.text = user?.name
                        tvEmail.text = user?.email
                    } else {
                        // Tampilkan error code asli dari Laravel
                        val errorCode = response.code()
                        Toast.makeText(this@ProfileActivity, "Error $errorCode: Gagal memuat profil", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Koneksi Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Token hilang! Silakan login ulang.", Toast.LENGTH_SHORT).show()
        }
    }
}