package com.hanna.ngopidulz_frontend

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DaftarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_daftar)

        // Penanganan sistem status bar window padding
        val rootLayout = findViewById<android.view.View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Aksi klik tombol back arrow di bagian atas
        val btnBack = findViewById<ImageView>(R.id.btn_back_daftar)
        btnBack.setOnClickListener {
            finish() // Menutup halaman daftar dan balik ke login
        }

        // Aksi klik teks "Sudah punya akun? Masuk"
        val tvKeLogin = findViewById<TextView>(R.id.tv_ke_login)
        tvKeLogin.setOnClickListener {
            finish() // Kembali ke halaman Login sebelumnya
        }
    }
}