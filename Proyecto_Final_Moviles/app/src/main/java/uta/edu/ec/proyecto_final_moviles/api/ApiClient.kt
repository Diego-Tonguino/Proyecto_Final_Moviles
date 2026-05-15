package uta.edu.ec.proyecto_final_moviles.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Restauramos a localhost que es la configuración que te funcionaba con adb reverse
    private const val BASE_URL = "http://localhost:5033/"

    val apiService: NorthwindApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NorthwindApi::class.java)
    }
}
