package com.example.userstoryapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        enum class UserPreferences {
            UserUID, UserName, UserEmail, UserToken, UserLastLogin
        }

        @Volatile
        private var INSTANCE: SettingPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    private val token = stringPreferencesKey(UserPreferences.UserToken.name)
    private val uid = stringPreferencesKey(UserPreferences.UserUID.name)
    private val name = stringPreferencesKey(UserPreferences.UserName.name)
    private val email = stringPreferencesKey(UserPreferences.UserEmail.name)
    private val lastLogin = stringPreferencesKey(UserPreferences.UserLastLogin.name)

    fun getUserToken(): Flow<String> = dataStore.data.map { it[token] ?: "Not Set" }
    fun getUserUid(): Flow<String> = dataStore.data.map { it[uid] ?: "Not Set" }
    fun getUserName(): Flow<String> = dataStore.data.map { it[name] ?: "Not Set" }
    fun getUserEmail(): Flow<String> = dataStore.data.map { it[email] ?: "Not Set" }

    suspend fun saveLogin(userToken: String, userUid: String, userName: String, userEmail: String) {
        dataStore.edit { pref ->
            pref[token] = userToken
            pref[uid] = userUid
            pref[name] = userName
            pref[email] = userEmail
            pref[lastLogin] = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(
                Date()
            )
        }
    }

    suspend fun clearLogin() {
        dataStore.edit { pref -> pref.clear() }
    }
}