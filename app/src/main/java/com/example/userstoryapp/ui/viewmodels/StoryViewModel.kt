package com.example.userstoryapp.ui.viewmodels

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.userstoryapp.R
import com.example.userstoryapp.data.remote.response.Story
import com.example.userstoryapp.data.remote.response.StoryList
import com.example.userstoryapp.data.remote.response.StoryUpload
import com.example.userstoryapp.data.remote.retrofit.ApiConfig
import com.example.userstoryapp.utils.Helper
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoryViewModel : ViewModel() {
    val isLoading = MutableLiveData(View.GONE)
    val isSuccessUploadStory = MutableLiveData(false)
    val storyList = MutableLiveData<List<Story>>()
    val error = MutableLiveData("")
    val isError = MutableLiveData(true)
    val isLocationPicked = MutableLiveData(false)
    val lat = MutableLiveData(0.0)
    val lon = MutableLiveData(0.0)
    val coordinateTemp = MutableLiveData(LatLng(-5.135399, 119.423790))

    fun loadStoryLocation(context: Context, token: String) {
        val client = ApiConfig.getApiService().storyListLocation(token, 100)
        client.enqueue(object : Callback<StoryList> {
            override fun onResponse(call: Call<StoryList>, response: Response<StoryList>) {
                if (response.isSuccessful) {
                    isError.postValue(false)
                    storyList.postValue(response.body()?.listStory)
                } else {
                    isError.postValue(true)
                    error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryList>, t: Throwable) {
                isLoading.postValue(View.GONE)
                isError.postValue(true)
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue("${context.getString(R.string.error_fetch_data)} : ${t.message}")
            }
        })
    }

    fun uploadStory(
        context: Context,
        token: String,
        image: File,
        description: String,
        withLocation: Boolean = false,
        lat: String? = null,
        lon: String? = null
    ) {
        isLoading.postValue(View.VISIBLE)
        val storyDescription = description.toRequestBody("text/plain".toMediaType())
        val reqImageFile =
            Helper.reduceFileImage(image).asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", image.name, reqImageFile)

        val client = if (withLocation) {
            val positionLat = lat?.toRequestBody("text/plain".toMediaType())
            val positionLon = lon?.toRequestBody("text/plain".toMediaType())
            ApiConfig.getApiService()
                .storyUpload(token, imageMultipart, storyDescription, positionLat!!, positionLon!!)
        } else {
            ApiConfig.getApiService().storyUpload(token, imageMultipart, storyDescription)
        }
        client.enqueue(object : Callback<StoryUpload> {
            override fun onResponse(call: Call<StoryUpload>, response: Response<StoryUpload>) {
                when (response.code()) {
                    401 -> error.postValue(context.getString(R.string.error_header_token))
                    413 -> error.postValue(context.getString(R.string.error_large_payload))
                    201 -> isSuccessUploadStory.postValue(true)
                    else -> error.postValue("Error ${response.code()} : ${response.message()}")
                }
                isLoading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<StoryUpload>, t: Throwable) {
                isLoading.postValue(View.GONE)
                Log.e(TAG, "onFailure: ${t.message}")
                error.postValue("${context.getString(R.string.error_send_payload)} : ${t.message}")
            }
        })
    }

    companion object {
        private val TAG = StoryViewModel::class.java.simpleName
    }
}