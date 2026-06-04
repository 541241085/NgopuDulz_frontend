package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etKonfirmasiPassword: EditText
    private lateinit var btnProsesDaftar: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar)

        // Inisialisasi komponen UI dari XML kamu Koh
        etNama = findViewById(R.id.et_daftar_nama)
        etEmail = findViewById(R.id.et_daftar_email)
        etPassword = findViewById(R.id.et_daftar_password)
        etKonfirmasiPassword = findViewById(R.id.et_daftar_konfirmasi_password)
        btnProsesDaftar = findViewById(R.id.btn_proses_daftar)

        val tvKeLogin = findViewById<TextView>(R.id.tv_ke_login)
        val btnBack = findViewById<ImageView>(R.id.btn_back_daftar)

        // Pindah ke halaman Login
        tvKeLogin.setOnClickListener {
            startActivity(Intent(this, MasukActivity::class.java))
            finish()
        }

        // Tombol Kembali
        btnBack.setOnClickListener {
            finish()
        }

        // 🔥 KLIK TOMBOL DAFTAR UTAMA 🔥
        btnProsesDaftar.setOnClickListener {
            eksekusiPendaftaranKeLaravel()
        }
    }

    private fun eksekusiPendaftaranKeLaravel() {
        // 1. Ambil teks dan bersihkan spasi liar
        val nama = etNama.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val konfirmasiPassword = etKonfirmasiPassword.text.toString().trim()

        // 2. Validasi Lapisan Pertama: Gak boleh ada yang kosong
        if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || konfirmasiPassword.isEmpty()) {
            Toast.makeText(this, "Aduh Koh, semua field wajib diisi kabeh!", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Validasi Lapisan Kedua: Minimal password Laravel biasanya 8 karakter
        if (password.length < 8) {
            Toast.makeText(this, "Password minimal harus 8 karakter ya Koh!", Toast.LENGTH_SHORT).show()
            return
        }

        // 4. Validasi Lapisan Ketiga: Password & Konfirmasi wajib kembar identik
        if (password != konfirmasiPassword) {
            Toast.makeText(this, "Password konfirmasi gak cocok! Cek typonya Koh", Toast.LENGTH_SHORT).show()
            return
        }

        // 5. Kirim data ke server Laravel NgopuDulz
        ApiConfig.getApiService().registerUser(nama, email, password, konfirmasiPassword)
            .enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DaftarActivity, "Akun NgopuDulz Berhasil Dibuat! 🎉", Toast.LENGTH_LONG).show()

                        // Otomatis oper user ke halaman MasukActivity biar tinggal login gampang
                        val intent = Intent(this@DaftarActivity, MasukActivity::class.java)
                        startActivity(intent)
                        finish() // Tutup halaman daftar
                    } else {
                        // Jika email ternyata sudah terdaftar sebelumnya di database
                        val kodeError = response.code()
                        if (kodeError == 422 || kodeError == 400) {
                            Toast.makeText(this@DaftarActivity, "Email sudah dipakai orang lain, ganti yang baru!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@DaftarActivity, "Gagal mendaftar: Kode $kodeError", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    Toast.makeText(this@DaftarActivity, "Koneksi putus: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}