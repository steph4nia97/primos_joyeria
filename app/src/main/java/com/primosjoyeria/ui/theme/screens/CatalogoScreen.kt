package com.primosjoyeria.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.ui.theme.UiState
import com.primosjoyeria.ui.theme.components.AnimatedButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // 💎 Lista de productos con imagen
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.productos, key = { it.id }) { producto ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            // 🔹 Imagen del producto
                            Image(
                                painter = painterResource(id = producto.imagenRes),
                                contentDescription = producto.nombre,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(Modifier.height(8.dp))

                            // 🔹 Datos del producto
                            Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("$${producto.precio}", style = MaterialTheme.typography.bodyMedium)

                            Spacer(Modifier.height(8.dp))

                            // 🔹 Botón para agregar al carrito
                            Button(
                                onClick = {
                                    onAdd(producto)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("✨ ${producto.nombre} agregado al carrito")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Agregar al carrito")
                            }
                        }
                    }
                }
            }
        }
    }
}
