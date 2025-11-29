package com.primosjoyeria.ui.theme.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.primosjoyeria.data.remote.auth.RetrofitClient
import com.primosjoyeria.data.remote.auth.MetalPrecioDto
import kotlinx.coroutines.launch

class MetalViewModel : ViewModel() {

    var oro: MetalPrecioDto? by mutableStateOf(null)
        private set

    var loading: Boolean by mutableStateOf(false)
        private set

    var error: String? by mutableStateOf(null)
        private set

    fun cargarPrecioOro() {
        viewModelScope.launch {
            try {
                loading = true
                error = null
                oro = RetrofitClient.api.getPrecioOro()
            } catch (e: Exception) {
                error = e.message ?: "Error al obtener el valor del oro"
            } finally {
                loading = false
            }
        }
    }
}