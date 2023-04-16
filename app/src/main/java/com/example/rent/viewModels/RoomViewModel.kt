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

    private val _dueInvoices: MutableLiveData<List<Invoice>> = MutableLiveData()
    val dueInvoices: LiveData<List<Invoice>>
        get() = _dueInvoices

    private val _payments: MutableLiveData<List<Payment>> = MutableLiveData()
    val payments: LiveData<List<Payment>>
        get() = _payments

    fun getRooms() {
        coroutineScope.launch {

            val result = kotlin.runCatching {
                repository.getRooms()
            }
            result.onSuccess { rooms ->
                _rooms.postValue(rooms)
            }.onFailure {
                it.printStackTrace()
            }

        }
    }
    fun getAvailableRooms() {
        coroutineScope.launch {
            val result = kotlin.runCatching {
                repository.getAvailableRooms()
            }

            result.onSuccess { rooms ->
                _availableRooms.postValue(rooms)
            }.onFailure {
                it.printStackTrace()
            }

        }
    }
    fun getOccupiedRooms() {
        coroutineScope.launch {
            val result = kotlin.runCatching {
                repository.getOccupiedRooms()
            }
            result.onSuccess { rooms ->
                _occupiedRooms.postValue(rooms)
            }.onFailure {
                it.printStackTrace()
            }

        }
    }

    fun getInvoices() {
        coroutineScope.launch {
            val result = kotlin.runCatching {
                repository.getInvoices()
            }
            result.onSuccess { invoices ->
                _invoices.postValue(invoices)
            }.onFailure {
                it.printStackTrace()
            }

        }
    }
    fun getDueInvoices(date:String) {
        coroutineScope.launch {
            val result = kotlin.runCatching {
                repository.getDueInvoices(date = date)
            }
            result.onSuccess { invoices ->
                _dueInvoices.postValue(invoices)
                _dueInvoices.value?.let { println("Due invoices: ${it.size} at $date") }
            }.onFailure {
                it.printStackTrace()
            }

        }
    }

    fun getPayments() {
        coroutineScope.launch {
            val result = kotlin.runCatching {
                repository.getPayments()
            }
            result.onSuccess { payments ->
                _payments.postValue(payments)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}


