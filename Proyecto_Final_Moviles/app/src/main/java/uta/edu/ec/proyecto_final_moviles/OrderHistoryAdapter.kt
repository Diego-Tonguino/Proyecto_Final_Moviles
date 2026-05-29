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
    private var productNames: Map<Int, String> = emptyMap(),
    private val onItemClick: ((OrderHistoryResponse) -> Unit)? = null
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    private var ordersFull: List<OrderHistoryResponse> = ArrayList(orders)

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
        holder.tvOrderDetailsList.text = "Toca para ver la factura"
        holder.tvOrderDetailsList.visibility = View.VISIBLE
        holder.tvOrderDetailsList.setTextColor(android.graphics.Color.parseColor("#4ADE80"))
        holder.itemView.setOnClickListener { onItemClick?.invoke(order) }
    }

    override fun getItemCount() = orders.size

    fun filterFull(query: String, filterType: String, dateFrom: String?, dateTo: String?) {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val dateFromParsed = if (!dateFrom.isNullOrEmpty()) {
            try { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateFrom) } catch (e: Exception) { null }
        } else null
        val dateToParsed = if (!dateTo.isNullOrEmpty()) {
            try { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateTo) } catch (e: Exception) { null }
        } else null

        orders = ordersFull.filter { order ->
            val matchesQuery = when {
                query.isEmpty() -> true
                filterType == "order" -> order.id.toString().contains(query)
                filterType == "product" -> {
                    order.orderDetails?.any { detail ->
                        val name = productNames[detail.productId] ?: ""
                        name.lowercase().contains(query.lowercase())
                    } ?: false
                }
                else -> order.id.toString().contains(query)
            }
            val orderDate = if (!order.orderDate.isNullOrEmpty()) {
                try { parser.parse(order.orderDate) } catch (e: Exception) { null }
            } else null
            val matchesDateFrom = dateFromParsed == null || (orderDate != null && !orderDate.before(dateFromParsed))
            val matchesDateTo = dateToParsed == null || (orderDate != null && !orderDate.after(dateToParsed))
            matchesQuery && matchesDateFrom && matchesDateTo
        }
        notifyDataSetChanged()
    }

    fun updateList(newList: List<OrderHistoryResponse>, newProductNames: Map<Int, String> = emptyMap()) {
        orders = newList
        ordersFull = ArrayList(newList)
        if (newProductNames.isNotEmpty()) productNames = newProductNames
        notifyDataSetChanged()
    }
}

