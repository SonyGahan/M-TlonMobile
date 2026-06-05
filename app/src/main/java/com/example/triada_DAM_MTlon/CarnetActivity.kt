package com.example.triada_DAM_MTlon

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.triada_DAM_MTlon.database.SQLiteHelper

class CarnetActivity : AppCompatActivity() {

    private lateinit var db: SQLiteHelper

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)

        val dniRecibido = intent.getStringExtra("DNI") ?: ""
        db = SQLiteHelper(this)

        val tvNombre = findViewById<TextView>(R.id.tvNombreCarnet)
        val tvDni = findViewById<TextView>(R.id.tvDniCarnet)
        val tvEmail = findViewById<TextView>(R.id.tvEmailCarnet)
        val tvVencimientoApto = findViewById<TextView>(R.id.tvVencimientoCarnet)
        val btnVolver = findViewById<Button>(R.id.btnVolverDeCarnet)
        val btnImprimir = findViewById<Button>(R.id.btnImprimirCarnet)
        val btnInicio = findViewById<Button>(R.id.btnInicioDeCarnet)

        if (dniRecibido.isNotEmpty()) {
            val socio = db.consultarEstadoDNI(dniRecibido)
            if (socio != null) {
                if (socio.tipoUsuario.equals("No Socio", ignoreCase = true)) {
                    Toast.makeText(this, "ERROR: Los clientes 'No Socio' no poseen carnet digital", Toast.LENGTH_LONG).show()
                    finish()
                    return
                }

                tvNombre.text = "${socio.nombre} ${socio.apellido}"
                tvDni.text = "DNI: $dniRecibido"
                tvEmail.text = "Email: ${socio.email}"

                // Invocación única al DTO.
                tvVencimientoApto.text = socio.obtenerTextoAptoVencimiento()
            }
        }

        btnImprimir.setOnClickListener {
            Toast.makeText(this, "Imprimiendo carnet...", Toast.LENGTH_SHORT).show()
        }

        btnInicio.setOnClickListener {
            val intentMenu = Intent(this, MenuActivity::class.java)
            intentMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentMenu)
            finish()
        }

        btnVolver.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::db.isInitialized) {
            db.close()
        }
    }
}