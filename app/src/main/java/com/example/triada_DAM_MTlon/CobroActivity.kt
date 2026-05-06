package com.example.triada_DAM_MTlon

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CobroActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobro)

        val dniRecibido = intent.getStringExtra("DNI_SOCIO") ?: ""

        val tvNombreCompleto = findViewById<TextView>(R.id.tvNombreCompleto)
        val tvDniCobro = findViewById<TextView>(R.id.tvDniCobro)
        val tvEmailCobro = findViewById<TextView>(R.id.tvEmailCobro)
        val tvTelefonoCobro = findViewById<TextView>(R.id.tvTelefonoCobro)
        val etMonto = findViewById<EditText>(R.id.etMonto)

        val spModoPago = findViewById<Spinner>(R.id.spModoPago)
        val opciones = arrayOf("Efectivo", "Transferencia")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spModoPago.adapter = adapter

        val btnRegistrarPago = findViewById<Button>(R.id.btnRegistrarPago)
        val btnVolverMenu = findViewById<Button>(R.id.btnVolverMenu)

        val db = Datos(this)

        if (dniRecibido.isNotEmpty()) {
            val cursor = db.obtenerSocio(dniRecibido)
            if (cursor.moveToFirst()) {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))

                tvNombreCompleto.text = "$nombre $apellido"
                tvDniCobro.text = "DNI: $dniRecibido"
                tvEmailCobro.text = "Email: $email"
                tvTelefonoCobro.text = "Teléfono: $telefono"
            }
            cursor.close()
        }

        btnRegistrarPago.setOnClickListener {
            val montoTxt = etMonto.text.toString().trim()
            val modoPago = spModoPago.selectedItem.toString()
            val montoNum = montoTxt.toIntOrNull()
            val dniNum = dniRecibido.toIntOrNull()

            if (montoNum != null && dniNum != null) {
                val fechaHoraActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss"))

                val mensaje = db.insertarPago(dniNum, montoNum, modoPago, fechaHoraActual)
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

                if (mensaje == "¡Pago exitoso!") {
                    val intentTicket = Intent(this, ComprobanteActivity::class.java)
                    intentTicket.putExtra("MONTO", montoTxt)
                    intentTicket.putExtra("DNI", dniRecibido)
                    intentTicket.putExtra("FECHA", fechaHoraActual)
                    intentTicket.putExtra("METODO", modoPago)
                    startActivity(intentTicket)
                    finish()
                }

            } else {
                Toast.makeText(this, "Complete todos los campos del pago", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolverMenu.setOnClickListener {
            val intentInicio = Intent(this, MenuActivity::class.java)
            startActivity(intentInicio)
            finish()
        }
    }
}