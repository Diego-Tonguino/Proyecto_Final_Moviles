package uta.edu.ec.proyecto_final_moviles

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class ErrorLogActivity : AppCompatActivity() {

    private lateinit var rvLog: RecyclerView
    private lateinit var tvCount: TextView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var adapter: ErrorLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_log)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { c ->
            c.hide(WindowInsetsCompat.Type.systemBars())
            c.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        supportActionBar?.hide()

        rvLog = findViewById(R.id.rvErrorLog)
        tvCount = findViewById(R.id.tvErrorCount)
        layoutEmpty = findViewById(R.id.layoutEmptyLog)

        // Botón regresar
        findViewById<MaterialCardView>(R.id.btnBackLog).setOnClickListener { finish() }

        // Botón limpiar
        findViewById<MaterialButton>(R.id.btnClearLog).setOnClickListener {
            ErrorLogger.clearLogs(this)
            refreshLogs()
        }

        // Configurar RecyclerView
        rvLog.layoutManager = LinearLayoutManager(this)
        adapter = ErrorLogAdapter(emptyList())
        rvLog.adapter = adapter

        refreshLogs()
    }

    private fun refreshLogs() {
        val logs = ErrorLogger.getLogs(this)
        adapter.updateLogs(logs)

        val errorCount = logs.count { it.level == "ERROR" }
        val warnCount  = logs.count { it.level == "WARNING" }
        val infoCount  = logs.count { it.level == "INFO" }

        tvCount.text = when {
            logs.isEmpty() -> "Sin registros"
            else -> "${logs.size} entradas  •  🔴 $errorCount  🟡 $warnCount  🔵 $infoCount"
        }

        if (logs.isEmpty()) {
            rvLog.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
        } else {
            rvLog.visibility = View.VISIBLE
            layoutEmpty.visibility = View.GONE
        }
    }
}
