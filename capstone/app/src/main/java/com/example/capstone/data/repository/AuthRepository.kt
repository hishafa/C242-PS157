package com.example.capstone.data.repository

import com.example.capstone.response.LoginResponse
import com.example.capstone.response.RegisterResponse
import com.example.capstone.data.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(
    private val registerService: ApiService,
    private val loginService: ApiService
) {

    fun registerUser(name: String, email: String, password: String, confirmPassword: String, callback: (RegisterResponse) -> Unit) {
        val call = registerService.register(name, email, password, confirmPassword)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    callback(response.body() ?: RegisterResponse(error = true, message = "Unknown response"))
                } else {
                    callback(RegisterResponse(error = true, message = response.message()))
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                callback(RegisterResponse(error = true, message = "Network Error: ${t.localizedMessage}"))
            }
        })
    }

    fun loginUser(email: String, password: String, callback: (LoginResponse) -> Unit) {
        val call = loginService.login(email, password)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    callback(response.body() ?: LoginResponse(error = true, message = "Unknown response"))
                } else {
                    callback(LoginResponse(error = true, message = response.message()))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback(LoginResponse(error = true, message = "Network Error: ${t.localizedMessage}"))
            }
        })
    }
}
