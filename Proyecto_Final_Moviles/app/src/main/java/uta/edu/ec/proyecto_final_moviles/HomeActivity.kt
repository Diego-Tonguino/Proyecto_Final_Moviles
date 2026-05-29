package uta.edu.ec.proyecto_final_moviles

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uta.edu.ec.proyecto_final_moviles.api.ApiClient
import uta.edu.ec.proyecto_final_moviles.models.Product

class HomeActivity : AppCompatActivity() {

    private lateinit var rvProducts: RecyclerView
    private lateinit var pbHome: ProgressBar
    private lateinit var tvUserName: TextView
    private lateinit var btnLogout: MaterialButton
    private lateinit var etSearch: EditText
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var adapter: ProductAdapter
    private lateinit var swipeRefresh: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    private lateinit var filterChipsScroll: HorizontalScrollView
    private lateinit var filterChipsContainer: LinearLayout
    private lateinit var btnFilter: MaterialCardView

    private var filterMinPrice: Double? = null
    private var filterMaxPrice: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { c ->
            c.hide(WindowInsetsCompat.Type.systemBars())
            c.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        supportActionBar?.hide()

        rvProducts = findViewById(R.id.rvProducts)
        pbHome = findViewById(R.id.pbHome)
        tvUserName = findViewById(R.id.tvUserName)
        btnLogout = findViewById(R.id.btnLogout)
        etSearch = findViewById(R.id.etSearch)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        filterChipsScroll = findViewById(R.id.filterChipsScroll)
        filterChipsContainer = findViewById(R.id.filterChipsContainer)
        btnFilter = findViewById(R.id.btnFilter)

        swipeRefresh.setOnRefreshListener { cargarProductos() }

        val prefs = getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE)
        tvUserName.text = prefs.getString("user_name", "Usuario")

        rvProducts.layoutManager = GridLayoutManager(this, 2)
        adapter = ProductAdapter(emptyList(),
            onProductClick = { product ->
                val intent = Intent(this@HomeActivity, ProductDetailActivity::class.java).apply {
                    putExtra("product_id", product.id)
                    putExtra("product_name", product.name)
                    putExtra("product_description", product.description)
                    putExtra("product_price", product.unitPrice)
                    putExtra("product_stock", product.unitsInStock.toInt())
                    putExtra("product_image", product.imageData)
                }
                startActivity(intent)
            }
        )
        rvProducts.adapter = adapter

        // Búsqueda por nombre en tiempo real
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { aplicarFiltros() }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnFilter.setOnClickListener { mostrarFiltros() }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_cart -> { startActivity(Intent(this, CartActivity::class.java)); true }
                R.id.nav_history -> { startActivity(Intent(this, HistoryActivity::class.java)); true }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivity::class.java)); true }
                else -> false
            }
        }

        btnLogout.setOnClickListener { mostrarConfirmacionCerrarSesion() }
        cargarProductos()
    }

    private fun mostrarFiltros() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_filters, null)
        dialog.setContentView(view)

        val slider = view.findViewById<com.google.android.material.slider.RangeSlider>(R.id.priceRangeSlider)
        val tvMin = view.findViewById<android.widget.TextView>(R.id.tvMinValue)
        val tvMax = view.findViewById<android.widget.TextView>(R.id.tvMaxValue)
        val btnApply = view.findViewById<MaterialButton>(R.id.btnApplyFilters)
        val btnClear = view.findViewById<MaterialButton>(R.id.btnClearFilters)

        // Configurar slider con el rango real de precios
        val globalMin = kotlin.math.floor(adapter.getMinPrice()).toFloat()
        val globalMax = kotlin.math.ceil(adapter.getMaxPrice()).toFloat()

        if (globalMin < globalMax) {
            slider.valueFrom = globalMin
            slider.valueTo = globalMax
        } else {
            slider.valueFrom = 0f
            slider.valueTo = (globalMax + 1f)
        }

        // Restaurar valores previos o usar el rango completo
        val currentMin = filterMinPrice?.toFloat() ?: slider.valueFrom
        val currentMax = filterMaxPrice?.toFloat() ?: slider.valueTo
        slider.values = listOf(
            currentMin.coerceIn(slider.valueFrom, slider.valueTo),
            currentMax.coerceIn(slider.valueFrom, slider.valueTo)
        )

        fun actualizarLabels(values: List<Float>) {
            tvMin.text = "$${values[0].toInt()}"
            tvMax.text = "$${values[1].toInt()}"
        }
        actualizarLabels(slider.values)

        slider.addOnChangeListener { _, _, _ ->
            actualizarLabels(slider.values)
        }

        btnApply.setOnClickListener {
            filterMinPrice = slider.values[0].toDouble()
            filterMaxPrice = slider.values[1].toDouble()
            actualizarChips()
            aplicarFiltros()
            dialog.dismiss()
        }
        btnClear.setOnClickListener {
            filterMinPrice = null; filterMaxPrice = null
            actualizarChips(); aplicarFiltros()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun aplicarFiltros() {
        adapter.filterFull(etSearch.text.toString(), filterMinPrice, filterMaxPrice)
    }

    private fun actualizarChips() {
        filterChipsContainer.removeAllViews()
        var hasChips = false
        if (filterMinPrice != null || filterMaxPrice != null) {
            hasChips = true
            val label = "Precio: $${filterMinPrice?.let { "%.0f".format(it) } ?: "0"} – $${filterMaxPrice?.let { "%.0f".format(it) } ?: "∞"}"
            filterChipsContainer.addView(crearChip(label) {
                filterMinPrice = null; filterMaxPrice = null
                actualizarChips(); aplicarFiltros()
            })
        }
        filterChipsScroll.visibility = if (hasChips) View.VISIBLE else View.GONE
    }

    private fun crearChip(text: String, onClose: () -> Unit): Chip {
        val chip = Chip(this)
        chip.text = text
        chip.isCloseIconVisible = true
        chip.setTextColor(Color.WHITE)
        chip.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#557c3aed"))
        chip.closeIconTint = ColorStateList.valueOf(Color.WHITE)
        chip.setOnCloseIconClickListener { onClose() }
        chip.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also { it.marginEnd = 8 }
        return chip
    }

    private fun cargarProductos() {
        pbHome.visibility = View.VISIBLE
        ApiClient.apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                pbHome.visibility = View.GONE
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateList(response.body()!!.filter { it.unitsInStock > 0 })
                } else {
                    val msg = "Error al cargar productos (HTTP ${response.code()})"
                    Toast.makeText(this@HomeActivity, msg, Toast.LENGTH_SHORT).show()
                    ErrorLogger.log(this@HomeActivity, msg, "HomeActivity")
                }
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                pbHome.visibility = View.GONE
                swipeRefresh.isRefreshing = false
                val msg = "Error de conexión: ${t.message}"
                Toast.makeText(this@HomeActivity, msg, Toast.LENGTH_SHORT).show()
                ErrorLogger.log(this@HomeActivity, msg, "HomeActivity")
            }
        })
    }

    private fun mostrarConfirmacionCerrarSesion() {
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_logout, null)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog.window?.setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
        view.findViewById<View>(R.id.btnCancelLogout).setOnClickListener { dialog.dismiss() }
        view.findViewById<View>(R.id.btnConfirmLogout).setOnClickListener { dialog.dismiss(); cerrarSesion() }
        dialog.show()
    }

    private fun cerrarSesion() {
        getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE).edit().clear().apply()
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
