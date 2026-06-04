package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class DetailProductAdminActivity : AppCompatActivity() {

    private var currentId: String = ""
    private var currentName: String = ""
    private var currentPrice: Int = 0
    private var currentCategory: String = ""
    private var currentImage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product_admin)

        // Tangkap data operan paket intent
        currentId = intent.getStringExtra("EXTRA_ID") ?: ""
        currentName = intent.getStringExtra("EXTRA_NAME") ?: "Menu Kopi"
        currentPrice = intent.getIntExtra("EXTRA_PRICE", 0)
        currentCategory = intent.getStringExtra("EXTRA_CATEGORY") ?: "kopi"
        currentImage = intent.getStringExtra("EXTRA_IMAGE") ?: ""

        findViewById<TextView>(R.id.tv_detail_name).text = currentName
        findViewById<TextView>(R.id.tv_detail_category).text = "KATEGORI: ${currentCategory.uppercase()}"

        val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        findViewById<TextView>(R.id.tv_detail_price).text = formatRp.format(currentPrice).replace(",00", "")

        findViewById<ImageView>(R.id.btn_back_detail).setOnClickListener { finish() }

        // TOMBOL EDIT DIKLIK -> Buka Form bawa data komplit
        findViewById<Button>(R.id.btn_detail_edit).setOnClickListener {
            val intent = Intent(this, FormProductActivity::class.java).apply {
                putExtra("EXTRA_PRODUCT_ID", currentId)
                putExtra("EXTRA_PRODUCT_NAME", currentName)
                putExtra("EXTRA_PRODUCT_PRICE", currentPrice)
                putExtra("EXTRA_PRODUCT_CATEGORY", currentCategory)
                putExtra("EXTRA_PRODUCT_IMAGE", currentImage)
            }
            startActivity(intent)
            finish() // Tutup halaman detail setelah dialihkan
        }

        // TOMBOL HAPUS DIKLIK
        findViewById<Button>(R.id.btn_detail_delete).setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Menu Tetap? ⚠️")
            .setMessage("Apakah kamu yakin ingin melenyapkan $currentName dari daftar kasir & customer?")
            .setPositiveButton("Hapus") { dialog, _ ->
                eksekusiHapusLaravel()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun eksekusiHapusLaravel() {
        val token = SessionManager(this).fetchAuthToken() ?: return
        ApiConfig.getApiService().deleteProduct("Bearer $token", currentId)
            .enqueue(object : retrofit2.Callback<GeneralResponse> {
                override fun onResponse(call: retrofit2.Call<GeneralResponse>, response: retrofit2.Response<GeneralResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DetailProductAdminActivity, "Menu sukses didelete!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                override fun onFailure(call: retrofit2.Call<GeneralResponse>, t: Throwable) {}
            })
    }
}