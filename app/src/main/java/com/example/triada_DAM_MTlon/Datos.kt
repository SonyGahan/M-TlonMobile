package com.example.triada_DAM_MTlon

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor
import android.content.ContentValues

class Datos(contexto: Context) : SQLiteOpenHelper(contexto, "MTlonDB.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_SOCIO_TABLE)
        db?.execSQL(CREATE_PAGO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS socio")
        db?.execSQL("DROP TABLE IF EXISTS pago")
        onCreate(db)
    }

    fun buscaSocio(dni: String): Int {
        val bd = this.readableDatabase
        val query = "SELECT COUNT(*) FROM socio WHERE dni = ?"
        val selectionArgs = arrayOf(dni)
        val cursor: Cursor = bd.rawQuery(query, selectionArgs)

        var cantidad = 0
        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0)
        }

        cursor.close()
        bd.close()
        return cantidad
    }

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
        contenedor.put("estado_cuota", "Al día")

        val resultado = db.insert("socio", null, contenedor)
        return if (resultado == -1L) "Falla en la carga de datos" else "Registro exitoso!"
    }

    fun obtenerSocio(dni: String): Cursor {
        val db = this.readableDatabase
        val query = "SELECT nombre, apellido, email, telefono FROM socio WHERE dni = ?"
        return db.rawQuery(query, arrayOf(dni))
    }

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

    fun actualizarApto(dni: String, nuevaFecha: String): Boolean {
        val db = this.writableDatabase
        val contenedor = ContentValues()
        contenedor.put("estado_apto", nuevaFecha)
        val resultado = db.update("socio", contenedor, "dni = ?", arrayOf(dni))
        return resultado > 0
    }

    fun actualizarVencimiento(dni: Int, nuevaFecha: String) {
        val db = this.writableDatabase
        val contenedor = ContentValues()
        contenedor.put("vencimiento", nuevaFecha)
        contenedor.put("estado_cuota", "Al día")
        db.update("socio", contenedor, "dni = ?", arrayOf(dni.toString()))
    }

    fun obtenerVencimientosComoLista(fechaHoy: String): List<List<String>> {
        val datos: MutableList<List<String>> = mutableListOf()
        val db = this.readableDatabase
        val sql = "SELECT dni, apellido, nombre, vencimiento FROM socio WHERE vencimiento <= ? ORDER BY vencimiento"
        val cursor = db.rawQuery(sql, arrayOf(fechaHoy))

        while (cursor.moveToNext()) {
            val fila: MutableList<String> = mutableListOf()
            fila.add(cursor.getString(cursor.getColumnIndexOrThrow("dni")))
            fila.add(cursor.getString(cursor.getColumnIndexOrThrow("apellido")))
            fila.add(cursor.getString(cursor.getColumnIndexOrThrow("nombre")))
            
            val vencISO = cursor.getString(cursor.getColumnIndexOrThrow("vencimiento"))
            val partes = vencISO.split("-")
            val vencFormat = if(partes.size == 3) "${partes[2]}-${partes[1]}-${partes[0]}" else vencISO
            fila.add(vencFormat)
            
            datos.add(fila)
        }
        cursor.close()
        return datos
    }

    fun consultarEstadoDNI(dni: String): Cursor {
        val db = this.readableDatabase
        val query = "SELECT nombre, apellido, email, telefono, tipo_usuario, estado_apto, estado_cuota, vencimiento FROM socio WHERE dni = ?"
        return db.rawQuery(query, arrayOf(dni))
    }

    fun cargarMorososDePrueba() {
        val db = this.writableDatabase
        val checkQuery = "SELECT dni FROM socio WHERE dni = 11111111"
        val cursor = db.rawQuery(checkQuery, null)
        val existe = cursor.moveToFirst()
        cursor.close()

        if (!existe) {
            val vencidos = listOf(
                arrayOf("11111111", "Juan", "Perez", "juan@test.com", "12345", "10-01-2023", "Socio", "2024-03-15", "Vencida"),
                arrayOf("22222222", "Maria", "Gomez", "maria@test.com", "12345", "12-02-2023", "Socio", "2024-04-10", "Vencida"),
                arrayOf("33333333", "Carlos", "Lopez", "carlos@test.com", "12345", "05-03-2023", "Socio", "2024-02-20", "Vencida"),
                arrayOf("44444444", "Ana", "Martinez", "ana@test.com", "12345", "20-04-2023", "Socio", "2024-01-05", "Vencida")
            )
            for (datos in vencidos) {
                val cv = ContentValues()
                cv.put("dni", datos[0].toInt())
                cv.put("nombre", datos[1])
                cv.put("apellido", datos[2])
                cv.put("email", datos[3])
                cv.put("telefono", datos[4])
                cv.put("estado_apto", datos[5])
                cv.put("tipo_usuario", datos[6])
                cv.put("vencimiento", datos[7])
                cv.put("estado_cuota", datos[8])
                db.insert("socio", null, cv)
            }
        }
    }

    companion object {
        private const val CREATE_SOCIO_TABLE = "CREATE TABLE socio " +
                "(dni INTEGER PRIMARY KEY, nombre TEXT, apellido TEXT, email TEXT, " +
                "telefono TEXT, estado_apto TEXT, tipo_usuario TEXT, vencimiento TEXT, estado_cuota TEXT)"

        private const val CREATE_PAGO_TABLE = "CREATE TABLE pago " +
                "(id_pago INTEGER PRIMARY KEY AUTOINCREMENT, dni_socio INTEGER, monto INTEGER, modo_pago TEXT, fecha TEXT, " +
                "FOREIGN KEY(dni_socio) REFERENCES socio(dni))"
    }
}