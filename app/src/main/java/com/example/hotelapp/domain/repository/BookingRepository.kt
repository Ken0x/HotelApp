package com.example.hotelapp.domain.repository

import com.example.hotelapp.domain.model.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getBookings(): Flow<List<Booking>>
    suspend fun createBooking(booking: Booking): String
}
