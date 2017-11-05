package br.com.gwelter.smack.Utilities

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley

/**
 * Created by Guilherme on 05/11/2017.
 */
class SharedPrefs (context: Context) {
    val PREFS_FILENAME = "prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    val IS_LOGED_IN = "isLogedIn"
    val AUTH_TOKEN = "auth_token"
    val USER_EMAIL = "user_email"

    var isLogedIn: Boolean
        get() = prefs.getBoolean(IS_LOGED_IN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGED_IN, value).apply()

    var authToken: String
        get() = prefs.getString(AUTH_TOKEN, "")
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()

    var userEmail: String
        get() = prefs.getString(USER_EMAIL, "")
        set(value) = prefs.edit().putString(USER_EMAIL, value).apply()

    val requestQueue = Volley.newRequestQueue(context)
}