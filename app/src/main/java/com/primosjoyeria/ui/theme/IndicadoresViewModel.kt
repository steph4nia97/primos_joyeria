package com.primosjoyeria.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.primosjoyeria.data.remote.auth.DolarDto
import com.primosjoyeria.data.remote.auth.RetrofitClient
import kotlinx.coroutines.launch

class IndicadoresViewModel : ViewModel() {

    var dolar by mutableStateOf<DolarDto?>(null)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var loading by mutableStateOf(false)
        private set

    fun cargarDolar() {
        viewModelScope.launch {
            try {
                loading = true
                error = null
                dolar = RetrofitClient.api.getDolarActual()
            } catch (e: Exception) {
                error = e.message ?: "Error al obtener el valor del dólar"
                dolar = null
            } finally {
                loading = false
            }
        }
    }
}