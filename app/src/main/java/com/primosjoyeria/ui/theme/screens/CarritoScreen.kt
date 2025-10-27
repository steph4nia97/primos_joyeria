package com.primosjoyeria.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.primosjoyeria.ui.theme.UiState
import com.primosjoyeria.ui.theme.components.QuantityStepper
import com.primosjoyeria.util.clp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    state: UiState,
    onInc: (Int) -> Unit,
    onDec: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onClear: () -> Unit,
    goBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito") },
                navigationIcon = { TextButton(onClick = goBack) { Text("Atrás") } },
                actions = {
                    TextButton(onClick = {
                        onClear()
                        scope.launch { snackbarHostState.showSnackbar("🧹 Carrito vaciado") }
                    }) { Text("Vaciar") }
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
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.carrito, key = { it.cartId }) { item ->
                    Card {
                        Column(Modifier.padding(12.dp)) {
                            Text(item.nombre, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text("${clp(item.precio)}  •  Cant: ${item.cantidad}")
                            Spacer(Modifier.height(8.dp))
                            QuantityStepper(
                                onInc = { onInc(item.productId) },
                                // Si la cantidad es 1 y presionan "-", eliminamos el item (no dejamos ir a 0/-)
                                onDec = {
                                    if (item.cantidad > 1) {
                                        onDec(item.productId)
                                    } else {
                                        onRemove(item.productId)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("🗑️ ${item.nombre} eliminado")
                                        }
                                    }
                                },
                                onRemove = {
                                    onRemove(item.productId)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("🗑️ ${item.nombre} eliminado")
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // ✅ Total desde la lista, nunca negativo
            val total = remember(state.carrito) {
                state.carrito.sumOf { it.precio * it.cantidad }.coerceAtLeast(0)
            }

            Divider(Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", style = MaterialTheme.typography.titleLarge)
                Text(clp(total), style = MaterialTheme.typography.titleLarge)
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { /* TODO: flujo de pago */ },
                enabled = total > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finalizar compra")
            }
        }
    }
}
