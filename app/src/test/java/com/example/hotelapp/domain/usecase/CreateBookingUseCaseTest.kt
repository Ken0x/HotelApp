package com.example.hotelapp.domain.usecase

import com.example.hotelapp.domain.model.Booking
import com.example.hotelapp.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CreateBookingUseCaseTest {

    private lateinit var useCase: CreateBookingUseCase
    private lateinit var fakeRepository: FakeBookingRepository

    @Before
    fun setUp() {
        fakeRepository = FakeBookingRepository()
        useCase = CreateBookingUseCase(fakeRepository)
    }

    @Test
    fun invoke_returnsBookingIdFromRepository() = runTest {
        val booking = Booking(
            id = "",
            hotelId = "h1",
            hotelName = "Hotel",
            city = "Paris",
            checkInDay = 19_000L,
            checkOutDay = 19_002L,
            totalPrice = 200.0
        )
        fakeRepository.bookingIdToReturn = "booking-123"

        val result = useCase(booking)

        assertEquals("booking-123", result)
        assertEquals(booking, fakeRepository.lastCreatedBooking)
    }

    @Test
    fun invoke_forwardsBookingToRepository() = runTest {
        val booking = Booking(
            id = "",
            hotelId = "h2",
            hotelName = "Other",
            city = "Rome",
            checkInDay = 19_100L,
            checkOutDay = 19_102L,
            totalPrice = 150.0
        )
        fakeRepository.bookingIdToReturn = "id-456"
        useCase(booking)
        assertEquals(booking, fakeRepository.lastCreatedBooking)
    }

    private class FakeBookingRepository : BookingRepository {
        var bookingIdToReturn = "default-id"
        var lastCreatedBooking: Booking? = null

        override fun getBookings(): Flow<List<Booking>> = flowOf(emptyList())

        override suspend fun createBooking(booking: Booking): String {
            lastCreatedBooking = booking
            return bookingIdToReturn
        }
    }
}
