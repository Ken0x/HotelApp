package com.example.hotelapp.domain.usecase

import com.example.hotelapp.domain.repository.HotelRepository
import javax.inject.Inject

/** Sync strategija: dohvat s API-ja i snimanje u Room (za app start i pull-to-refresh). */
class RefreshHotelsUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(city: String) = repository.refreshHotels(city)
}
