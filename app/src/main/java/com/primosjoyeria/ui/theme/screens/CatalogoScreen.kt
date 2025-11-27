package com.primosjoyeria.ui.theme.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.ui.theme.UiState
import com.primosjoyeria.ui.theme.components.AnimatedButton
import kotlinx.coroutines.launch
import com.primosjoyeria.R
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.primosjoyeria.ui.theme.IndicadoresViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)

@Composable


fun CatalogoScreen(
    state: UiState,
    onAdd: (Product) -> Unit,
    goCarrito: () -> Unit,
    onLogout: () -> Unit,
    indicadoresVm: IndicadoresViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Estado del dólar
    val dolar = indicadoresVm.dolar
    val errorDolar = indicadoresVm.error
    val loadingDolar = indicadoresVm.loading

    LaunchedEffect(Unit) {
        indicadoresVm.cargarDolar()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo Joyería") },
                actions = {
                    AnimatedButton(
                        onClick = goCarrito,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Carrito (${state.carrito.sumOf { it.cantidad }})")
                    }

                    Spacer(Modifier.width(10.dp))

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val numero = "56945425271"
                    val mensaje = "Hola! Quiero consultar por una joya de Primos Joyería 💎"
                    val url = "https://wa.me/$numero?text=${Uri.encode(mensaje)}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
                containerColor = Color(0xFF29B100),   // verde WhatsApp
                shape = CircleShape
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wsp),
                    contentDescription = "WhatsApp",
                    modifier = Modifier.size(40.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // ==== TARJETAS DE PRODUCTOS ====
                items(state.productos, key = { it.id }) { producto ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Image(
                                painter = painterResource(id = producto.imagenRes),
                                contentDescription = producto.nombre,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                producto.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "$${producto.precio}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    onAdd(producto)
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "${producto.nombre} agregado al carrito"
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Agregar al carrito")
                            }
                        }
                    }
                }

                // ==== ÍTEM FINAL: INDICADOR DEL DÓLAR ====
                item {
                    Spacer(Modifier.height(24.dp))

                    when {
                        loadingDolar -> {
                            Text(
                                "Cargando valor del dólar...",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }

                        dolar != null -> {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        "Indicador precio del dolar",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = "Dólar actual: $${dolar.valor} CLP",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Actualizado: ${dolar.fecha.take(10)}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "Los precios pueden variar según el día de consulta.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))
                        }

                        errorDolar != null -> {
                            Text(
                                text = "No se pudo obtener el valor del dólar",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}