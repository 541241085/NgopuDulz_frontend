package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MasukActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hubungkan dengan file XML desain temanmu
        setContentView(R.layout.activity_masuk) // Pastikan nama file XML-nya benar

        // Kenalkan elemen UI ke Kotlin
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvGoToRegister = findViewById<TextView>(R.id.tv_go_to_register)

        val sessionManager = SessionManager(this)

        // Logika saat tombol Belum Punya Akun (Daftar) di-klik
        tvGoToRegister.setOnClickListener {
            val intent = Intent(this, DaftarActivity::class.java)
            startActivity(intent)
        }

        // Logika saat tombol Masuk di-klik
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Cegah form kosong
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tembak API Laravel
            ApiConfig.getApiService().loginUser(email, password).enqueue(object : Callback<LoginResponse> {
                // Cari bagian ini di dalam Callback<LoginResponse> di MasukActivity-mu:
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val token = response.body()!!.token ?: ""

                        // 👇 2. PASTIKAN SEBELUM KATA 'MasukActivity)' ADA PEMANGGILAN SESSION MANAGER-NYA 👇
                        SessionManager(this@MasukActivity).saveAuthToken(token)

                        Toast.makeText(this@MasukActivity, "Selamat Datang di NgopuDulz!", Toast.LENGTH_SHORT).show()

                    } else {
                        // 👇 REVOLUSI BLOK ELSE DENGAN LOGIKA DETEKSI SUSPEND DI SINI KOH 👇
                        val kodeError = response.code()

                        when (kodeError) {
                            403 -> {
                                // 🛑 JIKA TERDETEKSI KODE 403 (SUSPENDED)
                                Toast.makeText(
                                    this@MasukActivity,
                                    "Akun Anda telah di-suspend! ⚠️ Silakan hubungi Admin.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            401 -> {
                                // ❌ JIKA SALAH EMAIL / PASSWORD
                                Toast.makeText(
                                    this@MasukActivity,
                                    "Email atau password yang Anda masukkan salah, Koh!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                // 🚨 JIKA ADA ERROR LAINNYA
                                Toast.makeText(
                                    this@MasukActivity,
                                    "Gagal masuk ke sistem: Kode $kodeError",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@MasukActivity, "Server Mati / Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

    }
}