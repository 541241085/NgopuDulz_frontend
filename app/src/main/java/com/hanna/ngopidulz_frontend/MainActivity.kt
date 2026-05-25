package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Kenalkan tombol-tombol navigasi
        val btnNavEksplor = findViewById<LinearLayout>(R.id.btn_nav_eksplor)
        val btnNavPesanan = findViewById<LinearLayout>(R.id.btn_nav_pesanan)
        val btnNavProfil = findViewById<LinearLayout>(R.id.btn_nav_profil)

        // 2. Beri fungsi klik (Intent) ke masing-class Activity
        btnNavEksplor.setOnClickListener {
            startActivity(Intent(this, EksplorActivity::class.java))
        }

        btnNavPesanan.setOnClickListener {
            startActivity(Intent(this, PesananActivity::class.java))
        }

        btnNavProfil.setOnClickListener {
            // Pastikan nama file profil-mu adalah ProfileActivity (sesuaikan jika namanya beda)
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Catatan: Tombol "Beranda" tidak perlu dikasih fungsi klik,
        // karena saat ini kita SUDAH BERADA di halaman Beranda (MainActivity).
    }
}