package com.example.userstoryapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.userstoryapp.data.remote.repository.StoryRepository
import com.example.userstoryapp.data.remote.response.Story

class StoryPagerViewModel(repository: StoryRepository) : ViewModel() {
    val story: LiveData<PagingData<Story>> = repository.getStory().cachedIn(viewModelScope)
}