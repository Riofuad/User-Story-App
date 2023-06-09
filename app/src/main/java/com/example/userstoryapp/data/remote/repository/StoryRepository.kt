package com.example.userstoryapp.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.userstoryapp.data.db.StoryDatabase
import com.example.userstoryapp.data.remote.StoryRemoteMediator
import com.example.userstoryapp.data.remote.response.Story
import com.example.userstoryapp.data.remote.retrofit.ApiService

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
) {
    fun getStory(): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}