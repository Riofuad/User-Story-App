package com.example.userstoryapp.data.remote.retrofit

import com.example.userstoryapp.data.remote.response.Login
import com.example.userstoryapp.data.remote.response.Register
import com.example.userstoryapp.data.remote.response.StoryList
import com.example.userstoryapp.data.remote.response.StoryUpload
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Login>

    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Register>

    @GET("stories")
    suspend fun storyList(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryList

    @GET("stories?location=1")
    fun storyListLocation(
        @Header("Authorization") token: String,
        @Query("size") size: Int
    ): Call<StoryList>

    @Multipart
    @POST("stories")
    fun storyUpload(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<StoryUpload>

    @Multipart
    @POST("stories")
    fun storyUpload(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ): Call<StoryUpload>
}