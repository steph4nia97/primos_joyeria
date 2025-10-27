package com.primosjoyeria.data.local

import androidx.room.*
import com.primosjoyeria.data.model.CartItem
import com.primosjoyeria.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    // ======== Catálogo ========

    @Query("SELECT * FROM producto")
    fun observarProductos(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductos(productos: List<Product>)

    @Update
    suspend fun actualizarProducto(producto: Product)

    @Delete
    suspend fun eliminarProducto(producto: Product)

    @Query("DELETE FROM producto WHERE id = :id")
    suspend fun eliminarProductoPorId(id: Int)

    @Query("SELECT COUNT(*) FROM producto")
    suspend fun countProductos(): Int

    @Query("UPDATE producto SET nombre = :nombre, precio = :precio WHERE id = :id")
    suspend fun updateCampos(id: Int, nombre: String, precio: Int)

    @Query("SELECT * FROM producto WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): Product?


    // ======== Carrito ========

    @Query("SELECT * FROM carrito")
    fun observarCarrito(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCarrito(item: CartItem)


    @Query("UPDATE carrito SET cantidad = cantidad + :delta WHERE productId = :productId")
    suspend fun actualizarCantidad(productId: Int, delta: Int): Int

    // Actualiza sin permitir negativos
    @Query("""
        UPDATE carrito 
        SET cantidad = cantidad + :delta 
        WHERE productId = :productId 
        AND (cantidad + :delta) >= 0
    """)
    suspend fun actualizarCantidadNoNegativa(productId: Int, delta: Int): Int

    @Query("DELETE FROM carrito WHERE productId = :productId")
    suspend fun eliminarDelCarrito(productId: Int)

    @Query("DELETE FROM carrito")
    suspend fun vaciarCarrito()


    @Query("SELECT cantidad FROM carrito WHERE productId = :productId LIMIT 1")
    suspend fun obtenerCantidad(productId: Int): Int?


    @Query("SELECT COALESCE(SUM(precio * cantidad), 0) FROM carrito")
    fun observarTotal(): Flow<Int>
}
