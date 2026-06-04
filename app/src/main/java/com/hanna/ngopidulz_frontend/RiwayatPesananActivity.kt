package com.hanna.ngopidulz_frontend

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class RiwayatPesananActivity : AppCompatActivity() {

    private lateinit var rvRiwayat: RecyclerView
    private lateinit var adapter: CustomerHistoryAdapter
    private var allOrders: List<CashierOrder> = emptyList()

    private lateinit var rgStatus: RadioGroup
    private lateinit var rbAntrian: RadioButton
    private lateinit var rbProses: RadioButton
    private lateinit var rbSelesai: RadioButton

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private lateinit var refreshRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_pesanan)

        refreshRunnable = object : Runnable {
            override fun run() {
                fetchRiwayatFromLaravel()
                handler.postDelayed(this, 5000)
            }
        }

        rgStatus = findViewById(R.id.rg_status_pelanggan)
        rbAntrian = findViewById(R.id.rb_user_antrian)
        rbProses = findViewById(R.id.rb_user_proses)
        rbSelesai = findViewById(R.id.rb_user_selesai)
        rvRiwayat = findViewById(R.id.rv_riwayat_pesanan)

        rvRiwayat.layoutManager = LinearLayoutManager(this)

        adapter = CustomerHistoryAdapter(emptyList()) { clickedOrder ->
            showOrderDetailDialog(clickedOrder)
        }
        rvRiwayat.adapter = adapter

        findViewById<ImageView>(R.id.btn_back_riwayat).setOnClickListener {
            finish()
        }

        rgStatus.setOnCheckedChangeListener { _, checkedId ->
            rbAntrian.setTextColor(Color.parseColor("#8E8E8E"))
            rbProses.setTextColor(Color.parseColor("#8E8E8E"))
            rbSelesai.setTextColor(Color.parseColor("#8E8E8E"))

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
                    // Gagal koneksi
                }
            })
    }

    private fun filterAndDisplayData() {
        val filteredList = when {
            rbAntrian.isChecked -> allOrders.filter { it.status.lowercase() == "pending" }
            rbProses.isChecked -> allOrders.filter { it.status.lowercase() == "diproses" }
            rbSelesai.isChecked -> allOrders.filter { it.status.lowercase() == "selesai" }
            else -> emptyList()
        }
        adapter.updateData(filteredList)

        // 🔥 LOGIKA SAKTI: CEK POP-UP BERDASARKAN ID PESANAN (MUNCUL 1 KALI PER TRANSAKSI) 🔥
        val sharedPref = getSharedPreferences("NgopuDulz_Popup_Prefs", android.content.Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        for (order in allOrders) {
            val statusPesanan = order.status?.lowercase() ?: "pending"
            val safeOrderId = order.id ?: continue

            // Jika status pesanan selesai, cek apakah ID ini sudah pernah memicu pop-up
            if (statusPesanan == "selesai") {
                val keyCatatan = "sudah_muncul_popup_$safeOrderId"
                val sudahPernahMuncul = sharedPref.getBoolean(keyCatatan, false)

                if (!sudahPernahMuncul) {
                    // Tampilkan pop-up premium dengan kode order spesifik
                    AlertDialog.Builder(this)
                        .setTitle("Kopi Kamu Sudah Ready! ☕✨")
                        .setMessage("Pesanan dengan kode #${safeOrderId.takeLast(5).uppercase()} telah selesai diracik oleh Barista. Silakan ambil di meja kasir NgopuDulz ya Koh!")
                        .setPositiveButton("Siap, Ambil!") { dialog, _ -> dialog.dismiss() }
                        .setCancelable(false)
                        .show()

                    // Kunci ID pesanan ini di memori HP agar tidak muncul lagi selamanya
                    editor.putBoolean(keyCatatan, true)
                    editor.apply()

                    break // Hentikan loop biar gak tumpuk-tumpuk kalau ada banyak pesanan selesai
                }
            }
        }
    }

    private fun showOrderDetailDialog(order: CashierOrder) {
        val safeId = order.id ?: "00000"
        val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

        val detailBuilder = StringBuilder()
        detailBuilder.append("Status Pembuatan: ${order.status.uppercase()}\n\n")
        detailBuilder.append("Daftar Menu:\n")

        order.items?.forEach { item ->
            val subtotalFormatted = formatRp.format(item.subtotal).replace(",00", "")
            detailBuilder.append("- ${item.product?.name} (${item.qty}x) -> $subtotalFormatted\n")
        }

        val totalFormatted = formatRp.format(order.totalPrice).replace(",00", "")
        detailBuilder.append("\nTotal Bayar: $totalFormatted")

        AlertDialog.Builder(this)
            .setTitle("Detail Nota Pesanan #${safeId.takeLast(5).uppercase()}")
            .setMessage(detailBuilder.toString())
            .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }
}