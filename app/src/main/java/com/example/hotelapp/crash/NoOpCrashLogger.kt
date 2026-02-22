package com.example.hotelapp.crash

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementacija koja samo logira u logcat. Za produkciju zamijeni s Firebase Crashlytics
 * ili drugim providerom kroz DI (npr. CrashLoggerModule).
 */
@Singleton
class NoOpCrashLogger @Inject constructor() : CrashLogger {

    override fun log(throwable: Throwable, message: String?) {
        val tag = "HotelApp"
        if (message != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, throwable.message ?: "Crash", throwable)
        }
    }
}
