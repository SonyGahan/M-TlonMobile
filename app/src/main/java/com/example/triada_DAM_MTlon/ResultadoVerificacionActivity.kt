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
        val btnNuevaConsulta = findViewById<Button>(R.id.btnNuevaConsulta)
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
                AlertDialog.Builder(this)
                    .setTitle("Renovar Apto Médico")
                    .setMessage("Ingrese la nueva fecha de carga (DD-MM-AAAA):")
                    .setView(input)
                    .setPositiveButton("Guardar") { _, _ ->
                        val nuevaFecha = input.text.toString().trim()
                        if (nuevaFecha.isNotEmpty()) {
                            db.actualizarApto(dniRecibido, nuevaFecha)
                            finish()
                            startActivity(intent)
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

        btnNuevaConsulta.setOnClickListener {
            val intentConsulta = Intent(this, VerificacionActivity::class.java)
            startActivity(intentConsulta)
            finish()
        }

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