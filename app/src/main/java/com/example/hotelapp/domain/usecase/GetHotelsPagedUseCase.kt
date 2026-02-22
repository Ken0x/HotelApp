package com.example.hotelapp.domain.usecase

import androidx.paging.PagingData
import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Paginirana lista hotela po gradu (iz Room PagingSource). Datum i raspon cijene filtriraju rezultate. */
class GetHotelsPagedUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    operator fun invoke(
        city: String,
        checkInDay: Long,
        checkOutDay: Long,
        minPrice: Double?,
        maxPrice: Double?,
        refreshTrigger: Flow<Unit>
    ): Flow<PagingData<Hotel>> =
        repository.getHotelsPaged(city, checkInDay, checkOutDay, minPrice, maxPrice, refreshTrigger)
}
