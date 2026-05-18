package uta.edu.ec.proyecto_final_moviles

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uta.edu.ec.proyecto_final_moviles.api.ApiClient
import uta.edu.ec.proyecto_final_moviles.models.RegisterCustomerRequest
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private lateinit var etRegCedula: TextInputEditText
    private lateinit var etRegNombre: TextInputEditText
    private lateinit var etRegApellido: TextInputEditText
    private lateinit var etRegEmail: TextInputEditText
    private lateinit var etRegPhone: TextInputEditText
    private lateinit var etRegCity: TextInputEditText
    private lateinit var etRegCountry: TextInputEditText
    private lateinit var etRegPostalCode: TextInputEditText
    private lateinit var etRegAddress: TextInputEditText
    private lateinit var etRegPassword: TextInputEditText
    private lateinit var etRegConfirmPassword: TextInputEditText

    private lateinit var tilRegCedula: TextInputLayout
    private lateinit var tilRegNombre: TextInputLayout
    private lateinit var tilRegApellido: TextInputLayout
    private lateinit var tilRegEmail: TextInputLayout
    private lateinit var tilRegPhone: TextInputLayout
    private lateinit var tilRegCity: TextInputLayout
    private lateinit var tilRegCountry: TextInputLayout
    private lateinit var tilRegPostalCode: TextInputLayout
    private lateinit var tilRegAddress: TextInputLayout
    private lateinit var tilRegPassword: TextInputLayout
    private lateinit var tilRegConfirmPassword: TextInputLayout

    private lateinit var tvProvincia: TextView
    private lateinit var btnRegister: Button
    private lateinit var btnNext: Button
    private lateinit var tvBackStep: TextView
    private lateinit var tvRegSubtitle: TextView
    private lateinit var pbRegister: ProgressBar
    
    private lateinit var layoutStep1: LinearLayout
    private lateinit var layoutStep2: LinearLayout
    private lateinit var layoutStep3: LinearLayout

    private var currentStep = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        // Vincular vistas
        etRegCedula = findViewById(R.id.etRegCedula)
        etRegNombre = findViewById(R.id.etRegNombre)
        etRegApellido = findViewById(R.id.etRegApellido)
        etRegEmail = findViewById(R.id.etRegEmail)
        etRegPhone = findViewById(R.id.etRegPhone)
        etRegCity = findViewById(R.id.etRegCity)
        etRegCountry = findViewById(R.id.etRegCountry)
        etRegPostalCode = findViewById(R.id.etRegPostalCode)
        etRegAddress = findViewById(R.id.etRegAddress)
        etRegPassword = findViewById(R.id.etRegPassword)
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword)

        tilRegCedula = findViewById(R.id.tilRegCedula)
        tilRegNombre = findViewById(R.id.tilRegNombre)
        tilRegApellido = findViewById(R.id.tilRegApellido)
        tilRegEmail = findViewById(R.id.tilRegEmail)
        tilRegPhone = findViewById(R.id.tilRegPhone)
        tilRegCity = findViewById(R.id.tilRegCity)
        tilRegCountry = findViewById(R.id.tilRegCountry)
        tilRegPostalCode = findViewById(R.id.tilRegPostalCode)
        tilRegAddress = findViewById(R.id.tilRegAddress)
        tilRegPassword = findViewById(R.id.tilRegPassword)
        tilRegConfirmPassword = findViewById(R.id.tilRegConfirmPassword)

        tvProvincia = findViewById(R.id.tvProvincia)
        btnRegister = findViewById(R.id.btnRegister)
        btnNext = findViewById(R.id.btnNext)
        tvBackStep = findViewById(R.id.tvBackStep)
        tvRegSubtitle = findViewById(R.id.tvRegSubtitle)
        pbRegister = findViewById(R.id.pbRegister)
        
        layoutStep1 = findViewById(R.id.layoutStep1)
        layoutStep2 = findViewById(R.id.layoutStep2)
        layoutStep3 = findViewById(R.id.layoutStep3)

        val tvBackToLogin = findViewById<TextView>(R.id.tvBackToLogin)

        setupInputFilters()
        setupTextWatchers()

        btnNext.setOnClickListener {
            avanzarPaso()
        }

        tvBackStep.setOnClickListener {
            retrocederPaso()
        }

        btnRegister.setOnClickListener {
            registrarFinal()
        }

        tvBackToLogin?.setOnClickListener {
            finish()
        }

        actualizarUI(false)
    }

    private fun setupInputFilters() {
        val strictFilter = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                val type = Character.getType(source[i].code)
                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt()) return@InputFilter ""
            }
            null
        }

        val noSpace = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) if (source[i] == ' ') return@InputFilter ""
            null
        }

        val lettersOnlyNoSpace = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (!Character.isLetter(source[i])) return@InputFilter ""
            }
            null
        }

        val cityFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val sb = StringBuilder()
            for (i in start until end) {
                val c = source[i]
                if (Character.isLetter(c) || c == ' ') {
                    if (c == ' ') {
                        if (dstart == 0 && i == start) continue
                        if (dstart > 0 && dest[dstart - 1] == ' ') continue
                    }
                    sb.append(c)
                }
            }
            if (source.isNotEmpty() && sb.isEmpty()) "" else null
        }

        val digitsOnly = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) if (!source[i].isDigit()) return@InputFilter ""
            null
        }

        etRegCedula.filters = arrayOf(strictFilter, noSpace, digitsOnly, InputFilter.LengthFilter(10))
        etRegPhone.filters = arrayOf(strictFilter, noSpace, digitsOnly, InputFilter.LengthFilter(10))
        etRegNombre.filters = arrayOf(strictFilter, lettersOnlyNoSpace, InputFilter.LengthFilter(15))
        etRegApellido.filters = arrayOf(strictFilter, lettersOnlyNoSpace, InputFilter.LengthFilter(15))
        etRegCity.filters = arrayOf(strictFilter, cityFilter, InputFilter.LengthFilter(20))
        etRegCountry.filters = arrayOf(strictFilter, cityFilter, InputFilter.LengthFilter(20))
        etRegPostalCode.filters = arrayOf(strictFilter, noSpace, digitsOnly, InputFilter.LengthFilter(5))
        etRegAddress.filters = arrayOf(strictFilter, InputFilter.LengthFilter(30))
        etRegEmail.filters = arrayOf(strictFilter, noSpace, InputFilter.LengthFilter(35))
        etRegPassword.filters = arrayOf(strictFilter, noSpace, InputFilter.LengthFilter(16))
        etRegConfirmPassword.filters = arrayOf(strictFilter, noSpace, InputFilter.LengthFilter(16))
    }

    private fun setupTextWatchers() {
        // Watcher para la cédula y provincia
        etRegCedula.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val cedula = s.toString()
                if (cedula.length >= 2) {
                    val provincia = obtenerProvincia(cedula)
                    if (provincia != null) {
                        tvProvincia.text = "Provincia: $provincia"
                        tvProvincia.visibility = View.VISIBLE
                        tvProvincia.setTextColor(getColor(android.R.color.holo_green_light))
                    } else {
                        tvProvincia.text = "Código de provincia inválido"
                        tvProvincia.visibility = View.VISIBLE
                        tvProvincia.setTextColor(getColor(android.R.color.holo_red_light))
                    }
                } else {
                    tvProvincia.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val capitalizationWatcher = object : TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting || s.isNullOrEmpty()) return
                isFormatting = true
                val original = s.toString()
                val formatted = original.split(" ").joinToString(" ") { word ->
                    word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                }
                if (formatted != original) s.replace(0, s.length, formatted)
                isFormatting = false
            }
        }
        etRegNombre.addTextChangedListener(capitalizationWatcher)
        etRegApellido.addTextChangedListener(capitalizationWatcher)
        etRegCity.addTextChangedListener(capitalizationWatcher)
        etRegCountry.addTextChangedListener(capitalizationWatcher)

        // Valor por defecto para País
        etRegCountry.setText("Ecuador")

        val inputs = listOf(etRegCedula, etRegNombre, etRegApellido, etRegEmail, etRegPhone, etRegCity, etRegCountry, etRegPostalCode, etRegAddress, etRegPassword, etRegConfirmPassword)
        val layouts = listOf(tilRegCedula, tilRegNombre, tilRegApellido, tilRegEmail, tilRegPhone, tilRegCity, tilRegCountry, tilRegPostalCode, tilRegAddress, tilRegPassword, tilRegConfirmPassword)
        
        val generalWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                layouts.forEach { it.error = null }
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        inputs.forEach { it.addTextChangedListener(generalWatcher) }
    }

    private fun obtenerProvincia(cedula: String): String? {
        if (cedula.length < 2) return null
        val codigo = cedula.substring(0, 2).toIntOrNull() ?: return null
        return when (codigo) {
            1 -> "Azuay"
            2 -> "Bolívar"
            3 -> "Cañar"
            4 -> "Carchi"
            5 -> "Cotopaxi"
            6 -> "Chimborazo"
            7 -> "El Oro"
            8 -> "Esmeraldas"
            9 -> "Guayas"
            10 -> "Imbabura"
            11 -> "Loja"
            12 -> "Los Ríos"
            13 -> "Manabí"
            14 -> "Morona Santiago"
            15 -> "Napo"
            16 -> "Pastaza"
            17 -> "Pichincha"
            18 -> "Tungurahua"
            19 -> "Zamora Chinchipe"
            20 -> "Galápagos"
            21 -> "Sucumbíos"
            22 -> "Orellana"
            23 -> "Santo Domingo de los Tsáchilas"
            24 -> "Santa Elena"
            30 -> "Extranjero"
            else -> null
        }
    }

    private fun validarCedulaEcuador(cedula: String): Boolean {
        if (cedula.length != 10) return false
        val codigoProvincia = cedula.substring(0, 2).toIntOrNull() ?: return false
        if (codigoProvincia < 1 || (codigoProvincia > 24 && codigoProvincia != 30)) return false
        
        val tercerDigito = cedula[2].toString().toInt()
        if (tercerDigito >= 6) return false
        
        val coeficientes = intArrayOf(2, 1, 2, 1, 2, 1, 2, 1, 2)
        val verificador = cedula[9].toString().toInt()
        var suma = 0
        
        for (i in 0 until 9) {
            var valor = cedula[i].toString().toInt() * coeficientes[i]
            if (valor > 9) valor -= 9
            suma += valor
        }
        
        val total = suma % 10
        val resultado = if (total == 0) 0 else 10 - total
        
        return resultado == verificador
    }

    private fun avanzarPaso() {
        when (currentStep) {
            1 -> {
                val cedula = etRegCedula.text.toString().trim()
                val nombre = etRegNombre.text.toString().trim()
                val apellido = etRegApellido.text.toString().trim()

                if (!validarCedulaEcuador(cedula)) {
                    tilRegCedula.error = "Cédula ecuatoriana inválida"
                    return
                }
                if (nombre.isEmpty()) { tilRegNombre.error = "Campo obligatorio"; return }
                if (apellido.isEmpty()) { tilRegApellido.error = "Campo obligatorio"; return }

                btnNext.isEnabled = false
                btnNext.text = "Verificando..."
                
                ApiClient.apiService.checkCedula(cedula).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        btnNext.isEnabled = true
                        btnNext.text = "Siguiente"
                        if (response.code() == 409) {
                            tilRegCedula.error = "Cédula ya registrada"
                        } else if (response.isSuccessful) {
                            currentStep++
                            actualizarUI(true)
                        } else {
                            tilRegCedula.error = "Error al verificar"
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        btnNext.isEnabled = true
                        btnNext.text = "Siguiente"
                        Toast.makeText(this@RegisterActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            2 -> {
                val email = etRegEmail.text.toString().trim()
                val phone = etRegPhone.text.toString().trim()
                val city = etRegCity.text.toString().trim()
                val country = etRegCountry.text.toString().trim()
                val postalCode = etRegPostalCode.text.toString().trim()
                val address = etRegAddress.text.toString().trim()

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    tilRegEmail.error = "Email inválido"
                    return
                }
                if (phone.length != 10 || !phone.startsWith("09")) {
                    tilRegPhone.error = "Formato 09xxxxxxxx"
                    return
                }
                if (city.isEmpty()) { tilRegCity.error = "Campo obligatorio"; return }
                if (country.isEmpty()) { tilRegCountry.error = "Campo obligatorio"; return }
                if (postalCode.isEmpty()) { tilRegPostalCode.error = "Campo obligatorio"; return }
                if (address.isEmpty()) { tilRegAddress.error = "Campo obligatorio"; return }

                btnNext.isEnabled = false
                btnNext.text = "Verificando..."
                ApiClient.apiService.checkEmail(email).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.code() == 409) {
                            btnNext.isEnabled = true
                            btnNext.text = "Siguiente"
                            tilRegEmail.error = "Email en uso"
                        } else {
                            ApiClient.apiService.checkPhone(phone).enqueue(object : Callback<ResponseBody> {
                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                    btnNext.isEnabled = true
                                    btnNext.text = "Siguiente"
                                    if (response.code() == 409) {
                                        tilRegPhone.error = "Teléfono registrado"
                                    } else if (response.isSuccessful) {
                                        currentStep++
                                        actualizarUI(true)
                                    } else {
                                        tilRegPhone.error = "Error al verificar"
                                    }
                                }
                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                    btnNext.isEnabled = true
                                    btnNext.text = "Siguiente"
                                    Toast.makeText(this@RegisterActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        btnNext.isEnabled = true
                        btnNext.text = "Siguiente"
                        Toast.makeText(this@RegisterActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun retrocederPaso() {
        if (currentStep > 1) {
            currentStep--
            actualizarUI(true)
        }
    }

    private fun actualizarUI(animate: Boolean) {
        layoutStep1.visibility = if (currentStep == 1) View.VISIBLE else View.GONE
        layoutStep2.visibility = if (currentStep == 2) View.VISIBLE else View.GONE
        layoutStep3.visibility = if (currentStep == 3) View.VISIBLE else View.GONE
        
        tvBackStep.visibility = if (currentStep > 1) View.VISIBLE else View.GONE
        btnNext.visibility = if (currentStep < 3) View.VISIBLE else View.GONE
        btnRegister.visibility = if (currentStep == 3) View.VISIBLE else View.GONE
        
        val targetProgress = when (currentStep) {
            1 -> 33
            2 -> 66
            3 -> 100
            else -> 0
        }

        if (animate) {
            val animation = ObjectAnimator.ofInt(pbRegister, "progress", pbRegister.progress, targetProgress)
            animation.duration = 500
            animation.interpolator = DecelerateInterpolator()
            animation.start()
        } else {
            pbRegister.progress = targetProgress
        }
        
        when (currentStep) {
            1 -> { tvRegSubtitle.text = "Paso 1 de 3: Identidad" }
            2 -> { tvRegSubtitle.text = "Paso 2 de 3: Contacto" }
            3 -> { tvRegSubtitle.text = "Paso 3 de 3: Seguridad" }
        }
    }

    private fun registrarFinal() {
        val pass = etRegPassword.text.toString()
        val conf = etRegConfirmPassword.text.toString()

        val passwordError = validatePassword(pass)
        if (passwordError != null) {
            tilRegPassword.error = passwordError
            return
        }
        
        if (pass != conf) {
            tilRegConfirmPassword.error = "No coinciden"
            return
        }

        btnRegister.isEnabled = false
        btnRegister.text = "Creando cuenta..."

        val request = RegisterCustomerRequest(
            cedula = etRegCedula.text.toString().trim(),
            firstName = etRegNombre.text.toString().trim(),
            lastName = etRegApellido.text.toString().trim(),
            email = etRegEmail.text.toString().trim(),
            phone = etRegPhone.text.toString().trim(),
            city = etRegCity.text.toString().trim(),
            country = etRegCountry.text.toString().trim(),
            postalCode = etRegPostalCode.text.toString().trim(),
            address = etRegAddress.text.toString().trim(),
            password = pass,
            passwordConfirm = conf
        )

        ApiClient.apiService.registerCustomer(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                btnRegister.isEnabled = true
                btnRegister.text = "Crear Cuenta"
                if (response.isSuccessful) {
                    // Guardar datos de envío localmente para usarlos en la orden
                    val prefs = getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE)
                    prefs.edit().apply {
                        putString("user_city", request.city)
                        putString("user_country", request.country)
                        putString("user_postal_code", request.postalCode)
                        putString("user_address", request.address)
                        putBoolean("just_registered", true)
                    }.apply()
                    Toast.makeText(this@RegisterActivity, "¡Cuenta creada exitosamente!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Error al registrar", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                btnRegister.isEnabled = true
                btnRegister.text = "Crear Cuenta"
                Toast.makeText(this@RegisterActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validatePassword(p: String): String? {
        if (p.length < 6) return "Mínimo 6 caracteres"
        if (!p.any { it.isUpperCase() }) return "Falta 1 mayúscula"
        if (!p.any { it.isLowerCase() }) return "Falta 1 minúscula"
        if (!p.any { it.isDigit() }) return "Falta 1 número"
        if (!p.any { "!@#$%^&*".contains(it) }) return "Falta 1 carácter especial"
        return null
    }
}
