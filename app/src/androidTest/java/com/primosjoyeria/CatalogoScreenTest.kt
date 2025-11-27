package com.primosjoyeria.ui.catalogo
import com.primosjoyeria.ui.theme.screens.CatalogoScreen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.ui.theme.UiState
import org.junit.Rule
import org.junit.Test

import com.primosjoyeria.R
import com.primosjoyeria.data.model.CartItem


class CatalogoScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun catalogoMuestraNombreYPrecioDeProducto() {
        // 👇 Ajusta esto a tu UiState real
        val fakeState = UiState(
            productos = listOf(
                Product(
                    id = 1,
                    nombre = "Anillo oro 18k",
                    precio = 199990,
                    imagenRes = R.drawable.logo   // o cualquier drawable que tengas
                )
            ),
            carrito = emptyList<CartItem>()
        )

        composeRule.setContent {
            CatalogoScreen(
                state = fakeState,
                onAdd = {},
                goCarrito = {},
                onLogout = {}
            )
        }

        // Verifica que se muestre el nombre y el precio
        composeRule.onNodeWithText("Anillo oro 18k").assertIsDisplayed()
        composeRule.onNodeWithText("$199990").assertIsDisplayed()
    }
}
