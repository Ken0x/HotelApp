package com.example.hotelapp.data.local

import androidx.paging.PagingSource
import com.example.hotelapp.data.local.dao.HotelDao
import com.example.hotelapp.data.local.entity.HotelEntity
import com.example.hotelapp.domain.model.Hotel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Room-backed local source: čita/piše preko [HotelDao], mapira entity ↔ domain u data sloju.
 */
class LocalDataSourceImpl @Inject constructor(
    private val hotelDao: HotelDao
) : LocalDataSource {

    override fun getHotelsByCity(city: String): Flow<List<Hotel>> =
        hotelDao.getHotelsByCity(city).map { it.toDomain() }

    override fun getHotelsByCityPaged(city: String, minPrice: Double, maxPrice: Double): PagingSource<Int, HotelEntity> =
        hotelDao.getHotelsByCityPaged(city, minPrice, maxPrice)

    override fun getHotelsByCityAndDateRangePaged(
        city: String,
        checkInDay: Long,
        checkOutDay: Long,
        minPrice: Double,
        maxPrice: Double
    ): PagingSource<Int, HotelEntity> =
        hotelDao.getHotelsByCityAndDateRangePaged(city, checkInDay, checkOutDay, minPrice, maxPrice)

    override suspend fun saveHotels(hotels: List<Hotel>) {
        if (hotels.isEmpty()) return
        val city = hotels.first().city
        val existingMap = hotelDao.getHotelsByCityOnce(city).toDomain().associateBy { it.id }
        val merged = hotels.map { h ->
            val existing = existingMap[h.id]
            val priceToUse = when {
                h.pricePerNight != null && h.pricePerNight!! > 0 -> h.pricePerNight
                existing?.pricePerNight != null && existing.pricePerNight > 0 -> existing.pricePerNight
                else -> null
            }
            h.copy(pricePerNight = priceToUse, isFavorite = existing?.isFavorite ?: false)
        }
        hotelDao.deleteByCity(city)
        hotelDao.insertAll(merged.toEntity())
    }

    override suspend fun saveHotel(hotel: Hotel) {
        val existing = hotelDao.getHotelByIdOnce(hotel.id)
        val priceToUse = when {
            hotel.pricePerNight != null && hotel.pricePerNight!! > 0 -> hotel.pricePerNight
            existing?.pricePerNight != null && existing.pricePerNight > 0 -> existing.pricePerNight
            else -> null
        }
        val isFavorite = existing?.isFavorite ?: false
        hotelDao.insertOrReplace(hotel.copy(pricePerNight = priceToUse, isFavorite = isFavorite).toEntity())
    }

    override fun getHotelById(id: String): Flow<Hotel?> =
        hotelDao.getHotelById(id).map { it?.toDomain() }

    override fun getFavoriteHotels(): Flow<List<Hotel>> =
        hotelDao.getFavoriteHotels().map { it.toDomain() }

    override suspend fun toggleFavorite(hotelId: String) {
        hotelDao.toggleFavorite(hotelId)
    }

    override suspend fun clearAll() {
        hotelDao.deleteAll()
    }
}
