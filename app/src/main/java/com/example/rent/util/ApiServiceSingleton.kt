package com.example.rent.util

import com.example.rent.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceSingleton {

    fun createApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://xode.rw/landlord/api/management.php/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }
}