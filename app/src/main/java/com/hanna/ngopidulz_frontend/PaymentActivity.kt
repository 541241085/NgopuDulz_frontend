package com.hanna.ngopidulz_frontend

import android.annotation.SuppressLint
import android.content.Intent
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
                // 🔥 GUNAKAN ONPAGESTARTED BIAR LEBIH SENSITIF 🔥
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    val currentUrl = url ?: ""

                    // Deteksi jika URL mengandung kata kunci sukses dari Midtrans
                    if (currentUrl.contains("finish") || currentUrl.contains("success") || currentUrl.contains("unfinish") || currentUrl.contains("status_code=200")) {

                        Toast.makeText(this@PaymentActivity, "Transaksi Diproses, Kembali ke Menu!", Toast.LENGTH_SHORT).show()

                        // Lempar langsung ke halaman Utama / Menu Customer
                        val intent = Intent(
                            this@PaymentActivity,
                            MainActivity::class.java
                        ) // Sesuaikan dengan nama Activity Menu-mu
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Bersihkan riwayat back
                        startActivity(intent)
                        finish()
                    }
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