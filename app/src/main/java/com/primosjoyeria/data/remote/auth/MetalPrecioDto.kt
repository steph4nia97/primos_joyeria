package com.primosjoyeria.data.remote.auth

data class MetalPrecioDto(
    val metal: String,
    val precioOnzaClp: Double,
    val precioGramoClp: Double,
    val fecha: String
)