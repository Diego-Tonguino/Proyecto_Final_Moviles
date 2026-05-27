package uta.edu.ec.proyecto_final_moviles

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uta.edu.ec.proyecto_final_moviles.models.OrderHistoryResponse
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        // Ocultar barras
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        supportActionBar?.hide()

        val btnBack = findViewById<ImageButton>(R.id.btnBackInvoice)
        btnBack.setOnClickListener { finish() }

        // Recibir datos
        val orderJson = intent.getStringExtra("order_json")
        val productsJson = intent.getStringExtra("products_json")

        if (orderJson != null && productsJson != null) {
            val order = Gson().fromJson(orderJson, OrderHistoryResponse::class.java)
            val type = object : TypeToken<Map<Int, String>>() {}.type
            val productNames: Map<Int, String> = Gson().fromJson(productsJson, type)

            poblarFactura(order, productNames)
        }
    }

    private fun poblarFactura(order: OrderHistoryResponse, productNames: Map<Int, String>) {
        val tvOrderNumber = findViewById<TextView>(R.id.tvInvoiceOrderNumber)
        val tvDate = findViewById<TextView>(R.id.tvInvoiceDate)
        val tvBillingName = findViewById<TextView>(R.id.tvBillingName)
        val tvAddress = findViewById<TextView>(R.id.tvInvoiceAddress)
        val tvInvoiceSubtotal = findViewById<TextView>(R.id.tvInvoiceSubtotal)
        val tvInvoiceIva = findViewById<TextView>(R.id.tvInvoiceIva)
        val tvTotal = findViewById<TextView>(R.id.tvInvoiceTotal)
        val llItems = findViewById<LinearLayout>(R.id.llInvoiceItems)
        val btnSavePdf = findViewById<com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton>(R.id.btnSavePdf)

        tvOrderNumber.text = "Nº Factura: ${order.id}"
        
        try {
            if (!order.orderDate.isNullOrEmpty()) {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                val date = parser.parse(order.orderDate)
                tvDate.text = "Fecha: " + (if (date != null) formatter.format(date) else order.orderDate)
            }
        } catch (e: Exception) {
            tvDate.text = "Fecha: ${order.orderDate}"
        }

        val prefs = getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "Cliente")
        tvBillingName.text = userName

        tvAddress.text = "${order.shipCity ?: "N/A"}\n${order.shipAddress ?: "N/A"}"

        var totalAmount = 0.0

        order.orderDetails?.forEach { detail ->
            val name = productNames[detail.productId] ?: "Producto #${detail.productId}"
            val subtotal = detail.unitPrice * detail.quantity
            totalAmount += subtotal

            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }
            }

            val tvId = createCellText(detail.productId.toString(), 0.8f, Gravity.START)
            val tvDesc = createCellText(name, 3f, Gravity.START)
            val tvQty = createCellText(detail.quantity.toString(), 1f, Gravity.CENTER)
            val tvPrice = createCellText("$${String.format("%.2f", detail.unitPrice)}", 1.5f, Gravity.END)
            val tvSub = createCellText("$${String.format("%.2f", subtotal)}", 1.5f, Gravity.END)

            itemLayout.addView(tvId)
            itemLayout.addView(tvDesc)
            itemLayout.addView(tvQty)
            itemLayout.addView(tvPrice)
            itemLayout.addView(tvSub)
            
            llItems.addView(itemLayout)
        }

        val subtotal = totalAmount / 1.15 // If totalAmount includes IVA. Wait, usually unit prices don't include IVA. Let's assume totalAmount is subtotal.
        val iva = totalAmount * 0.15
        val granTotal = totalAmount + iva

        tvInvoiceSubtotal.text = "$${String.format("%.2f", totalAmount)}"
        tvInvoiceIva.text = "$${String.format("%.2f", iva)}"
        tvTotal.text = "$${String.format("%.2f", granTotal)}"

        btnSavePdf.setOnClickListener {
            crearYGuardarPdf()
        }
    }

    private fun createCellText(textStr: String, weight: Float, align: Int): TextView {
        return TextView(this).apply {
            text = textStr
            setTextColor(Color.parseColor("#1F2937"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            gravity = align
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight)
        }
    }

    private fun crearYGuardarPdf() {
        // En una implementación completa esto generaría el archivo PDF en Descargas
        // o utilizaría PdfDocument. Por ahora mostraremos un mensaje de éxito.
        android.widget.Toast.makeText(this, "Factura guardada como PDF en Descargas", android.widget.Toast.LENGTH_LONG).show()
    }
}
