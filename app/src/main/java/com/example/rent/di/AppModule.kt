package com.example.rent.di

import com.example.rent.data.repositories.RentalRepository
import com.example.rent.data.repositories.impl.RentalRepositoryImpl
import com.example.rent.data.repositories.impl.UserRepositoryImpl
import com.example.rent.network.ApiService
import com.example.rent.viewModels.LoginViewModel
import com.example.rent.viewModels.RoomViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideRentalRepository(apiService: ApiService): RentalRepositoryImpl {
        return RentalRepositoryImpl(apiService)
    }

    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    @Provides
    fun provideUserRepository(apiService: ApiService): UserRepositoryImpl {
        return UserRepositoryImpl(apiService)
    }

    @Provides
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
             .baseUrl("https://xode.rw/landlord/api/management.php/")
             .addConverterFactory(GsonConverterFactory.create())
             .build()
             .create(ApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideRoomViewModel(
        repository: RentalRepositoryImpl,
        coroutineScope: CoroutineScope
    ): RoomViewModel {
        return RoomViewModel(repository, coroutineScope)
    }
@Provides
@Singleton
fun provideLoginViewModel(userRepository: UserRepositoryImpl):LoginViewModel{
    return LoginViewModel(userRepository)
}
}




