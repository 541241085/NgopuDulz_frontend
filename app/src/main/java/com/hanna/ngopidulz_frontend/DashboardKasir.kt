package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class DashboardKasir : AppCompatActivity() {

    private lateinit var rvKasir: RecyclerView
    private lateinit var adapter: CashierOrderAdapter
    private var allOrders: List<CashierOrder> = emptyList()

    private lateinit var tvPendapatan: TextView
    private lateinit var rgStatus: RadioGroup

    // 👇 1. UPDATE VARIABEL 3 TOMBOL 👇
    private lateinit var rbAntrian: RadioButton
    private lateinit var rbProses: RadioButton
    private lateinit var rbSelesai: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_kasir)

        tvPendapatan = findViewById(R.id.tv_pendapatan_hari_ini)
        rgStatus = findViewById(R.id.rg_status)

        // 👇 2. SAMBUNGKAN KE ID XML YANG BARU 👇
        rbAntrian = findViewById(R.id.rb_antrian)
        rbProses = findViewById(R.id.rb_proses)
        rbSelesai = findViewById(R.id.rb_selesai)
        rvKasir = findViewById(R.id.rv_kasir_antrian)

        rvKasir.layoutManager = LinearLayoutManager(this)

        adapter = CashierOrderAdapter(emptyList()) { order, statusBaru ->
            // Panggil fungsi ubah status kamu dengan melempar statusBaru-nya!
            eksekusiUbahStatusPesanan(order.id, statusBaru)
        }
        rvKasir.adapter = adapter

        rgStatus.setOnCheckedChangeListener { _, _ -> filterAndDisplayData() }

        fetchOrdersFromLaravel()

        val btnLogout = findViewById<ImageView>(R.id.btn_logout_kasir)
        btnLogout.setOnClickListener {
            SessionManager(this).clearSession()
            Toast.makeText(this, "Berhasil Logout!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MasukActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun fetchOrdersFromLaravel() {
        val token = SessionManager(this).fetchAuthToken() ?: return

        ApiConfig.getApiService().getCashierOrders("Bearer $token").enqueue(object : Callback<CashierOrderResponse> {
            override fun onResponse(call: Call<CashierOrderResponse>, response: Response<CashierOrderResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    allOrders = response.body()!!.data ?: emptyList()
                    filterAndDisplayData()
                    calculateRevenue()
                }
            }
            override fun onFailure(call: Call<CashierOrderResponse>, t: Throwable) {
                Toast.makeText(this@DashboardKasir, "Error koneksi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 👇 3. LOGIKA FILTER & PENGHITUNG BARU 👇
    private fun filterAndDisplayData() {
        // Tentukan data mana yang masuk ke RecyclerView
        val filteredList = when {
            rbAntrian.isChecked -> {
                // Kasir bisa langsung lihat pesanan baru masuk tanpa nunggu webhook internet
                allOrders.filter { it.status.lowercase() == "pending" }
            }
            rbProses.isChecked -> {
                // Tab Proses: Status "diproses"
                allOrders.filter { it.status.lowercase() == "diproses" }
            }
            rbSelesai.isChecked -> {
                // Tab Selesai: Status "selesai"
                allOrders.filter { it.status.lowercase() == "selesai" }
            }
            else -> emptyList()
        }

        adapter.updateData(filteredList)

        // Hitung jumlah masing-masing untuk di-update ke teks tombolnya
        val countAntrian = allOrders.count { it.status.lowercase() == "pending" && it.paymentStatus.lowercase() == "dibayar" }
        val countProses = allOrders.count { it.status.lowercase() == "diproses" }
        val countSelesai = allOrders.count { it.status.lowercase() == "selesai" }

        rbAntrian.text = "Antrian ($countAntrian)"
        rbProses.text = "Proses ($countProses)"
        rbSelesai.text = "Selesai ($countSelesai)"
    }

    private fun calculateRevenue() {
        // 🔥 OTOMATIS: Semua yang statusnya "selesai" langsung dihitung sebagai pendapatan lunas 🔥
        val totalPendapatan = allOrders
            .filter { it.status.lowercase() == "selesai" }
            .sumOf { it.totalPrice }

        val formatRp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        tvPendapatan.text = "Pendapatan Hari Ini: ${formatRp.format(totalPendapatan).replace(",00", "")}"
    }

    private fun changeOrderStatus(order: CashierOrder) {
        val token = SessionManager(this).fetchAuthToken() ?: return

        // Logika perpindahan status: pending -> diproses -> selesai
        val nextStatus = if (order.status.lowercase() == "pending") "diproses" else "selesai"
        val requestBody = StatusRequest(status = nextStatus)

        ApiConfig.getApiService().updateOrderStatus("Bearer $token", order.id ?: "" , requestBody)
            .enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DashboardKasir, "Status pindah ke $nextStatus", Toast.LENGTH_SHORT).show()
                        fetchOrdersFromLaravel() // Refresh data otomatis biar pindah tab
                    }
                }
                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    Toast.makeText(this@DashboardKasir, "Gagal update status", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun eksekusiUbahStatusPesanan(orderId: String?, statusBaru: String) {
        if (orderId == null) return
        val token = SessionManager(this).fetchAuthToken() ?: return

        // 1. Bungkus status baru ("diproses" / "selesai") ke dalam objek StatusRequest
        val requestBody = StatusRequest(status = statusBaru)

        // 2. Tembak endpoint update status kasir di Laravel
        // 💡 Catatan: Sesuaikan '.updateOrderStatus' dengan nama fungsi @PUT Kasir di ApiService-mu ya Koh!
        ApiConfig.getApiService().updateOrderStatus("Bearer $token", orderId, requestBody)
            .enqueue(object : retrofit2.Callback<GeneralResponse> {
                override fun onResponse(call: retrofit2.Call<GeneralResponse>, response: retrofit2.Response<GeneralResponse>) {
                    if (response.isSuccessful) {
                        android.widget.Toast.makeText(
                            this@DashboardKasir,
                            "Berhasil diperbarui jadi ${statusBaru.uppercase()}!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()

                        // 🟢 REFRESH: Ambil ulang data dari Laravel biar list di layar otomatis ter-update
                        fetchOrdersFromLaravel()
                    } else {
                        android.widget.Toast.makeText(this@DashboardKasir, "Gagal mengubah status: ${response.code()}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<GeneralResponse>, t: Throwable) {
                    android.widget.Toast.makeText(this@DashboardKasir, "Error koneksi: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            })
    }
}