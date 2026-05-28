package com.hanna.ngopidulz_frontend

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class PesananActivity : AppCompatActivity() {

    private lateinit var rvCart: RecyclerView
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesanan)

        val btnBack = findViewById<ImageView>(R.id.btn_back_pesanan)
        val tvSubtotal = findViewById<TextView>(R.id.tv_subtotal)
        val tvTotalPayment = findViewById<TextView>(R.id.tv_total_payment)
        val tvBottomTotal = findViewById<TextView>(R.id.tv_bottom_total)
        val btnCheckout = findViewById<CardView>(R.id.btn_checkout)

        rvCart = findViewById(R.id.rv_cart_items)
        rvCart.layoutManager = LinearLayoutManager(this)

        btnBack.setOnClickListener { finish() }

        if (CartHelper.cartList.isNotEmpty()) {
            cartAdapter = CartAdapter(CartHelper.cartList)
            rvCart.adapter = cartAdapter

            val subtotalAll = CartHelper.getGrandTotal()
            val serviceFee = 2000
            val grandTotal = subtotalAll + serviceFee

            tvSubtotal.text = formatRupiah(subtotalAll)
            tvTotalPayment.text = formatRupiah(grandTotal)
            tvBottomTotal.text = formatRupiah(grandTotal)

            // ==========================================
            // 🔥 LOGIKA MIDTRANS & LARAVEL DI SINI 🔥
            // ==========================================
            btnCheckout.setOnClickListener {
                val sessionManager = SessionManager(this)
                val token = sessionManager.fetchAuthToken()

                if (token == null) {
                    Toast.makeText(this, "Sesi habis, silakan login ulang!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val tvCheckoutText = btnCheckout.getChildAt(0) as TextView
                tvCheckoutText.text = "Memproses..."
                btnCheckout.isEnabled = false

                // 1. Bungkus semua barang di keranjang jadi format OrderItemRequest
                val itemsRequest = CartHelper.cartList.map { cartItem ->
                    OrderItemRequest(
                        productId = cartItem.product.id!!,
                        qty = cartItem.quantity,
                        subtotal = cartItem.subtotal
                    )
                }

                // 2. Masukkan ke kardus besar OrderRequest
                val orderData = OrderRequest(
                    totalPrice = grandTotal,
                    items = itemsRequest
                )

                // 3. Tembak ke Laravel
                val bearerToken = "Bearer $token"
                ApiConfig.getApiService().createOrder(bearerToken, orderData).enqueue(object : Callback<OrderResponse> {
                    override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                        tvCheckoutText.text = "Bayar Sekarang"
                        btnCheckout.isEnabled = true

                        if (response.isSuccessful && response.body() != null) {
                            val paymentUrl = response.body()?.paymentUrl

                            if (paymentUrl != null) {
                                // Keranjang dikosongkan karena sudah sukses masuk sistem
                                CartHelper.clearCart()

                                // BUKA BROWSER UNTUK BAYAR MIDTRANS
                                val intent = Intent(this@PesananActivity, PaymentActivity::class.java)
                                intent.putExtra("PAYMENT_URL", paymentUrl)
                                startActivity(intent)

                                // Tutup halaman pesanan
                                finish()
                            } else {
                                Toast.makeText(this@PesananActivity, "Gagal mendapatkan link pembayaran", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorCode = response.code()
                            val errorMessage = response.errorBody()?.string() ?: "Error tidak diketahui"
                            Toast.makeText(this@PesananActivity, "Error $errorCode", Toast.LENGTH_LONG).show()
                            println("ERROR LARAVEL: $errorMessage")
                        }
                    }

                    override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                        tvCheckoutText.text = "Bayar Sekarang"
                        btnCheckout.isEnabled = true
                        Toast.makeText(this@PesananActivity, "Koneksi Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

        } else {
            Toast.makeText(this, "Keranjang masih kosong!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun formatRupiah(number: Int): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(number).replace("Rp", "Rp ").replace(",00", "")
    }
}