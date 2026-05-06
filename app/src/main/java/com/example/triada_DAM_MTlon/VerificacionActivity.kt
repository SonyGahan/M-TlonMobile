package com.example.triada_DAM_MTlon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class VerificacionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verificacion)

        val etDniBuscar = findViewById<EditText>(R.id.etDniBuscar)
        val btnVerificar = findViewById<Button>(R.id.btnVerificar)

        btnVerificar.setOnClickListener {
            val dniABuscar = etDniBuscar.text.toString().trim()
            val flujoRecibido = intent.getStringExtra("FLUJO") ?: "REGISTRO"
            val db = Datos(this)

            if (dniABuscar.isNotEmpty()) {
                val existeSocio = db.buscaSocio(dniABuscar) > 0

                if (flujoRecibido == "PAGOS") {
                    if (existeSocio) {
                        val intentCobro = Intent(this, CobroActivity::class.java)
                        intentCobro.putExtra("DNI_SOCIO", dniABuscar)
                        startActivity(intentCobro)
                    } else {
                        Toast.makeText(this, "DNI no registrado como socio. Debe registrarlo primero.", Toast.LENGTH_LONG).show()
                    }
                } else {

                    val intentResultado = Intent(this, ResultadoVerificacionActivity::class.java)
                    intentResultado.putExtra("DNI_BUSCADO", dniABuscar)
                    startActivity(intentResultado)
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese un DNI", Toast.LENGTH_SHORT).show()
            }
        }

        val btnVolverMenu = findViewById<Button>(R.id.btnVolverMenuVerificacion)
        btnVolverMenu.setOnClickListener {
            finish()
        }
    }
}