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




@Composable
fun EditProductDialog(
    initialName: String,
    initialPrice: Int,
    onDismiss: () -> Unit,
    onConfirm: (newName: String, newPrice: Int) -> Unit
) {
    var nombre by remember { mutableStateOf(initialName) }
    var precio by remember { mutableStateOf(initialPrice.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Precio") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val nuevoPrecio = precio.toIntOrNull()
                if (nombre.isNotBlank() && nuevoPrecio != null) {
                    onConfirm(nombre.trim(), nuevoPrecio)
                }
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    repo: CatalogRepository,
    goBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var productos by remember { mutableStateOf<List<Product>>(emptyList()) }
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var editing by remember { mutableStateOf<Product?>(null) } // 👈 producto en edición

    // Cargar productos desde la BD
    LaunchedEffect(Unit) {
        repo.productos().collect { productos = it }
    }

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
                onValueChange = { precio = it.filter { ch -> ch.isDigit() } },
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

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // 🔸 Botón Editar
                                TextButton(onClick = { editing = producto }) {
                                    Text("Editar")
                                }

                                // 🔸 Botón Eliminar
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

    // 🔹 Mostrar diálogo si hay un producto en edición
    editing?.let { producto ->
        EditProductDialog(
            initialName = producto.nombre,
            initialPrice = producto.precio,
            onDismiss = { editing = null },
            onConfirm = { newName, newPrice ->
                scope.launch {
                    repo.actualizarProducto(producto.id, newName, newPrice)
                    mensaje = "Producto actualizado correctamente"
                    editing = null
                }
            }
        )
    }
}
