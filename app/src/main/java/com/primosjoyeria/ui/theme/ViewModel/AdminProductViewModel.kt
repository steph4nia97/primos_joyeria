package com.primosjoyeria.ui.theme.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.data.remote.auth.ProductDto

import kotlinx.coroutines.launch
import com.primosjoyeria.data.repository.CatalogRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

sealed class CrearProductoUiState {
    object Idle : CrearProductoUiState()
    object Loading : CrearProductoUiState()
    data class Success(val mensaje: String) : CrearProductoUiState()
    data class Error(val mensaje: String) : CrearProductoUiState()
}

class AdminProductosViewModel(
    private val repository: CatalogRepository
) : ViewModel() {

    // Lista de productos que ve el admin (desde Room, sincronizados con backend)
    val productos: StateFlow<List<Product>> =
        repository.productos()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Estado de creación / edición
    var uiState: CrearProductoUiState by mutableStateOf(CrearProductoUiState.Idle)
        private set

    fun resetState() {
        uiState = CrearProductoUiState.Idle
    }

    // 🔹 Crear producto (con URL)
    fun crearProducto(
        nombre: String,
        precioTexto: String,
        imagenUrlTexto: String
    ) {
        val precio = precioTexto.toIntOrNull()
        if (precio == null || precio <= 0) {
            uiState = CrearProductoUiState.Error("Precio inválido")
            return
        }

        val imagenUrlLimpia = imagenUrlTexto.trim().ifBlank { null }

        uiState = CrearProductoUiState.Loading

        viewModelScope.launch {
            try {
                repository.agregarProducto(
                    nombre = nombre.trim(),
                    precio = precio,
                    imagenUrl = imagenUrlLimpia
                )
                uiState = CrearProductoUiState.Success("Producto creado correctamente")
            } catch (e: Exception) {
                uiState = CrearProductoUiState.Error("Error al crear: ${e.message}")
            }
        }
    }

    // 🔹 Actualizar producto (con URL)
    fun actualizarProducto(
        id: Long,
        nombre: String,
        precioTexto: String,
        imagenUrlTexto: String
    ) {
        val precio = precioTexto.toIntOrNull()
        if (precio == null || precio <= 0) {
            uiState = CrearProductoUiState.Error("Precio inválido")
            return
        }

        val imagenUrlLimpia = imagenUrlTexto.trim().ifBlank { null }

        uiState = CrearProductoUiState.Loading

        viewModelScope.launch {
            try {
                repository.actualizarProducto(
                    id = id,
                    nombre = nombre.trim(),
                    precio = precio,
                    imagenUrl = imagenUrlLimpia
                )
                uiState = CrearProductoUiState.Success("Producto actualizado correctamente")
            } catch (e: Exception) {
                uiState = CrearProductoUiState.Error("Error al actualizar: ${e.message}")
            }
        }
    }

    // 🔹 Eliminar producto
    fun eliminarProducto(id: Long) {
        viewModelScope.launch {
            try {
                repository.eliminarProducto(id)
                // si quieres, podrías actualizar uiState con un mensaje
            } catch (e: Exception) {
                // podrías setear un Error aquí si quieres manejarlo en UI
            }
        }
    }
}
