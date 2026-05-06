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

                val fechaCarga = LocalDate.parse(fechaCargaAptoStr)
                val vencimientoApto = fechaCarga.plusYears(1)

                tvNombre.text = "$nombre $apellido"
                tvDni.text = "DNI: $dniRecibido"
                tvEmail.text = "Email: $email"

                tvVencimientoApto.text = "Apto Médico Vence: $vencimientoApto"
            }
            cursor.close()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
}
