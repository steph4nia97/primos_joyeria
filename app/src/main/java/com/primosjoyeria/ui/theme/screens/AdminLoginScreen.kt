package com.primosjoyeria.ui.theme.screens
import com.primosjoyeria.ui.theme.AuthViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.primosjoyeria.ui.theme.AuthUiState

@Composable
fun AdminLoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val state = authViewModel.uiState

    LaunchedEffect(state) {
        when (state) {
            is AuthUiState.Success -> {
                if (state.rol == "ADMIN") {
                    // Es admin → entra al panel
                    onLoginSuccess()
                    authViewModel.resetState()
                    error = null
                } else {
                    // Logeó pero no es admin
                    error = "Este usuario no tiene rol ADMIN"
                    authViewModel.resetState()
                }
            }
            is AuthUiState.Error -> {
                error = state.message
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Inicio de sesión - Administrador",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                error = null
                authViewModel.loginAdmin(email, password)
            },
            enabled = state !is AuthUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state is AuthUiState.Loading) "Ingresando..." else "Ingresar")
        }

        Spacer(Modifier.height(10.dp))

        TextButton(onClick = onBack) {
            Text("Volver")
        }
    }
}