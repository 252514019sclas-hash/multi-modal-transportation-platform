package com.simats.multimodaltransportationrecommendation.data.local

import android.content.Context
import android.content.SharedPreferences
import com.simats.multimodaltransportationrecommendation.data.model.auth.UserData

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "movesmart_user_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone"

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun saveUserSession(userData: UserData) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, userData.userId)
            putString(KEY_FULL_NAME, userData.fullName)
            putString(KEY_EMAIL, userData.email)
            putString(KEY_PHONE, userData.phone)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUser(): UserData? {
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        val fullName = prefs.getString(KEY_FULL_NAME, "") ?: ""
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val phone = prefs.getString(KEY_PHONE, null)
        return UserData(userId = userId, fullName = fullName, email = email, phone = phone)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
