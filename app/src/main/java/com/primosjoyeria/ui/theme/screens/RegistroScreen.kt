package com.primosjoyeria.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun RegistroScreen(
    onRegistrar: suspend (correo: String, pass: String, sexo: String, edad: Int) -> Result<Unit>,
    onRegistroExitoso: () -> Unit,
    goBack: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("F") }
    var edad by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    TextButton(onClick = goBack) { Text("Atrás") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = sexo,
                onValueChange = { sexo = it },
                label = { Text("Sexo (F/M/X)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it.filter { ch -> ch.isDigit() } },
                label = { Text("Edad") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val e = edad.toIntOrNull()
                    if (correo.isBlank() || pass.isBlank() || sexo.isBlank() || e == null) {
                        mensaje = "Completa todos los campos con valores válidos"
                        return@Button
                    }
                    scope.launch {
                        val res = onRegistrar(correo, pass, sexo, e)
                        if (res.isSuccess) {
                            onRegistroExitoso()
                        } else {
                            mensaje = res.exceptionOrNull()?.message ?: "Error al registrar"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Registrar cuenta") }

            mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
    }
}
