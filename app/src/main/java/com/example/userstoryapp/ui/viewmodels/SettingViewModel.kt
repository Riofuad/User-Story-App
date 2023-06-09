package com.example.userstoryapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.userstoryapp.utils.SettingPreferences
import kotlinx.coroutines.launch

class SettingViewModel(private val preferences: SettingPreferences) : ViewModel() {
    fun getUserPreferences(property: String): LiveData<String> {
        return when (property) {
            SettingPreferences.Companion.UserPreferences.UserUID.name -> preferences.getUserUid()
                .asLiveData()
            SettingPreferences.Companion.UserPreferences.UserToken.name -> preferences.getUserToken()
                .asLiveData()
            SettingPreferences.Companion.UserPreferences.UserName.name -> preferences.getUserName()
                .asLiveData()
            SettingPreferences.Companion.UserPreferences.UserEmail.name -> preferences.getUserEmail()
                .asLiveData()
            else -> preferences.getUserUid().asLiveData()
        }
    }

    fun setUserPreferences(
        userToken: String,
        userUid: String,
        userName: String,
        userEmail: String
    ) {
        viewModelScope.launch {
            preferences.saveLogin(userToken, userUid, userName, userEmail)
        }
    }

    fun clearUserPreferences() {
        viewModelScope.launch {
            preferences.clearLogin()
        }
    }
}