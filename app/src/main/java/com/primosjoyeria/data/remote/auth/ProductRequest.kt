package com.primosjoyeria.data.remote.auth

data class ProductRequest(
    val nombre: String,
    val precio: Int,
    val imagenUrl: String? = null // por ahora no lo usas, va null
)