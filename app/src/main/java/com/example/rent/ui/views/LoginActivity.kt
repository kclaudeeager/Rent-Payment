package com.example.rent.ui.views

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rent.data.repositories.UserRepository
import com.example.rent.network.ApiService
import com.example.rent.ui.theme.RentTheme
import com.example.rent.util.LoginResult
import com.example.rent.viewModels.LoginViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.rent.ui.theme.White
class LoginActivity:  ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RentTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    val apiService = createApiService()
                    val userRepository = UserRepository(apiService)
                    val loginViewModel = LoginViewModel(userRepository)
                    LoginScreen(loginViewModel)
                }
            }
        }

    }

}
@Composable
fun LoginScreen(loginViewModel: LoginViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginResult by loginViewModel.loginResult.observeAsState()
    var loggingIn by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Add your UI components for the login screen here
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.padding(16.dp),
            placeholder = { Text("Enter Username") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Enter password") }
        )

        Button(
            enabled = !loggingIn,
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty() && !loggingIn) {
                    loggingIn = true
                    loginViewModel.login(username, password)
                }

            },
            modifier = Modifier.padding(16.dp)
        ) {
            if (loggingIn) {

                CircularProgressIndicator(
                    color = White,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(48.dp)
                )

            } else {
                Text(text = "Log In")
            }

        }

        loginResult?.let {
            loggingIn = false
            when (it) {
                
                is LoginResult.Success -> {
                  println("Result $it")
                    password=""
                    Toast.makeText(
                        LocalContext.current,
                        "Login successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is LoginResult.Error -> {
                    println("Result $it")
                    password=""
                    Toast.makeText(
                        LocalContext.current,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    RentTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            val apiService = createApiService()
            val userRepository = UserRepository(apiService)
            val loginViewModel = LoginViewModel(userRepository)
            LoginScreen(loginViewModel)
        }
    }
}

private fun createApiService(): ApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://uncle.itec.rw/RestaurantApi/management.php/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(ApiService::class.java)
}



