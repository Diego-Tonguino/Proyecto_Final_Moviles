package uta.edu.ec.proyecto_final_moviles

import android.os.Bundle
import android.util.Base64
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.button.MaterialButton
import uta.edu.ec.proyecto_final_moviles.models.CartManager
import uta.edu.ec.proyecto_final_moviles.models.Product

class ProductDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Ocultar barra de estado y navegación (Efecto inmersivo) igual que en Home
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        supportActionBar?.hide()

        // Vincular Vistas
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val ivProductImageDetail = findViewById<ImageView>(R.id.ivProductImageDetail)
        val tvProductNameDetail = findViewById<TextView>(R.id.tvProductNameDetail)
        val tvProductPriceDetail = findViewById<TextView>(R.id.tvProductPriceDetail)
        val tvProductStockDetail = findViewById<TextView>(R.id.tvProductStockDetail)
        val btnAddToCartDetail = findViewById<MaterialButton>(R.id.btnAddToCartDetail)
        val btnDetailPlus = findViewById<MaterialButton>(R.id.btnDetailPlus)
        val btnDetailMinus = findViewById<MaterialButton>(R.id.btnDetailMinus)
        val tvDetailQuantity = findViewById<TextView>(R.id.tvDetailQuantity)

        // Recibir datos del Intent
        val productId = intent.getIntExtra("product_id", 0)
        val productName = intent.getStringExtra("product_name") ?: "Producto"
        val productDescription = intent.getStringExtra("product_description")
        val productPrice = intent.getDoubleExtra("product_price", 0.0)
        val productStock = intent.getIntExtra("product_stock", 0)
        val productImageData = intent.getStringExtra("product_image")

        // Variable de cantidad
        var quantity = 1
        tvDetailQuantity.text = quantity.toString()

        btnDetailPlus.setOnClickListener {
            if (quantity < productStock) {
                quantity++
                tvDetailQuantity.text = quantity.toString()
            } else {
                Toast.makeText(this, "Stock máximo: $productStock unidades", Toast.LENGTH_SHORT).show()
            }
        }

        btnDetailMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvDetailQuantity.text = quantity.toString()
            }
        }

        // Asignar a vistas
        tvProductNameDetail.text = productName
        tvProductPriceDetail.text = "$${String.format("%.2f", productPrice)}"
        tvProductStockDetail.text = "Unidades disponibles: $productStock"

        // Mostrar descripción real del producto o texto genérico si no tiene
        val tvProductDescription = findViewById<TextView>(R.id.tvProductDescription)
        tvProductDescription.text = if (!productDescription.isNullOrBlank()) {
            productDescription
        } else {
            "Sin descripción disponible para este producto."
        }

        // Cargar imagen usando la misma lógica de ProductAdapter
        cargarImagen(ivProductImageDetail, productImageData, productId)

        // Botón Volver
        btnBack.setOnClickListener {
            finish()
        }

        // Botón Agregar al Carrito
        btnAddToCartDetail.setOnClickListener {
            val product = Product(
                id = productId,
                name = productName,
                description = productDescription,
                unitPrice = productPrice,
                unitsInStock = productStock,
                imageData = productImageData,
                imageContentType = null
            )
            
            if (CartManager.addProduct(product, quantity)) {
                Toast.makeText(this, "$productName (x$quantity) añadido al carrito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Stock máximo alcanzado para $productName", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarImagen(imageView: ImageView, imageData: String?, productId: Int) {
        if (!imageData.isNullOrEmpty()) {
            try {
                if (imageData.startsWith("data:image", ignoreCase = true)) {
                    val base64Solo = imageData.substringAfter(",")
                    val imageBytes = Base64.decode(base64Solo, Base64.DEFAULT)
                    cargarImagenConGlide(imageView, imageBytes)
                } else if (imageData.startsWith("0x", ignoreCase = true)) {
                    val imageBytes = hexStringToByteArray(imageData.substring(2))
                    cargarImagenConGlide(imageView, imageBytes)
                } else if (imageData.startsWith("http", ignoreCase = true) || imageData.startsWith("/")) {
                    val imageUrl = if (imageData.startsWith("/")) "${uta.edu.ec.proyecto_final_moviles.api.ApiClient.BASE_URL.removeSuffix("/")}$imageData" else imageData
                    cargarImagenConGlide(imageView, imageUrl)
                } else if (isBase64(imageData)) {
                    val imageBytes = Base64.decode(imageData, Base64.DEFAULT)
                    cargarImagenConGlide(imageView, imageBytes)
                } else {
                    imageView.setImageResource(R.drawable.bg_welcome)
                }
            } catch (e: Exception) {
                imageView.setImageResource(R.drawable.bg_welcome)
            }
        } else {
            val imageUrl = "${uta.edu.ec.proyecto_final_moviles.api.ApiClient.BASE_URL}api/products/$productId/image"
            Glide.with(this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.bg_welcome)
                .error(R.drawable.bg_welcome)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        }
    }

    private fun cargarImagenConGlide(imageView: ImageView, modelo: Any) {
        Glide.with(this)
            .asBitmap()
            .load(modelo)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.bg_welcome)
            .error(R.drawable.bg_welcome)
            .transition(BitmapTransitionOptions.withCrossFade())
            .into(imageView)
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    private fun isBase64(s: String): Boolean {
        return try {
            Base64.decode(s, Base64.DEFAULT)
            true
        } catch (e: Exception) {
            false
        }
    }
}
