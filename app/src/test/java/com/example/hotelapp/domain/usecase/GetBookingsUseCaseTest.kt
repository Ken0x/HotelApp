package com.example.hotelapp.domain.usecase

import com.example.hotelapp.domain.model.Booking
import com.example.hotelapp.domain.repository.BookingRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetBookingsUseCaseTest {

    private lateinit var useCase: GetBookingsUseCase
    private lateinit var fakeRepository: FakeBookingRepository

    @Before
    fun setUp() {
        fakeRepository = FakeBookingRepository()
        useCase = GetBookingsUseCase(fakeRepository)
    }

    @Test
    fun invoke_emitsBookingsFromRepository() = runTest {
        val bookings = listOf(
            Booking("b1", "h1", "Hotel One", "Paris", 19_000L, 19_002L, 200.0),
            Booking("b2", "h2", "Hotel Two", "Rome", 19_100L, 19_102L, 150.0)
        )
        fakeRepository.bookingsFlow = flowOf(bookings)

        val result = mutableListOf<List<Booking>>()
        useCase().collect { result.add(it) }

        assertEquals(1, result.size)
        assertEquals(bookings, result[0])
    }

    @Test
    fun invoke_emitsEmptyListWhenRepositoryReturnsEmpty() = runTest {
        fakeRepository.bookingsFlow = flowOf(emptyList())
        val result = mutableListOf<List<Booking>>()
        useCase().collect { result.add(it) }
        assertEquals(1, result.size)
        assertEquals(emptyList<Booking>(), result[0])
    }

    private class FakeBookingRepository : BookingRepository {
        var bookingsFlow = flowOf(emptyList<Booking>())

        override fun getBookings() = bookingsFlow
        override suspend fun createBooking(booking: Booking) = "id"
    }
}
