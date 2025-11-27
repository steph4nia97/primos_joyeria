package com.primosjoyeria.data.remote.auth

data class LoginRequest(
    val correo: String,
    val password: String
)