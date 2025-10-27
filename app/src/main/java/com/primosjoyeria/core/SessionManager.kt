package com.primosjoyeria.core

import android.content.Context
import android.content.SharedPreferences

object SessionManager { //quien esta conectado y guardar la sesion
    private const val PREFS_NAME = "PrimosJoyeriaPrefs"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_USERNAME = "username"
    private const val KEY_ROLE = "role" // admin o user

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun login(ctx: Context, username: String, role: String) {
        prefs(ctx).edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putString(KEY_USERNAME, username)
            .putString(KEY_ROLE, role)
            .apply()
    }

    fun logout(ctx: Context) {
        prefs(ctx).edit().clear().apply()
    }

    fun isLoggedIn(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_LOGGED_IN, false)

    fun username(ctx: Context): String? =
        prefs(ctx).getString(KEY_USERNAME, null)

    fun role(ctx: Context): String =
        prefs(ctx).getString(KEY_ROLE, "user") ?: "user"
}
