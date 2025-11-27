package com.primosjoyeria.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import com.primosjoyeria.ui.theme.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.primosjoyeria.ui.theme.AuthUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    authViewModel: AuthViewModel,   //viene de appnav
    onRegistroExitoso: () -> Unit,
    goBack: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("X") }
    var edad by remember { mutableStateOf("") }

    var mensaje by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val authState = authViewModel.uiState

    // VALIDACIONES
    fun correoValido(c: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(c).matches()

    fun passValida(p: String) = p.length >= 6
    fun sexoValido(s: String) = s.uppercase() in listOf("F", "M", "X")
    fun edadValida(e: Int?) = e != null && e in 1..100


    LaunchedEffect(authState) {
        when (authState) {
            is AuthUiState.Success -> {
                mensaje = "✅ Cuenta creada correctamente"
                delay(1200)
                authViewModel.resetState()
                onRegistroExitoso()
            }
            is AuthUiState.Error -> {
                mensaje = authState.message
            }
            else -> Unit
        }
    }

    // UI
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

            // CORREO
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = correo.isNotBlank() && !correoValido(correo)
            )
            if (correo.isNotBlank() && !correoValido(correo)) {
                Text("Correo inválido", color = MaterialTheme.colorScheme.error)
            }

            // CONTRASEÑA
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Contraseña (mínimo 6 caracteres)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = pass.isNotBlank() && !passValida(pass)
            )

            // SEXO
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
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("F", "M", "X").forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                sexo = it
                                expanded = false
                            }
                        )
                    }
                }
            }

            // EDAD
            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it.filter(Char::isDigit) },
                label = { Text("Edad (1–100)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = edad.isNotBlank() &&
                        !(edad.toIntOrNull()?.let { it in 1..100 } ?: false)
            )

            // BOTÓN REGISTRAR
            Button(
                onClick = {
                    val e = edad.toIntOrNull()

                    when {
                        correo.isBlank() || pass.isBlank() || sexo.isBlank() || edad.isBlank() ->
                            mensaje = "Completa todos los campos"

                        !correoValido(correo) ->
                            mensaje = "Correo inválido"

                        !passValida(pass) ->
                            mensaje = "La contraseña debe tener mínimo 6 caracteres"

                        !sexoValido(sexo) ->
                            mensaje = "Sexo inválido (F, M, X)"

                        !edadValida(e) ->
                            mensaje = "Edad debe ser entre 1 y 100"

                        else -> {
                            mensaje = null
                            // 👇 Llamamos directo al ViewModel, él ya lanza su corrutina
                            authViewModel.register(
                                correo.trim(),
                                pass,
                                sexo,
                                e!!
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar cuenta")
            }

            // MENSAJE
            mensaje?.let {
                Text(
                    it,
                    color = if ("✅" in it) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}