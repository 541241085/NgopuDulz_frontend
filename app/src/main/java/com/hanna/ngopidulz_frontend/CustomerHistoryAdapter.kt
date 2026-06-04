package com.hanna.ngopidulz_frontend

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class CustomerHistoryAdapter(
    private var orderList: List<CashierOrder>,
    private val onItemClick: (CashierOrder) -> Unit // 👈 TAMBAHKAN INI
) : RecyclerView.Adapter<CustomerHistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvId: TextView = itemView.findViewById(R.id.tv_riwayat_id)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_riwayat_status_badge)
        val tvProducts: TextView = itemView.findViewById(R.id.tv_riwayat_products)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_riwayat_total_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]

        val safeId = order.id ?: "00000"
        holder.tvId.text = "#${safeId.takeLast(5).uppercase()}"

        // Nampilin ringkasan item di card luar
        val itemsText = order.items?.joinToString("\n") { "${it.qty}x ${it.product?.name}" }
        holder.tvProducts.text = itemsText ?: "Item tidak diketahui"

        val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.tvPrice.text = formatRp.format(order.totalPrice).replace(",00", "")

        // Pewarnaan Badge Status
        when (order.status.lowercase()) {
            "pending" -> {
                holder.tvStatus.text = "ANTRIAN"
                holder.tvStatus.setTextColor(Color.parseColor("#E5A93C"))
                holder.tvStatus.setBackgroundColor(Color.parseColor("#2E2516"))
            }
            "diproses" -> {
                holder.tvStatus.text = "DIPROSES"
                holder.tvStatus.setTextColor(Color.parseColor("#42A5F5"))
                holder.tvStatus.setBackgroundColor(Color.parseColor("#162335"))
            }
            "selesai" -> {
                holder.tvStatus.text = "SELESAI"
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                holder.tvStatus.setBackgroundColor(Color.parseColor("#142A16"))
            }
        }

        // 🔥 LOGIKA KETIKA CARD DIKLIK 🔥
        holder.itemView.setOnClickListener {
            onItemClick(order)
        }
    }

    override fun getItemCount() = orderList.size

    fun updateData(newList: List<CashierOrder>) {
        orderList = newList
        notifyDataSetChanged()
    }
}