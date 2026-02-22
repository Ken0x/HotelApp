package com.example.hotelapp.domain.usecase

import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHotelsUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    /**
     * Pretraga po gradu i opciono po rasponu datuma (epoch day). Filtriranje po datumu radi lokalno nad kešom.
     */
    operator fun invoke(
        city: String,
        checkInDay: Long? = null,
        checkOutDay: Long? = null
    ): Flow<List<Hotel>> = repository.getHotels(city, checkInDay, checkOutDay)
}
