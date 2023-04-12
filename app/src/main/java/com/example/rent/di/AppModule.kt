package com.example.rent.di

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.rent.data.repositories.RentalRepository
import com.example.rent.data.repositories.impl.RentalRepositoryImpl
import com.example.rent.data.repositories.UserRepository
import com.example.rent.data.repositories.impl.UserRepositoryImpl
import com.example.rent.network.ApiService
import com.example.rent.ui.views.LoginActivity
import com.example.rent.ui.views.MainActivity
import com.example.rent.work.CustomWorkerFactory
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideRentalRepository(apiService: ApiService): RentalRepository {
        return RentalRepositoryImpl(apiService)
    }

    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    @Provides
    fun provideUserRepository(apiService: ApiService): UserRepository {
        return UserRepositoryImpl(apiService)
    }

    @Provides
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://uncle.itec.rw/RestaurantApi/management.php/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}




