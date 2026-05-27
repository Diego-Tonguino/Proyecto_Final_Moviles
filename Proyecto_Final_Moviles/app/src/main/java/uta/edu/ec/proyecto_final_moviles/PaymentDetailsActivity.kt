package uta.edu.ec.proyecto_final_moviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uta.edu.ec.proyecto_final_moviles.api.ApiClient
import uta.edu.ec.proyecto_final_moviles.models.CartManager
import uta.edu.ec.proyecto_final_moviles.models.CreateOrderDetailRequest
import uta.edu.ec.proyecto_final_moviles.models.CreateOrderRequest

class PaymentDetailsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CUSTOMER_ID = "customer_id"
        const val EXTRA_WALLET_BALANCE = "wallet_balance"
        const val EXTRA_TOTAL = "total_amount"
        const val EXTRA_METHOD = "payment_method"
    }

    private var total = 0.0
    private var customerId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        customerId = intent.getStringExtra(EXTRA_CUSTOMER_ID) ?: ""
        total = intent.getDoubleExtra(EXTRA_TOTAL, 0.0)
        val method = intent.getIntExtra(EXTRA_METHOD, 1)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { c ->
            c.hide(WindowInsetsCompat.Type.systemBars())
            c.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        supportActionBar?.hide()

        when (method) {
            0 -> setupWallet()
            1 -> setupCreditCard()
            2 -> setupPaypal()
            3 -> setupBitcoin()
            else -> setupCreditCard()
        }
    }

    // ─────────────────────────────────────────────
    // 0 · BILLETERA (Amazon Pay style)
    // ─────────────────────────────────────────────
    private fun setupWallet() {
        setContentView(R.layout.activity_payment_wallet)
        setupBackButton()
        val btn = findViewById<MaterialButton>(R.id.btnConfirmPayment)
        btn.text = "PAGAR \$${String.format("%.2f", total)}"
        btn.setOnClickListener { enviarOrden() }
    }

    // ─────────────────────────────────────────────
    // 1 · TARJETA (original – se mantiene igual)
    // ─────────────────────────────────────────────
    private fun setupCreditCard() {
        setContentView(R.layout.activity_payment_details_card)
        setupBackButton()

        val etCardName = findViewById<TextInputEditText>(R.id.etCardName)
        val etCardNumber = findViewById<TextInputEditText>(R.id.etCardNumber)
        val etExpiry = findViewById<TextInputEditText>(R.id.etExpiry)
        val etCvv = findViewById<TextInputEditText>(R.id.etCvv)
        val tvCardPreviewName = findViewById<TextView>(R.id.tvCardPreviewName)
        val tvCardPreviewNumber = findViewById<TextView>(R.id.tvCardPreviewNumber)
        val tvCardPreviewExpiry = findViewById<TextView>(R.id.tvCardPreviewExpiry)
        val cbSaveCard = findViewById<CheckBox>(R.id.cbSaveCard)
        val btn = findViewById<MaterialButton>(R.id.btnConfirmPayment)
        btn.text = "PAGAR \$${String.format("%.2f", total)}"

        etCardName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvCardPreviewName.text = if (s.isNullOrBlank()) "NOMBRE APELLIDO" else s.toString().uppercase()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val raw = s?.toString()?.filter { it.isDigit() } ?: ""
                tvCardPreviewNumber.text = raw.chunked(4).joinToString(" ").padEnd(19, '•')
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        etExpiry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvCardPreviewExpiry.text = if (s.isNullOrBlank()) "MM/AA" else s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
                val txt = s?.toString() ?: return
                if (txt.length == 2 && !txt.contains("/")) {
                    etExpiry.removeTextChangedListener(this)
                    etExpiry.setText("$txt/")
                    etExpiry.setSelection(etExpiry.text?.length ?: 0)
                    etExpiry.addTextChangedListener(this)
                }
            }
        })

        findViewById<View>(R.id.rowSaveCard).setOnClickListener {
            cbSaveCard.isChecked = !cbSaveCard.isChecked
        }

        btn.setOnClickListener {
            val name = etCardName.text.toString().trim()
            val number = etCardNumber.text.toString().trim()
            val expiry = etExpiry.text.toString().trim()
            val cvv = etCvv.text.toString().trim()
            when {
                name.isEmpty() -> Toast.makeText(this, "Ingresa el nombre del titular", Toast.LENGTH_SHORT).show()
                number.length < 13 -> Toast.makeText(this, "Número de tarjeta inválido", Toast.LENGTH_SHORT).show()
                expiry.length < 5 -> Toast.makeText(this, "Ingresa la fecha MM/AA", Toast.LENGTH_SHORT).show()
                cvv.length < 3 -> Toast.makeText(this, "CVV debe tener 3 dígitos", Toast.LENGTH_SHORT).show()
                else -> enviarOrden()
            }
        }
    }

    // ─────────────────────────────────────────────
    // 2 · PAYPAL (estilo oficial)
    // ─────────────────────────────────────────────
    private fun setupPaypal() {
        setContentView(R.layout.activity_payment_paypal)
        setupBackButton()
        val btn = findViewById<MaterialButton>(R.id.btnConfirmPayment)
        btn.text = "Pagar con PayPal"
        btn.setOnClickListener { enviarOrden() }
    }

    // ─────────────────────────────────────────────
    // 3 · BITCOIN
    // ─────────────────────────────────────────────
    private fun setupBitcoin() {
        setContentView(R.layout.activity_payment_bitcoin)
        setupBackButton()
        val btn = findViewById<MaterialButton>(R.id.btnConfirmPayment)
        btn.text = "CONFIRMAR PAGO"
        btn.setOnClickListener { enviarOrden() }
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    private fun setupBackButton() {
        findViewById<android.widget.ImageButton>(R.id.btnBack)?.setOnClickListener { finish() }
    }

    private fun enviarOrden() {
        val prefs = getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE)
        val shipCity = prefs.getString("user_city", "Quito") ?: "Quito"
        val shipCountry = prefs.getString("user_country", "Ecuador") ?: "Ecuador"
        val shipAddress = prefs.getString("user_address", "N/A") ?: "N/A"
        val shipPostalCode = prefs.getString("user_postal_code", "000000") ?: "000000"

        val orderDetails = CartManager.items.map {
            CreateOrderDetailRequest(it.product.id, it.product.unitPrice, it.quantity)
        }
        val request = CreateOrderRequest(customerId, shipAddress, shipCity, shipCountry, shipPostalCode, orderDetails)

        val btn = findViewById<MaterialButton>(R.id.btnConfirmPayment)
        btn.isEnabled = false
        btn.text = "Procesando..."

        ApiClient.apiService.createOrder(request).enqueue(object : Callback<okhttp3.ResponseBody> {
            override fun onResponse(call: Call<okhttp3.ResponseBody>, response: Response<okhttp3.ResponseBody>) {
                if (response.isSuccessful) {
                    CartManager.clearCart()
                    startActivity(Intent(this@PaymentDetailsActivity, PaymentSuccessActivity::class.java).apply {
                        putExtra(PaymentSuccessActivity.EXTRA_TOTAL, total)
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    finish()
                } else {
                    btn.isEnabled = true
                    btn.text = "Reintentar"
                    Toast.makeText(this@PaymentDetailsActivity, "Error al procesar: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
                btn.isEnabled = true
                btn.text = "Reintentar"
                Toast.makeText(this@PaymentDetailsActivity, "Error de conexión", Toast.LENGTH_LONG).show()
            }
        })
    }
}
