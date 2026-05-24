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
        Handler(Looper.getMainLooper()).postDelayed({

            // CEK OTOMATIS: Udah pernah login atau belum?
            if (sessionManager.isLoggedIn()) {
                // Kalo udah, langsung lempar ke MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Kalo belum, lempar ke MasukActivity (Login)
                startActivity(Intent(this, MasukActivity::class.java))
            }

            // Hancurkan halaman loading ini
            finish()

        }, 2000)
    }
}