package com.primosjoyeria.ui.theme.screens



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.primosjoyeria.ui.theme.UiState
import com.primosjoyeria.ui.theme.components.QuantityStepper
import com.primosjoyeria.util.clp



@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

@Composable
fun CarritoScreen(
    state: com.primosjoyeria.ui.theme.UiState,  // puedes dejarlo calificado para evitar dudas
    onInc: (Int) -> Unit,
    onDec: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onClear: () -> Unit,
    goBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito") },
                navigationIcon = { TextButton(onClick = goBack) { Text("Atrás") } },
                actions = { TextButton(onClick = onClear) { Text("Vaciar") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.carrito, key = { it.cartId }) { item ->
                    Card {
                        Column(Modifier.padding(12.dp)) {
                            Text(item.nombre, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text("${clp(item.precio)}  •  Cant: ${item.cantidad}")
                            Spacer(Modifier.height(8.dp))
                            QuantityStepper(
                                onInc = { onInc(item.productId) },
                                onDec = { onDec(item.productId) },
                                onRemove = { onRemove(item.productId) }
                            )
                        }
                    }
                }
            }
            Divider(Modifier.padding(vertical = 8.dp))
            Text(
                text = "Total: ${clp(state.total)}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { /* TODO: flujo de pago */ },
                enabled = state.total > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finalizar compra")
            }
        }
    }
}
