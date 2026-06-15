package com.example.hotelapp.work

import com.example.hotelapp.domain.model.Booking

/**
 * Zakazuje podsjetnik (notifikaciju) za nadolazeću rezervaciju.
 * Implementacija koristi WorkManager da prikaže notifikaciju dan prije check-in datuma.
 */
interface BookingReminderScheduler {

    /**
     * Zakazuje jedan podsjetnik za rezervaciju: notifikacija će se prikazati dan prije
     * [Booking.checkInDay] u 9:00 (lokalno vrijeme). Ako je taj trenutak već prošao, ništa se ne zakazuje.
     */
    fun scheduleReminder(booking: Booking)
}
