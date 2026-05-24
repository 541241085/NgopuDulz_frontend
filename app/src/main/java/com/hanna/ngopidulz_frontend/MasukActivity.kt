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
                        // Kalau sukses, ambil token dari Laravel
                        val token = response.body()?.token

                        if (token != null) {
                            // 1. Simpan Token ke HP
                            sessionManager.saveAuthToken(token)

                            Toast.makeText(this@MasukActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                            // 2. Lempar user ke halaman Main Activity
                            val intent = Intent(this@MasukActivity, MainActivity::class.java)
                            // Bersihkan tumpukan halaman biar nggak bisa di-back ke halaman login lagi
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@MasukActivity, "Token tidak ditemukan!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Kalau salah password atau email belum terdaftar
                        Toast.makeText(this@MasukActivity, "Login Gagal! Cek email & password", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Kalau Laravel mati, XAMPP mati, atau tidak ada internet
                    Toast.makeText(this@MasukActivity, "Error Koneksi: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}