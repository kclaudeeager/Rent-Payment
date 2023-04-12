package com.example.rent.data.repositories

import com.example.rent.data.models.User
import com.example.rent.network.ApiService
import com.google.gson.Gson
import javax.security.auth.login.LoginException

open class UserRepository(private val apiService: ApiService) {

    open suspend fun login(username: String, password: String): User? {
        // Call the API to log in the user
        val response = apiService.login(username, password)


        // Check if the response was successful
        if (response.isSuccessful) {
            // Return the logged-in user
            val responseData = response.body()?.string() ?: ""
            // Parse the JSON response manually to retrieve the user data
            //println("RESPONSE BODY: $responseData")

            return Gson().fromJson(responseData, User::class.java)
        } else {
            // Handle the error
            throw LoginException("Login failed: ${response.code()}")
        }
    }
}



