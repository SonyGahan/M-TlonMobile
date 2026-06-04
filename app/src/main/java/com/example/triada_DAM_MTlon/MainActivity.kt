package com.example.triada_DAM_MTlon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.triada_DAM_MTlon.database.SQLiteHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasenia = findViewById<EditText>(R.id.etContrasena)

        val db = SQLiteHelper(this)
        db.cargarMorososDePrueba()

        btnIngresar.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val contrasenia = etContrasenia.text.toString()

            if (usuario == "admin" && contrasenia == "1234") {
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Usuario/contraseña incorrectos, intente nuevamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
}