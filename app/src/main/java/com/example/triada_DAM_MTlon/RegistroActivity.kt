package com.example.triada_DAM_MTlon

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import android.content.Intent

class RegistroActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val dniRecibido = intent.getStringExtra("DNI_NUEVO") ?: ""

        val etDniReg = findViewById<EditText>(R.id.etDniReg)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etTelefono = findViewById<EditText>(R.id.etTelefono)
        val etApto = findViewById<EditText>(R.id.etApto)
        val rbSocio = findViewById<RadioButton>(R.id.rbSocio)
        val rbNoSocio = findViewById<RadioButton>(R.id.rbNoSocio)
        val btnRegistrarSocio = findViewById<Button>(R.id.btnRegistrarSocio)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        etDniReg.setText(dniRecibido)

        btnRegistrarSocio.setOnClickListener {
            var categoria = ""
            if (rbSocio.isChecked) {
                categoria = "Socio"
            } else if (rbNoSocio.isChecked) {
                categoria = "No Socio"
            }

            val fechaHoy: LocalDate = LocalDate.now()
            val vencimiento = fechaHoy.plusMonths(1).toString()

            val db = Datos(this)
            val mensaje = db.insertarSocio(
                etDniReg.text.toString().toInt(),
                etNombre.text.toString(),
                etApellido.text.toString(),
                etEmail.text.toString(),
                etTelefono.text.toString(),
                etApto.text.toString(),
                categoria,
                vencimiento
            )

            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

            if (mensaje == "Insert exitoso") {
                val intentCobro = Intent(this, CobroActivity::class.java)
                intentCobro.putExtra("DNI_SOCIO", etDniReg.text.toString())
                startActivity(intentCobro)
                finish()
            }

            btnCancelar.setOnClickListener {
                finish()
            }
        }
    }
}