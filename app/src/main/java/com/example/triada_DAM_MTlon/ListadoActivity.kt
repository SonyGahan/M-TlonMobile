package com.example.triada_DAM_MTlon

import android.content.Intent
import android.os.Bundle
import android.os.Build
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import java.time.LocalDate

class ListadoActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado)

        val btnVolver = findViewById<Button>(R.id.btnVolverListado)
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)

        val base = Datos(this)
        base.cargarMorososDePrueba()
        val fechaHoy = LocalDate.now().toString()
        val resultadosConsulta: List<List<String>> = base.obtenerVencimientosComoLista(fechaHoy)
        
        val encabezados = arrayOf("DNI", "Apellido", "Nombre", "Vence")

        for (titulo in encabezados) {
            val tvHeader = TextView(this)
            tvHeader.text = titulo
            tvHeader.setTextColor("#00E5FF".toColorInt())
            tvHeader.setPadding(12, 16, 12, 16)
            tvHeader.setTypeface(null, android.graphics.Typeface.BOLD)
            gridLayout.addView(tvHeader)
        }

        for (fila in resultadosConsulta) {
            for (dato in fila) {
                val textView = TextView(this)
                textView.text = dato
                textView.setTextColor(getColor(android.R.color.white))
                textView.setPadding(12, 12, 12, 12)

                val params = GridLayout.LayoutParams()
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                textView.layoutParams = params

                gridLayout.addView(textView)
            }
        }

        btnVolver.setOnClickListener {
            val intentInicio = Intent(this, MenuActivity::class.java)
            intentInicio.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentInicio)
        }
    }
}