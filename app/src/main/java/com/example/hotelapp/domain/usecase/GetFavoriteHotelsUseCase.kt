package com.example.hotelapp.domain.usecase

import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteHotelsUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    operator fun invoke(): Flow<List<Hotel>> = repository.getFavoriteHotels()
}
