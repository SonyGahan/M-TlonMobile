package com.example.triada_DAM_MTlon

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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

        if (dniRecibido.isNotEmpty()) {
            val cursor = db.consultarEstadoDNI(dniRecibido)
            if (cursor.moveToFirst()) {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))

                val fechaCargaAptoStr = cursor.getString(cursor.getColumnIndexOrThrow("estado_apto"))

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
            cursor.close()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
}
