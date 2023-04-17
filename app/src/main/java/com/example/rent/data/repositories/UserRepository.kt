package com.example.rent.data.repositories

import com.example.rent.data.models.User
import com.example.rent.network.ApiService
import com.google.gson.Gson
import javax.security.auth.login.LoginException

interface UserRepository {

    open suspend fun login(username: String, password: String): User?

}



