package com.hanna.ngopidulz_frontend

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatPesananActivity : AppCompatActivity() {

    private lateinit var rvRiwayat: RecyclerView
    private lateinit var adapter: CustomerHistoryAdapter
    private var allOrders: List<CashierOrder> = emptyList()

    private lateinit var rgStatus: RadioGroup
    private lateinit var rbAntrian: RadioButton
    private lateinit var rbProses: RadioButton
    private lateinit var rbSelesai: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_pesanan)

        // Inisialisasi Komponen
        rgStatus = findViewById(R.id.rg_status_pelanggan)
        rbAntrian = findViewById(R.id.rb_user_antrian)
        rbProses = findViewById(R.id.rb_user_proses)
        rbSelesai = findViewById(R.id.rb_user_selesai)
        rvRiwayat = findViewById(R.id.rv_riwayat_pesanan)

        rvRiwayat.layoutManager = LinearLayoutManager(this)
        adapter = CustomerHistoryAdapter(emptyList())
        rvRiwayat.adapter = adapter

        // Tombol Back Kembali
        findViewById<ImageView>(R.id.btn_back_riwayat).setOnClickListener {
            finish()
        }

        // Jalur Pindah Tab Navigasi Atas
        rgStatus.setOnCheckedChangeListener { _, checkedId ->
            // Reset semua warna teks tombol jadi abu-abu
            rbAntrian.setTextColor(Color.parseColor("#8E8E8E"))
            rbProses.setTextColor(Color.parseColor("#8E8E8E"))
            rbSelesai.setTextColor(Color.parseColor("#8E8E8E"))

            // Yang aktif berubah jadi emas
            when (checkedId) {
                R.id.rb_user_antrian -> rbAntrian.setTextColor(Color.parseColor("#E5A93C"))
                R.id.rb_user_proses -> rbProses.setTextColor(Color.parseColor("#E5A93C"))
                R.id.rb_user_selesai -> rbSelesai.setTextColor(Color.parseColor("#E5A93C"))
            }
            filterAndDisplayData()
        }

        fetchRiwayatFromLaravel()
    }

    private fun fetchRiwayatFromLaravel() {
        val token = SessionManager(this).fetchAuthToken() ?: return

        ApiConfig.getApiService().getCustomerOrders("Bearer $token")
            .enqueue(object : Callback<CashierOrderResponse> {
                override fun onResponse(call: Call<CashierOrderResponse>, response: Response<CashierOrderResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        allOrders = response.body()!!.data ?: emptyList()
                        filterAndDisplayData()
                    }
                }
                override fun onFailure(call: Call<CashierOrderResponse>, t: Throwable) {
                    Toast.makeText(this@RiwayatPesananActivity, "Gagal memuat riwayat", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun filterAndDisplayData() {
        val filteredList = when {
            rbAntrian.isChecked -> {
                // Cuma tampilkan yang pending dan sudah lunas terbayar Midtrans
                // Sementara diloloskan walaupun "pending" biar kelihatan pas ngetes di localhost
                allOrders.filter { it.status.lowercase() == "pending" }
            }
            rbProses.isChecked -> {
                allOrders.filter { it.status.lowercase() == "diproses" }
            }
            rbSelesai.isChecked -> {
                allOrders.filter { it.status.lowercase() == "selesai" }
            }
            else -> emptyList()
        }
        adapter.updateData(filteredList)
    }
}