package com.example.hotelapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.hotelapp.crash.CrashLogger
import com.example.hotelapp.work.WarmupWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltAndroidApp
class HotelApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var crashLogger: CrashLogger

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        installCrashHandler()
        Executors.newSingleThreadExecutor().execute {
            WorkManager.getInstance(this).enqueue(
                OneTimeWorkRequestBuilder<WarmupWorker>().build()
            )
        }
    }

    private fun installCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            crashLogger.log(throwable, "Uncaught in thread: ${thread.name}")
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
