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

class ToggleFavoriteUseCaseTest {

    private lateinit var useCase: ToggleFavoriteUseCase
    private lateinit var fakeRepository: FakeHotelRepository

    @Before
    fun setUp() {
        fakeRepository = FakeHotelRepository()
        useCase = ToggleFavoriteUseCase(fakeRepository)
    }

    @Test
    fun invoke_callsRepositoryToggleFavorite() = runTest {
        useCase("hotel-123")
        assertEquals("hotel-123", fakeRepository.lastToggledId)
    }

    private class FakeHotelRepository : HotelRepository {
        var lastToggledId: String? = null
        override fun getHotels(city: String, checkInDay: Long?, checkOutDay: Long?): Flow<List<Hotel>> = flowOf(emptyList<Hotel>())
        override fun getHotelsPaged(city: String, checkInDay: Long, checkOutDay: Long, minPrice: Double?, maxPrice: Double?, refreshTrigger: Flow<Unit>): Flow<PagingData<Hotel>> =
            Pager(PagingConfig(20), pagingSourceFactory = {
                object : PagingSource<Int, Hotel>() {
                    override fun getRefreshKey(state: PagingState<Int, Hotel>): Int? = null
                    override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, Hotel> =
                        PagingSource.LoadResult.Page(emptyList(), null, null)
                }
            }).flow
        override fun getHotelById(id: String) = flowOf<Hotel>()
        override fun getFavoriteHotels(): Flow<List<Hotel>> = flowOf(emptyList<Hotel>())
        override suspend fun toggleFavorite(hotelId: String) {
            lastToggledId = hotelId
        }
        override suspend fun refreshHotels(city: String) {}
    }
}
