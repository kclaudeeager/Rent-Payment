package com.example.rent.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rent.data.models.Invoice
import com.example.rent.data.models.Payment
import com.example.rent.data.models.Room
import com.example.rent.data.models.User
import com.example.rent.data.repositories.impl.RentalRepositoryImpl
import com.example.rent.util.UserSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val repository: RentalRepositoryImpl,
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
   private val _duePayments: MutableLiveData<List<Payment>> = MutableLiveData()
    val duePayments : LiveData<List<Payment>>
    get() = _duePayments

    var user: User? =UserSingleton.user


    fun getRooms() {
        coroutineScope.launch {

            val result = kotlin.runCatching {
                user?.let { repository.getRooms(it.co_id) }
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
                user?.let { repository.getAvailableRooms(it.co_id) }
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
              user?.let { repository.getOccupiedRooms(it.co_id) }
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
                user?.let {  repository.getInvoices(it.co_id)}
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
                user?.let {
                    repository.getDueInvoices(companyId = it.co_id, date = date)
                }
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
                user?.let {
                    repository.getPayments(it.co_id)
                }
            }
            result.onSuccess { payments ->
                _payments.postValue(payments)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
    fun getDuePayments(date:String) {
        coroutineScope.launch {
            val result = kotlin.runCatching {
                user?.let {
                    repository.getDuePayments(it.co_id,date)
                }
            }
            result.onSuccess { payments ->
                _duePayments.postValue(payments)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}


