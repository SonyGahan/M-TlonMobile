package com.example.triada_DAM_MTlon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnRegistro = findViewById<Button>(R.id.btnRegistro)
        val btnVencimientos = findViewById<Button>(R.id.btnVencimientos)

        btnRegistro.setOnClickListener {
            val intentar = Intent(this, VerificacionActivity::class.java)
            startActivity(intentar)
        }

        btnVencimientos.setOnClickListener {
            val intentListado = Intent(this, ListadoActivity::class.java)
            startActivity(intentListado)
        }

        val btnPagos = findViewById<Button>(R.id.btnPagos)

        btnPagos.setOnClickListener {
            val intent = Intent(this, VerificacionActivity::class.java)
            intent.putExtra("FLUJO", "PAGOS")
            startActivity(intent)
        }

        val btnSalir = findViewById<Button>(R.id.btnSalir)
        btnSalir.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
