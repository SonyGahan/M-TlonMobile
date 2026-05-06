package com.example.triada_DAM_MTlon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ListadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado)

        val gridLayoutResultado = findViewById<GridLayout>(R.id.gridLayout)
        val btnVolver = findViewById<Button>(R.id.btnVolverListado)
        val base = Datos(this)
        val resultadosConsulta: List<List<String>> = base.obtenerVencimientosComoLista()

        for (fila in resultadosConsulta) {
            for (dato in fila) {
                val textView = TextView(this)
                textView.text = dato
                textView.setTextColor(getColor(android.R.color.white))
                textView.setPadding(8, 8, 8, 8)

                val params = GridLayout.LayoutParams()
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                textView.layoutParams = params

                gridLayoutResultado.addView(textView)
            }
        }

        btnVolver.setOnClickListener {
            val intentInicio = Intent(this, MenuActivity::class.java)
            startActivity(intentInicio)
        }
    }
}