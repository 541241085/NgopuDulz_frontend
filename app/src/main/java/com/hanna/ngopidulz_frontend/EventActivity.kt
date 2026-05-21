package com.hanna.ngopidulz_frontend

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_event)

        // Mengatur padding otomatis agar tidak menabrak status bar atas/bawah HP
        val rootLayout = findViewById<android.view.View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // TAMBAHAN: Logika untuk tombol kembali (Back)
        val btnBack = findViewById<ImageView>(R.id.btn_back_event)
        btnBack.setOnClickListener {
            finish() // Menutup halaman event dan kembali ke dashboard utama
        }
    }
}