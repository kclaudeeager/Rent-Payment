package com.example.rent.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rent.data.repositories.RentalRepository

class RentalWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val rentalRepository: RentalRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Call the method in the rental repository to get the list of available rooms
            val availableRooms = rentalRepository.getAvailableRooms()

            // Do something with the available rooms

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
