package com.primosjoyeria.data.local

import androidx.room.*
import com.primosjoyeria.data.model.CartItem
import com.primosjoyeria.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos ORDER BY nombre")
    fun observarProductos(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductos(items: List<Product>)

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun countProductos(): Int

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
