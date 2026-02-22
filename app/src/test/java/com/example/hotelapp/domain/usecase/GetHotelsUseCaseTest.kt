package com.example.hotelapp.domain.usecase

import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetHotelsUseCaseTest {

    private lateinit var useCase: GetHotelsUseCase
    private lateinit var fakeRepository: FakeHotelRepository

    @Before
    fun setUp() {
        fakeRepository = FakeHotelRepository()
        useCase = GetHotelsUseCase(fakeRepository)
    }

    @Test
    fun invoke_emitsHotelsFromRepository() = runTest {
        val city = "Barcelona"
        val expected = listOf(
            Hotel("1", "Hotel One", city, 100.0),
            Hotel("2", "Hotel Two", city, null)
        )
        fakeRepository.hotelsForCity = flowOf(expected)

        val result = mutableListOf<List<Hotel>>()
        useCase(city).collect { result.add(it) }

        assertEquals(1, result.size)
        assertEquals(expected, result[0])
    }

    @Test
    fun invoke_emitsEmptyListWhenRepositoryReturnsEmpty() = runTest {
        fakeRepository.hotelsForCity = flowOf(emptyList())

        val result = mutableListOf<List<Hotel>>()
        useCase("Unknown").collect { result.add(it) }

        assertEquals(1, result.size)
        assertEquals(emptyList<Hotel>(), result[0])
    }

    /** Fake [HotelRepository] for unit tests. */
    private class FakeHotelRepository : HotelRepository {
        var hotelsForCity: Flow<List<Hotel>> = flowOf(emptyList())
        var hotelById: Flow<Hotel> = flowOf()

        override fun getHotels(city: String): Flow<List<Hotel>> = hotelsForCity
        override fun getHotelById(id: String): Flow<Hotel> = hotelById
    }
}
