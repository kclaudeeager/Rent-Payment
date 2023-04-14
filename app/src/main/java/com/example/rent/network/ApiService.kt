package com.example.rent.network

import android.net.Credentials
import com.example.rent.data.models.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("rooms")
    suspend fun getRooms(): List<Room>

    @GET("invoices")
    suspend fun getInvoices(): List<Invoice>

    @GET("payments")
    suspend fun getPayments(): List<Payment>

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): User

    @POST("login")
    @FormUrlEncoded
    suspend fun login(@Field("username") username: String, @Field("password") password: String): Response<ResponseBody>
}

