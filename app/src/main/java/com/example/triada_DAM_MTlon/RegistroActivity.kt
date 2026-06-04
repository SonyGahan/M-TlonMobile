package com.example.triada_DAM_MTlon

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.triada_DAM_MTlon.database.SQLiteHelper
import com.example.triada_DAM_MTlon.model.SocioDTO
import java.time.LocalDate

class RegistroActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val dniRecibido = intent.getStringExtra("DNI_NUEVO") ?: ""

        val socioEdicion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("SOCIO_EDICION", SocioDTO::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("SOCIO_EDICION") as? SocioDTO
        }

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

        val db = SQLiteHelper(this)

        if (socioEdicion != null) {
            etDniReg.setText(socioEdicion.dni)
            etDniReg.isEnabled = false

            etNombre.setText(socioEdicion.nombre)
            etApellido.setText(socioEdicion.apellido)
            etEmail.setText(socioEdicion.email)
            etTelefono.setText(socioEdicion.telefono)
            etApto.setText(socioEdicion.estadoApto)

            if (socioEdicion.tipoUsuario.equals("Socio", ignoreCase = true)) {
                rbSocio.isChecked = true
            } else {
                rbNoSocio.isChecked = true
            }

            btnRegistrarSocio.text = "Guardar Cambios"
        } else {
            etDniReg.setText(dniRecibido)
            etDniReg.isEnabled = true
            btnRegistrarSocio.text = "Registrar"
        }

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
            val telephoneTxt = etTelefono.text.toString().trim()
            val aptoTexto = etApto.text.toString().trim()

            if (dniTxt.isEmpty() || nombreTxt.isEmpty() || apellidoTxt.isEmpty() ||
                emailTxt.isEmpty() || telephoneTxt.isEmpty() || aptoTexto.isEmpty()) {
                Toast.makeText(this, "ERROR: Todos los campos son obligatorios", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches() || !emailTxt.contains(".")) {
                Toast.makeText(this, "ERROR: Formato de correo electrónico inválido (ej: usuario@dominio.com)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.PHONE.matcher(telephoneTxt).matches() || telephoneTxt.length < 10) {
                Toast.makeText(this, "ERROR: Formato de teléfono inválido (debe ingresar código de área + número, mínimo 10 dígitos)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val regexFecha = Regex("""^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-\d{4}$""")
            if (!regexFecha.matches(aptoTexto)) {
                Toast.makeText(this, "ERROR: La fecha del apto debe seguir el formato DD-MM-AAAA", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            try {
                val partes = aptoTexto.split("-")
                val fechaIngresada = LocalDate.of(partes[2].toInt(), partes[1].toInt(), partes[0].toInt())
                if (fechaIngresada.isAfter(LocalDate.now())) {
                    Toast.makeText(this, "ERROR: La fecha de emisión del apto médico no puede ser posterior al día de hoy", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            } catch (e: Exception) {
                Toast.makeText(this, "ERROR: La consistencia cronológica de la fecha es inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoria = if (rbSocio.isChecked) "Socio" else "No Socio"

            if (socioEdicion != null) {
                val socioModificado = SocioDTO(
                    dni = dniTxt,
                    nombre = nombreTxt,
                    apellido = apellidoTxt,
                    email = emailTxt,
                    telefono = telephoneTxt,
                    tipoUsuario = categoria,
                    estadoApto = aptoTexto,
                    estadoCuota = socioEdicion.estadoCuota,
                    vencimiento = socioEdicion.vencimiento
                )

                val exito = db.modificarSocioCompleto(socioModificado)
                if (exito) {
                    Toast.makeText(this, "¡SQLiteHelper modificados con éxito!", Toast.LENGTH_SHORT).show()

                    val intentMenu = Intent(this, MenuActivity::class.java)
                    intentMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intentMenu)
                    finish()
                } else {
                    Toast.makeText(this, "Error al actualizar los datos en la base de datos", Toast.LENGTH_SHORT).show()
                }

            } else {
                val CorporateHoy: LocalDate = LocalDate.now()
                val vencimientoCuota = if (categoria == "Socio") CorporateHoy.plusMonths(1).toString() else CorporateHoy.toString()
                val estadoCuotaInicial = if (categoria == "Socio") "Impaga" else "No aplica"

                if (db.buscaSocio(dniTxt) > 0) {
                    Toast.makeText(this, "ERROR: El DNI $dniTxt ya se encuentra registrado en el sistema", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val resultadoRowId = db.insertarSocio(
                    dni = dniTxt,
                    nombre = nombreTxt,
                    apellido = apellidoTxt,
                    email = emailTxt,
                    telefono = telephoneTxt,
                    apto = aptoTexto,
                    tipoUsuario = categoria,
                    vencimiento = vencimientoCuota,
                    estadoCuota = estadoCuotaInicial
                )

                if (resultadoRowId != -1L) {
                    Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()

                    val nuevoSocioDTO = SocioDTO(
                        dni = dniTxt,
                        nombre = nombreTxt,
                        apellido = apellidoTxt,
                        email = emailTxt,
                        telefono = telephoneTxt,
                        tipoUsuario = categoria,
                        estadoApto = aptoTexto,
                        estadoCuota = estadoCuotaInicial,
                        vencimiento = vencimientoCuota
                    )

                    val intentCobro = Intent(this, CobroActivity::class.java)
                    intentCobro.putExtra("DNI", dniTxt)
                    intentCobro.putExtra("SOCIO_OBJETO", nuevoSocioDTO)
                    startActivity(intentCobro)
                    finish()
                } else {
                    Toast.makeText(this, "Error: Falla en la carga de datos en la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnCancelar.setOnClickListener { finish() }
    }
}