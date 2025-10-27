package com.primosjoyeria.ui.theme.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.primosjoyeria.ui.theme.screens.CatalogoScreen
import com.primosjoyeria.ui.theme.screens.RegistroScreen
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()
    var loginError by remember { mutableStateOf<String?>(null) }

    NavHost(navController = nav, startDestination = Routes.Login) {

        // LOGIN (usuarios de tabla usuario)
        composable(Routes.Login) {
            LoginScreen(
                alIniciarSesion = { correo, pass ->
                    scope.launch {
                        val ok = repo.verificarCredenciales(correo, pass)
                        if (ok) {
                            loginError = null
                            nav.navigate(Routes.Catalogo) {
                                popUpTo(Routes.Login) { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            loginError = "Usuario no registrado o credenciales incorrectas"
                        }
                    }
                },
                alRegistrarClick = { nav.navigate(Routes.Registro) },
                onAdminClick = { nav.navigate(Routes.AdminLogin) },
                mensajeError = loginError
            )
        }
        // REGISTRO
        composable(Routes.Registro) {
            RegistroScreen(
                onRegistrar = { correo, pass, sexo, edad ->
                    repo.registrarUsuario(correo, pass, sexo, edad)
                },
                onRegistroExitoso = { nav.popBackStack() }, // vuelve al Login
                goBack = { nav.popBackStack() }
            )
        }

        composable(Routes.Catalogo) {
            CatalogoScreen(
                state = uiState,
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
        // CARRITO
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

        // ADMIN LOGIN
        composable(Routes.AdminLogin) {
            AdminLoginScreen(
                onLoginSuccess = { nav.navigate(Routes.AdminPanel) },
                onBack = { nav.popBackStack() }
            )
        }

        // ADMIN PANEL
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
