package uta.edu.ec.proyecto_final_moviles

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ErrorLogAdapter(
    private var logs: List<ErrorLogger.LogEntry>
) : RecyclerView.Adapter<ErrorLogAdapter.LogViewHolder>() {

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLevel: TextView   = view.findViewById(R.id.tvLogLevel)
        val tvSource: TextView  = view.findViewById(R.id.tvLogSource)
        val tvTime: TextView    = view.findViewById(R.id.tvLogTime)
        val tvMessage: TextView = view.findViewById(R.id.tvLogMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_error_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val entry = logs[position]

        holder.tvLevel.text = entry.level
        holder.tvSource.text = entry.source
        holder.tvMessage.text = entry.message

        // Mostrar solo hora si el timestamp es largo
        holder.tvTime.text = if (entry.timestamp.length > 11)
            entry.timestamp.substring(0, 16) else entry.timestamp

        // Color del badge según nivel
        val bgColor = when (entry.level) {
            "ERROR"   -> Color.parseColor("#CCef4444")
            "WARNING" -> Color.parseColor("#CCf59e0b")
            "INFO"    -> Color.parseColor("#CC3b82f6")
            else      -> Color.parseColor("#CC6b7280")
        }
        holder.tvLevel.setBackgroundColor(bgColor)
    }

    override fun getItemCount() = logs.size

    fun updateLogs(newLogs: List<ErrorLogger.LogEntry>) {
        logs = newLogs
        notifyDataSetChanged()
    }
}
