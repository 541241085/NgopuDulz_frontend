package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DaftarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar)

        val tvKeLogin = findViewById<TextView>(R.id.tv_ke_login)
        val btnBack = findViewById<ImageView>(R.id.btn_back_daftar)

        // Kalau klik "Sudah punya akun? Masuk", pindah ke halaman Login
        tvKeLogin.setOnClickListener {
            startActivity(Intent(this, MasukActivity::class.java))
            finish() // Tutup halaman daftar ini biar memori HP nggak penuh
        }

        // Fungsi tombol panah kembali (back) di pojok kiri atas
        btnBack.setOnClickListener {
            finish()
        }
    }
}