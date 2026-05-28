package com.hanna.ngopidulz_frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CashierOrderAdapter(
    private var orderList: List<CashierOrder>,
    private val onActionClick: (CashierOrder) -> Unit // Fungsi saat tombol dipencet
) : RecyclerView.Adapter<CashierOrderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tv_order_id)
        val tvCustomerName: TextView = itemView.findViewById(R.id.tv_customer_name)
        val tvProductDetails: TextView = itemView.findViewById(R.id.tv_product_details)
        val tvPaymentStatus: TextView = itemView.findViewById(R.id.tv_payment_status)
        val btnAction: Button = itemView.findViewById(R.id.btn_action_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kasir_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]

        holder.tvOrderId.text = "#${order.id.takeLast(5).uppercase()}" // Ambil 5 huruf terakhir ID biar pendek
        holder.tvCustomerName.text = order.user?.name ?: "Pelanggan"

        // Gabungkan semua item kopi jadi 1 teks (Contoh: "2x Americano \n 1x Latte")
        val itemsText = order.items?.joinToString("\n") { "${it.qty}x ${it.product?.name}" }
        holder.tvProductDetails.text = itemsText ?: "Item tidak diketahui"

        holder.tvPaymentStatus.text = "Status Bayar: ${order.paymentStatus.uppercase()}"

        // 🔥 LOGIKA TOMBOL BERDASARKAN STATUS 🔥
        when (order.status) {
            "pending" -> {
                holder.btnAction.visibility = View.VISIBLE
                holder.btnAction.text = "Kerjakan"
            }
            "diproses" -> {
                holder.btnAction.visibility = View.VISIBLE
                holder.btnAction.text = "Selesaikan"
            }
            "selesai" -> {
                // Kalau udah selesai, sembunyikan tombolnya
                holder.btnAction.visibility = View.GONE
            }
        }

        // Kalau tombol diklik, kirim sinyal ke Activity
        holder.btnAction.setOnClickListener {
            onActionClick(order)
        }
    }

    override fun getItemCount() = orderList.size

    fun updateData(newList: List<CashierOrder>) {
        orderList = newList
        notifyDataSetChanged()
    }
}