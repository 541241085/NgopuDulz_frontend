package com.hanna.ngopidulz_frontend

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminUsersFragment : Fragment() {

    private lateinit var rvUsers: RecyclerView
    private lateinit var adapter: AdminUserAdapter
    private var allUsers: List<AdminUserModel> = emptyList()

    private lateinit var rgKategori: RadioGroup
    private lateinit var rbCustomer: RadioButton
    private lateinit var rbKasir: RadioButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_users, container, false)

        rgKategori = view.findViewById(R.id.rg_kategori_user)
        rbCustomer = view.findViewById(R.id.rb_cat_customer)
        rbKasir = view.findViewById(R.id.rb_cat_kasir)
        rvUsers = view.findViewById(R.id.rv_admin_users)

        rvUsers.layoutManager = LinearLayoutManager(requireContext())

        adapter = AdminUserAdapter(emptyList()) { user ->
            // Ketika tombol aksi diklik, tentukan status kebalikannya
            val statusBaru = if (user.status?.lowercase() == "suspend") "active" else "suspend"
            eksekusiUbahStatusUser(user.id, statusBaru)
        }
        rvUsers.adapter = adapter

        // Logika Perpindahan Kategori Tab Atas
        rgKategori.setOnCheckedChangeListener { _, checkedId ->
            rbCustomer.setTextColor(Color.parseColor("#8E8E8E"))
            rbKasir.setTextColor(Color.parseColor("#8E8E8E"))

            when (checkedId) {
                R.id.rb_cat_customer -> rbCustomer.setTextColor(Color.parseColor("#E5A93C"))
                R.id.rb_cat_kasir -> rbKasir.setTextColor(Color.parseColor("#E5A93C"))
            }
            filterDanTampilkanData()
        }

        fetchDaftarUsersDariLaravel()
        return view
    }

    private fun fetchDaftarUsersDariLaravel() {
        val token = SessionManager(requireContext()).fetchAuthToken() ?: return

        ApiConfig.getApiService().getAllUsers("Bearer $token")
            .enqueue(object : Callback<AdminUserResponse> {
                override fun onResponse(call: Call<AdminUserResponse>, response: Response<AdminUserResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        allUsers = response.body()!!.data ?: emptyList()
                        filterDanTampilkanData()
                    }
                }
                override fun onFailure(call: Call<AdminUserResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Gagal memuat akun user", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun filterDanTampilkanData() {
        val filteredList = when {
            // Filter Khusus Akun Customer / Pelanggan
            rbCustomer.isChecked -> allUsers.filter { it.role.lowercase() == "customer" }

            // Filter Khusus Akun Kasir / Pegawai Toko
            rbKasir.isChecked -> allUsers.filter { it.role.lowercase() == "cashier" || it.role.lowercase() == "kasir" }

            else -> emptyList()
        }
        adapter.updateData(filteredList)
    }

    private fun eksekusiUbahStatusUser(userId: String, statusBaru: String) {
        val token = SessionManager(requireContext()).fetchAuthToken() ?: return

        val requestBody = HashMap<String, String>()
        requestBody["status"] = statusBaru // Mengirim "active" atau "suspend"

        ApiConfig.getApiService().updateUserStatus("Bearer $token", userId, requestBody)
            .enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "SUKSES! Akun berhasil diubah", Toast.LENGTH_SHORT).show()
                        fetchDaftarUsersDariLaravel()
                    } else {
                        // 👇 KITA BONGKAR ISI PESAN ERROR ASLI DARI LARAVEL DI SINI Koh 👇
                        try {
                            val errorRaw = response.errorBody()?.string() ?: "{}"
                            val jsonObject = org.json.JSONObject(errorRaw)

                            // Ambil field 'error' dan 'line' yang dikirim dari try-catch Laravel
                            val pesanErrorAsli = jsonObject.optString("error", "Gagal tanpa pesan")
                            val barisError = jsonObject.optString("line", "0")

                            // Tampilkan langsung di layar HP kamu
                            Toast.makeText(
                                requireContext(),
                                "🚨 LARAVEL CRASH di Baris $barisError: $pesanErrorAsli",
                                Toast.LENGTH_LONG
                            ).show()

                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "GAGAL! Kode Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    // 🔴 ALERT 3: JIKA KONEKSI INTERNET PUTUS / IP LAPTOP SALAH
                    Toast.makeText(
                        requireContext(),
                        "ERROR KONEKSI! Jalur putus: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}