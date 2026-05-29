package uta.edu.ec.proyecto_final_moviles

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import androidx.recyclerview.widget.LinearLayoutManager
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
import uta.edu.ec.proyecto_final_moviles.models.OrderHistoryResponse
import uta.edu.ec.proyecto_final_moviles.models.Product

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvOrderHistory: RecyclerView
    private lateinit var tvEmptyHistory: TextView
    private lateinit var pbHistory: ProgressBar
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var adapter: OrderHistoryAdapter
    private lateinit var etHistorySearch: EditText
    private lateinit var btnHistoryFilter: MaterialCardView
    private lateinit var historyFilterChipsScroll: HorizontalScrollView
    private lateinit var historyFilterChipsContainer: LinearLayout
    private var productNames: Map<Int, String> = emptyMap()

    // Estado filtros
    private var filterType: String = "order" // "order" | "product"
    private var filterDateFrom: String? = null
    private var filterDateTo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { c ->
            c.hide(WindowInsetsCompat.Type.systemBars())
            c.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        supportActionBar?.hide()

        rvOrderHistory = findViewById(R.id.rvOrderHistory)
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory)
        pbHistory = findViewById(R.id.pbHistory)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        etHistorySearch = findViewById(R.id.etHistorySearch)
        btnHistoryFilter = findViewById(R.id.btnHistoryFilter)
        historyFilterChipsScroll = findViewById(R.id.historyFilterChipsScroll)
        historyFilterChipsContainer = findViewById(R.id.historyFilterChipsContainer)

        bottomNavigation.selectedItemId = R.id.nav_history
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java)); finish(); true }
                R.id.nav_cart -> { startActivity(Intent(this, CartActivity::class.java)); finish(); true }
                R.id.nav_history -> true
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivity::class.java)); finish(); true }
                else -> false
            }
        }

        etHistorySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { aplicarFiltros() }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnHistoryFilter.setOnClickListener { mostrarFiltrosHistorial() }

        setupRecyclerView()
        loadProductsAndHistory()
    }

    private fun mostrarFiltrosHistorial() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_history_filters, null)
        dialog.setContentView(view)

        val btnByOrder = view.findViewById<MaterialButton>(R.id.btnFilterByOrder)
        val btnByProduct = view.findViewById<MaterialButton>(R.id.btnFilterByProduct)
        val btnPickFrom = view.findViewById<com.google.android.material.card.MaterialCardView>(R.id.btnPickDateFrom)
        val btnPickTo = view.findViewById<com.google.android.material.card.MaterialCardView>(R.id.btnPickDateTo)
        val tvDateFrom = view.findViewById<android.widget.TextView>(R.id.tvDateFrom)
        val tvDateTo = view.findViewById<android.widget.TextView>(R.id.tvDateTo)
        val btnApply = view.findViewById<MaterialButton>(R.id.btnApplyHistoryFilters)
        val btnClear = view.findViewById<MaterialButton>(R.id.btnClearHistoryFilters)

        // Estado interno del diálogo
        var tempDateFrom: String? = filterDateFrom
        var tempDateTo: String? = filterDateTo

        actualizarBotonesFilterType(btnByOrder, btnByProduct)
        tvDateFrom.text = filterDateFrom ?: "Seleccionar"
        tvDateTo.text = filterDateTo ?: "Seleccionar"

        btnByOrder.setOnClickListener {
            filterType = "order"; actualizarBotonesFilterType(btnByOrder, btnByProduct)
        }
        btnByProduct.setOnClickListener {
            filterType = "product"; actualizarBotonesFilterType(btnByOrder, btnByProduct)
        }

        // DatePicker Desde
        btnPickFrom.setOnClickListener {
            val cal = java.util.Calendar.getInstance()
            android.app.DatePickerDialog(
                this,
                android.R.style.Theme_Material_Dialog_MinWidth,
                { _, year, month, day ->
                    tempDateFrom = "%02d/%02d/%04d".format(day, month + 1, year)
                    tvDateFrom.text = tempDateFrom
                    tvDateFrom.setTextColor(android.graphics.Color.WHITE)
                },
                cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH),
                cal.get(java.util.Calendar.DAY_OF_MONTH)
            ).show()
        }

        // DatePicker Hasta
        btnPickTo.setOnClickListener {
            val cal = java.util.Calendar.getInstance()
            android.app.DatePickerDialog(
                this,
                android.R.style.Theme_Material_Dialog_MinWidth,
                { _, year, month, day ->
                    tempDateTo = "%02d/%02d/%04d".format(day, month + 1, year)
                    tvDateTo.text = tempDateTo
                    tvDateTo.setTextColor(android.graphics.Color.WHITE)
                },
                cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH),
                cal.get(java.util.Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnApply.setOnClickListener {
            filterDateFrom = tempDateFrom
            filterDateTo = tempDateTo
            actualizarChips()
            aplicarFiltros()
            dialog.dismiss()
        }
        btnClear.setOnClickListener {
            filterType = "order"
            filterDateFrom = null; filterDateTo = null
            tempDateFrom = null; tempDateTo = null
            actualizarChips(); aplicarFiltros()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun actualizarBotonesFilterType(btnOrder: MaterialButton, btnProduct: MaterialButton) {
        val purple = Color.parseColor("#7c3aed")
        val transparent = Color.parseColor("#33FFFFFF")
        if (filterType == "order") {
            btnOrder.backgroundTintList = ColorStateList.valueOf(purple)
            btnProduct.backgroundTintList = ColorStateList.valueOf(transparent)
        } else {
            btnOrder.backgroundTintList = ColorStateList.valueOf(transparent)
            btnProduct.backgroundTintList = ColorStateList.valueOf(purple)
        }
    }

    private fun aplicarFiltros() {
        val query = etHistorySearch.text.toString()
        adapter.filterFull(query, filterType, filterDateFrom, filterDateTo)
        // Mostrar mensaje vacío si no hay resultados
        tvEmptyHistory.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun actualizarChips() {
        historyFilterChipsContainer.removeAllViews()
        var hasChips = false

        // Chip tipo búsqueda
        val typeLabel = if (filterType == "product") "Buscar: Producto" else "Buscar: # Orden"
        historyFilterChipsContainer.addView(crearChipReadOnly(typeLabel))
        hasChips = true

        if (filterDateFrom != null || filterDateTo != null) {
            val dateLabel = "Fecha: ${filterDateFrom ?: "..."} → ${filterDateTo ?: "..."}"
            historyFilterChipsContainer.addView(crearChip(dateLabel) {
                filterDateFrom = null; filterDateTo = null
                actualizarChips(); aplicarFiltros()
            })
        }

        historyFilterChipsScroll.visibility = if (hasChips) View.VISIBLE else View.GONE
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
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).also { it.marginEnd = 8 }
        return chip
    }

    private fun crearChipReadOnly(text: String): Chip {
        val chip = Chip(this)
        chip.text = text
        chip.isCloseIconVisible = false
        chip.setTextColor(Color.WHITE)
        chip.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#335b21d7"))
        chip.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).also { it.marginEnd = 8 }
        return chip
    }

    private fun setupRecyclerView() {
        rvOrderHistory.layoutManager = LinearLayoutManager(this)
        adapter = OrderHistoryAdapter(emptyList(), emptyMap()) { order ->
            val intent = Intent(this, OrderDetailActivity::class.java).apply {
                putExtra("order_json", com.google.gson.Gson().toJson(order))
                putExtra("products_json", com.google.gson.Gson().toJson(productNames))
            }
            startActivity(intent)
        }
        rvOrderHistory.adapter = adapter
    }

    private fun loadProductsAndHistory() {
        pbHistory.visibility = View.VISIBLE
        tvEmptyHistory.visibility = View.GONE
        rvOrderHistory.visibility = View.GONE

        ApiClient.apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful && response.body() != null) {
                    productNames = response.body()!!.associate { it.id to it.name }
                }
                loadOrderHistory()
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) { loadOrderHistory() }
        })
    }

    private fun loadOrderHistory() {
        val prefs = getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE)
        val customerId = prefs.getString("user_id", "") ?: ""
        if (customerId.isEmpty()) {
            val msg = "Error: No se encontró el ID del cliente."
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            ErrorLogger.log(this, msg, "HistoryActivity", "WARNING")
            tvEmptyHistory.visibility = View.VISIBLE
            return
        }
        pbHistory.visibility = View.VISIBLE

        ApiClient.apiService.getOrdersByCustomer(customerId).enqueue(object : Callback<List<OrderHistoryResponse>> {
            override fun onResponse(call: Call<List<OrderHistoryResponse>>, response: Response<List<OrderHistoryResponse>>) {
                pbHistory.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val orders = response.body()!!
                    if (orders.isEmpty()) {
                        tvEmptyHistory.visibility = View.VISIBLE
                    } else {
                        rvOrderHistory.visibility = View.VISIBLE
                        adapter.updateList(orders.sortedByDescending { it.orderDate }, productNames)
                        actualizarChips() // mostrar chips iniciales (tipo búsqueda)
                    }
                } else {
                    tvEmptyHistory.visibility = View.VISIBLE
                    val msg = "Error al cargar el historial (HTTP ${response.code()})"
                    Toast.makeText(this@HistoryActivity, msg, Toast.LENGTH_SHORT).show()
                    ErrorLogger.log(this@HistoryActivity, msg, "HistoryActivity")
                }
            }
            override fun onFailure(call: Call<List<OrderHistoryResponse>>, t: Throwable) {
                pbHistory.visibility = View.GONE
                tvEmptyHistory.visibility = View.VISIBLE
                val msg = "Error de red en historial: ${t.message}"
                Toast.makeText(this@HistoryActivity, msg, Toast.LENGTH_SHORT).show()
                ErrorLogger.log(this@HistoryActivity, msg, "HistoryActivity")
            }
        })
    }
}
