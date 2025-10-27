package com.primosjoyeria.data.local

import androidx.room.*
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.data.model.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    // ===== Catálogo =====
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



    // ===== Carrito =====
    @Query("SELECT * FROM carrito")
    fun observarCarrito(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCarrito(item: CartItem)

    @Query("UPDATE carrito SET cantidad = cantidad + :delta WHERE productId = :productId")
    suspend fun actualizarCantidad(productId: Int, delta: Int): Int

    @Query("DELETE FROM carrito WHERE productId = :productId")
    suspend fun eliminarDelCarrito(productId: Int)

    @Query("DELETE FROM carrito")
    suspend fun vaciarCarrito()
}
