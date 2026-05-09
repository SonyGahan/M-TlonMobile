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
        val resultadosConsulta = mutableListOf<List<String>>()
        
        // FORZAMOS UNA FILA HARDCODEADA PARA VER SI SE RENDERIZA
        resultadosConsulta.add(listOf("123", "PRUEBA", "ALUMNO", "99-99-9999"))
        
        // Intentamos traer los de la DB
        try {
            resultadosConsulta.addAll(base.obtenerVencimientosComoLista(fechaHoy))
        } catch (e: Exception) { }
        
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
                textView.setTextColor(android.graphics.Color.WHITE)
                textView.setBackgroundColor(android.graphics.Color.parseColor("#333344"))
                textView.setPadding(12, 12, 12, 12)
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