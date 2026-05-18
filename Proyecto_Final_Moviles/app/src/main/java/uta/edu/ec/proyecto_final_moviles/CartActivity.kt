package uta.edu.ec.proyecto_final_moviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uta.edu.ec.proyecto_final_moviles.api.ApiClient
import uta.edu.ec.proyecto_final_moviles.models.CartManager
import uta.edu.ec.proyecto_final_moviles.models.CreateOrderDetailRequest
import uta.edu.ec.proyecto_final_moviles.models.CreateOrderRequest

class CartActivity : AppCompatActivity() {

    private lateinit var rvCartItems: RecyclerView
    private lateinit var tvEmptyCart: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvIva: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var pbCart: ProgressBar
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var adapter: CartAdapter
    private lateinit var etCartAddress: EditText
    private lateinit var etCartPostalCode: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Ocultar barra de estado y navegación (Efecto inmersivo)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        supportActionBar?.hide()

        rvCartItems = findViewById(R.id.rvCartItems)
        tvEmptyCart = findViewById(R.id.tvEmptyCart)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvIva = findViewById(R.id.tvIva)
        tvTotal = findViewById(R.id.tvTotal)
        btnCheckout = findViewById(R.id.btnCheckout)
        pbCart = findViewById(R.id.pbCart)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        etCartAddress = findViewById(R.id.etCartAddress)
        etCartPostalCode = findViewById(R.id.etCartPostalCode)

        val prefs = getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE)
        etCartAddress.setText(prefs.getString("user_address", ""))
        etCartPostalCode.setText(prefs.getString("user_postal_code", ""))

        bottomNavigation.selectedItemId = R.id.nav_cart
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_cart -> true
                R.id.nav_history -> {
                    val intent = Intent(this@CartActivity, HistoryActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        setupRecyclerView()
        updateTotals()

        btnCheckout.setOnClickListener {
            if (CartManager.items.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            } else {
                procesarCompra()
            }
        }
    }

    private fun setupRecyclerView() {
        rvCartItems.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(
            cartItems = CartManager.items,
            onQuantityChange = { item, newQuantity ->
                CartManager.updateQuantity(item.product.id, newQuantity)
                adapter.notifyDataSetChanged()
                updateTotals()
            },
            onRemoveItem = { item ->
                CartManager.removeProduct(item.product.id)
                adapter.updateData(CartManager.items)
                updateTotals()
            }
        )
        rvCartItems.adapter = adapter
    }

    private fun updateTotals() {
        if (CartManager.items.isEmpty()) {
            tvEmptyCart.visibility = View.VISIBLE
            rvCartItems.visibility = View.GONE
        } else {
            tvEmptyCart.visibility = View.GONE
            rvCartItems.visibility = View.VISIBLE
        }

        tvSubtotal.text = "$${String.format("%.2f", CartManager.getSubtotal())}"
        tvIva.text = "$${String.format("%.2f", CartManager.getIva())}"
        tvTotal.text = "$${String.format("%.2f", CartManager.getTotal())}"
    }

    private fun procesarCompra() {
        pbCart.visibility = View.VISIBLE
        btnCheckout.isEnabled = false

        // 1. Validar el stock actual de los productos en el carrito contra el servidor
        ApiClient.apiService.getProducts().enqueue(object : Callback<List<uta.edu.ec.proyecto_final_moviles.models.Product>> {
            override fun onResponse(call: Call<List<uta.edu.ec.proyecto_final_moviles.models.Product>>, response: Response<List<uta.edu.ec.proyecto_final_moviles.models.Product>>) {
                if (response.isSuccessful && response.body() != null) {
                    val serverProducts = response.body()!!
                    var valid = true
                    var errorMsg = ""

                    for (item in CartManager.items.toList()) {
                        val productServer = serverProducts.find { it.id == item.product.id }
                        if (productServer == null) {
                            valid = false
                            errorMsg = "Ups! El producto '${item.product.name}' ya no existe en la tienda."
                            CartManager.removeProduct(item.product.id)
                            break
                        } else if (productServer.unitsInStock < item.quantity) {
                            valid = false
                            errorMsg = "Ups! Solo quedan ${productServer.unitsInStock} unidades de '${item.product.name}'."
                            // Actualizamos a lo máximo disponible
                            if (productServer.unitsInStock > 0) {
                                CartManager.updateQuantity(item.product.id, productServer.unitsInStock)
                            } else {
                                CartManager.removeProduct(item.product.id)
                            }
                            break
                        }
                    }

                    if (!valid) {
                        pbCart.visibility = View.GONE
                        btnCheckout.isEnabled = true
                        adapter.updateData(CartManager.items)
                        updateTotals()
                        Toast.makeText(this@CartActivity, errorMsg, Toast.LENGTH_LONG).show()
                    } else {
                        // 2. Si el stock está bien, validamos el cliente
                        validarClienteYEnviarOrden()
                    }
                } else {
                    pbCart.visibility = View.GONE
                    btnCheckout.isEnabled = true
                    Toast.makeText(this@CartActivity, "Error al validar disponibilidad de productos.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<uta.edu.ec.proyecto_final_moviles.models.Product>>, t: Throwable) {
                pbCart.visibility = View.GONE
                btnCheckout.isEnabled = true
                Toast.makeText(this@CartActivity, "Error de red al verificar stock.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validarClienteYEnviarOrden() {
        val prefs = getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE)
        val customerId = prefs.getString("user_id", "") ?: ""

        // Si no hay ID o el ID es un GUID (largo > 5), buscamos el ID real de 5 letras
        if (customerId.isEmpty() || customerId.length > 5) {
            val userEmail = prefs.getString("user_email", "") ?: ""
            if (userEmail.isEmpty()) {
                pbCart.visibility = View.GONE
                btnCheckout.isEnabled = true
                Toast.makeText(this, "Error: No se encontró el correo del usuario.", Toast.LENGTH_SHORT).show()
                return
            }

            ApiClient.apiService.getAllCustomers().enqueue(object : Callback<List<uta.edu.ec.proyecto_final_moviles.models.CustomerProfile>> {
                override fun onResponse(call: Call<List<uta.edu.ec.proyecto_final_moviles.models.CustomerProfile>>, response: Response<List<uta.edu.ec.proyecto_final_moviles.models.CustomerProfile>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val customer = response.body()!!.find { it.email == userEmail }
                        if (customer != null) {
                            prefs.edit().putString("user_id", customer.id).apply()
                            enviarOrden(customer.id)
                        } else {
                            pbCart.visibility = View.GONE
                            btnCheckout.isEnabled = true
                            Toast.makeText(this@CartActivity, "Error: No se encontró el perfil del cliente.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        pbCart.visibility = View.GONE
                        btnCheckout.isEnabled = true
                        Toast.makeText(this@CartActivity, "Error al buscar cliente.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<uta.edu.ec.proyecto_final_moviles.models.CustomerProfile>>, t: Throwable) {
                    pbCart.visibility = View.GONE
                    btnCheckout.isEnabled = true
                    Toast.makeText(this@CartActivity, "Error de red: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            enviarOrden(customerId)
        }
    }

    private fun enviarOrden(customerId: String) {
        val prefs = getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE)
        val shipCity = prefs.getString("user_city", "") ?: ""
        val shipCountry = prefs.getString("user_country", "Ecuador") ?: "Ecuador"
        
        val shipAddress = etCartAddress.text.toString().trim()
        val shipPostalCode = etCartPostalCode.text.toString().trim()

        if (shipAddress.isEmpty() || shipPostalCode.isEmpty()) {
            pbCart.visibility = View.GONE
            btnCheckout.isEnabled = true
            Toast.makeText(this, "Por favor ingrese Dirección y Código Postal", Toast.LENGTH_SHORT).show()
            return
        }

        // Guardar para futuros usos
        prefs.edit().apply {
            putString("user_address", shipAddress)
            putString("user_postal_code", shipPostalCode)
        }.apply()

        val orderDetails = CartManager.items.map {
            CreateOrderDetailRequest(
                productId = it.product.id,
                unitPrice = it.product.unitPrice,
                quantity = it.quantity
            )
        }

        val request = CreateOrderRequest(
            customerId = customerId,
            shipAddress = shipAddress.ifEmpty { "N/A" },
            shipCity = shipCity.ifEmpty { "N/A" },
            shipCountry = shipCountry.ifEmpty { "Ecuador" },
            shipPostalCode = shipPostalCode.ifEmpty { "000000" },
            orderDetails = orderDetails
        )

        pbCart.visibility = View.VISIBLE
        btnCheckout.isEnabled = false

        ApiClient.apiService.createOrder(request).enqueue(object : Callback<okhttp3.ResponseBody> {
            override fun onResponse(call: Call<okhttp3.ResponseBody>, response: Response<okhttp3.ResponseBody>) {
                pbCart.visibility = View.GONE
                btnCheckout.isEnabled = true

                if (response.isSuccessful) {
                    Toast.makeText(this@CartActivity, "¡Orden creada exitosamente!", Toast.LENGTH_LONG).show()
                    CartManager.clearCart()
                    startActivity(Intent(this@CartActivity, HomeActivity::class.java))
                    finish()
                } else if (response.code() == 401) {
                    Toast.makeText(this@CartActivity, "Sesión expirada. Por favor, cierra sesión en tu perfil y vuelve a ingresar.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@CartActivity, "Error al crear la orden: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
                pbCart.visibility = View.GONE
                btnCheckout.isEnabled = true
                Toast.makeText(this@CartActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
