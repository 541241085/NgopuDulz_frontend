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
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val token = response.body()?.token
                        val role = response.body()?.user?.role ?: "customer"

                        if (token != null) {
                            // 1. Simpan Token & Role ke Memori
                            sessionManager.saveAuthToken(token)
                            sessionManager.saveRole(role)

                            Toast.makeText(this@MasukActivity, "Login Berhasil sebagai: $role", Toast.LENGTH_SHORT).show()

                            // 2. Logika Pindah Halaman
                            val intent = when (role.lowercase()) {
                                "admin" -> Intent(this@MasukActivity, AdminDashboardActivity::class.java)
                                "kasir" -> Intent(this@MasukActivity, DashboardKasir::class.java)
                                else -> Intent(this@MasukActivity, MainActivity::class.java)
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            // JALUR JIKA LARAVEL SUKSES TAPI TOKEN KOSONG
                            Toast.makeText(this@MasukActivity, "Aneh, respon 200 OK tapi Token Kosong!", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // 🔥 INI PENYELAMATNYA: NAMPILIN ERROR DARI LARAVEL 🔥
                        val errorCode = response.code()
                        Toast.makeText(this@MasukActivity, "Gagal Login! Kode Error: $errorCode", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@MasukActivity, "Server Mati / Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

    }
}