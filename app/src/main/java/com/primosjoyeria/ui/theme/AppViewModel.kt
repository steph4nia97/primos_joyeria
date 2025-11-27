package com.primosjoyeria.ui.theme


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.data.repository.CatalogRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class AppViewModel(private val repo: CatalogRepository) : ViewModel() {


    val state: StateFlow<UiState> =
        combine(repo.productos(), repo.carrito()) { prods, cart ->
            UiState(productos = prods, carrito = cart)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState()
        )


    fun seedIfEmpty() = viewModelScope.launch { repo.seedIfEmpty() }


    fun addToCart(p: Product) = viewModelScope.launch { repo.agregarAlCarrito(p) }
    fun inc(id: Int)        = viewModelScope.launch { repo.cambiarCantidad(id, +1) }
    fun dec(id: Int)        = viewModelScope.launch { repo.cambiarCantidad(id, -1) }
    fun remove(id: Int)     = viewModelScope.launch { repo.quitarDelCarrito(id) }
    fun clearCart()         = viewModelScope.launch { repo.vaciarCarrito() }
}
