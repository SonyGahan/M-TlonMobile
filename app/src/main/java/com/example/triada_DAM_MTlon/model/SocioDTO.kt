package com.example.triada_DAM_MTlon.model

import java.io.Serializable

data class SocioDTO(
    val dni: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val estadoApto: String,
    val tipoUsuario: String, // Aquí se guardará "Socio" o "No Socio"
    val vencimiento: String,
    val estadoCuota: String
) : Serializable