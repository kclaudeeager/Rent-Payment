package com.example.rent.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rent.data.repositories.UserRepository
import com.example.rent.util.LoginResult
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.security.auth.login.LoginException

class LoginViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult>
     get() = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val loginResponse = userRepository.login(username, password)
                _loginResult.value = loginResponse?.let { LoginResult.Success(it) }
            } catch (e: LoginException) {
                _loginResult.value = e.message?.let { LoginResult.Error(it) }
            }
        }
    }
}


