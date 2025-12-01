package com.primosjoyeria.data.remote.auth

data class RegistroRequest (
    val correo: String,
    val password: String,
    val sexo: String,
    val edad: Int
)
