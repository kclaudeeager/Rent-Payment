package com.example.rent.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rent.data.repositories.UserRepository
import com.example.rent.data.repositories.impl.UserRepositoryImpl
import com.example.rent.util.LoginResult
import com.example.rent.util.LoginResultSingleton
import com.example.rent.util.UserSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.security.auth.login.LoginException

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepositoryImpl) :
    ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult>
     get() = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val result = kotlin.runCatching {
                userRepository.login(username, password)
            }
            result.onSuccess { user ->
                if(user!=null) {
                    if (user.acc_id != null) {
                        _loginResult.value = user.let {user->
                            LoginResult.Success(user)
                        }
                        UserSingleton.user=user
                        loginResult.value?.let { LoginResultSingleton.setLoginResult(it) }
                    } else {
                        _loginResult.value = LoginResult.Error("Authentication error")
                        LoginResultSingleton.resetLoginResult()
                    }

                }
                else{
                    _loginResult.value = LoginResult.Error("Authentication error")
                    LoginResultSingleton.resetLoginResult()
                }
            }.onFailure { exception ->
                _loginResult.value = LoginResult.Error(exception.message ?: "Unknown error")
                LoginResultSingleton.resetLoginResult()
            }
        }
    }
fun logout(){
    _loginResult.value=null
    UserSingleton.user=null
}
    fun resetLoginResult() {
        _loginResult.value=null
    }


}


