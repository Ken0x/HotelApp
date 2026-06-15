package com.example.hotelapp.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hotelapp.MainActivity
import com.example.hotelapp.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val CHANNEL_ID = "booking_reminders"
private const val NOTIFICATION_ID_BASE = 2000

/** Ključevi za [androidx.work.WorkManager] input data. */
object BookingReminderWorkerKeys {
    const val BOOKING_ID = "booking_id"
    const val HOTEL_NAME = "hotel_name"
    const val CHECK_IN_DAY = "check_in_day"
}

/**
 * Jednokratni Worker koji prikazuje notifikaciju podsjetnika dan prije check-in datuma rezervacije.
 * Zakazuje ga [BookingReminderScheduler] nakon kreiranja rezervacije.
 */
@HiltWorker
class BookingReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val bookingId = inputData.getString(BookingReminderWorkerKeys.BOOKING_ID) ?: return Result.failure()
        val hotelName = inputData.getString(BookingReminderWorkerKeys.HOTEL_NAME).orEmpty()
        val title = applicationContext.getString(R.string.notification_reminder_title)
        val text = applicationContext.getString(R.string.notification_reminder_text, hotelName.ifEmpty { "your hotel" })

        createChannelIfNeeded()
        val openAppIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_TAB_BOOKINGS, true)
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            bookingId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = NOTIFICATION_ID_BASE + bookingId.hashCode().and(0x7FFF)
        notificationManager.notify(notificationId, notification)

        return Result.success()
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            applicationContext.getString(R.string.notification_channel_reminders_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Reminders for upcoming hotel check-ins" }
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}
