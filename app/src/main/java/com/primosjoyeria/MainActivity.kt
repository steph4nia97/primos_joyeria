package com.primosjoyeria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.primosjoyeria.data.local.AppDb

import com.primosjoyeria.data.repository.CatalogRepositoryRoom
import com.primosjoyeria.ui.theme.AppTheme
import com.primosjoyeria.ui.theme.nav.AppNav
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDb::class.java,
            "primos_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        val repo = CatalogRepositoryRoom(
            dao = db.productoDao(),
            userDao = db.userDao()
        )
        setContent {
            AppTheme {
                AppNav(repo)
            }
        }
    }
}
