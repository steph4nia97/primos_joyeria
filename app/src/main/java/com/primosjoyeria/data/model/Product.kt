package com.primosjoyeria.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "producto")
data class Product(
    @PrimaryKey val id: Long,
    val nombre: String,
    val precio: Int,
    val imagenUrl: String?
)

@Entity(tableName = "carrito")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val cartId: Int = 0,
    val productId: Long,
    val nombre: String,
    val precio: Int,
    val cantidad: Int
)
