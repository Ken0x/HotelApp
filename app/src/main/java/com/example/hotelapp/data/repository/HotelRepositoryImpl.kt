package com.example.hotelapp.data.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.Pager
import androidx.paging.map
import com.example.hotelapp.crash.CrashLogger
import com.example.hotelapp.data.enrichment.HotelDataEnhancer
import com.example.hotelapp.data.local.LocalDataSource
import com.example.hotelapp.data.local.toDomain
import com.example.hotelapp.data.remote.RemoteDataSource
import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.domain.repository.HotelRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

class HotelRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val hotelDataEnhancer: HotelDataEnhancer,
    private val crashLogger: CrashLogger
) : HotelRepository {

    override fun getHotels(
        city: String,
        checkInDay: Long?,
        checkOutDay: Long?
    ): Flow<List<Hotel>> = flow {
        coroutineScope {
            launch {
                try {
                    val hotels = remoteDataSource.getHotels(city).map { hotelDataEnhancer.enhance(it) }
                    localDataSource.saveHotels(hotels)
                } catch (e: Exception) {
                    crashLogger.log(e, "getHotels: fetch or save failed for city=$city")
                }
            }
        }
        emitAll(
            localDataSource.getHotelsByCity(city).map { cached ->
                filterByDateRange(cached, checkInDay, checkOutDay)
            }
        )
    }

    override fun getHotelsPaged(
        city: String,
        checkInDay: Long,
        checkOutDay: Long,
        minPrice: Double?,
        maxPrice: Double?,
        refreshTrigger: Flow<Unit>
    ): Flow<PagingData<Hotel>> {
        val min = minPrice?.takeIf { it >= 0 } ?: -1.0
        val max = maxPrice?.takeIf { it >= 0 } ?: -1.0
        return merge(flowOf(Unit), refreshTrigger).flatMapLatest {
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = {
                    if (checkInDay >= 0 && checkOutDay >= 0) {
                        localDataSource.getHotelsByCityAndDateRangePaged(city, checkInDay, checkOutDay, min, max)
                    } else {
                        localDataSource.getHotelsByCityPaged(city, min, max)
                    }
                }
            ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
        }
    }

    private fun filterByDateRange(
        hotels: List<Hotel>,
        checkInDay: Long?,
        checkOutDay: Long?
    ): List<Hotel> {
        if (checkInDay == null || checkOutDay == null || checkInDay < 0 || checkOutDay < 0) return hotels
        return hotels.filter { hotel ->
            val from = hotel.availableFromDay
            val to = hotel.availableToDay
            from != null && to != null && checkInDay <= to && checkOutDay >= from
        }
    }

    override fun getHotelById(id: String): Flow<Hotel> = flow {
        coroutineScope {
            launch {
                try {
                    remoteDataSource.getHotelById(id)?.let { localDataSource.saveHotel(hotelDataEnhancer.enhance(it)) }
                } catch (e: Exception) {
                    crashLogger.log(e, "getHotelById: fetch or save failed for id=$id")
                }
            }
        }
        emitAll(localDataSource.getHotelById(id).mapNotNull { it })
    }

    override fun getFavoriteHotels(): Flow<List<Hotel>> =
        localDataSource.getFavoriteHotels()

    override suspend fun toggleFavorite(hotelId: String) {
        localDataSource.toggleFavorite(hotelId)
    }

    override suspend fun refreshHotels(city: String) {
        val hotels = remoteDataSource.getHotels(city).map { hotelDataEnhancer.enhance(it) }
        localDataSource.saveHotels(hotels)
    }
}
