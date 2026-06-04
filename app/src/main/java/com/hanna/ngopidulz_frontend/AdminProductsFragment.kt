package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProductsFragment : Fragment() {

    private lateinit var rvProducts: RecyclerView
    private lateinit var adapter: AdminProductAdapter
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_product, container, false)

        rvProducts = view.findViewById(R.id.rv_admin_products)
        fabAdd = view.findViewById(R.id.fab_add_product)

        // 1. Atur Layout Manager
        rvProducts.layoutManager = LinearLayoutManager(requireContext())

        // 2. CUKUP INISIALISASI 1 ADAPTER SAJA (PAKET LENGKAP DETAIL)
        adapter = AdminProductAdapter(emptyList(),
            onEditClick = { product ->
                // Ketika item produk diklik, langsung buka halaman DETAIL ADMIN bawa paket lengkap!
                val intent = Intent(requireContext(), DetailProductAdminActivity::class.java).apply {
                    putExtra("EXTRA_ID", product.id)
                    putExtra("EXTRA_NAME", product.name)
                    putExtra("EXTRA_PRICE", product.price)
                    putExtra("EXTRA_CATEGORY", product.category)
                    putExtra("EXTRA_IMAGE", product.image)
                }
                startActivity(intent)
            },
            onDeleteClick = { product ->
                showConfirmDeleteDialog(product)
            }
        )

        // 3. Tempelkan adapter tunggal ini ke RecyclerView
        rvProducts.adapter = adapter

        // 4. Tombol Tambah Menu Baru (FAB)
        fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), FormProductActivity::class.java)
            startActivity(intent)
        }

        // 5. Tembak Laravel load data menu kopi
        fetchDaftarProdukDariLaravel()

        return view
    }

    private fun fetchDaftarProdukDariLaravel() {
        val token = SessionManager(requireContext()).fetchAuthToken() ?: return

        ApiConfig.getApiService().getAdminProducts("Bearer $token")
            .enqueue(object : Callback<AdminProductResponse> {
                override fun onResponse(call: Call<AdminProductResponse>, response: Response<AdminProductResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val items = response.body()!!.data ?: emptyList()

                        // Detektor jumlah data asli server
                        Toast.makeText(requireContext(), "Berhasil memuat ${items.size} menu kopi!", Toast.LENGTH_SHORT).show()

                        adapter.updateData(items)
                    } else {
                        val kodeError = response.code()
                        Toast.makeText(
                            requireContext(),
                            "🚨 SERVER MENOLAK! Kode: $kodeError",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                override fun onFailure(call: Call<AdminProductResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Gagal koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showConfirmDeleteDialog(product: ProductModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Menu Kopi? ⚠️")
            .setMessage("Apakah kamu yakin ingin menghapus ${product.name} dari daftar menu NgopuDulz?")
            .setPositiveButton("Ya, Hapus") { dialog, _ ->
                eksekusiHapusKeLaravel(product.id)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun eksekusiHapusKeLaravel(productId: String) {
        val token = SessionManager(requireContext()).fetchAuthToken() ?: return

        ApiConfig.getApiService().deleteProduct("Bearer $token", productId)
            .enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Menu berhasil dihapus!", Toast.LENGTH_SHORT).show()
                        fetchDaftarProdukDariLaravel() // Auto refresh list layar
                    }
                }
                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {}
            })
    }
}