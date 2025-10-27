package com.primosjoyeria.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.primosjoyeria.data.model.CartItem
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.data.model.User

@Database(
    entities = [Product::class, CartItem::class, User::class],
    version = 3
)
abstract class AppDb : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun userDao(): UserDao
}