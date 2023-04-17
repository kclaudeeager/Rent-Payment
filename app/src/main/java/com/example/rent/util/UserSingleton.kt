package com.example.rent.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.rent.data.models.User

object UserSingleton {
    var user: User? by mutableStateOf(null)
}