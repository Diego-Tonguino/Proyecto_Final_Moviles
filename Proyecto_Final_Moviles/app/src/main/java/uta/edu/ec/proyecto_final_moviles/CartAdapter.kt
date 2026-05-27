package uta.edu.ec.proyecto_final_moviles

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import uta.edu.ec.proyecto_final_moviles.models.CartItem

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onRemoveItem: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCartProduct: ImageView = view.findViewById(R.id.ivCartProduct)
        val tvCartProductName: TextView = view.findViewById(R.id.tvCartProductName)
        val tvCartProductUnitPrice: TextView = view.findViewById(R.id.tvCartProductUnitPrice)
        val tvCartProductPrice: TextView = view.findViewById(R.id.tvCartProductPrice)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val btnMinus: ImageView = view.findViewById(R.id.btnMinus)
        val btnPlus: ImageView = view.findViewById(R.id.btnPlus)
        val ivRemoveProduct: ImageView = view.findViewById(R.id.ivRemoveProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.tvCartProductName.text = item.product.name
        holder.tvCartProductUnitPrice.text = "$${String.format("%.2f", item.product.unitPrice)}"
        holder.tvCartProductPrice.text = "$${String.format("%.2f", item.subtotal)}"
        holder.tvQuantity.text = item.quantity.toString()

        // Carga de imagen usando la misma lógica que ProductAdapter
        val imageData = item.product.imageData

        if (!imageData.isNullOrEmpty()) {
            try {
                if (imageData.startsWith("data:image", ignoreCase = true)) {
                    val base64Solo = imageData.substringAfter(",")
                    val imageBytes = Base64.decode(base64Solo, Base64.DEFAULT)
                    cargarImagenConGlide(holder, imageBytes)
                } else if (imageData.startsWith("0x", ignoreCase = true)) {
                    val imageBytes = hexStringToByteArray(imageData.substring(2))
                    cargarImagenConGlide(holder, imageBytes)
                } else if (imageData.startsWith("http", ignoreCase = true) || imageData.startsWith("/")) {
                    val imageUrl = if (imageData.startsWith("/")) "http://localhost:5033$imageData" else imageData
                    cargarImagenConGlide(holder, imageUrl)
                } else if (isBase64(imageData)) {
                    val imageBytes = Base64.decode(imageData, Base64.DEFAULT)
                    cargarImagenConGlide(holder, imageBytes)
                } else {
                    holder.ivCartProduct.setImageResource(R.drawable.bg_welcome)
                }
            } catch (e: Exception) {
                holder.ivCartProduct.setImageResource(R.drawable.bg_welcome)
            }
        } else {
            val imageUrl = "http://localhost:5033/api/products/${item.product.id}/image"
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.bg_welcome)
                .error(R.drawable.bg_welcome)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.ivCartProduct)
        }

        // Botones
        holder.btnMinus.setOnClickListener {
            if (item.quantity > 1) {
                onQuantityChange(item, item.quantity - 1)
            }
        }

        holder.btnPlus.setOnClickListener {
            if (item.quantity < item.product.unitsInStock) {
                onQuantityChange(item, item.quantity + 1)
            }
        }

        holder.ivRemoveProduct.setOnClickListener {
            onRemoveItem(item)
        }
        
        // Deshabilitar botón + si llega al máximo stock
        holder.btnPlus.alpha = if (item.quantity >= item.product.unitsInStock) 0.5f else 1.0f
    }

    override fun getItemCount() = cartItems.size

    fun updateData(newItems: List<CartItem>) {
        cartItems = newItems
        notifyDataSetChanged()
    }

    private fun cargarImagenConGlide(holder: CartViewHolder, modelo: Any) {
        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(modelo)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.bg_welcome)
            .error(R.drawable.bg_welcome)
            .transition(BitmapTransitionOptions.withCrossFade())
            .into(holder.ivCartProduct)
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
