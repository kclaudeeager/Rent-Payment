package com.example.rent.data.repositories.impl

import com.example.rent.data.models.Invoice
import com.example.rent.data.models.Payment
import com.example.rent.data.models.Room
import com.example.rent.data.repositories.RentalRepository
import com.example.rent.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RentalRepositoryImpl(private val apiService: ApiService) : RentalRepository {

    override suspend fun getAvailableRooms(companyId: String): List<Room> {
        return withContext(Dispatchers.IO) {
            apiService.getRooms(companyId).filter { !it.isOccupied }
        }
    }
    override suspend fun getDueInvoices(companyId: String,date:String): List<Invoice> {
        // Call the API to get the list of invoices
        return withContext(Dispatchers.IO) {
             apiService.getDueInvoices(companyId,date)
        }
    }
    override suspend fun getOccupiedRooms(companyId: String): List<Room> {
        return withContext(Dispatchers.IO) {
            apiService.getRooms(companyId).filter { it.isOccupied }
        }
    }


    override suspend fun getInvoices(companyId: String): List<Invoice> {
        return withContext(Dispatchers.IO) {
            apiService.getInvoices(companyId)
        }
    }

    override suspend fun getPayments(companyId: String): List<Payment> {
        return withContext(Dispatchers.IO) {
            apiService.getPayments(companyId)
        }
    }

    override suspend fun getDuePayments(companyId: String, date: String): List<Payment> {
       return  withContext(Dispatchers.IO) {
           apiService.getDuePayments(companyId,date)
       }
    }

    override suspend fun getRooms(companyId: String): List<Room> {
        return withContext(Dispatchers.IO) {
            apiService.getRooms(companyId)
        }
    }
}
