package com.example.hotelapp.data.repository

import com.example.hotelapp.data.remote.RemoteDataSource
import com.example.hotelapp.domain.model.Hotel
import kotlinx.coroutines.delay

/** Fake [RemoteDataSource] for integration tests. */
class FakeRemoteDataSource : RemoteDataSource {

    var hotelsToReturn: List<Hotel> = emptyList()
    var hotelByIdToReturn: Hotel? = null
    /** Delay before returning from [getHotels] to allow asserting cached-first behaviour. */
    var getHotelsDelayMs: Long = 0

    override suspend fun getHotels(city: String): List<Hotel> {
        if (getHotelsDelayMs > 0) delay(getHotelsDelayMs)
        return hotelsToReturn
    }

    override suspend fun getHotelById(id: String): Hotel? = hotelByIdToReturn
}
