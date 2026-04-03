package com.zyablik.trucklognew.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("trucklog_prefs", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun saveUserRole(role: String) {
        prefs.edit().putString("user_role", role).apply()
    }

    fun getUserRole(): String? {
        return prefs.getString("user_role", null)
    }

    fun saveUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
    }

    fun getUserName(): String? {
        return prefs.getString("user_name", null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
