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

        etApto.addTextChangedListener(object : android.text.TextWatcher {
            private var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (isUpdating) return
                isUpdating = true
                var str = s.toString().replace(Regex("[^\\d]"), "")
                if (str.length > 8) str = str.substring(0, 8)
                val sb = StringBuilder()
                for (i in str.indices) {
                    sb.append(str[i])
                    if ((i == 1 || i == 3) && i != str.length - 1) {
                        sb.append("-")
                    }
                }
                etApto.setText(sb.toString())
                etApto.setSelection(etApto.text.length)
                isUpdating = false
            }
        })

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

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
                Toast.makeText(this, "ERROR: Formato de email inválido", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.PHONE.matcher(telefonoTxt).matches()) {
                Toast.makeText(this, "ERROR: Formato de teléfono inválido", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val regexFecha = Regex("""^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-\d{4}$""")
            if (!regexFecha.matches(aptoTexto)) {
                Toast.makeText(this, "ERROR: La fecha debe ser DD-MM-AAAA", Toast.LENGTH_LONG).show()
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

            if (mensaje == "Registro exitoso!") {
                if (categoria == "Socio") {
                    val intentCobro = Intent(this, CobroActivity::class.java)
                    intentCobro.putExtra("DNI_SOCIO", etDniReg.text.toString())
                    startActivity(intentCobro)
                    finish()
                } else {
                    finish()
                }
            }
        }

        btnCancelar.setOnClickListener { finish() }
    }
}
