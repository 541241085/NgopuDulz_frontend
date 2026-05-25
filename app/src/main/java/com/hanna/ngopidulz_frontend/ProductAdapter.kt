package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(private val productList: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_item_price)
        val ivImage: ImageView = itemView.findViewById(R.id.iv_item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.tvName.text = product.name

        // Format harga jadi Rupiah (contoh: Rp 18.000)
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        holder.tvPrice.text = formatRupiah.format(product.price).replace("Rp", "Rp ").replace(",00", "")

        // Catatan: Untuk sementara gambar pakai bawaan XML (img_macchiato).
        // Besok kita tambahkan library "Glide" untuk meload image_url asli dari internet.

        // Fungsi saat salah satu menu diklik -> Pindah ke Detail Produk
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            // Pastikan nama activity detail-mu adalah DetailActivityIceCaramell
            val intent = Intent(context, DetailActivityIceCaramell::class.java)
            // Lempar data produk ke halaman detail
            intent.putExtra("EXTRA_PRODUCT", product)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}