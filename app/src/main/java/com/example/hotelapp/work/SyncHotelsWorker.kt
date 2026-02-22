package com.example.hotelapp.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hotelapp.domain.usecase.RefreshHotelsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** Pozadinski sync hotela; poziva [RefreshHotelsUseCase]. Grad se prosljeđuje preko InputData. */
@HiltWorker
class SyncHotelsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val refreshHotelsUseCase: RefreshHotelsUseCase
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val city = inputData.getString(KEY_CITY)?.takeIf { it.isNotBlank() }
            ?: return Result.success()
        return try {
            refreshHotelsUseCase(city)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_CITY = "city"
    }
}
