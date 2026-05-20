package com.example.triada_DAM_MTlon.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.triada_DAM_MTlon.model.SocioDTO

class Datos(contexto: Context) : SQLiteOpenHelper(contexto, "gimnasio.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val tablaSocio = """
            CREATE TABLE socio (
                dni TEXT PRIMARY KEY,
                nombre TEXT,
                apellido TEXT,
                email TEXT,
                telefono TEXT,
                tipo_usuario TEXT,
                text_apto TEXT,
                estado_cuota TEXT,
                vencimiento TEXT
            )
        """.trimIndent()
        db?.execSQL(tablaSocio)

        val tablaPago = """
            CREATE TABLE pago (
                id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
                dni TEXT,
                monto REAL,
                fecha_pago TEXT,
                forma_pago TEXT,
                FOREIGN KEY(dni) REFERENCES socio(dni)
            )
        """.trimIndent()
        db?.execSQL(tablaPago)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS pago")
        db?.execSQL("DROP TABLE IF EXISTS socio")
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
        return cantidad
    }

    fun insertarSocio(
        dni: String, nombre: String, apellido: String, email: String,
        telefono: String, apto: String, tipoUsuario: String,
        vencimiento: String, estadoCuota: String
    ): Long {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put("dni", dni)
            put("nombre", nombre)
            put("apellido", apellido)
            put("email", email)
            put("telefono", telefono)
            put("tipo_usuario", tipoUsuario)
            put("text_apto", apto)
            put("estado_cuota", estadoCuota)
            put("vencimiento", vencimiento)
        }
        return db.insert("socio", null, valores)
    }

    fun consultarEstadoDNI(dni: String): SocioDTO? {
        val db = this.readableDatabase
        val query = "SELECT nombre, apellido, email, telefono, tipo_usuario, text_apto, estado_cuota, vencimiento FROM socio WHERE dni = ?"
        val cursor = db.rawQuery(query, arrayOf(dni))

        var socio: SocioDTO? = null

        if (cursor.moveToFirst()) {
            socio = SocioDTO(
                dni = dni,
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                tipoUsuario = cursor.getString(cursor.getColumnIndexOrThrow("tipo_usuario")),
                estadoApto = cursor.getString(cursor.getColumnIndexOrThrow("text_apto")),
                estadoCuota = cursor.getString(cursor.getColumnIndexOrThrow("estado_cuota")),
                vencimiento = cursor.getString(cursor.getColumnIndexOrThrow("vencimiento"))
            )
        }
        cursor.close()
        return socio
    }

    fun insertarPago(dni: String, monto: Double, modoPago: String, fecha: String): String {
        val db = this.writableDatabase
        val contenedor = ContentValues().apply {
            put("dni", dni)
            put("monto", monto)
            put("forma_pago", modoPago)
            put("fecha_pago", fecha)
        }
        val resultado = db.insert("pago", null, contenedor)
        return if (resultado == -1L) "Falla al registrar el pago" else "¡Pago exitoso!"
    }

    fun actualizarApto(dni: String, nuevaFecha: String): Boolean {
        val db = this.writableDatabase
        val contenedor = ContentValues().apply {
            put("text_apto", nuevaFecha)
        }
        val resultado = db.update("socio", contenedor, "dni = ?", arrayOf(dni))
        return resultado > 0
    }

    fun actualizarVencimiento(dni: String, nuevaFecha: String) {
        val db = this.writableDatabase
        val contenedor = ContentValues().apply {
            put("vencimiento", nuevaFecha)
            put("estado_cuota", "Al día")
        }
        db.update("socio", contenedor, "dni = ?", arrayOf(dni))
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

    fun cargarMorososDePrueba() {
        val db = this.writableDatabase
        val checkQuery = "SELECT dni FROM socio WHERE dni = '11111111'"
        val cursor = db.rawQuery(checkQuery, null)
        val existe = cursor.moveToFirst()
        cursor.close()

        if (!existe) {
            insertarSocio("11111111", "Juan", "Perez", "juan@test.com", "12345", "10-01-2023", "Socio", "2023-03-15", "Vencida")
            insertarSocio("22222222", "Maria", "Gomez", "maria@test.com", "12345", "12-02-2023", "Socio", "2023-04-10", "Vencida")
            insertarSocio("33333333", "Carlos", "Lopez", "carlos@test.com", "12345", "05-03-2023", "Socio", "2023-02-20", "Vencida")
            insertarSocio("44444444", "Ana", "Martinez", "ana@test.com", "12345", "20-04-2023", "Socio", "2023-01-05", "Vencida")
        }
    }
}