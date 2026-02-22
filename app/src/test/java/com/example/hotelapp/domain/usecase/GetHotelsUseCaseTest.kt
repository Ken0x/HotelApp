package com.example.hotelapp.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
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

    @Test
    fun invoke_forwardsCheckInCheckOutToRepository() = runTest {
        fakeRepository.hotelsForCity = flowOf(emptyList())
        useCase("Paris", 19_000L, 19_002L).collect { }
        assertEquals(19_000L, fakeRepository.lastCheckInDay)
        assertEquals(19_002L, fakeRepository.lastCheckOutDay)
    }

    private class FakeHotelRepository : HotelRepository {
        var hotelsForCity: Flow<List<Hotel>> = flowOf(emptyList())
        var lastCheckInDay: Long? = null
        var lastCheckOutDay: Long? = null

        override fun getHotels(
            city: String,
            checkInDay: Long?,
            checkOutDay: Long?
        ): Flow<List<Hotel>> {
            lastCheckInDay = checkInDay
            lastCheckOutDay = checkOutDay
            return hotelsForCity
        }

        override fun getHotelsPaged(
            city: String,
            checkInDay: Long,
            checkOutDay: Long,
            minPrice: Double?,
            maxPrice: Double?,
            refreshTrigger: Flow<Unit>
        ): Flow<PagingData<Hotel>> = Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { EmptyPagingSource() }
        ).flow

        private class EmptyPagingSource : PagingSource<Int, Hotel>() {
            override fun getRefreshKey(state: androidx.paging.PagingState<Int, Hotel>): Int? = null
            override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, Hotel> =
                PagingSource.LoadResult.Page(emptyList(), null, null)
        }

        override fun getHotelById(id: String): Flow<Hotel> = flowOf()

        override fun getFavoriteHotels(): Flow<List<Hotel>> = flowOf(emptyList())

        override suspend fun toggleFavorite(hotelId: String) {}

        override suspend fun refreshHotels(city: String) {}
    }
}
