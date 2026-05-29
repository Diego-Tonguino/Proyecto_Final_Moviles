package uta.edu.ec.proyecto_final_moviles

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ErrorLogger — log persistente del lado del cliente.
 * Guarda hasta MAX_ENTRIES entradas en SharedPreferences (sin tocar el backend).
 */
object ErrorLogger {

    private const val PREFS_NAME = "NorthwindErrorLog"
    private const val KEY_LOGS   = "error_logs"
    private const val MAX_ENTRIES = 50

    data class LogEntry(
        val timestamp: String,
        val source: String,
        val message: String,
        val level: String = "ERROR"
    )

    /** Registra un error y opcionalmente muestra un Toast */
    fun log(context: Context, message: String, source: String = "App", level: String = "ERROR") {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_LOGS, "[]") ?: "[]"
        val array = try { JSONArray(existing) } catch (e: Exception) { JSONArray() }

        val entry = JSONObject().apply {
            put("timestamp", SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date()))
            put("source", source)
            put("message", message)
            put("level", level)
        }

        // Insertar al principio (más reciente primero)
        val newArray = JSONArray()
        newArray.put(entry)
        for (i in 0 until minOf(array.length(), MAX_ENTRIES - 1)) {
            newArray.put(array.getJSONObject(i))
        }

        prefs.edit().putString(KEY_LOGS, newArray.toString()).apply()
    }

    /** Obtiene todas las entradas del log */
    fun getLogs(context: Context): List<LogEntry> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_LOGS, "[]") ?: "[]"
        val array = try { JSONArray(json) } catch (e: Exception) { return emptyList() }
        val result = mutableListOf<LogEntry>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            result.add(LogEntry(
                timestamp = obj.optString("timestamp", ""),
                source    = obj.optString("source", "App"),
                message   = obj.optString("message", ""),
                level     = obj.optString("level", "ERROR")
            ))
        }
        return result
    }

    /** Borra todos los registros */
    fun clearLogs(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().remove(KEY_LOGS).apply()
    }

    /** Cuenta errores no info */
    fun errorCount(context: Context): Int =
        getLogs(context).count { it.level == "ERROR" || it.level == "WARNING" }
}
