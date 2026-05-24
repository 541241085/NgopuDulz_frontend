import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.hanna.ngopidulz_frontend.ItemAntrian
import com.hanna.ngopidulz_frontend.R

class PesananDibuatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesanan_dibuat) // Sesuaikan nama file XML kamu

        // 1. Tombol Back
        val btnBack = findViewById<ImageButton>(R.id.ic_back)
        btnBack.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya tanpa menghapus data
        }

        // 2. Tombol Selesai (Logic Hapus Data)
        val btnSelesai = findViewById<Button>(R.id.btnSelesai)
        btnSelesai.setOnClickListener {
            // DI SINI KAMU HAPUS DATA PESANANNYA
            // Contoh jika menggunakan List static atau Database:
            // DataManager.hapusPesanan(idPesanan)

            // Setelah data dihapus, balik ke halaman Antrian
            val intent = Intent(this, ItemAntrian::class.java)

            // Flag ini penting biar halaman antrian di-refresh dan pesanan hilang
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent)
            finish() // Tutup halaman ini
        }
    }
}