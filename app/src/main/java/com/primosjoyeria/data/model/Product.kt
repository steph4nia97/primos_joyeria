package com.primosjoyeria.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val precio: Int,
    val imagenUrl: String? = null,
    val stock: Int = 0
)

@Entity(tableName = "carrito")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val cartId: Int = 0,
    val productId: Int,
    val nombre: String,
    val precio: Int,
    val cantidad: Int
)
