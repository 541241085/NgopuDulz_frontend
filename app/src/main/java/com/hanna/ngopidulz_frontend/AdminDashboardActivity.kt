package com.hanna.ngopidulz_frontend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Buka dashboard sebagai halaman pertama kali muncul
        if (savedInstanceState == null) {
            loadFragment(AdminHomeFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    loadFragment(AdminHomeFragment())
                    true
                }
                // 👇 1. TAMBAHKAN TAB KHUSUS PRODUK DI SINI 👇
                R.id.nav_products -> {
                    loadFragment(AdminProductsFragment()) // Fragment list produk CRUD
                    true
                }
                R.id.nav_users -> {
                    loadFragment(AdminUsersFragment()) // Fragment list kelola akun (suspend)
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(AdminProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Fungsi untuk mengganti kaset (Fragment) di dalam FrameLayout
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}