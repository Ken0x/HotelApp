package com.example.hotelapp.data.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.Pager
import androidx.paging.map
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

/**
 * Offline-first: prvo emituje iz Room-a, u pozadini dohvaća s API-ja, snima u Room;
 * Flow iz locala ponovo emituje kad se baza ažurira. Filtriranje po gradu i rasponu datuma radi lokalno nad kešom.
 */
class HotelRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val hotelDataEnhancer: HotelDataEnhancer
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
                } catch (_: Exception) {
                    // ostavi postojeće lokalne podatke; ne prekida Flow
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

    /**
     * Lokalno filtriranje: isključi hotele koji nisu dostupni u odabranom rasponu.
     * Kad su [checkInDay] i [checkOutDay] postavljeni (≥ 0), uključeni su samo hotele čiji
     * raspon dostupnosti (availableFromDay..availableToDay) preklapa odabrani raspon.
     * Hotele bez unesenog raspona dostupnosti isključujemo kad je datum odabran.
     */
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
                } catch (_: Exception) {
                    // ostavi postojeće lokalne podatke
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
