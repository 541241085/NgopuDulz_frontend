package com.hanna.ngopidulz_frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class AdminProductAdapter(
    private var productList: List<ProductModel>,
    private val onEditClick: (ProductModel) -> Unit,
    private val onDeleteClick: (ProductModel) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_admin_product_name)
        val tvCategory: TextView = itemView.findViewById(R.id.tv_admin_product_category)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_admin_product_price)
        val btnEdit: ImageView = itemView.findViewById(R.id.btn_admin_edit_product)
        val btnDelete: ImageView = itemView.findViewById(R.id.btn_admin_delete_product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_admin, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.tvName.text = product.name
        holder.tvCategory.text = product.category?.uppercase() ?: "KOPI"

        val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.tvPrice.text = formatRp.format(product.price).replace(",00", "")

        holder.btnEdit.setOnClickListener { onEditClick(product) }
        holder.btnDelete.setOnClickListener { onDeleteClick(product) }
    }

    override fun getItemCount() = productList.size
    fun updateData(newList: List<ProductModel>) {
        this.productList = newList // 💡 Pastikan variabel ini diisi dengan data baru!
        notifyDataSetChanged()
    }

}