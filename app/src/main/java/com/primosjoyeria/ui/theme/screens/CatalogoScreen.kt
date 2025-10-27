package com.primosjoyeria.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.ui.theme.components.ProductCard
import androidx.compose.foundation.layout.Arrangement
import com.primosjoyeria.ui.theme.UiState
import com.primosjoyeria.ui.theme.components.AnimatedButton

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

@Composable
fun CatalogoScreen(
    state: UiState,
    onAdd: (Product) -> Unit,
    goCarrito: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo Joyería") },
                actions = {
                    AnimatedButton(
                        onClick = goCarrito,
                        modifier = Modifier.height(36.dp)
                    ){
                        Text("Carrito (${state.carrito.size})")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.productos, key = { it.id }) { p ->
                ProductCard(product = p, onAdd = { onAdd(p) })
            }
        }
    }
}
