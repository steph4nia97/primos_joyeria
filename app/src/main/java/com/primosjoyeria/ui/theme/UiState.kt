package com.primosjoyeria.ui.theme

import com.primosjoyeria.data.model.CartItem
import com.primosjoyeria.data.model.Product

data class UiState(
    val productos: List<Product> = emptyList(),
    val carrito: List<CartItem> = emptyList()
) {
    val total: Int
        get() = carrito.sumOf { it.precio * it.cantidad }
}
