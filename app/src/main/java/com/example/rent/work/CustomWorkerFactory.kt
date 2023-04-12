package com.example.rent.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.rent.data.repositories.RentalRepository
import com.example.rent.data.repositories.UserRepository
import com.example.rent.network.ApiService
import javax.inject.Inject

class CustomWorkerFactory @Inject constructor(
    private val rentalRepository: RentalRepository,
    private val userRepository: UserRepository,
    private val apiService: ApiService
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        return when (workerClassName) {
            RentalWorker::class.java.name -> {
                RentalWorker(appContext, workerParameters, rentalRepository)
            }
            // Add other worker classes here if needed
            else -> {
                // Return null if the worker class is not supported
                null
            }
        }
    }
}

