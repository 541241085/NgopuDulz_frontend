package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EksplorActivity : AppCompatActivity() {
    private lateinit var rvProducts: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eksplor)

        val btnBack = findViewById<ImageView>(R.id.btn_back_eksplor)
        rvProducts = findViewById(R.id.rv_products)

        // Setup RecyclerView agar menampilkan 2 kolom
        rvProducts.layoutManager = GridLayoutManager(this, 2)
        // Agar scrollnya mulus di dalam NestedScrollView
        rvProducts.isNestedScrollingEnabled = false
        btnBack.setOnClickListener {
            finish()
        }// Panggil ID angka keranjangnya
        // Panggil ID angka keranjangnya

        fetchProducts()
        val tvCartBadge = findViewById<TextView>(R.id.tv_cart_badge)
        val cart = findViewById<CardView>(R.id.btn_cart_circle)
        tvCartBadge.text = "0"
        cart.setOnClickListener {
            startActivity(Intent(this, PesananActivity::class.java))
        }
    }
    // Fungsi ini akan otomatis dipanggil setiap kali halaman Eksplor terbuka/muncul di layar
    override fun onResume() {
        super.onResume()
        val tvCartBadge = findViewById<TextView>(R.id.tv_cart_badge)

        // 👇 Ambil jumlah dari fungsi baru di CartHelper 👇
        val totalQty = CartHelper.getTotalQuantity()
        tvCartBadge.text = totalQty.toString()
    }
    private fun fetchProducts() {
        val sessionManager = SessionManager(this)
        val token = sessionManager.fetchAuthToken()

        if (token != null) {
            val bearerToken = "Bearer $token"
            // 👇 Ubah Call<List<Product>> jadi Call<ProductResponse> 👇
            ApiConfig.getApiService().getProducts(bearerToken).enqueue(object : Callback<ProductResponse> {
                override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                    if (response.isSuccessful && response.body() != null) {

                        // 👇 Buka kardus "data"-nya di sini 👇
                        val products = response.body()!!.data

                        // Masukkan data ke Adapter
                        productAdapter = ProductAdapter(products)
                        rvProducts.adapter = productAdapter

                    } else {
                        val errorCode = response.code()
                        Toast.makeText(this@EksplorActivity, "Error $errorCode: Gagal mengambil data", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    Toast.makeText(this@EksplorActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Sesi habis, silakan login ulang", Toast.LENGTH_SHORT).show()
        }
    }
}
