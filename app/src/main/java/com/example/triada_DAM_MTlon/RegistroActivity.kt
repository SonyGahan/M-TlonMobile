package com.example.triada_DAM_MTlon

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
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

        etDniReg.setText(dniRecibido)

        btnRegistrarSocio.setOnClickListener {
            val dniTxt = etDniReg.text.toString().trim()
            val nombreTxt = etNombre.text.toString().trim()
            val apellidoTxt = etApellido.text.toString().trim()
            val emailTxt = etEmail.text.toString().trim()
            val telefonoTxt = etTelefono.text.toString().trim()
            val aptoTexto = etApto.text.toString().trim()

            if (dniTxt.isEmpty() || nombreTxt.isEmpty() || apellidoTxt.isEmpty() ||
                emailTxt.isEmpty() || telefonoTxt.isEmpty() || aptoTexto.isEmpty()) {
                Toast.makeText(this, "ERROR: Todos los campos son obligatorios", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val categoria = if (rbSocio.isChecked) "Socio" else "No Socio"
            val fechaHoy: LocalDate = LocalDate.now()
            val vencimientoCuota = fechaHoy.plusMonths(1).toString()
            val db = Datos(this)
            val mensaje = db.insertarSocio(
                dniTxt.toInt(),
                nombreTxt,
                apellidoTxt,
                emailTxt,
                telefonoTxt,
                aptoTexto,
                categoria,
                vencimientoCuota
            )

            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

            if (mensaje == "Insert exitoso") {
                if (categoria == "Socio") {
                    val intentCobro = Intent(this, CobroActivity::class.java)
                    intentCobro.putExtra("DNI_SOCIO", etDniReg.text.toString())
                    startActivity(intentCobro)
                } else {
                    finish()
                }
            }
        }

        btnCancelar.setOnClickListener { finish() }
    }
}
