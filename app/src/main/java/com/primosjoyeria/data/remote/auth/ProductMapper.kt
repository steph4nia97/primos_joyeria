package com.primosjoyeria.data.remote.auth


import com.primosjoyeria.data.model.Product   // Entity de Room

// De DTO (backend) a Entity (Room)
fun ProductDto.toEntity(): Product =
    Product(
        id = this.id,
        nombre = this.nombre,
        precio = this.precio,
        imagenUrl = this.imagenUrl
    )