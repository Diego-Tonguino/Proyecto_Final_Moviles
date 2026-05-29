package uta.edu.ec.proyecto_final_moviles

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import uta.edu.ec.proyecto_final_moviles.models.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var productsFull: List<Product> = ArrayList(products)

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProductImage: ImageView = view.findViewById(R.id.ivProductImage)
        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val cardProduct: View = view.findViewById(R.id.cardProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.tvProductName.text = product.name
        holder.tvProductPrice.text = "$${String.format("%.2f", product.unitPrice)}"

        // ==========================================
        // CARGA DE IMAGEN MEJORADA (Sin caché para ver ediciones en vivo)
        // ==========================================
        val imageData = product.imageData

        if (!imageData.isNullOrEmpty()) {
            try {
                // CASO 1: Viene con prefijo web
                if (imageData.startsWith("data:image", ignoreCase = true)) {
                    val base64Solo = imageData.substringAfter(",")
                    val imageBytes = Base64.decode(base64Solo, Base64.DEFAULT)
                    cargarImagenConGlide(holder, imageBytes)
                }
                // CASO 2: Hexadecimal SQL
                else if (imageData.startsWith("0x", ignoreCase = true)) {
                    val imageBytes = hexStringToByteArray(imageData.substring(2))
                    cargarImagenConGlide(holder, imageBytes)
                }
                // CASO 3: URL Directa
                else if (imageData.startsWith("http", ignoreCase = true) || imageData.startsWith("/")) {
                    val imageUrl = if (imageData.startsWith("/")) "https://northwind-api-uta-anffgshdbxfjaecr.switzerlandnorth-01.azurewebsites.net$imageData" else imageData
                    cargarImagenConGlide(holder, imageUrl)
                }
                // CASO 4: Base64 Puro
                else if (isBase64(imageData)) {
                    val imageBytes = Base64.decode(imageData, Base64.DEFAULT)
                    cargarImagenConGlide(holder, imageBytes)
                }
                else {
                    holder.ivProductImage.setImageResource(R.drawable.bg_welcome)
                }
            } catch (e: Exception) {
                holder.ivProductImage.setImageResource(R.drawable.bg_welcome)
            }
        }
        else {
            // CASO 5: NO HAY IMAGEN EN JSON (API Optimizada). Llama al endpoint de imagen
            val imageUrl = "https://northwind-api-uta-anffgshdbxfjaecr.switzerlandnorth-01.azurewebsites.net/api/products/${product.id}/image"

            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Obliga a descargar siempre la imagen nueva (ignora disco)
                .skipMemoryCache(true) // Ignora la copia en memoria RAM
                .placeholder(R.drawable.bg_welcome)
                .error(R.drawable.bg_welcome)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.ivProductImage)
        }
        // ==========================================

        holder.cardProduct.setOnClickListener { onProductClick(product) }
    }

    override fun getItemCount() = products.size

    fun filter(text: String) {
        filterFull(text, null, null)
    }

    fun filterFull(text: String, minPrice: Double?, maxPrice: Double?) {
        products = productsFull.filter { product ->
            val matchesName = text.isEmpty() || product.name.lowercase().contains(text.lowercase())
            val matchesMin = minPrice == null || product.unitPrice >= minPrice
            val matchesMax = maxPrice == null || product.unitPrice <= maxPrice
            matchesName && matchesMin && matchesMax
        }
        notifyDataSetChanged()
    }

    fun updateList(newList: List<Product>) {
        products = newList
        productsFull = ArrayList(newList)
        notifyDataSetChanged()
    }

    fun getMinPrice(): Float = productsFull.minOfOrNull { it.unitPrice }?.toFloat() ?: 0f
    fun getMaxPrice(): Float = productsFull.maxOfOrNull { it.unitPrice }?.toFloat() ?: 1000f

    // Utilidad para cargar imágenes evitando caché en otras situaciones
    private fun cargarImagenConGlide(holder: ProductViewHolder, modelo: Any) {
        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(modelo)
            .diskCacheStrategy(DiskCacheStrategy.NONE) // Evita caché
            .skipMemoryCache(true) // Evita caché
            .placeholder(R.drawable.bg_welcome)
            .error(R.drawable.bg_welcome)
            .transition(BitmapTransitionOptions.withCrossFade())
            .into(holder.ivProductImage)
    }

    // Convertir Hex String (de SQL Server) a ByteArray
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

    // Comprobar si un String es válido en Base64
    private fun isBase64(s: String): Boolean {
        return try {
            Base64.decode(s, Base64.DEFAULT)
            true
        } catch (e: Exception) {
            false
        }
    }
}
