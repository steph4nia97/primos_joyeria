package com.primosjoyeria.data.remote.auth

data class LoginResponse(
    val id: Long,
    val correo: String,
    val rol: String
)