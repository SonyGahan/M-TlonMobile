package com.example.triada_DAM_MTlon

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ComprobanteActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprobante)

        val monto = intent.getStringExtra("MONTO") ?: "0"
        val dni = intent.getStringExtra("DNI") ?: ""
        val fecha = intent.getStringExtra("FECHA") ?: ""
        val metodo = intent.getStringExtra("METODO") ?: ""

        findViewById<TextView>(R.id.tvMontoTicket).text = "$$monto"
        findViewById<TextView>(R.id.tvIdClienteTicket).text = "ID Cliente: $dni"
        findViewById<TextView>(R.id.tvFechaTicket).text = "Fecha: $fecha"
        findViewById<TextView>(R.id.tvMetodoTicket).text = "Método: $metodo"

        findViewById<Button>(R.id.btnImprimirTicket).setOnClickListener {
            android.widget.Toast.makeText(this, "Imprimiendo comprobante...", android.widget.Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnFinalizarTicket).setOnClickListener {
            val intentMenu = Intent(this, MenuActivity::class.java)
            intentMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentMenu)
        }
    }
}