package com.example.hotelapp.domain.usecase

import com.example.hotelapp.domain.model.Hotel
import javax.inject.Inject

/**
 * Lokalno sortiranje i filtriranje keširane liste hotela.
 * Primjenjuje se samo nad podacima koji su već u memoriji (iz keša).
 */
class FilterHotelsUseCase @Inject constructor() {

    /**
     * @param hotels lista hotela (npr. iz keša)
     * @param sortByPriceAsc true = jeftiniji prvo, false = skuplji prvo; hoteli bez cijene idu na kraj
     * @param minPrice minimalna cijena po noći (null = bez filtra); zadržavaju se hoteli gdje je cijena >= minPrice; hoteli s null cijenom se isključuju kad je minPrice postavljen
     * @param maxPrice maksimalna cijena po noći (null = bez filtra); zadržavaju se hoteli gdje je cijena <= maxPrice; hoteli s null cijenom se zadržavaju
     */
    operator fun invoke(
        hotels: List<Hotel>,
        sortByPriceAsc: Boolean,
        minPrice: Double?,
        maxPrice: Double?
    ): List<Hotel> {
        val filtered = hotels.filter { hotel ->
            val p = hotel.pricePerNight
            val okMin = minPrice == null || (p != null && p >= minPrice)
            val okMax = maxPrice == null || p == null || p <= maxPrice
            okMin && okMax
        }
        val priceComparator = compareBy<Hotel> { it.pricePerNight == null }
            .then(
                if (sortByPriceAsc)
                    compareBy { it.pricePerNight ?: 0.0 }
                else
                    compareByDescending { it.pricePerNight ?: 0.0 }
            )
        return filtered.sortedWith(priceComparator)
    }
}
