package com.example.hotelapp.domain.usecase

import com.example.hotelapp.domain.model.Hotel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/** Dohvaća hotele po listi ID-ova; koristi se za ekran usporedbe. */
class GetHotelsByIdsUseCase @Inject constructor(
    private val getHotelByIdUseCase: GetHotelByIdUseCase
) {
    operator fun invoke(ids: List<String>): Flow<List<Hotel>> =
        if (ids.isEmpty()) kotlinx.coroutines.flow.flowOf(emptyList())
        else combine(ids.map { getHotelByIdUseCase(it) }) { it.toList() }
}
