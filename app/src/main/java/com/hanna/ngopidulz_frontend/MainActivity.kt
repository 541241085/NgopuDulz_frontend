package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Pastikan ID di bawah ini sama persis dengan yang ada di XML
        val btnNavEksplor = findViewById<LinearLayout>(R.id.btn_nav_eksplor)
        val btnNavPesanan = findViewById<LinearLayout>(R.id.btn_nav_pesanan)
        val btnNavProfil = findViewById<LinearLayout>(R.id.btn_nav_profil)

        btnNavEksplor.setOnClickListener {
            startActivity(Intent(this, EksplorActivity::class.java))
        }

        btnNavPesanan.setOnClickListener {
            // Hapus spasi tambahan pada nama kelas
            startActivity(Intent(this, RiwayatPesananActivity::class.java))
        }

        btnNavProfil.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}