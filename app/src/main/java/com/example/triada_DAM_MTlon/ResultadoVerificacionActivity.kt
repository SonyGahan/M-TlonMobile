package com.example.triada_DAM_MTlon

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultadoVerificacionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_verificacion)

        val dniRecibido = intent.getStringExtra("DNI_BUSCADO") ?: ""
        val db = Datos(this)

        val llNoRegistrado = findViewById<LinearLayout>(R.id.llNoRegistrado)
        val llRegistrado = findViewById<LinearLayout>(R.id.llRegistrado)
        val llTarjetaCuota = findViewById<LinearLayout>(R.id.llTarjetaCuota)

        // Referenciamos los botones principales
        val btnNuevaConsulta = findViewById<Button>(R.id.btnNuevaConsulta)
        val btnVolverMenuResult = findViewById<Button>(R.id.btnVolverMenuResult)
        val btnImprimirCarnet = findViewById<Button>(R.id.btnImprimirCarnet)

        val cursor = db.consultarEstadoDNI(dniRecibido)

        if (cursor.moveToFirst()) {
            // CASO: DNI ENCONTRADO
            llRegistrado.visibility = View.VISIBLE
            llNoRegistrado.visibility = View.GONE

            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
            val tipoUsuario = cursor.getString(cursor.getColumnIndexOrThrow("tipo_usuario"))
            val estadoApto = cursor.getString(cursor.getColumnIndexOrThrow("estado_apto"))
            val estadoCuota = cursor.getString(cursor.getColumnIndexOrThrow("estado_cuota"))

            findViewById<TextView>(R.id.tvNombreResult).text = "$nombre $apellido"
            findViewById<TextView>(R.id.tvTipoSocioBadge).text = tipoUsuario
            findViewById<TextView>(R.id.tvAptoResult).text = "Apto Físico: $estadoApto"

            // Lógica de visibilidad exclusiva para SOCIOS[cite: 5]
            if (tipoUsuario.equals("Socio", ignoreCase = true)) {
                llTarjetaCuota.visibility = View.VISIBLE
                btnImprimirCarnet.visibility = View.VISIBLE
                findViewById<TextView>(R.id.tvEstadoCuota).text = "Estado de Cuota: $estadoCuota"

                // Solo asignamos el click si es socio para evitar errores
                btnImprimirCarnet.setOnClickListener {
                    val intentCarnet = Intent(this, CarnetActivity::class.java)
                    intentCarnet.putExtra("DNI", dniRecibido)
                    startActivity(intentCarnet)
                }
            } else {
                // Si es "No Socio", ocultamos los elementos de socio[cite: 5]
                llTarjetaCuota.visibility = View.GONE
                btnImprimirCarnet.visibility = View.GONE
            }

        } else {
            // CASO: DNI NO ENCONTRADO (FLUJO DE REGISTRO NUEVO)[cite: 5]
            llNoRegistrado.visibility = View.VISIBLE
            llRegistrado.visibility = View.GONE
            btnImprimirCarnet.visibility = View.GONE // Blindaje: ocultar por las dudas

            findViewById<TextView>(R.id.tvDniBuscado).text = "DNI: $dniRecibido"

            val btnIrARegistro = findViewById<Button>(R.id.btnIrARegistro)
            btnIrARegistro.setOnClickListener {
                val intentRegistro = Intent(this, RegistroActivity::class.java)
                intentRegistro.putExtra("DNI_NUEVO", dniRecibido)
                startActivity(intentRegistro)
            }
        }
        cursor.close()

        // Listeners de navegación general[cite: 5]
        btnNuevaConsulta.setOnClickListener {
            val intentConsulta = Intent(this, VerificacionActivity::class.java)
            startActivity(intentConsulta)
            finish()
        }

        btnVolverMenuResult.setOnClickListener {
            val intentMenu = Intent(this, MenuActivity::class.java)
            intentMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentMenu)
        }
    }
}