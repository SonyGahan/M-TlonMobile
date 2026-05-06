package com.example.triada_DAM_MTlon

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor
import android.content.ContentValues

// Cambiamos la versión a 2 para forzar la actualización de tablas
class Datos(contexto: Context) : SQLiteOpenHelper(contexto, "MTlonDB.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Ejecución de las sentencias de creación definidas en el companion object
        db?.execSQL(CREATE_SOCIO_TABLE)
        db?.execSQL(CREATE_PAGO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Se eliminan las tablas existentes para evitar conflictos de estructura
        db?.execSQL("DROP TABLE IF EXISTS socio")
        db?.execSQL("DROP TABLE IF EXISTS pago")
        onCreate(db)
    }

    // Verifica si un DNI ya existe para el flujo de Registro o Pagos[cite: 5]
    fun buscaSocio(dni: String): Int {
        val bd = this.readableDatabase
        val query = "SELECT COUNT(*) FROM socio WHERE dni = ?"
        val selectionArgs = arrayOf(dni)
        val cursor: Cursor = bd.rawQuery(query, selectionArgs)

        var cantidad = 0
        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0) // Retorna el recuento de filas[cite: 5]
        }

        cursor.close()
        bd.close()
        return cantidad
    }

    // Inserta un nuevo socio con su estado inicial de cuota y apto médico[cite: 5]
    fun insertarSocio(dni: Int, nombre: String, apellido: String, email: String, telefono: String, apto: String, categoria: String, vencimiento: String): String {
        val db = this.writableDatabase
        val contenedor = ContentValues()

        contenedor.put("dni", dni)
        contenedor.put("nombre", nombre)
        contenedor.put("apellido", apellido)
        contenedor.put("email", email)
        contenedor.put("telefono", telefono)
        contenedor.put("estado_apto", apto)
        contenedor.put("tipo_usuario", categoria)
        contenedor.put("vencimiento", vencimiento)
        contenedor.put("estado_cuota", "Al día") // Valor por defecto sugerido[cite: 5]

        val resultado = db.insert("socio", null, contenedor)
        return if (resultado == -1L) "Falla en la carga de datos" else "Insert exitoso"
    }

    // Recupera datos básicos para la pantalla de carga de Cobro[cite: 5]
    fun obtenerSocio(dni: String): Cursor {
        val db = this.readableDatabase
        val query = "SELECT nombre, apellido, email, telefono FROM socio WHERE dni = ?"
        return db.rawQuery(query, arrayOf(dni))
    }

    // Registra la operación de pago en la tabla correspondiente[cite: 5]
    fun insertarPago(dni: Int, monto: Int, modoPago: String, fecha: String): String {
        val db = this.writableDatabase
        val contenedor = ContentValues()

        contenedor.put("dni_socio", dni)
        contenedor.put("monto", monto)
        contenedor.put("modo_pago", modoPago)
        contenedor.put("fecha", fecha)

        val resultado = db.insert("pago", null, contenedor)
        return if (resultado == -1L) "Falla al registrar el pago" else "¡Pago exitoso!"
    }

    // Obtiene el listado ordenado para el módulo de Vencimientos[cite: 5]
    fun obtenerVencimientosComoLista(): List<List<String>> {
        val datos: MutableList<List<String>> = mutableListOf()
        val db = this.readableDatabase
        // Incluimos order by para jerarquizar la información[cite: 5]
        val sql = "SELECT dni, apellido, nombre, vencimiento FROM socio ORDER BY vencimiento"
        val cursor = db.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            val fila: MutableList<String> = mutableListOf()
            fila.add(cursor.getString(cursor.getColumnIndexOrThrow("dni")))
            fila.add(cursor.getString(cursor.getColumnIndexOrThrow("apellido")))
            fila.add(cursor.getString(cursor.getColumnIndexOrThrow("nombre")))
            fila.add(cursor.getString(cursor.getColumnIndexOrThrow("vencimiento")))
            datos.add(fila)
        }
        cursor.close()
        return datos
    }

    // Consulta extendida para la pantalla de Carnet y Resultados[cite: 5]
    fun consultarEstadoDNI(dni: String): Cursor {
        val db = this.readableDatabase
        // La proyección debe coincidir exactamente con las columnas de la tabla[cite: 3, 5]
        val query = "SELECT nombre, apellido, email, telefono, tipo_usuario, estado_apto, estado_cuota, vencimiento FROM socio WHERE dni = ?"
        return db.rawQuery(query, arrayOf(dni))
    }

    // Definición de constantes para miembros estáticos[cite: 3]
    companion object {
        private const val CREATE_SOCIO_TABLE = "CREATE TABLE socio " +
                "(dni INTEGER PRIMARY KEY, nombre TEXT, apellido TEXT, email TEXT, " +
                "telefono TEXT, estado_apto TEXT, tipo_usuario TEXT, vencimiento TEXT, estado_cuota TEXT)"

        private const val CREATE_PAGO_TABLE = "CREATE TABLE pago " +
                "(id_pago INTEGER PRIMARY KEY AUTOINCREMENT, dni_socio INTEGER, monto INTEGER, modo_pago TEXT, fecha TEXT, " +
                "FOREIGN KEY(dni_socio) REFERENCES socio(dni))"
    }
}