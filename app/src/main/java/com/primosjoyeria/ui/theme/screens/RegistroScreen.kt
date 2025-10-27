package com.primosjoyeria.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

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
    var expanded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // --- Validaciones ---
    fun correoValido(c: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(c).matches()
    fun passValida(p: String): Boolean = p.length >= 6
    fun sexoValido(s: String): Boolean = s.uppercase() in listOf("F", "M", "X")
    fun edadValida(e: Int?): Boolean = e != null && e in 1..100

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    TextButton(onClick = goBack) { Text("Atrás") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 📧 Correo
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = correo.isNotBlank() && !correoValido(correo)
            )
            if (correo.isNotBlank() && !correoValido(correo)) {
                Text(
                    "Correo inválido (ejemplo: nombre@dominio.cl)",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // 🔐 Contraseña
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Contraseña (mínimo 6 caracteres)") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = pass.isNotBlank() && !passValida(pass)
            )
            if (pass.isNotBlank() && !passValida(pass)) {
                Text(
                    "La contraseña debe tener al menos 6 caracteres",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // 🚻 Selector de sexo
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = sexo,
                    onValueChange = {},
                    label = { Text("Sexo") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("F", "M", "X").forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                sexo = opcion
                                expanded = false
                            }
                        )
                    }
                }
            }

            // 🎂 Edad
            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it.filter { ch -> ch.isDigit() } },
                label = { Text("Edad (1 a 100)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = edad.isNotBlank() &&
                        !(edad.toIntOrNull()?.let { it in 1..100 } ?: false)
            )
            if (edad.isNotBlank() &&
                !(edad.toIntOrNull()?.let { it in 1..100 } ?: false)
            ) {
                Text(
                    "La edad debe estar entre 1 y 100",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // 🔘 Botón Registrar
            Button(
                onClick = {
                    val e = edad.toIntOrNull()
                    when {
                        correo.isBlank() || pass.isBlank() || sexo.isBlank() || edad.isBlank() -> {
                            mensaje = "Completa todos los campos"
                        }
                        !correoValido(correo) -> mensaje = "Correo inválido"
                        !passValida(pass) -> mensaje = "La contraseña debe tener al menos 6 caracteres"
                        !sexoValido(sexo) -> mensaje = "Selecciona un sexo válido (F, M o X)"
                        !edadValida(e) -> mensaje = "La edad debe estar entre 1 y 100"
                        else -> {
                            scope.launch {
                                val res = onRegistrar(correo, pass, sexo, e!!)
                                if (res.isSuccess) {
                                    mensaje = "✅ Usuario registrado correctamente"
                                    // Pequeña pausa opcional para mostrar el mensaje antes de volver
                                    delay(1200)
                                    onRegistroExitoso()
                                } else {
                                    mensaje = res.exceptionOrNull()?.message ?: "Error al registrar"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar cuenta")
            }

            // 📨 Mensaje final
            mensaje?.let {
                Text(
                    it,
                    color = if (it.contains("✅")) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
