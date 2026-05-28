package com.hanna.ngopidulz_frontend

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled") // Biar Android Studio gak protes
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        webView = findViewById(R.id.webview_midtrans)

        // Tangkap URL Midtrans yang dikirim dari halaman Pesanan
        val url = intent.getStringExtra("PAYMENT_URL")

        if (url != null) {
            // 👇 3 BARIS INI MUTLAK WAJIB BUAT MIDTRANS! 👇
            val webSettings = webView.settings
            webSettings.javaScriptEnabled = true // Aktifkan JavaScript
            webSettings.domStorageEnabled = true // Aktifkan DOM Storage (penting buat simpen data sementara)
            webSettings.loadWithOverviewMode = true
            webSettings.useWideViewPort = true

            // Biar webnya gak loncat ke browser Chrome, tapi tetep di dalam aplikasi
            webView.webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return false
                }
            }

            // Muat halamannya!
            webView.loadUrl(url)
        } else {
            Toast.makeText(this, "Gagal mendapatkan link pembayaran", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // Biar pas tombol Back dipencet, webnya mundur, gak langsung tutup aplikasinya
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}