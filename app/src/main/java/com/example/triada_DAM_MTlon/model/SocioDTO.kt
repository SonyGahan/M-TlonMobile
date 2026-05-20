package com.example.triada_DAM_MTlon.model

// Agregamos la implementación de Serializable para que pueda viajar entre actividades
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
) : java.io.Serializable