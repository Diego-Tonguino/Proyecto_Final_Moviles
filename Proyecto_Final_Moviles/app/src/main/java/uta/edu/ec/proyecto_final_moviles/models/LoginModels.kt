package uta.edu.ec.proyecto_final_moviles.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val email: String, 
    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("id", alternate = ["Id"])
    val id: String?,
    @SerializedName("firstName", alternate = ["FirstName"])
    val firstName: String?,
    @SerializedName("lastName", alternate = ["LastName"])
    val lastName: String?
)

data class RegisterCustomerRequest(
    val cedula: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val city: String,
    val password: String,
    val passwordConfirm: String
)

data class CustomerProfile(
    val id: String,
    val cedula: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val city: String?,
    val currentBalance: Double
)

data class UpdateCustomerRequest(
    val id: String,
    val cedula: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val city: String?,
    val password: String?,
    val passwordConfirm: String?
)
