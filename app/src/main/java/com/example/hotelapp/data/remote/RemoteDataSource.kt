package com.example.hotelapp.data.remote

import com.example.hotelapp.domain.model.Hotel

interface RemoteDataSource {
    suspend fun getHotels(city: String): List<Hotel>
    suspend fun getHotelById(id: String): Hotel?
}
