package com.example.triada_DAM_MTlon

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CarnetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)

        val dniRecibido = intent.getStringExtra("DNI") ?: ""
        val db = Datos(this)

        val tvNombre = findViewById<TextView>(R.id.tvNombreCarnet)
        val tvDni = findViewById<TextView>(R.id.tvDniCarnet)
        val tvEmail = findViewById<TextView>(R.id.tvEmailCarnet)
        val tvVencimiento = findViewById<TextView>(R.id.tvVencimientoCarnet)
        val btnVolver = findViewById<Button>(R.id.btnVolverDeCarnet)

        if (dniRecibido.isNotEmpty()) {
            val cursor = db.consultarEstadoDNI(dniRecibido)
            if (cursor.moveToFirst()) {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val vencimiento = cursor.getString(cursor.getColumnIndexOrThrow("vencimiento"))

                tvNombre.text = "$nombre $apellido"
                tvDni.text = "DNI: $dniRecibido"
                tvEmail.text = "Email: $email"
                tvVencimiento.text = "Vencimiento: $vencimiento"
            }
            cursor.close()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
}