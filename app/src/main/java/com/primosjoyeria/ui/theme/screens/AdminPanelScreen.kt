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
import retrofit2.http.Url


@Composable
fun EditProductDialog(
    initialName: String,
    initialPrice: Int,
    initialUrl: String?,
    onDismiss: () -> Unit,
    onConfirm: (newName: String, newPrice: Int, newUrl: String?) -> Unit
) {
    var nombre by remember { mutableStateOf(initialName) }
    var precio by remember { mutableStateOf(initialPrice.toString()) }
    var url by remember { mutableStateOf(initialUrl.orEmpty()) }

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
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL imagen (raw GitHub, opcional)") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val nuevoPrecio = precio.toIntOrNull()
                val urlLimpia = url.trim().ifBlank { null }

                if (nombre.isNotBlank() && nuevoPrecio != null) {
                    onConfirm(nombre.trim(), nuevoPrecio, urlLimpia)
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
    var imagenUrl by remember { mutableStateOf("") }

    var mensaje by remember { mutableStateOf<String?>(null) }
    var editing by remember { mutableStateOf<Product?>(null) }

    // Cargar productos desde la BD (Room)
    LaunchedEffect(Unit) {
        repo.productos().collect { productos = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { goBack() }) {
                        Text(
                            "Cerrar sesión",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
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

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = imagenUrl,
                onValueChange = { imagenUrl = it },
                label = { Text("URL imagen (raw GitHub, opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    val p = precio.toIntOrNull()
                    val urlLimpia = imagenUrl.trim().ifBlank { null }

                    if (nombre.isNotBlank() && p != null) {
                        scope.launch {
                            try {
                                repo.agregarProducto(nombre, p, urlLimpia)
                                nombre = ""
                                precio = ""
                                imagenUrl = ""
                                mensaje = "Producto agregado correctamente"
                            } catch (e: Exception) {
                                mensaje = "Error al agregar: ${e.message}"
                            }
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

            // Lista de productos existentes
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
                                Text(
                                    "$${producto.precio}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (!producto.imagenUrl.isNullOrBlank()) {
                                    Text(
                                        "Tiene imagen",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = { editing = producto }) {
                                    Text("Editar")
                                }

                                TextButton(onClick = {
                                    scope.launch {
                                        try {
                                            repo.eliminarProducto(producto.id)
                                        } catch (e: Exception) {
                                            mensaje = "Error al eliminar: ${e.message}"
                                        }
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

    editing?.let { producto ->
        EditProductDialog(
            initialName = producto.nombre,
            initialPrice = producto.precio,
            initialUrl = producto.imagenUrl,
            onDismiss = { editing = null },
            onConfirm = { newName, newPrice, newUrl ->
                scope.launch {
                    try {
                        repo.actualizarProducto(
                            producto.id,
                            newName,
                            newPrice,
                            newUrl
                        )
                        mensaje = "Producto actualizado correctamente"
                    } catch (e: Exception) {
                        mensaje = "Error al actualizar: ${e.message}"
                    } finally {
                        editing = null
                    }
                }
            }
        )
    }
}
