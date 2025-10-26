package com.primosjoyeria.ui.theme.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.primosjoyeria.data.repository.CatalogRepository
import com.primosjoyeria.ui.theme.AppViewModel
import com.primosjoyeria.ui.theme.UiState
import com.primosjoyeria.ui.theme.screens.CarritoScreen
import com.primosjoyeria.ui.theme.screens.LoginScreen

import com.primosjoyeria.ui.theme.screens.CatalogoScreen
import com.primosjoyeria.ui.theme.nav.Routes

@Composable
fun AppNav(repo: CatalogRepository) {
    val nav = rememberNavController()

    val vm: AppViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AppViewModel(repo) as T
        }
    )

    LaunchedEffect(Unit) { vm.seedIfEmpty() }

    // 👇 Nómbralo distinto y tipalo explícitamente
    val uiState: UiState = vm.state.collectAsStateWithLifecycle().value
    NavHost(navController = nav, startDestination = Routes.Login) {
        composable(Routes.Login) {
            LoginScreen(
                alIniciarSesion = { correo, pass ->
                    // Aquí puedes validar el login o navegar directo
                    nav.navigate(Routes.Catalogo)
                },
                alRegistrarClick = {
                    // Podrías agregar luego una pantalla de registro
                }
            )
        }

        composable(Routes.Catalogo) {
            CatalogoScreen(
                state = uiState,
                onAdd = vm::addToCart,
                goCarrito = { nav.navigate(Routes.Carrito) }
            )
        }

        composable(Routes.Carrito) {
            CarritoScreen(
                state = uiState,
                onInc = vm::inc,
                onDec = vm::dec,
                onRemove = vm::remove,
                onClear = vm::clearCart,
                goBack = { nav.popBackStack() }
            )
        }
    }
}