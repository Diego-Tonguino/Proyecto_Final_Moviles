package uta.edu.ec.proyecto_final_moviles

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uta.edu.ec.proyecto_final_moviles.api.ApiClient
import uta.edu.ec.proyecto_final_moviles.models.Product
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class HomeActivity : AppCompatActivity() {

    private lateinit var rvProducts: RecyclerView
    private lateinit var pbHome: ProgressBar
    private lateinit var tvUserName: TextView
    private lateinit var btnLogout: MaterialButton
    private lateinit var etSearch: EditText
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // Ocultar barra de estado y navegación (Efecto inmersivo)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }


        supportActionBar?.hide()

        // Vincular vistas
        rvProducts = findViewById(R.id.rvProducts)
        pbHome = findViewById(R.id.pbHome)
        tvUserName = findViewById(R.id.tvUserName)
        btnLogout = findViewById(R.id.btnLogout)
        etSearch = findViewById(R.id.etSearch)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Mostrar nombre del usuario guardado
        val prefs = getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "Usuario")
        tvUserName.text = userName

        // Configurar RecyclerView
        rvProducts.layoutManager = GridLayoutManager(this, 2)
        adapter = ProductAdapter(emptyList(), 
            onProductClick = { product ->
                Toast.makeText(this, "${product.name}", Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = { product, icon ->
                product.isFavorite = !product.isFavorite
                if (product.isFavorite) {
                    icon.setImageResource(android.R.drawable.btn_star_big_on)
                    icon.setColorFilter(getColor(android.R.color.holo_orange_light))
                } else {
                    icon.setImageResource(android.R.drawable.btn_star_big_off)
                    icon.setColorFilter(getColor(android.R.color.white))
                }
            }
        )
        rvProducts.adapter = adapter

        // Filtro de búsqueda
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Navegación inferior
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_favorites -> {
                    Toast.makeText(this, "Favoritos próximamente", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_cart -> {
                    Toast.makeText(this, "Carrito próximamente", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_history -> {
                    Toast.makeText(this, "Historial próximamente", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        btnLogout.setOnClickListener {
            mostrarConfirmacionCerrarSesion()
        }

        cargarProductos()
    }

    private fun cargarProductos() {
        pbHome.visibility = View.VISIBLE
        
        ApiClient.apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                pbHome.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val products = response.body()!!
                    adapter.updateList(products)
                } else {
                    Toast.makeText(this@HomeActivity, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                pbHome.visibility = View.GONE
                Toast.makeText(this@HomeActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarConfirmacionCerrarSesion() {
        // Usamos un Dialog normal en lugar de un BottomSheet
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_logout, null)
        dialog.setContentView(view)

        // Hacemos el fondo de la ventana transparente para que se vean los bordes redondeados
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Limitamos el ancho al 85% de la pantalla para que se vea más pequeño y centrado
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog.window?.setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)

        view.findViewById<View>(R.id.btnCancelLogout).setOnClickListener {
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnConfirmLogout).setOnClickListener {
            dialog.dismiss()
            cerrarSesion()
        }

        dialog.show()
    }


    private fun cerrarSesion() {
        val prefs = getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
