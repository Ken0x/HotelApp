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

class RefreshHotelsUseCaseTest {

    private lateinit var useCase: RefreshHotelsUseCase
    private lateinit var fakeRepository: FakeHotelRepository

    @Before
    fun setUp() {
        fakeRepository = FakeHotelRepository()
        useCase = RefreshHotelsUseCase(fakeRepository)
    }

    @Test
    fun invoke_callsRepositoryRefreshHotels() = runTest {
        useCase("Barcelona")
        assertEquals("Barcelona", fakeRepository.lastRefreshedCity)
    }

    @Test
    fun invoke_forwardsCityToRepository() = runTest {
        useCase("Paris")
        assertEquals("Paris", fakeRepository.lastRefreshedCity)
    }

    private class FakeHotelRepository : HotelRepository {
        var lastRefreshedCity: String? = null

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
            pagingSourceFactory = { object : PagingSource<Int, Hotel>() {
                override fun getRefreshKey(state: androidx.paging.PagingState<Int, Hotel>): Int? = null
                override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, Hotel> =
                    PagingSource.LoadResult.Page(emptyList(), null, null)
            } }
        ).flow

        override fun getHotelById(id: String): Flow<Hotel> = flowOf<Hotel>()
        override fun getFavoriteHotels() = flowOf(emptyList<Hotel>())
        override suspend fun toggleFavorite(hotelId: String) {}
        override suspend fun refreshHotels(city: String) {
            lastRefreshedCity = city
        }
    }
}
