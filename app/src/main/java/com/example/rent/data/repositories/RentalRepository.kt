package com.example.rent.data.repositories

import com.example.rent.data.models.Invoice
import com.example.rent.data.models.Payment
import com.example.rent.data.models.Room

import com.example.rent.network.ApiService

open class RentalRepository(private val apiService: ApiService) {

    open suspend fun getAvailableRooms(): List<Room> {
        // Call the API to get the list of available rooms
        return apiService.getRooms().filter { !it.isOccupied }
    }

    open suspend fun getOccupiedRooms(): List<Room> {
        // Call the API to get the list of occupied rooms
        return apiService.getRooms().filter { it.isOccupied }
    }

    open suspend fun getInvoices(): List<Invoice> {
        // Call the API to get the list of invoices
        return apiService.getInvoices()
    }
    open suspend fun getDueInvoices(date:String): List<Invoice> {
        // Call the API to get the list of invoices
        return apiService.getDueInvoices(date)
    }

    open suspend fun getPayments(): List<Payment> {
        // Call the API to get the list of payments
        return apiService.getPayments()
    }

    open suspend fun getRooms(): List<Room>{
    return  apiService.getRooms()
    }
}
