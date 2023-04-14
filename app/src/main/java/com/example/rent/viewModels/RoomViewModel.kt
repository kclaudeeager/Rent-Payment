package com.example.rent.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rent.data.models.Invoice
import com.example.rent.data.models.Payment
import com.example.rent.data.models.Room
import com.example.rent.data.repositories.RentalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class RoomViewModel @Inject constructor(
    private val repository: RentalRepository,
    private val coroutineScope: CoroutineScope
) : ViewModel() {

    private val _rooms: MutableLiveData<List<Room>> = MutableLiveData()
    val rooms: LiveData<List<Room>>
        get() = _rooms
    private val _availableRooms: MutableLiveData<List<Room>> = MutableLiveData()
    val availableRooms: LiveData<List<Room>>
        get() = _availableRooms
    private val _occupiedRooms: MutableLiveData<List<Room>> = MutableLiveData()
    val occupiedRooms: LiveData<List<Room>>
        get() = _occupiedRooms

    private val _invoices: MutableLiveData<List<Invoice>> = MutableLiveData()
    val invoices: LiveData<List<Invoice>>
        get() = _invoices

    private val _payments: MutableLiveData<List<Payment>> = MutableLiveData()
    val payments: LiveData<List<Payment>>
        get() = _payments

    fun getRooms() {
        coroutineScope.launch {
            val rooms = repository.getRooms()
            println("Rooms: $rooms")
            _rooms.postValue(rooms)
        }
    }
    fun getAvailableRooms() {
        coroutineScope.launch {
            val rooms = repository.getAvailableRooms()
            println("Rooms: $rooms")
            _availableRooms.postValue(rooms)
        }
    }
    fun getOccupiedRooms() {
        coroutineScope.launch {
            val rooms = repository.getOccupiedRooms()
            println("Rooms: $rooms")
            _occupiedRooms.postValue(rooms)
        }
    }

    fun getInvoices() {
        coroutineScope.launch {
            val invoices = repository.getInvoices()
            _invoices.postValue(invoices)
        }
    }

    fun getPayments() {
        coroutineScope.launch {
            val payments = repository.getPayments()
            _payments.postValue(payments)
        }
    }
}
