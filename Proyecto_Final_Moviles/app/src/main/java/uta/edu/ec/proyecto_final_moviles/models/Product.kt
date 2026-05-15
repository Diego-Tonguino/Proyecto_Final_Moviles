package uta.edu.ec.proyecto_final_moviles.models

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id", alternate = ["Id"])
    val id: Int,
    
    @SerializedName("name", alternate = ["Name"])
    val name: String,
    
    @SerializedName("unitPrice", alternate = ["UnitPrice"])
    val unitPrice: Double,
    
    @SerializedName("unitsInStock", alternate = ["UnitsInStock"])
    val unitsInStock: Int,
    
    @SerializedName("imageData", alternate = ["ImageData"])
    val imageData: String? = null,
    
    @SerializedName("imageContentType", alternate = ["ImageContentType"])
    val imageContentType: String? = null,
    
    // Campo local para manejar el estado de favoritos en la UI
    var isFavorite: Boolean = false
)
