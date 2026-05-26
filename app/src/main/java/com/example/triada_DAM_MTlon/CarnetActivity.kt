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
import com.example.triada_DAM_MTlon.database.Datos
import java.time.LocalDate

class CarnetActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)

        val dniRecibido = intent.getStringExtra("DNI") ?: ""
        val db = Datos(this)

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
                    finish() // Destruye la actividad para que no se renderice la interfaz.
                    return
                }

                val nombre = socio.nombre
                val apellido = socio.apellido
                val email = socio.email
                val fechaCargaAptoStr = socio.estadoApto

                var textoVencimiento = "Apto Médico: $fechaCargaAptoStr"
                try {
                    val partesApto = fechaCargaAptoStr.split("-", "/")
                    if (partesApto.size == 3) {
                        val anio = if (partesApto[2].length == 4) partesApto[2].toInt() else partesApto[0].toInt()
                        val mes = partesApto[1].toInt()
                        val dia = if (partesApto[2].length == 4) partesApto[0].toInt() else partesApto[2].toInt()
                        val fechaCarga = LocalDate.of(anio, mes, dia)
                        val vencimientoApto = fechaCarga.plusYears(1)
                        val formatterOut = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        val vigente = if (LocalDate.now().isBefore(vencimientoApto) || LocalDate.now().isEqual(vencimientoApto)) "Vigente" else "Vencido"
                        textoVencimiento = "Apto Médico: $vigente, Vencimiento: ${vencimientoApto.format(formatterOut)}"
                    }
                } catch (e: Exception) { }

                tvNombre.text = "$nombre $apellido"
                tvDni.text = "DNI: $dniRecibido"
                tvEmail.text = "Email: $email"
                tvVencimientoApto.text = textoVencimiento
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

        btnVolver.setOnClickListener {
            finish()
        }
    }
}