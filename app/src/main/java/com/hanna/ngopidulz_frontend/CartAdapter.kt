package com.hanna.ngopidulz_frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(private val cartList: List<CartItem>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_cart_title)
        val tvSub: TextView = itemView.findViewById(R.id.tv_cart_sub)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_cart_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]

        holder.tvTitle.text = item.product.name
        holder.tvSub.text = "Jumlah: ${item.quantity} | Catatan: ${item.notes}"

        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        holder.tvPrice.text = formatRupiah.format(item.subtotal).replace("Rp", "Rp ").replace(",00", "")
    }

    override fun getItemCount(): Int {
        return cartList.size
    }
}