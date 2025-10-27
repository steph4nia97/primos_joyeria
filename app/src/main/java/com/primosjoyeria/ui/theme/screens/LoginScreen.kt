package com.primosjoyeria.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.primosjoyeria.ui.theme.FormViewModel
import com.primosjoyeria.ui.theme.EstadoFormularioLogin
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import com.primosjoyeria.R


@Composable
fun LoginScreen(
    alIniciarSesion: (String, String) -> Unit,
    alRegistrarClick: () -> Unit,
    onAdminClick: () -> Unit,  // boton admin
    vm: FormViewModel = viewModel()
) {
    val formulario: EstadoFormularioLogin = vm.estado.collectAsState().value

    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Primos Joyería",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )

            Text("Bienvenido a Primos Joyería", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = formulario.email.valor,
                onValueChange = { vm.alCambiarEmail(it) },
                label = { Text("Correo electrónico") },
                isError = formulario.email.error != null,
                supportingText = {
                    formulario.email.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    if (formulario.email.error != null)
                        Icon(Icons.Filled.Error, contentDescription = "Error")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formulario.password.valor,
                onValueChange = { vm.alCambiarPassword(it) },
                label = { Text("Contraseña") },
                isError = formulario.password.error != null,
                supportingText = {
                    formulario.password.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    if (formulario.password.error != null)
                        Icon(Icons.Filled.Error, contentDescription = "Error")
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { vm.enviarFormulario(alIniciarSesion) },
                enabled = formulario.email.valor.isNotBlank() &&
                        formulario.password.valor.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }

            TextButton(onClick = alRegistrarClick) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }


            TextButton(onClick = { onAdminClick() }) {
                Text("¿Eres administrador? Inicia sesión aquí")
            }


        }
    }
}
