package uta.edu.ec.proyecto_final_moviles.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Restauramos a localhost que es la configuración que te funcionaba con adb reverse
    private const val BASE_URL = "https://northwind-api-uta-anflgshdbxfjancr.switzerlandnorth-01.azurewebsites.net/"

    var authToken: String? = null

    private val client = okhttp3.OkHttpClient.Builder()
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            authToken?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }
            chain.proceed(requestBuilder.build())
        }
        .build()

    val apiService: NorthwindApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NorthwindApi::class.java)
    }
}
