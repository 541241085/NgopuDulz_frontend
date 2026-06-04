package com.hanna.ngopidulz_frontend

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminUserAdapter(
    private var userList: List<AdminUserModel>,
    private val onActionClick: (AdminUserModel) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_user_name)
        val tvEmail: TextView = itemView.findViewById(R.id.tv_user_email)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_user_status) // Inisialisasi teks status
        val btnToggle: TextView = itemView.findViewById(R.id.btn_toggle_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_users_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.tvName.text = user.name
        holder.tvEmail.text = user.email

        // Ambil status, kalau null otomatis ubah jadi "active"
        val statusAkun = user.status?.lowercase() ?: "active"

        // 🔥 LOGIKA BARU: DIPISAH ANTARA STATUS DAN TOMBOL AKSI 🔥
        if (statusAkun == "suspend") {
            // Tampilan Status Akun
            holder.tvStatus.text = "STATUS: SUSPENDED 🛑"
            holder.tvStatus.setTextColor(Color.parseColor("#EF5350")) // Merah

            // Tampilan Tombol Aksi
            holder.btnToggle.text = "AKTIFKAN"
            holder.btnToggle.setTextColor(Color.parseColor("#E5A93C")) // Emas
            holder.btnToggle.setBackgroundColor(Color.parseColor("#2E2516"))
        } else {
            // Tampilan Status Akun
            holder.tvStatus.text = "STATUS: ACTIVE 🟢"
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")) // Hijau

            // Tampilan Tombol Aksi
            holder.btnToggle.text = "SUSPEND"
            holder.btnToggle.setTextColor(Color.parseColor("#EF5350")) // Merah
            holder.btnToggle.setBackgroundColor(Color.parseColor("#2D1919"))
        }

        holder.btnToggle.setOnClickListener { onActionClick(user) }
    }

    override fun getItemCount() = userList.size

    fun updateData(newList: List<AdminUserModel>) {
        userList = newList
        notifyDataSetChanged()
    }
}