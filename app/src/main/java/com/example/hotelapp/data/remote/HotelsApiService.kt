package com.example.hotelapp.data.remote

import com.example.hotelapp.data.remote.dto.HotelDto
import com.example.hotelapp.data.remote.dto.HotelsSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HotelsApiService {

    @GET("v1/hotels/search")
    suspend fun searchHotels(
        @Query("city") city: String
    ): HotelsSearchResponse

    @GET("v1/hotels/{id}")
    suspend fun getHotelById(
        @Path("id") id: String
    ): HotelDto?
}
