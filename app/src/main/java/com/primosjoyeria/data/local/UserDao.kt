package com.primosjoyeria.data.local

import androidx.room.*
import com.primosjoyeria.data.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long

    @Query("SELECT COUNT(*) FROM usuario WHERE correo = :correo")
    suspend fun countByEmail(correo: String): Int

    @Query("SELECT * FROM usuario WHERE correo = :correo LIMIT 1")
    suspend fun findByEmail(correo: String): User?

    @Query("SELECT COUNT(*) FROM usuario WHERE correo = :correo AND pass = :pass")
    suspend fun validate(correo: String, pass: String): Int
}
