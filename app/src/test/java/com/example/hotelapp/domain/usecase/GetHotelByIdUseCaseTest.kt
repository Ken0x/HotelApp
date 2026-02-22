package com.example.hotelapp.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetHotelByIdUseCaseTest {

    private lateinit var useCase: GetHotelByIdUseCase
    private lateinit var fakeRepository: FakeHotelRepository

    @Before
    fun setUp() {
        fakeRepository = FakeHotelRepository()
        useCase = GetHotelByIdUseCase(fakeRepository)
    }

    @Test
    fun invoke_emitsHotelFromRepository() = runTest {
        val hotel = Hotel("id-1", "Grand Hotel", "Barcelona", 120.0)
        fakeRepository.hotelById = flowOf(hotel)

        val result = mutableListOf<Hotel>()
        useCase("id-1").collect { result.add(it) }

        assertEquals(1, result.size)
        assertEquals(hotel, result[0])
    }

    @Test
    fun invoke_forwardsIdToRepository() = runTest {
        fakeRepository.hotelById = flowOf()
        useCase("requested-id").collect { }
        assertEquals("requested-id", fakeRepository.lastRequestedId)
    }

    private class FakeHotelRepository : HotelRepository {
        var hotelById = flowOf<Hotel>()
        var lastRequestedId: String? = null

        override fun getHotels(
            city: String,
            checkInDay: Long?,
            checkOutDay: Long?
        ): Flow<List<Hotel>> = flowOf(emptyList())

        override fun getHotelsPaged(
            city: String,
            checkInDay: Long,
            checkOutDay: Long,
            minPrice: Double?,
            maxPrice: Double?,
            refreshTrigger: Flow<Unit>
        ): Flow<PagingData<Hotel>> = Pager(
            config = PagingConfig(20),
            pagingSourceFactory = { EmptyHotelPagingSource() }
        ).flow

        private class EmptyHotelPagingSource : PagingSource<Int, Hotel>() {
            override fun getRefreshKey(state: PagingState<Int, Hotel>): Int? = null
            override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, Hotel> =
                PagingSource.LoadResult.Page(emptyList(), null, null)
        }

        override fun getHotelById(id: String): Flow<Hotel> {
            lastRequestedId = id
            return hotelById
        }

        override fun getFavoriteHotels() = flowOf(emptyList<Hotel>())
        override suspend fun toggleFavorite(hotelId: String) {}
        override suspend fun refreshHotels(city: String) {}
    }
}

