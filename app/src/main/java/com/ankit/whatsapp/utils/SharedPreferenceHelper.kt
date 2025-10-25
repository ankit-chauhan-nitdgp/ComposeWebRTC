package com.ankit.whatsapp.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.ankit.whatsapp.data.User
import com.google.gson.Gson

object SharedPreferenceHelper {

    const val SHARED_PREF_NAME = "WHATSAPP_PREFERENCE"
    private fun prefs(context: Context) =
        context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)

    const val IS_LOGGED_IN_KEY = "IS_LOGGED_IN_KEY"
    const val USER_DATA = "user_data"


    // String preferences
    fun readStringSharedPreference(context: Context, key: String) : String? {
        val pref = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        return pref.getString(key, "")
    }

    fun writeStringSharedPreference(context: Context, key: String, value: String) {
        val pref = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        pref.edit {
            putString(key, value)
        }
    }

    // Boolean preferences
    fun readBooleanSharedPreference(context: Context, key: String) : Boolean? {
        val pref = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        return pref.getBoolean(key, false)
    }

    fun writeBooleanSharedPreference(context: Context, key: String, value: Boolean) {
        val pref = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        pref.edit {
            putBoolean(key, value)
        }
    }

    // Integer preferences
    fun readIntegerSharedPreference(context: Context, key: String) : Int? {
        val pref = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        return pref.getInt(key, -1)
    }

    fun writeIntegerSharedPreference(context: Context, key: String, value: Int) {
        val pref = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        pref.edit {
            putInt(key, value)
        }
    }

    // to maintain current user data
    fun writeUserData(context: Context, user: User) {
        val json = Gson().toJson(user)
        prefs(context).edit { putString(USER_DATA, json) }
    }

    fun readUserData(context: Context): User? {
        val json = prefs(context).getString(USER_DATA, null) ?: return null
        return Gson().fromJson(json, User::class.java)
    }

    fun clearUserData(context: Context) {
        prefs(context).edit { remove(USER_DATA) }
    }


}