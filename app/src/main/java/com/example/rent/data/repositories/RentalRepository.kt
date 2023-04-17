package com.example.rent.data.repositories

import com.example.rent.data.models.Invoice
import com.example.rent.data.models.Payment
import com.example.rent.data.models.Room

import com.example.rent.network.ApiService

 interface RentalRepository{

    open suspend fun getAvailableRooms(companyId: String): List<Room>

    open suspend fun getOccupiedRooms(companyId: String): List<Room>

    open suspend fun getInvoices(companyId:String): List<Invoice>
    open suspend fun getDueInvoices(companyId: String,date:String): List<Invoice>

    open suspend fun getPayments(companyId: String): List<Payment>
    open suspend fun getDuePayments(companyId: String,date:String): List<Payment>
    open suspend fun getRooms(companyId: String): List<Room>
}
