package com.primosjoyeria.data.repository

import com.primosjoyeria.data.local.ProductoDao
import com.primosjoyeria.data.local.UserDao
import com.primosjoyeria.data.model.CartItem
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.data.model.User
import kotlinx.coroutines.flow.Flow
import com.primosjoyeria.data.remote.auth.ApiService
import com.primosjoyeria.data.remote.auth.ProductDto
import com.primosjoyeria.data.remote.auth.ProductRequest
private fun ProductDto.toEntity(): Product =
    Product(
        id = this.id,
        nombre = this.nombre,
        precio = this.precio,
        imagenUrl = this.imagenUrl
    )

// ---- Contrato del repositorio ----
interface CatalogRepository {
    fun productos(): Flow<List<Product>>
    fun carrito(): Flow<List<CartItem>>

    suspend fun seedIfEmpty()

    // Carrito
    suspend fun agregarAlCarrito(p: Product)
    suspend fun cambiarCantidad(productId: Long, delta: Int)
    suspend fun quitarDelCarrito(productId: Long)
    suspend fun vaciarCarrito()

    suspend fun agregarProducto(
        nombre: String,
        precio: Int,
        imagenUrl: String?
    )    suspend fun eliminarProducto(id: Long)
    suspend fun actualizarProducto(
        id: Long,
        nombre: String,
        precio: Int,
        imagenUrl: String?
    )

    // Users
    suspend fun registrarUsuario(
        correo: String,
        password: String,
        sexo: String,
        edad: Int
    ): Result<Unit>

    suspend fun verificarCredenciales(correo: String, password: String): Boolean
}

// ---- Implementación: Room + Backend ----
class CatalogRepositoryRoom(
    private val dao: ProductoDao,
    private val userDao: UserDao,
    private val api: ApiService
) : CatalogRepository {

    override fun productos(): Flow<List<Product>> = dao.observarProductos()
    override fun carrito(): Flow<List<CartItem>> = dao.observarCarrito()


    override suspend fun seedIfEmpty() {
        try {
            // 1) Traer productos desde el backend
            val remotos: List<ProductDto> = api.getProductos()
            val entidades: List<Product> = remotos.map { it.toEntity() }

            // 2) Limpiar lo que haya en Room
            dao.borrarTodo()


            dao.insertProductos(entidades)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // ===== Carrito =====

    override suspend fun agregarAlCarrito(p: Product) {
        val updated = dao.actualizarCantidadNoNegativa(p.id, +1)
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

    override suspend fun cambiarCantidad(productId: Long, delta: Int) {
        dao.actualizarCantidadNoNegativa(productId, delta)
        val cant = dao.obtenerCantidad(productId) ?: 0
        if (cant <= 0) {
            dao.eliminarDelCarrito(productId)
        }
    }

    override suspend fun quitarDelCarrito(productId: Long) {
        dao.eliminarDelCarrito(productId)
    }

    override suspend fun vaciarCarrito() {
        dao.vaciarCarrito()
    }

    // ===== Admin: ahora opera sobre el BACKEND =====

    // 🔹 CREAR
    override suspend fun agregarProducto(
        nombre: String,
        precio: Int,
        imagenUrl: String?
    ) {
        val request = ProductRequest(
            nombre = nombre.trim(),
            precio = precio,
            imagenUrl = imagenUrl?.trim().takeUnless { it.isNullOrEmpty() }
        )

        // 1) Crear en backend (Oracle)
        val creado: ProductDto = api.crearProducto(request)

        // 2) Reflejar en Room (cache local)
        dao.insertProductos(listOf(creado.toEntity()))
    }

    override suspend fun eliminarProducto(id: Long) {
        // 1) Eliminar en backend
        api.eliminarProducto(id)

        // 2) Eliminar en Room
        dao.eliminarProductoPorId(id)
    }

    // 🔹 ACTUALIZAR
    override suspend fun actualizarProducto(
        id: Long,
        nombre: String,
        precio: Int,
        imagenUrl: String?
    ) {
        val request = ProductRequest(
            nombre = nombre.trim(),
            precio = precio,
            imagenUrl = imagenUrl?.trim().takeUnless { it.isNullOrEmpty() }
        )

        // 1) Actualizar en backend
        val actualizado: ProductDto = api.actualizarProducto(id, request)

        // 2) Reflejar cambio en Room
        dao.updateCampos(
            id = id,
            nombre = actualizado.nombre,
            precio = actualizado.precio,
            imagenUrl = actualizado.imagenUrl
        )
    }

    // ===== Users (igual que antes, solo Room) =====

    override suspend fun registrarUsuario(
        correo: String,
        password: String,
        sexo: String,
        edad: Int
    ): Result<Unit> = try {
        if (userDao.countByEmail(correo.trim()) > 0) {
            Result.failure(IllegalArgumentException("El correo ya está registrado"))
        } else {
            userDao.insert(
                User(
                    correo = correo.trim(),
                    pass = password,
                    sexo = sexo.trim().uppercase(),
                    edad = edad
                )
            )
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun verificarCredenciales(correo: String, password: String): Boolean {
        return userDao.validate(correo.trim(), password) > 0
    }
}