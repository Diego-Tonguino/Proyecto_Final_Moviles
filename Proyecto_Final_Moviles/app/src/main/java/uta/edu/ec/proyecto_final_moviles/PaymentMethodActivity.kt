package uta.edu.ec.proyecto_final_moviles

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import uta.edu.ec.proyecto_final_moviles.models.CartManager

class PaymentMethodActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CUSTOMER_ID = "customer_id"
        const val EXTRA_WALLET_BALANCE = "wallet_balance"
        const val EXTRA_TOTAL = "total_amount"
    }

    private var selectedMethod = 0 // 0=Wallet, 1=Credit, 2=Paypal, 3=Bitcoin

    private lateinit var cardWallet: MaterialCardView
    private lateinit var cardCredit: MaterialCardView
    private lateinit var cardPaypal: MaterialCardView
    private lateinit var cardBitcoin: MaterialCardView

    private lateinit var radioWallet: ImageView
    private lateinit var radioCredit: ImageView
    private lateinit var radioPaypal: ImageView
    private lateinit var radioBitcoin: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { c ->
            c.hide(WindowInsetsCompat.Type.systemBars())
            c.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        supportActionBar?.hide()

        val customerId = intent.getStringExtra(EXTRA_CUSTOMER_ID) ?: ""
        val walletBalance = intent.getDoubleExtra(EXTRA_WALLET_BALANCE, 0.0)
        val total = intent.getDoubleExtra(EXTRA_TOTAL, 0.0)

        cardWallet = findViewById(R.id.cardWallet)
        cardCredit = findViewById(R.id.cardCredit)
        cardPaypal = findViewById(R.id.cardPaypal)
        cardBitcoin = findViewById(R.id.cardBitcoin)

        radioWallet = findViewById(R.id.radioWallet)
        radioCredit = findViewById(R.id.radioCredit)
        radioPaypal = findViewById(R.id.radioPaypal)
        radioBitcoin = findViewById(R.id.radioBitcoin)

        val tvWalletBalance = findViewById<TextView>(R.id.tvWalletBalanceMethod)
        tvWalletBalance.text = "Saldo: $${String.format("%.2f", walletBalance)}"

        val tvTotal = findViewById<TextView>(R.id.tvTotalAmount)
        tvTotal.text = "$${String.format("%.2f", total)}"

        // Set initial selection
        selectMethod(0)

        // Click listeners
        cardWallet.setOnClickListener { selectMethod(0) }
        cardCredit.setOnClickListener { selectMethod(1) }
        cardPaypal.setOnClickListener { selectMethod(2) }
        cardBitcoin.setOnClickListener { selectMethod(3) }

        // Also allow clicking the row
        findViewById<android.view.View>(R.id.rowWallet).setOnClickListener { selectMethod(0) }
        findViewById<android.view.View>(R.id.rowCredit).setOnClickListener { selectMethod(1) }
        findViewById<android.view.View>(R.id.rowPaypal).setOnClickListener { selectMethod(2) }
        findViewById<android.view.View>(R.id.rowBitcoin).setOnClickListener { selectMethod(3) }

        findViewById<MaterialButton>(R.id.btnNext).setOnClickListener {
            val intent = Intent(this, PaymentDetailsActivity::class.java).apply {
                putExtra(PaymentDetailsActivity.EXTRA_CUSTOMER_ID, customerId)
                putExtra(PaymentDetailsActivity.EXTRA_WALLET_BALANCE, walletBalance)
                putExtra(PaymentDetailsActivity.EXTRA_TOTAL, total)
                putExtra(PaymentDetailsActivity.EXTRA_METHOD, selectedMethod)
            }
            startActivity(intent)
        }

        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun selectMethod(method: Int) {
        selectedMethod = method

        val purple = android.graphics.Color.parseColor("#a78bfa")
        val dim = android.graphics.Color.parseColor("#33FFFFFF")

        // Reset all
        listOf(cardWallet, cardCredit, cardPaypal, cardBitcoin).forEach {
            it.strokeWidth = 1
            it.strokeColor = dim
        }
        listOf(radioWallet, radioCredit, radioPaypal, radioBitcoin).forEach {
            it.setImageDrawable(getDrawable(android.R.drawable.presence_invisible))
            it.setColorFilter(android.graphics.Color.GRAY)
        }

        // Highlight selected
        val selectedCard = listOf(cardWallet, cardCredit, cardPaypal, cardBitcoin)[method]
        val selectedRadio = listOf(radioWallet, radioCredit, radioPaypal, radioBitcoin)[method]

        selectedCard.strokeWidth = 4
        selectedCard.strokeColor = purple
        selectedRadio.setImageDrawable(getDrawable(android.R.drawable.presence_online))
        selectedRadio.setColorFilter(purple)
    }
}
