package com.example.triada_DAM_MTlon

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ResultadoVerificacionActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_verificacion)

        val dniRecibido = intent.getStringExtra("DNI_BUSCADO") ?: ""
        val db = Datos(this)

        val llNoRegistrado = findViewById<LinearLayout>(R.id.llNoRegistrado)
        val llRegistrado = findViewById<LinearLayout>(R.id.llRegistrado)
        val llTarjetaCuota = findViewById<LinearLayout>(R.id.llTarjetaCuota)
        val btnAtrasResult = findViewById<Button>(R.id.btnAtrasResult)
        val btnVolverMenuResult = findViewById<Button>(R.id.btnVolverMenuResult)
        val btnImprimirCarnet = findViewById<Button>(R.id.btnImprimirCarnet)
        val cursor = db.consultarEstadoDNI(dniRecibido)

        if (cursor.moveToFirst()) {
            llRegistrado.visibility = View.VISIBLE
            llNoRegistrado.visibility = View.GONE

            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
            val tipoUsuario = cursor.getString(cursor.getColumnIndexOrThrow("tipo_usuario"))
            val estadoApto = cursor.getString(cursor.getColumnIndexOrThrow("estado_apto"))
            val estadoCuota = cursor.getString(cursor.getColumnIndexOrThrow("estado_cuota"))
            val vencimiento = cursor.getString(cursor.getColumnIndexOrThrow("vencimiento"))

            findViewById<TextView>(R.id.tvNombreResult).text = "$nombre $apellido"
            findViewById<TextView>(R.id.tvTipoSocioBadge).text = tipoUsuario

            var textoApto = "Apto Físico: $estadoApto"
            try {
                val partesApto = estadoApto.split("-", "/")
                if (partesApto.size == 3) {
                    val anio = if (partesApto[2].length == 4) partesApto[2].toInt() else partesApto[0].toInt()
                    val mes = partesApto[1].toInt()
                    val dia = if (partesApto[2].length == 4) partesApto[0].toInt() else partesApto[2].toInt()
                    val fechaCarga = LocalDate.of(anio, mes, dia)
                    val vencimientoApto = fechaCarga.plusYears(1)
                    val vigente = if (LocalDate.now().isBefore(vencimientoApto) || LocalDate.now().isEqual(vencimientoApto)) "Vigente" else "Vencido"
                    val formatterOut = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    textoApto = "Apto Médico: $vigente, Vencimiento: ${vencimientoApto.format(formatterOut)}"
                }
            } catch (e: Exception) { }
            findViewById<TextView>(R.id.tvAptoResult).text = textoApto

            val btnRenovarApto = findViewById<Button>(R.id.btnRenovarApto)
            btnRenovarApto.setOnClickListener {
                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_DATETIME
                input.hint = "DD-MM-AAAA"
                input.addTextChangedListener(object : android.text.TextWatcher {
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
                            if ((i == 1 || i == 3) && i != str.length - 1) sb.append("-")
                        }
                        input.setText(sb.toString())
                        input.setSelection(input.text.length)
                        isUpdating = false
                    }
                })
                AlertDialog.Builder(this)
                    .setTitle("Renovar Apto Médico")
                    .setMessage("Ingrese la nueva fecha de carga (DD-MM-AAAA):")
                    .setView(input)
                    .setPositiveButton("Guardar") { _, _ ->
                        val nuevaFecha = input.text.toString().trim()
                        val regexFecha = Regex("""^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-\d{4}$""")
                        if (nuevaFecha.isNotEmpty() && regexFecha.matches(nuevaFecha)) {
                            db.actualizarApto(dniRecibido, nuevaFecha)
                            finish()
                            startActivity(intent)
                        } else {
                            android.widget.Toast.makeText(this, "Fecha inválida, debe ser DD-MM-AAAA", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

            if (tipoUsuario.equals("Socio", ignoreCase = true)) {
                llTarjetaCuota.visibility = View.VISIBLE
                btnImprimirCarnet.visibility = View.VISIBLE
                
                val partesVenc = vencimiento.split("-")
                val vencFormat = if(partesVenc.size == 3) "${partesVenc[2]}-${partesVenc[1]}-${partesVenc[0]}" else vencimiento
                findViewById<TextView>(R.id.tvEstadoCuota).text = "Estado de Cuota: $estadoCuota, Vence: $vencFormat"

                btnImprimirCarnet.setOnClickListener {
                    val intentCarnet = Intent(this, CarnetActivity::class.java)
                    intentCarnet.putExtra("DNI", dniRecibido)
                    startActivity(intentCarnet)
                }
            } else {
                llTarjetaCuota.visibility = View.GONE
                btnImprimirCarnet.visibility = View.GONE
            }

        } else {
            llNoRegistrado.visibility = View.VISIBLE
            llRegistrado.visibility = View.GONE
            btnImprimirCarnet.visibility = View.GONE

            findViewById<TextView>(R.id.tvDniBuscado).text = "DNI: $dniRecibido"

            val btnIrARegistro = findViewById<Button>(R.id.btnIrARegistro)
            btnIrARegistro.setOnClickListener {
                val intentRegistro = Intent(this, RegistroActivity::class.java)
                intentRegistro.putExtra("DNI_NUEVO", dniRecibido)
                startActivity(intentRegistro)
            }
        }
        cursor.close()

        btnAtrasResult.setOnClickListener {
            finish()
        }

        btnVolverMenuResult.setOnClickListener {
            val intentMenu = Intent(this, MenuActivity::class.java)
            intentMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentMenu)
        }
    }
}