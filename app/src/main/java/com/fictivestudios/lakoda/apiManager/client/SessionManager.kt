package com.fictivestudios.docsvisor.apiManager.client

import android.content.Context
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.ravebae.utils.Constants.Companion.ACCESS_TOKEN

class SessionManager(context: Context) {
    //    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
//private var prefs=PreferenceUtils.saveString(context.getString(R.string.app_name),"appName")
    companion object {
//        const val USER_TOKEN = "user_token"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {

        PreferenceUtils.saveString(ACCESS_TOKEN,token)
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return PreferenceUtils.getString(ACCESS_TOKEN)

    }
}