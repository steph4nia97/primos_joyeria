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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.primosjoyeria.ui.theme.UiState
import com.primosjoyeria.ui.theme.components.AnimatedButton
import kotlinx.coroutines.launch

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

@Composable
fun CatalogoScreen(
    state: UiState,
    onAdd: (Product) -> Unit,
    goCarrito: () -> Unit,
    onLogout: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo Joyería") },
                actions = {
                    AnimatedButton(
                        onClick = goCarrito,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Carrito (${state.carrito.size})")
                    }

                    Spacer(Modifier.width(8.dp))

                    TextButton(onClick = onLogout) {
                        Text(
                            "Cerrar sesión",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) } // 👈 Snackbar aquí
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // 💎 Lista de productos
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.productos) { producto ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("$${producto.precio}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = {
                                onAdd(producto)
                                // 👇 Mostrar Snackbar
                                scope.launch {
                                    snackbarHostState.showSnackbar("✨ Agregado al carrito")
                                }
                            }) {
                                Text("Agregar al carrito")
                            }
                        }
                    }
                }
            }
        }
    }
}