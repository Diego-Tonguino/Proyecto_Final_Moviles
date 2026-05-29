package uta.edu.ec.proyecto_final_moviles.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import uta.edu.ec.proyecto_final_moviles.models.LoginRequest
import uta.edu.ec.proyecto_final_moviles.models.LoginResponse
import uta.edu.ec.proyecto_final_moviles.models.Product
import uta.edu.ec.proyecto_final_moviles.models.RegisterCustomerRequest

interface NorthwindApi {
    @POST("api/customers/login")
    fun loginCustomer(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/customers")
    fun registerCustomer(@Body request: RegisterCustomerRequest): Call<ResponseBody>

    @GET("api/products")
    fun getProducts(): Call<List<Product>>

    // Endpoints de verificación para validación en tiempo real
    @GET("api/customers/check-cedula/{cedula}")
    fun checkCedula(@Path("cedula") cedula: String): Call<ResponseBody>

    @GET("api/customers/check-email/{email}")
    fun checkEmail(@Path("email") email: String): Call<ResponseBody>

    @GET("api/customers/check-phone/{phone}")
    fun checkPhone(@Path("phone") phone: String): Call<ResponseBody>

    @GET("api/customers/{id}")
    fun getCustomer(@Path("id") id: String): Call<uta.edu.ec.proyecto_final_moviles.models.CustomerProfile>

    @GET("api/customers")
    fun getAllCustomers(): Call<List<uta.edu.ec.proyecto_final_moviles.models.CustomerProfile>>

    @PUT("api/customers/{id}")
    fun updateCustomer(@Path("id") id: String, @Body request: uta.edu.ec.proyecto_final_moviles.models.UpdateCustomerRequest): Call<ResponseBody>

    @retrofit2.http.Multipart
    @POST("api/customers/{id}/photo")
    fun uploadProfilePicture(@Path("id") id: String, @retrofit2.http.Part file: okhttp3.MultipartBody.Part): Call<ResponseBody>

    @retrofit2.http.DELETE("api/customers/{id}/photo")
    fun deleteProfilePicture(@Path("id") id: String): Call<ResponseBody>

    @POST("CreateOrder")
    fun createOrder(@Body request: uta.edu.ec.proyecto_final_moviles.models.CreateOrderRequest): Call<okhttp3.ResponseBody>

    @GET("api/orders/customer/{customerId}")
    fun getOrdersByCustomer(@Path("customerId") customerId: String): Call<List<uta.edu.ec.proyecto_final_moviles.models.OrderHistoryResponse>>
}
