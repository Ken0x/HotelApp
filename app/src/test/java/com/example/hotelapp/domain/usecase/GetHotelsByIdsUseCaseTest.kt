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

class GetHotelsByIdsUseCaseTest {

    private lateinit var useCase: GetHotelsByIdsUseCase
    private lateinit var fakeRepository: FakeHotelRepository

    @Before
    fun setUp() {
        fakeRepository = FakeHotelRepository()
        val getHotelByIdUseCase = GetHotelByIdUseCase(fakeRepository)
        useCase = GetHotelsByIdsUseCase(getHotelByIdUseCase)
    }

    @Test
    fun invoke_emptyIds_emitsEmptyList() = runTest {
        val result = mutableListOf<List<Hotel>>()
        useCase(emptyList()).collect { result.add(it) }
        assertEquals(1, result.size)
        assertEquals(emptyList<Hotel>(), result[0])
    }

    @Test
    fun invoke_twoIds_emitsCombinedHotels() = runTest {
        val h1 = Hotel("id1", "Hotel A", "Paris", 100.0)
        val h2 = Hotel("id2", "Hotel B", "Rome", 150.0)
        fakeRepository.hotelsById["id1"] = flowOf(h1)
        fakeRepository.hotelsById["id2"] = flowOf(h2)
        val result = mutableListOf<List<Hotel>>()
        useCase(listOf("id1", "id2")).collect { result.add(it) }
        assertEquals(1, result.size)
        assertEquals(2, result[0].size)
        assertEquals(h1, result[0][0])
        assertEquals(h2, result[0][1])
    }

    private class FakeHotelRepository : HotelRepository {
        val hotelsById = mutableMapOf<String, Flow<Hotel>>()

        override fun getHotels(city: String, checkInDay: Long?, checkOutDay: Long?): Flow<List<Hotel>> = flowOf(emptyList())
        override fun getHotelsPaged(
            city: String,
            checkInDay: Long,
            checkOutDay: Long,
            minPrice: Double?,
            maxPrice: Double?,
            refreshTrigger: Flow<Unit>
        ): Flow<PagingData<Hotel>> = Pager(
            config = PagingConfig(20),
            pagingSourceFactory = {
                object : PagingSource<Int, Hotel>() {
                    override fun getRefreshKey(state: PagingState<Int, Hotel>): Int? = null
                    override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, Hotel> =
                        PagingSource.LoadResult.Page(emptyList(), null, null)
                }
            }
        ).flow
        override fun getHotelById(id: String): Flow<Hotel> = hotelsById[id] ?: flowOf()
        override fun getFavoriteHotels(): Flow<List<Hotel>> = flowOf(emptyList())
        override suspend fun toggleFavorite(hotelId: String) {}
        override suspend fun refreshHotels(city: String) {}
    }
}
