package com.primosjoyeria.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.primosjoyeria.R
import com.primosjoyeria.ui.theme.FormViewModel
import com.primosjoyeria.ui.theme.EstadoFormularioLogin
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.animateFloatAsState
import com.primosjoyeria.ui.theme.AuthUiState
import com.primosjoyeria.ui.theme.AuthViewModel
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    alIniciarSesion: () -> Unit,
    alRegistrarClick: () -> Unit,
    onAdminClick: () -> Unit,
    vm: FormViewModel = viewModel()
) {
    val formulario: EstadoFormularioLogin = vm.estado.collectAsState().value
    val authState = authViewModel.uiState
    var mensajeError by remember { mutableStateOf<String?>(null) }

    // 💫 EFECTO DEL LOGO: animación infinita (latido suave)
    val infiniteTransition = rememberInfiniteTransition(label = "logoPulse")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScaleAnim"
    )

    LaunchedEffect(authState) {
        when (authState) {
            is AuthUiState.Success -> {
                if (authState.rol == "CLIENTE") {
                    mensajeError = null
                    alIniciarSesion()
                    authViewModel.resetState()
                } else {
                    mensajeError =
                        "Estas credenciales son de administrador. Usa el login de administrador."
                    authViewModel.resetState()
                }
            }

            is AuthUiState.Error -> mensajeError = authState.message
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
                    .graphicsLayer(
                        scaleX = logoScale,
                        scaleY = logoScale
                    )
                    .padding(bottom = 16.dp)
            )

            Text("Bienvenido a Primos Joyería", style = MaterialTheme.typography.headlineSmall)

            // ==== EMAIL ====
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

            // ==== PASSWORD ====
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

            // ==== BOTÓN LOGIN ====
            PressableButton(
                onClick = {
                    vm.enviarFormulario { email, password ->
                        mensajeError = null
                        authViewModel.login(email, password)
                    }
                },
                enabled = formulario.email.valor.isNotBlank() &&
                        formulario.password.valor.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }

            // ==== ERROR ====
            AnimatedVisibility(
                visible = mensajeError != null,
                enter = slideInVertically { fullHeight -> -fullHeight } + fadeIn(),
                exit = slideOutVertically { fullHeight -> -fullHeight } + fadeOut()
            ) {
                Text(
                    text = mensajeError.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            TextButton(onClick = alRegistrarClick) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }

            TextButton(onClick = onAdminClick) {
                Text("¿Eres administrador? Inicia sesión aquí")
            }
        }
    }
}

@Composable
private fun PressableButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        label = "pressScale"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interaction,
        modifier = modifier.scale(scale)
    ) {
        content()
    }
}