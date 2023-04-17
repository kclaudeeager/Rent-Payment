package com.example.rent.data.repositories.impl

import com.example.rent.data.models.User
import com.example.rent.data.repositories.UserRepository
import com.example.rent.network.ApiService
import com.google.gson.Gson
import javax.security.auth.login.LoginException

class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {

    override suspend fun login(username: String, password: String): User? {

            val response = apiService.login(username, password)
            if (response.isSuccessful) {
                val responseData = response.body()?.string() ?: ""
                // Parse the JSON response manually to retrieve the user data
                return Gson().fromJson(responseData, User::class.java)
            } else {
                throw LoginException("Login failed: ${response.code()}")
            }
        }


}

