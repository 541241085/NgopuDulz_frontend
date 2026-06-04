package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProfileFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvRole: TextView
    private lateinit var btnLogout: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 🔥 Hubungkan ke layout XML profil yang sudah kita benerin tadi Koh
        val view = inflater.inflate(R.layout.fragment_admin_profile, container, false)

        // Inisialisasi Komponen UI dari objek 'view'
        tvName = view.findViewById(R.id.tv_profile_name)
        tvEmail = view.findViewById(R.id.tv_profile_email)
        tvRole = view.findViewById(R.id.tv_profile_role)
        btnLogout = view.findViewById(R.id.btn_profile_logout)

        // 1. Ambil data admin asli dari server Laravel
        fetchDataProfilAdmin()

        // 2. Pasang pemicu klik tombol logout
        btnLogout.setOnClickListener {
            tampilkanDialogKonfirmasiLogout()
        }

        return view
    }

    private fun fetchDataProfilAdmin() {
        val token = SessionManager(requireContext()).fetchAuthToken() ?: return

        ApiConfig.getApiService().getAdminProfile("Bearer $token")
            .enqueue(object : Callback<AdminUserModel> {
                override fun onResponse(call: Call<AdminUserModel>, response: Response<AdminUserModel>) {
                    if (response.isSuccessful && response.body() != null) {
                        val admin = response.body()!!

                        // Set data asli ke layar HP
                        tvName.text = admin.name
                        tvEmail.text = admin.email
                        tvRole.text = "CENTRAL ${admin.role.uppercase()}"
                    }
                }
                override fun onFailure(call: Call<AdminUserModel>, t: Throwable) {
                    Toast.makeText(requireContext(), "Gagal sinkronisasi data profil", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun tampilkanDialogKonfirmasiLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Keluar ⚠️")
            .setMessage("Apakah kamu yakin ingin keluar dari sesi Admin NgopuDulz?")
            .setPositiveButton("Keluar") { dialog, _ ->
                eksekusiLogoutKeLaravel()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun eksekusiLogoutKeLaravel() {
        val sessionManager = SessionManager(requireContext())
        val token = sessionManager.fetchAuthToken() ?: return

        ApiConfig.getApiService().logout("Bearer $token")
            .enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    sessionManager.clearAuthToken()
                    Toast.makeText(requireContext(), "Berhasil keluar sesi", Toast.LENGTH_SHORT).show()
                    navigasiKeLogin()
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    sessionManager.clearAuthToken()
                    navigasiKeLogin()
                }
            })
    }

    private fun navigasiKeLogin() {
        val intent = Intent(requireContext(), MasukActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}