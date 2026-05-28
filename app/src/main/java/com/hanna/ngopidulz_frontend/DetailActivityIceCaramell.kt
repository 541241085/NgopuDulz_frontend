package com.hanna.ngopidulz_frontend

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class DetailActivityIceCaramell : AppCompatActivity() {

    private var quantity = 1
    private var basePrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_ice_caramell)// Sesuaikan nama layout-mu

        val sessionManager = SessionManager(this)
        val token = sessionManager.fetchAuthToken()

        val product = intent.getSerializableExtra("EXTRA_PRODUCT") as? Product

        val btnBack = findViewById<ImageButton>(R.id.ic_back)
        val tvName = findViewById<TextView>(R.id.tv_detail_name)
        val tvPrice = findViewById<TextView>(R.id.tv_detail_price)
        val tvDesc = findViewById<TextView>(R.id.tv_detail_desc)

        val btnMinus = findViewById<ImageButton>(R.id.btnKurang)
        val btnPlus = findViewById<ImageButton>(R.id.btnTambah)
        val tvQty = findViewById<TextView>(R.id.tvJumlah)
        val etCatatan = findViewById<EditText>(R.id.etCatatan) // ID untuk catatan
        val btnAddToCart = findViewById<Button>(R.id.btnTambahKeKeranjang)

        if (product != null) {
            tvName.text = product.name
            tvDesc.text = product.description ?: "Tidak ada deskripsi tersedia."
            basePrice = product.price ?: 0
            updateTotalPrice(tvPrice, btnAddToCart)
        }

        btnBack.setOnClickListener { finish() }

        btnPlus.setOnClickListener {
            quantity++
            tvQty.text = quantity.toString()
            updateTotalPrice(tvPrice, btnAddToCart)
        }

        btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQty.text = quantity.toString()
                updateTotalPrice(tvPrice, btnAddToCart)
            }
        }

        // 🔥 LOGIKA TEMBAK API LARAVEL 🔥
        btnAddToCart.setOnClickListener {
            val notes = etCatatan.text.toString().trim()
            val calculatedTotal = basePrice * quantity

            // 👇 UBAH BAGIAN INI: Tambahkan barang baru ke dalam List 👇
            if (product != null) {
                CartHelper.cartList.add(
                    CartItem(
                        product = product,
                        quantity = quantity,
                        subtotal = calculatedTotal,
                        notes = notes
                    )
                )
            }

            Toast.makeText(this, "Berhasil masuk keranjang!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateTotalPrice(tvPrice: TextView, btnAdd: Button) {
        val total = basePrice * quantity
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        val formatted = formatRupiah.format(total).replace("Rp", "Rp ").replace(",00", "")

        tvPrice.text = formatted
        btnAdd.text = "Tambah ke Keranjang - $formatted"
    }
}