package com.hanna.ngopidulz_frontend

// 1. Bikin cetakan untuk 1 baris barang di keranjang
data class CartItem(
    val product: Product,
    var quantity: Int,
    var subtotal: Int,
    var notes: String
)

// 2. Bikin tas keranjang besar untuk menampung banyak barang
object CartHelper {
    // Ini daftar barangnya (List)
    val cartList = mutableListOf<CartItem>()

    // Fungsi otomatis ngitung jumlah barang buat ditaruh di Badge Merah
    fun getTotalQuantity(): Int {
        return cartList.sumOf { it.quantity }
    }

    // Fungsi otomatis ngitung total harga seluruh barang di keranjang
    fun getGrandTotal(): Int {
        return cartList.sumOf { it.subtotal }
    }

    // Fungsi untuk mengosongkan keranjang (dipakai setelah sukses bayar)
    fun clearCart() {
        cartList.clear()
    }
}