package com.example.rent.data.repositories.impl

import com.example.rent.data.models.Invoice
import com.example.rent.data.models.Payment
import com.example.rent.data.models.Room
import com.example.rent.data.repositories.RentalRepository
import com.example.rent.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RentalRepositoryImpl(private val apiService: ApiService) : RentalRepository(apiService) {

    override suspend fun getAvailableRooms(): List<Room> {
        return withContext(Dispatchers.IO) {
            apiService.getRooms().filter { !it.isOccupied }
        }
    }

    override suspend fun getOccupiedRooms(): List<Room> {
        return withContext(Dispatchers.IO) {
            apiService.getRooms().filter { it.isOccupied }
        }
    }

    override suspend fun getInvoices(): List<Invoice> {
        return withContext(Dispatchers.IO) {
            apiService.getInvoices()
        }
    }

    override suspend fun getPayments(): List<Payment> {
        return withContext(Dispatchers.IO) {
            apiService.getPayments()
        }
    }

    override suspend fun getRooms(): List<Room> {
        return withContext(Dispatchers.IO) {
            apiService.getRooms()
        }
    }
}
