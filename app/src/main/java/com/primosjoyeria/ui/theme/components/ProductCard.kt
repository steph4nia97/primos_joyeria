package com.primosjoyeria.ui.theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.util.clp
import com.primosjoyeria.R


@Composable
fun ProductCard(product: Product, onAdd: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(all = 12.dp)) {

            val painter = rememberAsyncImagePainter(
                model = "https://picsum.photos/600/400"
            )


            Image(
                painter = painter,
                contentDescription = product.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(8.dp))

            Text(text = product.nombre, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(text = clp(product.precio))
            Spacer(Modifier.height(8.dp))
            Row {
                Button(onClick = onAdd) { Text("Agregar") }
            }
        }
    }
}
