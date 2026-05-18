package uta.edu.ec.proyecto_final_moviles.models

object CartManager {
    private val _items = mutableListOf<CartItem>()
    val items: List<CartItem> get() = _items

    fun addProduct(product: Product, quantity: Int = 1): Boolean {
        val existingItem = _items.find { it.product.id == product.id }
        if (existingItem != null) {
            val newQuantity = existingItem.quantity + quantity
            if (newQuantity <= product.unitsInStock) {
                existingItem.quantity = newQuantity
                return true
            }
            return false // Supera el stock
        } else {
            if (quantity <= product.unitsInStock) {
                _items.add(CartItem(product, quantity))
                return true
            }
            return false // Supera el stock
        }
    }

    fun removeProduct(productId: Int) {
        _items.removeAll { it.product.id == productId }
    }

    fun updateQuantity(productId: Int, newQuantity: Int): Boolean {
        val item = _items.find { it.product.id == productId }
        if (item != null) {
            if (newQuantity > 0 && newQuantity <= item.product.unitsInStock) {
                item.quantity = newQuantity
                return true
            } else if (newQuantity == 0) {
                removeProduct(productId)
                return true
            }
        }
        return false
    }

    fun getSubtotal(): Double {
        return _items.sumOf { it.subtotal }
    }

    fun getIva(): Double {
        return getSubtotal() * 0.15
    }

    fun getTotal(): Double {
        return getSubtotal() + getIva()
    }

    fun getItemCount(): Int {
        return _items.sumOf { it.quantity }
    }

    fun clearCart() {
        _items.clear()
    }
}
