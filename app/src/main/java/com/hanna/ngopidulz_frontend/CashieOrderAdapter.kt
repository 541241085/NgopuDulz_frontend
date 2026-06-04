package com.hanna.ngopidulz_frontend

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CashierOrderAdapter(
    private var orderList: List<CashierOrder>,
    // 👇 1. UPDATE: Tambahkan parameter String untuk melempar status baru ke Activity 👇
    private val onActionClick: (CashierOrder, String) -> Unit
) : RecyclerView.Adapter<CashierOrderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tv_order_id)
        val tvCustomerName: TextView = itemView.findViewById(R.id.tv_customer_name)
        val tvProductDetails: TextView = itemView.findViewById(R.id.tv_product_details)
        val tvStatusPesanan: TextView = itemView.findViewById(R.id.tv_status_pesanan)
        val btnAction: Button = itemView.findViewById(R.id.btn_action_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kasir_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]

        val safeId = order.id ?: "00000"
        holder.tvOrderId.text = "#${safeId.takeLast(5).uppercase()}"
        holder.tvCustomerName.text = order.user?.name ?: "Pelanggan"

        val itemsText = order.items?.joinToString("\n") { "${it.qty}x ${it.product?.name}" }
        holder.tvProductDetails.text = itemsText ?: "Item tidak diketahui"

        // Ambil status pesanan aman dari null
        val statusPesanan = order.status?.lowercase() ?: "pending"

        // 🔥 2. LOGIKA DINAMIS TEKS INDIKATOR & TOMBOL AKSI 🔥
        when (statusPesanan) {
            "pending" -> {
                holder.tvStatusPesanan.text = "Status Pesanan: ANTRIAN BARU ⏳"
                holder.tvStatusPesanan.setTextColor(Color.parseColor("#E5A93C")) // Emas

                holder.btnAction.visibility = View.VISIBLE
                holder.btnAction.text = "Kerjakan"
                // Pencet "Kerjakan" -> Lempar status "diproses" ke Laravel
                holder.btnAction.setOnClickListener { onActionClick(order, "diproses") }
            }
            "diproses" -> {
                holder.tvStatusPesanan.text = "Status Pesanan: SEDANG DIBUAT ☕"
                holder.tvStatusPesanan.setTextColor(Color.parseColor("#42A5F5")) // Biru

                holder.btnAction.visibility = View.VISIBLE
                holder.btnAction.text = "Selesaikan"
                // Pencet "Selesaikan" -> Lempar status "selesai" ke Laravel
                holder.btnAction.setOnClickListener { onActionClick(order, "selesai") }
            }
            "selesai" -> {
                holder.tvStatusPesanan.text = "Status Pesanan: SELESAI ✅"
                holder.tvStatusPesanan.setTextColor(Color.parseColor("#4CAF50")) // Hijau

                // Kalau sudah selesai, tombol dihilangkan
                holder.btnAction.visibility = View.GONE
            }
            else -> {
                holder.tvStatusPesanan.text = "Status Pesanan: ${statusPesanan.uppercase()}"
                holder.btnAction.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = orderList.size

    fun updateData(newList: List<CashierOrder>) {
        orderList = newList
        notifyDataSetChanged()
    }
}