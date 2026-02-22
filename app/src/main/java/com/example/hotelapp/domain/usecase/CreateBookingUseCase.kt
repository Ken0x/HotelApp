package com.example.hotelapp.domain.usecase

import com.example.hotelapp.domain.model.Booking
import com.example.hotelapp.domain.repository.BookingRepository
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(booking: Booking): String =
        repository.createBooking(booking)
}
