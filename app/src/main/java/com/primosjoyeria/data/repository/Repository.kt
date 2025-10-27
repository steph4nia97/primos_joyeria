package com.primosjoyeria.data.repository

import com.primosjoyeria.data.local.ProductoDao
import com.primosjoyeria.data.model.CartItem
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.data.model.User
import com.primosjoyeria.data.local.UserDao

import kotlinx.coroutines.flow.Flow
import com.primosjoyeria.R

// ---- Interfaz del repositorio ----
interface CatalogRepository {
    fun productos(): Flow<List<Product>>
    fun carrito(): Flow<List<CartItem>>
    suspend fun seedIfEmpty()
    suspend fun agregarAlCarrito(p: Product)
    suspend fun cambiarCantidad(productId: Int, delta: Int)
    suspend fun quitarDelCarrito(productId: Int)
    suspend fun vaciarCarrito()

    //Funciones para el panel de administración
    suspend fun agregarProducto(nombre: String, precio: Int)
    suspend fun eliminarProducto(id: Int)

    suspend fun actualizarProducto(id: Int, nombre: String, precio: Int)

    // 👇 NUEVO: Usuarios
    suspend fun registrarUsuario(correo: String, password: String, sexo: String, edad: Int): Result<Unit>
    suspend fun verificarCredenciales(correo: String, password: String): Boolean
}

// ---- Implementación con Room ----
class CatalogRepositoryRoom(
    private val dao: ProductoDao,
    private val userDao: UserDao // 👈 INYECTA UserDao
) : CatalogRepository {

    override fun productos(): Flow<List<Product>> = dao.observarProductos()
    override fun carrito(): Flow<List<CartItem>> = dao.observarCarrito()

    override suspend fun seedIfEmpty() {
        if (dao.countProductos() == 0) {
            val inicial = listOf(
                Product(nombre = "Aros Perla", precio = 12990, imagenRes = R.drawable.aros_perla),
                Product(nombre = "Collar Plata 925", precio = 24990, imagenRes = R.drawable.collar_plata),
                Product(nombre = "Pulsera Acero", precio = 14990, imagenRes = R.drawable.pulsera_acero)
            )
            dao.insertProductos(inicial)
        }
    }

    override suspend fun agregarAlCarrito(p: Product) {
        val updated = dao.actualizarCantidad(p.id, 1)
        if (updated == 0) {
            dao.upsertCarrito(CartItem(productId = p.id, nombre = p.nombre, precio = p.precio, cantidad = 1))
        }
    }

    override suspend fun cambiarCantidad(productId: Int, delta: Int) {
        // Intenta actualizar sin pasar a negativo
        val updated = try {
            dao.actualizarCantidadNoNegativa(productId, delta)
        } catch (_: Exception) {
            // si usas la versión antigua, usa esta línea:
            dao.actualizarCantidad(productId, delta)
        }

        // Si la cantidad quedó en 0 (o no existe), elimínalo del carrito
        val cant = dao.obtenerCantidad(productId) ?: 0
        if (cant <= 0) {
            dao.eliminarDelCarrito(productId)
        }
    }

    override suspend fun quitarDelCarrito(productId: Int) {
        dao.eliminarDelCarrito(productId)
    }

    override suspend fun vaciarCarrito() {
        dao.vaciarCarrito()
    }

    override suspend fun agregarProducto(nombre: String, precio: Int) {
        val nuevo = Product(nombre = nombre, precio = precio, imagenRes = R.drawable.logo)
        dao.insertProductos(listOf(nuevo))
    }

    override suspend fun eliminarProducto(id: Int) {
        dao.eliminarProductoPorId(id)
    }

    override suspend fun actualizarProducto(id: Int, nombre: String, precio: Int) {
        dao.updateCampos(id, nombre, precio)
    }

    // ==== Usuarios ====
    override suspend fun registrarUsuario(
        correo: String,
        pass: String,
        sexo: String,
        edad: Int
    ): Result<Unit> = try {
        if (userDao.countByEmail(correo.trim()) > 0) {
            Result.failure(IllegalArgumentException("El correo ya está registrado"))
        } else {
            userDao.insert(
                User(
                    correo = correo.trim(),
                    pass = pass,                 // en producción: hashea
                    sexo = sexo.trim().uppercase(),
                    edad = edad
                )
            )
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun verificarCredenciales(correo: String, pass: String): Boolean {
        return userDao.validate(correo.trim(), pass) > 0
    }
}