package com.example.hotelapp.domain.usecase

import com.example.hotelapp.domain.repository.HotelRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(hotelId: String) {
        repository.toggleFavorite(hotelId)
    }
}
