package uta.edu.ec.proyecto_final_moviles

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.button.MaterialButton

class PaymentSuccessActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TOTAL = "total_amount"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_success)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { c ->
            c.hide(WindowInsetsCompat.Type.systemBars())
            c.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        supportActionBar?.hide()

        // Animate the checkmark in
        val topSection = findViewById<com.google.android.material.card.MaterialCardView>(R.id.topSection)
        val contentCard = findViewById<com.google.android.material.card.MaterialCardView>(R.id.contentCard)

        topSection.alpha = 0f
        contentCard.alpha = 0f
        contentCard.translationY = 60f

        topSection.animate().alpha(1f).setDuration(600).start()
        contentCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(300)
            .start()

        // Continuar comprando → Home
        findViewById<MaterialButton>(R.id.btnContinueShopping).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        // Ver historial
        findViewById<TextView>(R.id.tvViewHistory).setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
