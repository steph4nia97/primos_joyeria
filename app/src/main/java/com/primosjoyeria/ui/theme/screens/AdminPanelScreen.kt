package com.primosjoyeria.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.data.repository.CatalogRepository
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    repo: CatalogRepository,
    goBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var productos by remember { mutableStateOf<List<Product>>(emptyList()) }

    // Cargar productos de la BD
    LaunchedEffect(Unit) {
        repo.productos().collect { productos = it }
    }

    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // 🔹 Formulario de agregar producto
            Text("Agregar nuevo producto", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    val p = precio.toIntOrNull()
                    if (nombre.isNotBlank() && p != null) {
                        scope.launch {
                            repo.agregarProducto(nombre, p)
                            nombre = ""
                            precio = ""
                            mensaje = "Producto agregado correctamente"
                        }
                    } else {
                        mensaje = "Debes ingresar nombre y precio válido"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar producto")
            }

            mensaje?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.primary)
            }

            Divider(Modifier.padding(vertical = 16.dp))

            // 🔹 Lista de productos existentes
            Text("Productos actuales", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(productos, key = { it.id }) { producto ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(producto.nombre, style = MaterialTheme.typography.bodyLarge)
                                Text("$${producto.precio}", style = MaterialTheme.typography.bodyMedium)
                            }

                            Row {
                                TextButton(onClick = {
                                    scope.launch {
                                        repo.eliminarProducto(producto.id)
                                    }
                                }) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
