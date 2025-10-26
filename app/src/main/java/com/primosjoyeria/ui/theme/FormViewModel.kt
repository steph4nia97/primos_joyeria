package com.primosjoyeria.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class CampoEstado(
    val valor: String = "",
    val error: String? = null,
    val tocado: Boolean = false
)

data class EstadoFormularioLogin(
    val email: CampoEstado = CampoEstado(),
    val password: CampoEstado = CampoEstado(),
    val enviando: Boolean = false
) {
    val esValido: Boolean
        get() = email.error == null && password.error == null &&
                email.valor.isNotBlank() && password.valor.isNotBlank()
}

class FormViewModel : ViewModel() {
    private val _estado = MutableStateFlow(EstadoFormularioLogin())
    val estado: StateFlow<EstadoFormularioLogin> = _estado

    fun alCambiarEmail(nuevo: String) = _estado.update { st ->
        val err = validarEmail(nuevo)
        st.copy(email = st.email.copy(valor = nuevo, error = if (st.email.tocado) err else null))
    }

    fun alPerderFocoEmail() = _estado.update { st ->
        st.copy(email = st.email.copy(tocado = true, error = validarEmail(st.email.valor)))
    }

    fun alCambiarPassword(nuevo: String) = _estado.update { st ->
        val err = validarPassword(nuevo)
        st.copy(password = st.password.copy(valor = nuevo, error = if (st.password.tocado) err else null))
    }

    fun alPerderFocoPassword() = _estado.update { st ->
        st.copy(password = st.password.copy(tocado = true, error = validarPassword(st.password.valor)))
    }

    fun enviarFormulario(onOk: (email: String, password: String) -> Unit) {
        _estado.update { st ->
            val emailErr = validarEmail(st.email.valor)
            val passErr = validarPassword(st.password.valor)
            st.copy(
                email = st.email.copy(tocado = true, error = emailErr),
                password = st.password.copy(tocado = true, error = passErr)
            )
        }
        val actual = _estado.value
        if (actual.esValido) onOk(actual.email.valor, actual.password.valor)
    }
}

/* === Reglas de validación === */
private fun validarEmail(s: String): String? {
    if (s.isBlank()) return "Ingresa tu correo electrónico"
    val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    return if (!regex.matches(s)) "Correo no válido, ingrese formato abc@abc.cl" else null
}

private fun validarPassword(s: String): String? {
    if (s.isBlank()) return "Ingresa tu contraseña"
    if (s.length < 6) return "Debe tener al menos 6 caracteres"
    return null
}
