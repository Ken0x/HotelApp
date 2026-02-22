package com.example.hotelapp.work

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/** Zakazuje periodični [SyncHotelsWorker] s mrežnim ograničenjem. */
class SyncScheduler @Inject constructor(
    private val workManager: WorkManager
) {

    /**
     * Zakazuje periodični sync za [city]. Koristi KEEP da ne duplicira postojeći posao.
     * Pokreće se samo kad je mreža dostupna.
     */
    fun scheduleSync(city: String) {
        if (city.isBlank()) return
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val inputData = workDataOf(SyncHotelsWorker.KEY_CITY to city)
        val request = PeriodicWorkRequestBuilder<SyncHotelsWorker>(
            REPEAT_INTERVAL_MINUTES, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    companion object {
        private const val WORK_NAME = "sync_hotels"
        private const val REPEAT_INTERVAL_MINUTES = 15L
    }
}
