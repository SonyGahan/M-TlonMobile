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

        val etDniBusqueda = findViewById<EditText>(R.id.etDniBuscar)
        val btnVerificar = findViewById<Button>(R.id.btnVerificar)
        val btnAtras = findViewById<Button>(R.id.btnVolverMenuVerificacion)

        val flujoRecibido = intent.getStringExtra("FLUJO") ?: ""

        btnVerificar.setOnClickListener {
            val dniTxt = etDniBusqueda.text.toString().trim()

            if (dniTxt.isEmpty()) {
                Toast.makeText(this, "ERROR: El campo DNI no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val regexDni = Regex("""^\d{7,8}$""")
            if (!regexDni.matches(dniTxt)) {
                Toast.makeText(this, "ERROR: Ingrese un DNI válido (debe tener entre 7 y 8 números, sin letras ni puntos)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val intentResultado = Intent(this, ResultadoVerificacionActivity::class.java)
            intentResultado.putExtra("DNI_BUSCADO", dniTxt)
            intentResultado.putExtra("FLUJO", flujoRecibido)
            startActivity(intentResultado)
            finish() //Mantiene limpia la pila de RAM.
        }

        btnAtras.setOnClickListener { finish() }
    }
}