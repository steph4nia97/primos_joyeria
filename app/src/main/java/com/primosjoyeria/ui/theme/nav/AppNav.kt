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
import com.primosjoyeria.ui.theme.screens.AdminLoginScreen
import com.primosjoyeria.ui.theme.screens.AdminPanelScreen
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

    val uiState: UiState = vm.state.collectAsStateWithLifecycle().value

    NavHost(navController = nav, startDestination = Routes.Login) {
        composable(Routes.Login) {
            LoginScreen(
                alIniciarSesion = { correo, pass ->
                    nav.navigate(Routes.Catalogo)
                },
                alRegistrarClick = { /* registrar */ },
                onAdminClick = { nav.navigate(Routes.AdminLogin) } // 👈 aquí lo conectas
            )
        }



        // Resto de tus pantallas (catálogo, carrito, etc.)
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

        // 👉 Ruta nueva del login admin (ya creada antes)
        composable(Routes.AdminLogin) {
            AdminLoginScreen(
                onLoginSuccess = { nav.navigate(Routes.AdminPanel) },
                onBack = { nav.popBackStack() }
            )
        }

        composable(Routes.AdminPanel) {
            AdminPanelScreen(
                repo = repo,
                goBack = { nav.popBackStack() }
            )
        }

    }
}
