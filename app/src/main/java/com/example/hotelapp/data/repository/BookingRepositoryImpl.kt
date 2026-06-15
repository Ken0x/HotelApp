package com.example.hotelapp.data.repository

import com.example.hotelapp.data.local.dao.BookingDao
import com.example.hotelapp.data.local.toDomain
import com.example.hotelapp.data.local.toEntity
import com.example.hotelapp.domain.model.Booking
import com.example.hotelapp.domain.repository.BookingRepository
import com.example.hotelapp.work.BookingReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val bookingDao: BookingDao,
    private val reminderScheduler: BookingReminderScheduler
) : BookingRepository {

    override fun getBookings(): Flow<List<Booking>> =
        bookingDao.getBookings().map { it.toDomain() }

    override suspend fun createBooking(booking: Booking): String {
        val id = booking.id.ifBlank { UUID.randomUUID().toString() }
        val entity = booking.copy(id = id).toEntity()
        bookingDao.insert(entity)
        reminderScheduler.scheduleReminder(booking.copy(id = id))
        return id
    }
}
