package com.example.rent.util

import com.example.rent.data.models.User

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
    object NetworkError : LoginResult()


}


