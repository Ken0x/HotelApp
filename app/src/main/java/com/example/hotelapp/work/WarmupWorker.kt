package com.example.hotelapp.work

import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hotelapp.data.local.dao.HotelDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Jednokratni Worker koji u pozadini „grije” Room bazu (prva konstrukcija [HotelDao] / AppDatabase
 * događa se u WorkManager threadu, ne na main threadu). Zakazuje se iz [HotelApplication] putem
 * Handler.post da ne blokira onCreate.
 */
@HiltWorker
class WarmupWorker @AssistedInject constructor(
    @Assisted appContext: android.content.Context,
    @Assisted params: WorkerParameters,
    private val hotelDao: HotelDao
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            hotelDao.getHotelsByCityOnce("")
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
