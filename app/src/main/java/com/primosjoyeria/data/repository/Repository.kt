package com.primosjoyeria.data.repository

import com.primosjoyeria.data.local.ProductoDao
import com.primosjoyeria.data.model.CartItem
import com.primosjoyeria.data.model.Product
import kotlinx.coroutines.flow.Flow

// ---- Interfaz del repositorio ----
interface CatalogRepository {
    fun productos(): Flow<List<Product>>
    fun carrito(): Flow<List<CartItem>>
    suspend fun seedIfEmpty()
    suspend fun agregarAlCarrito(p: Product)
    suspend fun cambiarCantidad(productId: Int, delta: Int)
    suspend fun quitarDelCarrito(productId: Int)
    suspend fun vaciarCarrito()

    // 🆕 Funciones para el panel de administración
    suspend fun agregarProducto(nombre: String, precio: Int)
    suspend fun eliminarProducto(id: Int)
}

// ---- Implementación con Room ----
class CatalogRepositoryRoom(private val dao: ProductoDao) : CatalogRepository {

    override fun productos(): Flow<List<Product>> = dao.observarProductos()
    override fun carrito(): Flow<List<CartItem>> = dao.observarCarrito()

    override suspend fun seedIfEmpty() {
        if (dao.countProductos() == 0) {
            val inicial = listOf(
                Product(nombre = "Aros Perla", precio = 12990),
                Product(nombre = "Collar Plata 925", precio = 24990),
                Product(nombre = "Pulsera Acero", precio = 14990)
            )
            dao.insertProductos(inicial)
        }
    }

    override suspend fun agregarAlCarrito(p: Product) {
        val updated = dao.actualizarCantidad(p.id, 1)
        if (updated == 0) {
            dao.upsertCarrito(
                CartItem(
                    productId = p.id,
                    nombre = p.nombre,
                    precio = p.precio,
                    cantidad = 1
                )
            )
        }
    }

    override suspend fun cambiarCantidad(productId: Int, delta: Int) {
        dao.actualizarCantidad(productId, delta)
    }

    override suspend fun quitarDelCarrito(productId: Int) {
        dao.eliminarDelCarrito(productId)
    }

    override suspend fun vaciarCarrito() {
        dao.vaciarCarrito()
    }

    // 🟩 Agregar nuevo producto (desde el panel admin)
    override suspend fun agregarProducto(nombre: String, precio: Int) {
        val nuevo = Product(nombre = nombre, precio = precio)
        dao.insertProductos(listOf(nuevo))
    }

    // 🟩 Eliminar producto por ID
    override suspend fun eliminarProducto(id: Int) {
        dao.eliminarProductoPorId(id)
    }
}
