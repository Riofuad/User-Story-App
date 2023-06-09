@file:Suppress("UNCHECKED_CAST")

package com.example.userstoryapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.userstoryapp.data.db.StoryDatabase
import com.example.userstoryapp.data.remote.repository.StoryRepository
import com.example.userstoryapp.data.remote.retrofit.ApiService

class StoryViewModelFactory(
    val context: Context,
    private val apiService: ApiService,
    val token: String
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryPagerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val database = StoryDatabase.getDatabase(context)
            return StoryPagerViewModel(StoryRepository(database, apiService, token)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}