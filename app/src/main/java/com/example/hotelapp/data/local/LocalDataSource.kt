package com.example.hotelapp.data.local

import androidx.paging.PagingSource
import com.example.hotelapp.data.local.entity.HotelEntity
import com.example.hotelapp.domain.model.Hotel
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getHotelsByCity(city: String): Flow<List<Hotel>>

    /** [minPrice]/[maxPrice]: -1.0 = bez filtra po cijeni. */
    fun getHotelsByCityPaged(city: String, minPrice: Double, maxPrice: Double): PagingSource<Int, HotelEntity>

    /** Paginirana lista hotela u gradu dostupnih u rasponu [checkInDay]..[checkOutDay]; -1.0 = bez filtra cijene. */
    fun getHotelsByCityAndDateRangePaged(
        city: String,
        checkInDay: Long,
        checkOutDay: Long,
        minPrice: Double,
        maxPrice: Double
    ): PagingSource<Int, HotelEntity>
    suspend fun saveHotels(hotels: List<Hotel>)
    suspend fun saveHotel(hotel: Hotel)
    fun getHotelById(id: String): Flow<Hotel?>
    fun getFavoriteHotels(): Flow<List<Hotel>>
    suspend fun toggleFavorite(hotelId: String)
    suspend fun clearAll()
}
