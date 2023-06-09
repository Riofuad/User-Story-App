package com.example.userstoryapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class Login(
    @field:SerializedName("loginResult")
    val loginResult: User,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
