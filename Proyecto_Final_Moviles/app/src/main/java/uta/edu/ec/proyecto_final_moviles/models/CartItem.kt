package uta.edu.ec.proyecto_final_moviles.models

data class CartItem(
    val product: Product,
    var quantity: Int
) {
    val subtotal: Double
        get() = product.unitPrice * quantity
}
