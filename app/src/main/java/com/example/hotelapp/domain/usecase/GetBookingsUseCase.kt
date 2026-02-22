package com.example.hotelapp.domain.usecase

import com.example.hotelapp.domain.model.Booking
import com.example.hotelapp.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookingsUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    operator fun invoke(): Flow<List<Booking>> = repository.getBookings()
}
