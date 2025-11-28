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
import com.primosjoyeria.ui.theme.AuthViewModel
import com.primosjoyeria.ui.theme.UiState
import com.primosjoyeria.ui.theme.screens.AdminLoginScreen
import com.primosjoyeria.ui.theme.screens.AdminPanelScreen
import com.primosjoyeria.ui.theme.screens.CarritoScreen
import com.primosjoyeria.ui.theme.screens.CatalogoScreen
import com.primosjoyeria.ui.theme.screens.LoginScreen
import com.primosjoyeria.ui.theme.screens.RegistroScreen
import androidx.lifecycle.viewmodel.compose.viewModel

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

    // 🔹 ViewModel de autenticación (login / registro / admin)
    val authViewModel: AuthViewModel = viewModel()

    // Semilla inicial de productos
    LaunchedEffect(Unit) {
        vm.seedIfEmpty()
    }

    val uiState: UiState = vm.state.collectAsStateWithLifecycle().value

    NavHost(
        navController = nav,
        startDestination = Routes.Login
    ) {

        // ========= LOGIN CLIENTE =========
        composable(Routes.Login) {
            LoginScreen(
                authViewModel = authViewModel,
                alIniciarSesion = {
                    nav.navigate(Routes.Catalogo) {
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                alRegistrarClick = { nav.navigate(Routes.Registro) },
                onAdminClick = { nav.navigate(Routes.AdminLogin) }
            )
        }

        // ========= REGISTRO CLIENTE =========
        composable(Routes.Registro) {
            RegistroScreen(
                authViewModel = authViewModel,     // ⬅️ si tu RegistroScreen usa AuthViewModel
                onRegistroExitoso = {
                    nav.popBackStack()
                },
                goBack = {
                    nav.popBackStack()
                }
            )
        }

        // ========= CATÁLOGO CLIENTE =========
        composable(Routes.Catalogo) {
            CatalogoScreen(
                state = uiState,
                viewModel = vm,                 // 👈 aquí sí va la variable vm
                onAdd = vm::addToCart,
                goCarrito = { nav.navigate(Routes.Carrito) },
                onLogout = {
                    nav.navigate(Routes.Login) {
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ========= CARRITO =========
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

        // ========= LOGIN ADMIN =========
        composable(Routes.AdminLogin) {
            AdminLoginScreen(
                authViewModel = authViewModel,     // ⬅️ aquí también
                onLoginSuccess = {
                    nav.navigate(Routes.AdminPanel) {
                        launchSingleTop = true
                    }
                },
                onBack = { nav.popBackStack() }
            )
        }

        // ========= PANEL ADMIN =========
        composable(Routes.AdminPanel) {
            AdminPanelScreen(
                repo = repo,
                goBack = {
                    nav.navigate(Routes.Login) {
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}