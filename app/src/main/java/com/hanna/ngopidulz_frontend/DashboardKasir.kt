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
    private var allOrders: List<CashierOrder> = emptyList() // Nyimpen semua pesanan dari Laravel

    private lateinit var tvPendapatan: TextView
    private lateinit var rgStatus: RadioGroup
    private lateinit var rbPending: RadioButton
    private lateinit var rbDone: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_kasir)

        tvPendapatan = findViewById(R.id.tv_pendapatan_hari_ini) // Pastikan id ini ada di xml header-mu
        rgStatus = findViewById(R.id.rg_status)
        rbPending = findViewById(R.id.rb_pending)
        rbDone = findViewById(R.id.rb_done)
        rvKasir = findViewById(R.id.rv_kasir_antrian)

        rvKasir.layoutManager = LinearLayoutManager(this)

        // Setup Adapter dan klik tombol
        adapter = CashierOrderAdapter(emptyList()) { clickedOrder ->
            changeOrderStatus(clickedOrder)
        }
        rvKasir.adapter = adapter

        // Kalau tab RadioButton dipindah, filter ulang tampilannya
        rgStatus.setOnCheckedChangeListener { _, _ -> filterAndDisplayData() }

        // Panggil data pertama kali buka
        fetchOrdersFromLaravel()
        val btnLogout = findViewById<ImageView>(R.id.btn_logout_kasir)
        btnLogout.setOnClickListener {
            // 1. Hapus memori Token & Role
            SessionManager(this).clearSession()

            Toast.makeText(this, "Berhasil Logout!", Toast.LENGTH_SHORT).show()

            // 2. Lempar balik ke halaman Login (MasukActivity)
            val intent = Intent(this, MasukActivity::class.java) // Sesuaikan nama file Login-mu

            // 3. Jurus pamungkas: Hapus riwayat halaman biar user gak bisa klik tombol "Back" di HP
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

    private fun filterAndDisplayData() {
        val filteredList = if (rbPending.isChecked) {
            // Tab Antrian: Tampilkan yang statusnya pending atau diproses
            allOrders.filter { it.status == "pending" || it.status == "diproses" }
        } else {
            // Tab Selesai: Tampilkan yang statusnya selesai
            allOrders.filter { it.status == "selesai" }
        }

        adapter.updateData(filteredList)

        // Update angka di tombol Radio
        val countPending = allOrders.count { it.status == "pending" || it.status == "diproses" }
        val countDone = allOrders.count { it.status == "selesai" }
        rbPending.text = "Antrian ($countPending)"
        rbDone.text = "Selesai ($countDone)"
    }

    private fun calculateRevenue() {
        // Hitung pendapatan (hanya dari pesanan yang LUNAS dan SELESAI)
        val totalPendapatan = allOrders
            .filter { it.paymentStatus == "dibayar" && it.status == "selesai" }
            .sumOf { it.totalPrice }

        val formatRp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        tvPendapatan.text = "Pendapatan Hari Ini: ${formatRp.format(totalPendapatan).replace(",00", "")}"
    }

    private fun changeOrderStatus(order: CashierOrder) {
        val token = SessionManager(this).fetchAuthToken() ?: return

        // Logika urutan status: pending -> diproses -> selesai
        val nextStatus = if (order.status == "pending") "diproses" else "selesai"
        val requestBody = StatusRequest(status = nextStatus)

        ApiConfig.getApiService().updateOrderStatus("Bearer $token", order.id, requestBody)
            .enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DashboardKasir, "Status jadi $nextStatus", Toast.LENGTH_SHORT).show()
                        fetchOrdersFromLaravel() // Refresh data terbaru
                    }
                }
                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    Toast.makeText(this@DashboardKasir, "Gagal update status", Toast.LENGTH_SHORT).show()
                }
            })

    }

}