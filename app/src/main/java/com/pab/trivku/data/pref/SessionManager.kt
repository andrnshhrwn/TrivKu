package com.pab.trivku.data.pref

import android.content.Context
import com.pab.trivku.data.models.User

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveSession(user: User) {
        val editor = prefs.edit()
        editor.putInt("user_id", user.id)
        editor.putString("user_name", user.username)
        editor.putString("user_email", user.email)
        editor.putBoolean("is_logged_in", true)
        editor.apply()
    }

    // Update Session
    fun updateUser(user: User) {
        val editor = prefs.edit()
        editor.putString("user_name", user.username)
        editor.putString("user_email", user.email)
        editor.apply()
    }

    fun getUserId(): Int = prefs.getInt("user_id", -1)

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun getUserName(): String? = prefs.getString("user_name", "")

    fun getUserEmail(): String? = prefs.getString("user_email", "")

    fun logout() {
        prefs.edit().clear().apply()
    }
}