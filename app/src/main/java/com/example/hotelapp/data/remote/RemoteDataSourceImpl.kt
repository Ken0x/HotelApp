package com.example.hotelapp.data.remote

import com.example.hotelapp.domain.model.Hotel
import javax.inject.Inject

/**
 * Koristi api.hotels-api.com.
 * getHotels(city) šalje grad u API pretragu.
 */
class RemoteDataSourceImpl @Inject constructor(
    private val api: HotelsApiService
) : RemoteDataSource {

    private var lastFetchedHotels: List<Hotel> = emptyList()

    override suspend fun getHotels(city: String): List<Hotel> {
        val response = api.searchHotels(city = city)
        val dtos = response.hotels ?: response.data ?: emptyList()
        lastFetchedHotels = dtos.toDomain()
        return lastFetchedHotels
    }

    override suspend fun getHotelById(id: String): Hotel? {
        return try {
            api.getHotelById(id)?.toDomain()
        } catch (e: Exception) {
            lastFetchedHotels.find { it.id == id }
        }
    }

}
