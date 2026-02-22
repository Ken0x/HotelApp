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

class GetFavoriteHotelsUseCaseTest {

    private lateinit var useCase: GetFavoriteHotelsUseCase
    private lateinit var fakeRepository: FakeHotelRepository

    @Before
    fun setUp() {
        fakeRepository = FakeHotelRepository()
        useCase = GetFavoriteHotelsUseCase(fakeRepository)
    }

    @Test
    fun invoke_emitsFavoriteHotelsFromRepository() = runTest {
        val favorites = listOf(
            Hotel("h1", "Favorite One", "Paris", 120.0),
            Hotel("h2", "Favorite Two", "Rome", 80.0)
        )
        fakeRepository.favoritesFlow = flowOf(favorites)
        val result = mutableListOf<List<Hotel>>()
        useCase().collect { result.add(it) }
        assertEquals(1, result.size)
        assertEquals(favorites, result[0])
    }

    @Test
    fun invoke_emitsEmptyListWhenNoFavorites() = runTest {
        fakeRepository.favoritesFlow = flowOf(emptyList())
        val result = mutableListOf<List<Hotel>>()
        useCase().collect { result.add(it) }
        assertEquals(1, result.size)
        assertEquals(emptyList<Hotel>(), result[0])
    }

    private class FakeHotelRepository : HotelRepository {
        var favoritesFlow = flowOf(emptyList<Hotel>())
        override fun getHotels(city: String, checkInDay: Long?, checkOutDay: Long?): Flow<List<Hotel>> = flowOf(emptyList<Hotel>())
        override fun getHotelsPaged(city: String, checkInDay: Long, checkOutDay: Long, minPrice: Double?, maxPrice: Double?, refreshTrigger: Flow<Unit>): Flow<PagingData<Hotel>> =
            Pager(PagingConfig(20), pagingSourceFactory = {
                object : PagingSource<Int, Hotel>() {
                    override fun getRefreshKey(state: PagingState<Int, Hotel>): Int? = null
                    override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, Hotel> =
                        PagingSource.LoadResult.Page(emptyList(), null, null)
                }
            }).flow
        override fun getHotelById(id: String): Flow<Hotel> = flowOf()
        override fun getFavoriteHotels() = favoritesFlow
        override suspend fun toggleFavorite(hotelId: String) {}
        override suspend fun refreshHotels(city: String) {}
    }
}
