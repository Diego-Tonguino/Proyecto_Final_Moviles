package uta.edu.ec.proyecto_final_moviles.models

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
    @SerializedName("customerId", alternate = ["CustomerId"])
    val customerId: String,
    
    @SerializedName("shipAddress", alternate = ["ShipAddress"])
    val shipAddress: String,
    
    @SerializedName("shipCity", alternate = ["ShipCity"])
    val shipCity: String,
    
    @SerializedName("shipCountry", alternate = ["ShipCountry"])
    val shipCountry: String,
    
    @SerializedName("shipPostalCode", alternate = ["ShipPostalCode"])
    val shipPostalCode: String,
    
    @SerializedName("orderDetails", alternate = ["OrderDetails"])
    val orderDetails: List<CreateOrderDetailRequest>
)

data class CreateOrderDetailRequest(
    @SerializedName("productId", alternate = ["ProductId"])
    val productId: Int,
    
    @SerializedName("unitPrice", alternate = ["UnitPrice"])
    val unitPrice: Double,
    
    @SerializedName("quantity", alternate = ["Quantity"])
    val quantity: Int
)
