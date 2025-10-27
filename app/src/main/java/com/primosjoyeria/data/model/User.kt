package com.primosjoyeria.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "usuario",
    indices = [Index(value = ["correo"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val correo: String,
    val pass: String, // para producción usa hashing; aquí simple por demo
    val sexo: String, // "M", "F", "Otro"
    val edad: Int
)
