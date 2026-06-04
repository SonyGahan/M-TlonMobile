package com.example.triada_DAM_MTlon

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.triada_DAM_MTlon.database.SQLiteHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class ResultadoVerificacionActivity : AppCompatActivity() {

    private lateinit var llRegistrado: LinearLayout
    private lateinit var llNoRegistrado: LinearLayout
    private lateinit var llTarjetaCuota: LinearLayout
    private lateinit var btnImprimirCarnet: Button
    private lateinit var btnEliminarSocio: Button
    private lateinit var btnModificarSocio: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_verificacion)

        llRegistrado = findViewById(R.id.llRegistrado)
        llNoRegistrado = findViewById(R.id.llNoRegistrado)
        llTarjetaCuota = findViewById(R.id.llTarjetaCuota)
        btnImprimirCarnet = findViewById(R.id.btnImprimirCarnet)
        btnEliminarSocio = findViewById(R.id.btnEliminarSocio)
        btnModificarSocio = findViewById(R.id.btnModificarSocio)

        val dniRecibido = intent.getStringExtra("DNI_BUSCADO") ?: ""

        val db = SQLiteHelper(this)

        val btnAtrasResult = findViewById<Button>(R.id.btnAtrasResult)
        btnAtrasResult.setOnClickListener { finish() }

        val btnCobrarCuota = findViewById<Button>(R.id.btnCobrarCuota)
        btnCobrarCuota.setOnClickListener {
            val intentCobro = Intent(this, CobroActivity::class.java)
            intentCobro.putExtra("DNI", dniRecibido)
            startActivity(intentCobro)
            finish()
        }

        btnEliminarSocio.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmar Baja")
            builder.setMessage("¿Está seguro de que desea dar de baja a este cliente del sistema? Sus registros históricos de pago no se perderán.")

            builder.setPositiveButton("Sí, dar de baja") { dialog, _ ->
                val exito = db.eliminarSocioLogico(dniRecibido)
                if (exito) {
                    Toast.makeText(this, "Cliente dado de baja correctamente", Toast.LENGTH_SHORT).show()
                    val intentMenu = Intent(this, MenuActivity::class.java)
                    intentMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intentMenu)
                    finish()
                } else {
                    Toast.makeText(this, "Error al procesar la baja", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            builder.show()
        }

        val socio = db.consultarEstadoDNI(dniRecibido)

        if (socio != null) {
            llRegistrado.visibility = View.VISIBLE
            llNoRegistrado.visibility = View.GONE
            btnEliminarSocio.visibility = View.VISIBLE
            btnModificarSocio.visibility = View.VISIBLE
            btnModificarSocio.setOnClickListener {
                val intentEditar = Intent(this, RegistroActivity::class.java)
                intentEditar.putExtra("SOCIO_EDICION", socio)
                startActivity(intentEditar)
                finish()
            }

            val nombreSocio = socio.nombre
            val apellidoSocio = socio.apellido
            val tipoUsuarioSocio = socio.tipoUsuario
            val estadoAptoSocio = socio.estadoApto
            val estadoCuotaSocio = socio.estadoCuota
            val vencimientoSocio = socio.vencimiento

            findViewById<TextView>(R.id.tvNombreResult).text = "$nombreSocio $apellidoSocio"
            findViewById<TextView>(R.id.tvTipoSocioBadge).text = tipoUsuarioSocio

            var textoApto = "Apto Físico: $estadoAptoSocio"
            try {
                val partesApto = estadoAptoSocio.split("-", "/")
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
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Renovar Apto Médico")
                builder.setMessage("Ingrese la fecha de emisión del nuevo certificado médico:")

                val inputFecha = EditText(this)
                inputFecha.hint = "DD-MM-AAAA"
                inputFecha.setPadding(50, 20, 50, 20)
                builder.setView(inputFecha)

                inputFecha.setOnClickListener {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)
                    val dpd = DatePickerDialog(this, { _, yearSel, monthSel, daySel ->
                        val diaFmt = String.format("%02d", daySel)
                        val mesFmt = String.format("%02d", monthSel + 1)
                        inputFecha.setText("$diaFmt-$mesFmt-$yearSel")
                    }, year, month, day)
                    dpd.show()
                }

                builder.setPositiveButton("Guardar") { dialog, _ ->
                    val nuevaFecha = inputFecha.text.toString().trim()
                    if (nuevaFecha.isNotEmpty()) {

                        try {
                            val partes = nuevaFecha.split("-")
                            val fechaSeleccionada = LocalDate.of(partes[2].toInt(), partes[1].toInt(), partes[0].toInt())
                            if (fechaSeleccionada.isAfter(LocalDate.now())) {
                                Toast.makeText(this, "ERROR: La fecha del nuevo apto médico no puede ser una fecha futura", Toast.LENGTH_LONG).show()
                                dialog.dismiss()
                                return@setPositiveButton
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this, "ERROR: Estructura de fecha inválida", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            return@setPositiveButton
                        }

                        val exito = db.actualizarApto(dniRecibido, nuevaFecha)
                        if (exito) {
                            Toast.makeText(this, "Apto Médico actualizado con éxito", Toast.LENGTH_SHORT).show()
                            recreate()
                        } else {
                            Toast.makeText(this, "Error al actualizar en la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismiss()
                }
                builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
                builder.show()
            }

            if (tipoUsuarioSocio.equals("Socio", ignoreCase = true)) {
                llTarjetaCuota.visibility = View.VISIBLE
                btnImprimirCarnet.visibility = View.VISIBLE

                val partesVenc = vencimientoSocio.split("-")
                val vencFormat = if (partesVenc.size == 3) "${partesVenc[2]}-${partesVenc[1]}-${partesVenc[0]}" else vencimientoSocio
                findViewById<TextView>(R.id.tvEstadoCuota).text = "Estado de Cuota: $estadoCuotaSocio, Vence: $vencFormat"

                btnImprimirCarnet.setOnClickListener {
                    val intentCarnet = Intent(this, CarnetActivity::class.java)
                    intentCarnet.putExtra("DNI", dniRecibido)
                    startActivity(intentCarnet)
                }
            } else {
                llTarjetaCuota.visibility = View.GONE
                btnImprimirCarnet.visibility = View.GONE
            }

            btnCobrarCuota.visibility = View.VISIBLE
            if (tipoUsuarioSocio.equals("No Socio", ignoreCase = true)) {
                btnCobrarCuota.text = "Cobrar Actividad"
            } else {
                btnCobrarCuota.text = "Registrar Pago de Cuota"
            }

        } else {
            llNoRegistrado.visibility = View.VISIBLE
            llRegistrado.visibility = View.GONE
            btnImprimirCarnet.visibility = View.GONE
            btnCobrarCuota.visibility = View.GONE
            btnEliminarSocio.visibility = View.GONE
            findViewById<TextView>(R.id.tvDniBuscado).text = "DNI: $dniRecibido"
            btnModificarSocio.visibility = View.GONE

            val btnIrARegistro = findViewById<Button>(R.id.btnIrARegistro)
            btnIrARegistro.setOnClickListener {
                val intentRegistro = Intent(this, RegistroActivity::class.java)
                intentRegistro.putExtra("DNI_NUEVO", dniRecibido)
                startActivity(intentRegistro)
                finish()
            }
        }
    }
}