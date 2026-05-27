package uta.edu.ec.proyecto_final_moviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import uta.edu.ec.proyecto_final_moviles.models.OrderHistoryResponse
import uta.edu.ec.proyecto_final_moviles.models.Product

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvOrderHistory: RecyclerView
    private lateinit var tvEmptyHistory: TextView
    private lateinit var pbHistory: ProgressBar
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var adapter: OrderHistoryAdapter
    private var productNames: Map<Int, String> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        supportActionBar?.hide()

        rvOrderHistory = findViewById(R.id.rvOrderHistory)
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory)
        pbHistory = findViewById(R.id.pbHistory)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        bottomNavigation.selectedItemId = R.id.nav_history
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_history -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        setupRecyclerView()
        loadProductsAndHistory()
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

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                // If products fail to load, we still load history, just without names
                loadOrderHistory()
            }
        })
    }

    private fun loadOrderHistory() {
        val prefs = getSharedPreferences("NorthwindPrefs", Context.MODE_PRIVATE)
        val customerId = prefs.getString("user_id", "") ?: ""

        if (customerId.isEmpty()) {
            Toast.makeText(this, "Error: No se encontró el ID del cliente.", Toast.LENGTH_SHORT).show()
            tvEmptyHistory.visibility = View.VISIBLE
            return
        }

        pbHistory.visibility = View.VISIBLE
        tvEmptyHistory.visibility = View.GONE
        rvOrderHistory.visibility = View.GONE

        ApiClient.apiService.getOrdersByCustomer(customerId).enqueue(object : Callback<List<OrderHistoryResponse>> {
            override fun onResponse(call: Call<List<OrderHistoryResponse>>, response: Response<List<OrderHistoryResponse>>) {
                pbHistory.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val orders = response.body()!!
                    if (orders.isEmpty()) {
                        tvEmptyHistory.visibility = View.VISIBLE
                    } else {
                        rvOrderHistory.visibility = View.VISIBLE
                        // Sort by date descending (newest first)
                        adapter.updateList(orders.sortedByDescending { it.orderDate }, productNames)
                    }
                } else {
                    tvEmptyHistory.visibility = View.VISIBLE
                    Toast.makeText(this@HistoryActivity, "Error al cargar el historial.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<OrderHistoryResponse>>, t: Throwable) {
                pbHistory.visibility = View.GONE
                tvEmptyHistory.visibility = View.VISIBLE
                Toast.makeText(this@HistoryActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
