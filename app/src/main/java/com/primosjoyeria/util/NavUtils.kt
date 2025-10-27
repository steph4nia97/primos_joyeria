package com.primosjoyeria.util
import com.primosjoyeria.ui.theme.nav.Routes

import androidx.navigation.NavController

fun NavController.navigateToLoginClearingBackstack() {
    navigate(Routes.Login) {
        popUpTo(Routes.Login) { inclusive = true }
        launchSingleTop = true
    }
}