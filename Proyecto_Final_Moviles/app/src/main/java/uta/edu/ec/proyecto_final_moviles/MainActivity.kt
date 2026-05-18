package uta.edu.ec.proyecto_final_moviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uta.edu.ec.proyecto_final_moviles.api.ApiClient
import uta.edu.ec.proyecto_final_moviles.models.LoginRequest
import uta.edu.ec.proyecto_final_moviles.models.LoginResponse
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvError: TextView
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val prefs = getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE)
        // Auto-login ha sido removido a petición del usuario.
        // Siempre se pedirá inicio de sesión al abrir la aplicación.

        setContentView(R.layout.activity_main)

        // Ocultar barra de estado y navegación (Efecto inmersivo)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        supportActionBar?.hide()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvError = findViewById(R.id.tvError)
        tvRegister = findViewById(R.id.tvRegister)

        setupInputFilters()
        setupTextWatchers()

        btnLogin.setOnClickListener {
            realizarLogin()
        }

        tvRegister.setOnClickListener {
            limpiarCampos()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupInputFilters() {
        val spaceFilter = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (source[i] == ' ') return@InputFilter ""
            }
            null
        }
        etEmail.filters = arrayOf(spaceFilter, InputFilter.LengthFilter(35))
        etPassword.filters = arrayOf(InputFilter.LengthFilter(16))
    }

    private fun setupTextWatchers() {
        val clearErrorWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                etEmail.setBackgroundResource(R.drawable.bg_input)
                etPassword.setBackgroundResource(R.drawable.bg_input)
                tvError.visibility = View.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        etEmail.addTextChangedListener(clearErrorWatcher)
        etPassword.addTextChangedListener(clearErrorWatcher)
    }

    override fun onResume() {
        super.onResume()
        limpiarCampos()
    }

    private fun limpiarCampos() {
        if (::etEmail.isInitialized) etEmail.text.clear()
        if (::etPassword.isInitialized) etPassword.text.clear()
        if (::tvError.isInitialized) tvError.visibility = View.GONE
    }

    private fun realizarLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) etEmail.setBackgroundResource(R.drawable.bg_input_error)
            if (password.isEmpty()) etPassword.setBackgroundResource(R.drawable.bg_input_error)
            mostrarError("Por favor, ingresa tu correo y contraseña.")
            return
        }

        tvError.visibility = View.GONE
        btnLogin.isEnabled = false
        btnLogin.text = "Verificando..."

        val request = LoginRequest(email = email, password = password)

        ApiClient.apiService.loginCustomer(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                btnLogin.isEnabled = true
                btnLogin.text = "Iniciar sesión"

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    val prefs = getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE)
                    val editor = prefs.edit()
                    editor.putString("northwind_token", loginResponse.token)
                    editor.putString("user_email", email) // Guardamos el email usado para iniciar sesión
                    if (loginResponse.id != null) {
                        editor.putString("user_id", loginResponse.id)
                    }
                    editor.putString("user_name", "${loginResponse.firstName ?: ""} ${loginResponse.lastName ?: ""}".trim())
                    
                    if (!prefs.getBoolean("just_registered", false)) {
                        editor.remove("user_address")
                        editor.remove("user_postal_code")
                    }
                    editor.remove("just_registered")
                    editor.apply()

                    ApiClient.authToken = loginResponse.token

                    // Obtener perfil completo del cliente para guardar datos de envío
                    val customerId = loginResponse.id
                    if (customerId != null) {
                        ApiClient.apiService.getCustomer(customerId).enqueue(object : Callback<uta.edu.ec.proyecto_final_moviles.models.CustomerProfile> {
                            override fun onResponse(call: Call<uta.edu.ec.proyecto_final_moviles.models.CustomerProfile>, response: Response<uta.edu.ec.proyecto_final_moviles.models.CustomerProfile>) {
                                if (response.isSuccessful && response.body() != null) {
                                    val profile = response.body()!!
                                    val editor = prefs.edit()
                                    editor.putString("user_id", profile.id)
                                    editor.putString("user_city", profile.city ?: "")
                                    
                                    // Solo sobrescribimos si la API devuelve un valor real,
                                    // de lo contrario conservamos lo que el usuario llenó en el registro.
                                    if (!profile.country.isNullOrEmpty()) {
                                        editor.putString("user_country", profile.country)
                                    } else if (!prefs.contains("user_country")) {
                                        editor.putString("user_country", "Ecuador")
                                    }
                                    
                                    if (!profile.postalCode.isNullOrEmpty()) {
                                        editor.putString("user_postal_code", profile.postalCode)
                                    }
                                    
                                    if (!profile.address.isNullOrEmpty()) {
                                        editor.putString("user_address", profile.address)
                                    }
                                    
                                    editor.apply()
                                }
                                Toast.makeText(this@MainActivity, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                                irAHome()
                            }
                            override fun onFailure(call: Call<uta.edu.ec.proyecto_final_moviles.models.CustomerProfile>, t: Throwable) {
                                // Si falla obtener el perfil, continuamos igual
                                Toast.makeText(this@MainActivity, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                                irAHome()
                            }
                        })
                    } else {
                        Toast.makeText(this@MainActivity, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                        irAHome()
                    }
                } else {
                    etEmail.setBackgroundResource(R.drawable.bg_input_error)
                    etPassword.setBackgroundResource(R.drawable.bg_input_error)
                    mostrarError("Correo o contraseña incorrectos.")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                btnLogin.isEnabled = true
                btnLogin.text = "Iniciar sesión"
                mostrarError("Error de conexión: ${t.localizedMessage}")
            }
        })
    }

    private fun irAHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun mostrarError(mensaje: String) {
        tvError.text = mensaje
        tvError.visibility = View.VISIBLE
    }
}
