package com.primosjoyeria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.primosjoyeria.data.local.AppDb
import com.primosjoyeria.data.repository.CatalogRepositoryRoom
import com.primosjoyeria.ui.theme.nav.AppNav
import com.primosjoyeria.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(applicationContext, AppDb::class.java, "joyeria.db").build()
        val repo = CatalogRepositoryRoom(db.productoDao())

        setContent {
            AppTheme {
                AppNav(repo)
            }
        }
    }
}
