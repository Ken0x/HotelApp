package com.example.hotelapp.domain.repository

import androidx.paging.PagingData
import com.example.hotelapp.domain.model.Hotel
import kotlinx.coroutines.flow.Flow

interface HotelRepository {
    fun getHotels(
        city: String,
        checkInDay: Long? = null,
        checkOutDay: Long? = null
    ): Flow<List<Hotel>>

    fun getHotelsPaged(
        city: String,
        checkInDay: Long,
        checkOutDay: Long,
        minPrice: Double?,
        maxPrice: Double?,
        refreshTrigger: Flow<Unit>
    ): Flow<PagingData<Hotel>>

    fun getHotelById(id: String): Flow<Hotel>
    fun getFavoriteHotels(): Flow<List<Hotel>>
    suspend fun toggleFavorite(hotelId: String)
    suspend fun refreshHotels(city: String)
}
