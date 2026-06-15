package com.example.hotelapp.work

import com.example.hotelapp.domain.model.Booking
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Zakazuje [BookingReminderWorker] dan prije check-in datuma u 9:00 (lokalno vrijeme).
 * Ako je taj trenutak u prošlosti, podsjetnik se ne zakazuje.
 */
class BookingReminderSchedulerImpl @Inject constructor(
    private val workManager: WorkManager
) : BookingReminderScheduler {

    override fun scheduleReminder(booking: Booking) {
        val zone = ZoneId.systemDefault()
        val reminderDate = LocalDate.ofEpochDay(booking.checkInDay - 1)
        val reminderInstant = reminderDate.atTime(REMINDER_HOUR, REMINDER_MINUTE).atZone(zone).toInstant()
        val now = Instant.now()
        val delayMillis = java.time.Duration.between(now, reminderInstant).toMillis()
        if (delayMillis <= 0) return

        val inputData = Data.Builder()
            .putString(BookingReminderWorkerKeys.BOOKING_ID, booking.id)
            .putString(BookingReminderWorkerKeys.HOTEL_NAME, booking.hotelName)
            .putLong(BookingReminderWorkerKeys.CHECK_IN_DAY, booking.checkInDay)
            .build()

        val request = OneTimeWorkRequestBuilder<BookingReminderWorker>()
            .setInputData(inputData)
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniqueWork(
            UNIQUE_WORK_NAME_PREFIX + booking.id,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    private companion object {
        const val REMINDER_HOUR = 9
        const val REMINDER_MINUTE = 0
        const val UNIQUE_WORK_NAME_PREFIX = "booking_reminder_"
    }
}
