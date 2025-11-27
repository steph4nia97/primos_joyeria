package com.primosjoyeria.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.primosjoyeria.data.remote.auth.LoginRequest
import com.primosjoyeria.data.remote.auth.RegistroRequest
import com.primosjoyeria.data.remote.auth.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val id: Long, val correo: String, val rol: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    var uiState: AuthUiState by mutableStateOf(AuthUiState.Idle)
        private set

    fun resetState() {
        uiState = AuthUiState.Idle
    }

    private fun mapError(prefix: String, e: Exception): AuthUiState.Error {
        val detalle = when (e) {
            is HttpException -> {
                when (e.code()) {
                    400 -> "Solicitud inválida."
                    401 -> "Credenciales incorrectas."
                    403 -> "Acceso no autorizado."
                    404 -> "Usuario no encontrado."
                    500 -> "Error interno del servidor. Inténtalo más tarde."
                    else -> "Error del servidor (${e.code()})."
                }
            }
            is IOException -> "No hay conexión. Revisa tu internet o si el backend está encendido."
            else -> e.localizedMessage ?: "Error desconocido."
        }
        return AuthUiState.Error("$prefix: $detalle")
    }


    fun login(correo: String, password: String) {
        uiState = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.api.login(
                    LoginRequest(correo = correo, password = password)
                )
                uiState = AuthUiState.Success(resp.id, resp.correo, resp.rol)
            } catch (e: Exception) {
                uiState = mapError("Error al iniciar sesión", e)
            }
        }
    }


    fun register(correo: String, password: String, sexo: String, edad: Int) {
        uiState = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.api.register(
                    RegistroRequest(
                        correo = correo,
                        password = password,
                        sexo = sexo,
                        edad = edad
                    )
                )
                uiState = AuthUiState.Success(resp.id, resp.correo, resp.rol)
            } catch (e: Exception) {
                uiState = mapError("Error al registrar", e)
            }
        }
    }


    fun loginAdmin(correo: String, password: String) {
        uiState = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.api.login(
                    LoginRequest(correo = correo, password = password)
                )
                uiState = AuthUiState.Success(resp.id, resp.correo, resp.rol)
            } catch (e: Exception) {
                uiState = mapError("Error al iniciar sesión como admin", e)
            }
        }
    }
}