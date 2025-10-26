package com.primosjoyeria.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.primosjoyeria.data.model.CartItem
import com.primosjoyeria.data.model.Product

@Database(entities = [Product::class, CartItem::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
}
