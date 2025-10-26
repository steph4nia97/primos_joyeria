package com.primosjoyeria.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.util.clp
import coil.compose.AsyncImage

@Composable
fun ProductCard(product: Product, onAdd: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            if (product.imagenUrl != null) {
                AsyncImage(
                    model = product.imagenUrl,
                    contentDescription = product.nombre,
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
            Text(product.nombre, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(clp(product.precio))
            Spacer(Modifier.height(8.dp))
            Row { Button(onClick = onAdd) { Text("Agregar") } }
        }
    }
}
