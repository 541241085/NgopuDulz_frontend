package com.hanna.ngopidulz_frontend

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FormProductActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etImage: EditText
    private lateinit var spCategory: Spinner
    private lateinit var btnSave: Button
    private lateinit var tvTitle: TextView

    private var isEditMode = false
    private var productId: String? = null
    private val categories = arrayOf("kopi", "non-kopi", "makanan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_product)

        etName = findViewById(R.id.et_product_name)
        etPrice = findViewById(R.id.et_product_price)
        etImage = findViewById(R.id.et_product_image)
        spCategory = findViewById(R.id.sp_product_category)
        btnSave = findViewById(R.id.btn_save_product)
        tvTitle = findViewById(R.id.tv_form_title)

        findViewById<ImageView>(R.id.btn_back_form).setOnClickListener { finish() }

        // Setup Dropdown Spinner Kategori
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = adapter

        // 🔥 LOGIKA DETEKSI GANDA: CEK APAKAH MAU EDIT ATAU TAMBAH 🔥
        if (intent.hasExtra("EXTRA_PRODUCT_ID")) {
            isEditMode = true
            tvTitle.text = "Ubah Detail Menu"
            productId = intent.getStringExtra("EXTRA_PRODUCT_ID")
            etName.setText(intent.getStringExtra("EXTRA_PRODUCT_NAME"))
            etPrice.setText(intent.getIntExtra("EXTRA_PRODUCT_PRICE", 0).toString())
            etImage.setText(intent.getStringExtra("EXTRA_PRODUCT_IMAGE"))

            val pos = categories.indexOf(intent.getStringExtra("EXTRA_PRODUCT_CATEGORY")?.lowercase())
            if (pos >= 0) spCategory.setSelection(pos)
        }

        btnSave.setOnClickListener { eksekusiSimpanKeLaravel() }
    }

    private fun eksekusiSimpanKeLaravel() {
        val name = etName.text.toString().trim()
        val priceStr = etPrice.text.toString().trim()
        val image = etImage.text.toString().trim()
        val category = spCategory.selectedItem.toString()

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Nama dan Harga wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val token = SessionManager(this).fetchAuthToken() ?: return
        val productData = ProductModel(
            id = productId ?: "",
            name = name,
            price = priceStr.toInt(),
            category = category,
            image = if (image.isEmpty()) "img_default.png" else image
        )

        val callAction = if (isEditMode) {
            // Tembak PUT untuk update data
            ApiConfig.getApiService().updateProduct("Bearer $token", productId!!, productData)
        } else {
            // Tembak POST untuk menu baru
            ApiConfig.getApiService().createProduct("Bearer $token", productData)
        }

        callAction.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@FormProductActivity, "Menu Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                    finish() // Tutup halaman form kembali ke list menu
                } else {
                    Toast.makeText(this@FormProductActivity, "Gagal menyimpan: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                Toast.makeText(this@FormProductActivity, "Error koneksi", Toast.LENGTH_SHORT).show()
            }
        })
    }
}