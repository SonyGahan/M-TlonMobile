package com.example.triada_DAM_MTlon.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class SocioDTO(
    val dni: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val tipoUsuario: String,
    val estadoApto: String,
    val estadoCuota: String,
    val vencimiento: String
) : java.io.Serializable {

    // Lógica centralizada de vigencia del apto médico.
    fun obtenerTextoAptoVencimiento(): String {
        var textoVencimiento = "Apto Médico: $estadoApto"
        if (estadoApto.isEmpty()) return "Apto Médico: No registrado"

        try {
            val partesApto = estadoApto.split("-", "/")
            if (partesApto.size == 3) {
                val anio = if (partesApto[2].length == 4) partesApto[2].toInt() else partesApto[0].toInt()
                val mes = partesApto[1].toInt()
                val dia = if (partesApto[2].length == 4) partesApto[0].toInt() else partesApto[2].toInt()

                val fechaCarga = LocalDate.of(anio, mes, dia)
                val vencimientoApto = fechaCarga.plusYears(1)
                val formatterOut = DateTimeFormatter.ofPattern("dd-MM-yyyy")

                val vigente = if (LocalDate.now().isBefore(vencimientoApto) || LocalDate.now().isEqual(vencimientoApto)) "Vigente" else "Vencido"
                textoVencimiento = "Apto Médico: $vigente, Vencimiento: ${vencimientoApto.format(formatterOut)}"
            }
        } catch (e: Exception) {
            textoVencimiento = "Apto Médico: Error de formato ($estadoApto)"
        }
        return textoVencimiento
    }
}