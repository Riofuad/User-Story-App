package com.example.userstoryapp.ui.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.userstoryapp.data.remote.response.Login
import com.example.userstoryapp.data.remote.response.Register
import com.example.userstoryapp.data.remote.retrofit.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {
    var isLoading = MutableLiveData(View.GONE)
    val error = MutableLiveData("")
    val tempEmail = MutableLiveData("")

    var loginResult = MutableLiveData<Login>()
    var registerResult = MutableLiveData<Register>()

    fun login(email: String, password: String) {
        tempEmail.postValue(email)
        isLoading.postValue(View.VISIBLE)
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if (response.isSuccessful) {
                    loginResult.postValue(response.body())
                } else {
                    response.errorBody()?.let {
                        val errorResponse = JSONObject(it.string())
                        val errorMessage = errorResponse.getString("message")
                        error.postValue("Login Error: $errorMessage")
                    }
                }
                isLoading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                isLoading.postValue(View.GONE)
                Log.d(TAG, "onFailure : ${t.message}")
                error.postValue(t.message)
            }
        })
    }

    fun register(name: String, email: String, password: String) {
        isLoading.postValue(View.VISIBLE)
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<Register> {
            override fun onResponse(call: Call<Register>, response: Response<Register>) {
                if (response.isSuccessful) {
                    registerResult.postValue(response.body())
                } else {
                    response.errorBody()?.let {
                        val errorResponse = JSONObject(it.string())
                        val errorMessage = errorResponse.getString("message")
                        error.postValue("Login Error: $errorMessage")
                    }
                }
                isLoading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<Register>, t: Throwable) {
                isLoading.postValue(View.GONE)
                Log.d(TAG, "onFailure : ${t.message}")
                error.postValue(t.message)
            }
        })
    }

    companion object {
        private val TAG = AuthViewModel::class.java.simpleName
    }
}