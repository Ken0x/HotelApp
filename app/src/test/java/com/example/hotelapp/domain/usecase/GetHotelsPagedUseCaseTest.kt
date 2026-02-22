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
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetHotelsPagedUseCaseTest {

    private lateinit var useCase: GetHotelsPagedUseCase
    private lateinit var fakeRepository: FakeHotelRepository

    @Before
    fun setUp() {
        fakeRepository = FakeHotelRepository()
        useCase = GetHotelsPagedUseCase(fakeRepository)
    }

    @Test
    fun invoke_returnsPagingFlowFromRepository() = runTest {
        val hotel = Hotel("h1", "Hotel One", "Paris", 100.0)
        fakeRepository.pagedResult = Pager(
            config = PagingConfig(20),
            pagingSourceFactory = {
                object : PagingSource<Int, Hotel>() {
                    override fun getRefreshKey(state: PagingState<Int, Hotel>): Int? = null
                    override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, Hotel> =
                        PagingSource.LoadResult.Page(listOf(hotel), null, null)
                }
            }
        ).flow
        val list = useCase("Paris", 0L, 1L, null, null, flowOf(Unit)).take(1).toList()
        Assert.assertEquals(1, list.size)
    }

    @Test
    fun invoke_forwardsParametersToRepository() = runTest {
        fakeRepository.lastCity = null
        useCase("Barcelona", 100L, 101L, 50.0, 200.0, flowOf(Unit)).take(1).toList()
        Assert.assertEquals("Barcelona", fakeRepository.lastCity)
        Assert.assertEquals(50.0, fakeRepository.lastMinPrice)
        Assert.assertEquals(200.0, fakeRepository.lastMaxPrice)
    }

    private class FakeHotelRepository : HotelRepository {
        var lastCity: String? = null
        var lastMinPrice: Double? = null
        var lastMaxPrice: Double? = null
        var pagedResult: Flow<PagingData<Hotel>> = Pager(
            config = PagingConfig(20),
            pagingSourceFactory = {
                object : PagingSource<Int, Hotel>() {
                    override fun getRefreshKey(state: PagingState<Int, Hotel>): Int? = null
                    override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, Hotel> =
                        PagingSource.LoadResult.Page(emptyList(), null, null)
                }
            }
        ).flow

        override fun getHotels(city: String, checkInDay: Long?, checkOutDay: Long?): Flow<List<Hotel>> = flowOf(emptyList())
        override fun getHotelsPaged(
            city: String,
            checkInDay: Long,
            checkOutDay: Long,
            minPrice: Double?,
            maxPrice: Double?,
            refreshTrigger: Flow<Unit>
        ): Flow<PagingData<Hotel>> {
            lastCity = city
            lastMinPrice = minPrice
            lastMaxPrice = maxPrice
            return pagedResult
        }
        override fun getHotelById(id: String): Flow<Hotel> = flowOf()
        override fun getFavoriteHotels(): Flow<List<Hotel>> = flowOf(emptyList())
        override suspend fun toggleFavorite(hotelId: String) {}
        override suspend fun refreshHotels(city: String) {}
    }
}
