package uta.edu.ec.proyecto_final_moviles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uta.edu.ec.proyecto_final_moviles.models.OrderHistoryResponse
import java.text.SimpleDateFormat
import java.util.Locale

class OrderHistoryAdapter(
    private var orders: List<OrderHistoryResponse>,
    private var productNames: Map<Int, String> = emptyMap()
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvOrderAddress: TextView = view.findViewById(R.id.tvOrderAddress)
        val tvOrderCityCountry: TextView = view.findViewById(R.id.tvOrderCityCountry)
        val tvOrderItems: TextView = view.findViewById(R.id.tvOrderItems)
        val tvOrderTotal: TextView = view.findViewById(R.id.tvOrderTotal)
        val tvOrderDetailsList: TextView = view.findViewById(R.id.tvOrderDetailsList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        
        holder.tvOrderId.text = "Orden #${order.id}"
        
        // Format date
        try {
            if (!order.orderDate.isNullOrEmpty()) {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = parser.parse(order.orderDate)
                holder.tvOrderDate.text = if (date != null) formatter.format(date) else order.orderDate
            } else {
                holder.tvOrderDate.text = "Fecha desconocida"
            }
        } catch (e: Exception) {
            holder.tvOrderDate.text = order.orderDate ?: ""
        }

        holder.tvOrderAddress.text = order.shipAddress ?: "N/A"
        holder.tvOrderCityCountry.text = "${order.shipCity ?: "N/A"}, ${order.shipCountry ?: "N/A"}"

        val details = order.orderDetails ?: emptyList()
        val totalItems = details.sumOf { it.quantity }
        val totalAmount = details.sumOf { it.unitPrice * it.quantity }

        holder.tvOrderItems.text = "$totalItems producto(s)"
        holder.tvOrderTotal.text = "$${String.format("%.2f", totalAmount)}"

        if (details.isNotEmpty()) {
            val detailsText = details.joinToString("\n") { detail ->
                val name = productNames[detail.productId] ?: "Producto #${detail.productId}"
                "• $name (${detail.quantity} x $${String.format("%.2f", detail.unitPrice)})"
            }
            holder.tvOrderDetailsList.text = detailsText
            holder.tvOrderDetailsList.visibility = View.VISIBLE
        } else {
            holder.tvOrderDetailsList.visibility = View.GONE
        }
    }

    override fun getItemCount() = orders.size

    fun updateList(newList: List<OrderHistoryResponse>, newProductNames: Map<Int, String> = emptyMap()) {
        orders = newList
        if (newProductNames.isNotEmpty()) {
            productNames = newProductNames
        }
        notifyDataSetChanged()
    }
}
