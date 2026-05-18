package uta.edu.ec.proyecto_final_moviles.models

import com.google.gson.annotations.SerializedName

data class OrderHistoryResponse(
    @SerializedName("id", alternate = ["Id", "orderId", "OrderId"])
    val id: Int,
    
    @SerializedName("orderDate", alternate = ["OrderDate"])
    val orderDate: String?,
    
    @SerializedName("shipAddress", alternate = ["ShipAddress"])
    val shipAddress: String?,
    
    @SerializedName("shipCity", alternate = ["ShipCity"])
    val shipCity: String?,
    
    @SerializedName("shipCountry", alternate = ["ShipCountry"])
    val shipCountry: String?,
    
    @SerializedName("shipPostalCode", alternate = ["ShipPostalCode"])
    val shipPostalCode: String?,
    
    @SerializedName("orderDetails", alternate = ["OrderDetails"])
    val orderDetails: List<OrderDetailResponse>?
)

data class OrderDetailResponse(
    @SerializedName("productId", alternate = ["ProductId"])
    val productId: Int,
    
    @SerializedName("unitPrice", alternate = ["UnitPrice"])
    val unitPrice: Double,
    
    @SerializedName("quantity", alternate = ["Quantity"])
    val quantity: Int
)
