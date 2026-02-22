package com.example.hotelapp.data.enrichment

import com.example.hotelapp.domain.model.Hotel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Popunjava nedostajuće [pricePerNight] i raspon [availableFromDay]..[availableToDay]
 * deterministički na temelju [Hotel.id], prije spremanja u Room.
 * Isti id uvijek daje iste vrijednosti – pogodno za testove.
 */
@Singleton
class HotelDataEnhancer @Inject constructor() {

    fun enhance(hotel: Hotel): Hotel {
        val seed = hotel.id.hashCode()
        val pricePerNight = when {
            hotel.pricePerNight != null && hotel.pricePerNight!! > 0 -> hotel.pricePerNight
            else -> generatePricePerNight(seed)
        }
        val (fromDay, toDay) = when {
            hotel.availableFromDay != null && hotel.availableToDay != null ->
                hotel.availableFromDay!! to hotel.availableToDay!!
            else -> generateAvailabilityRange(seed)
        }
        return hotel.copy(
            pricePerNight = pricePerNight,
            availableFromDay = fromDay,
            availableToDay = toDay
        )
    }

    private fun generatePricePerNight(seed: Int): Double {
        val normalized = kotlin.math.abs(seed) % (PRICE_MAX_KM - PRICE_MIN_KM + 1)
        return (PRICE_MIN_KM + normalized).toDouble()
    }

    private fun generateAvailabilityRange(seed: Int): Pair<Long, Long> {
        val absSeed = kotlin.math.abs(seed)
        val fromDay = EPOCH_DAY_BASE + (absSeed % 365)
        val windowDays = AVAILABILITY_WINDOW_MIN + (kotlin.math.abs(seed * 31) % (AVAILABILITY_WINDOW_EXTRA + 1))
        val toDay = fromDay + windowDays
        return fromDay to toDay
    }

    companion object {
        /** Minimalna generirana cijena po noći (KM). */
        const val PRICE_MIN_KM = 80

        /** Maksimalna generirana cijena po noći (KM). */
        const val PRICE_MAX_KM = 350

        /** Bazni epoch day za početak raspona dostupnosti (npr. ~2023). */
        const val EPOCH_DAY_BASE = 19_700L

        /** Minimalni broj dana u rasponu dostupnosti. */
        const val AVAILABILITY_WINDOW_MIN = 60

        /** Dodatni dani (0..EXTRA) za varijabilnost raspona. */
        const val AVAILABILITY_WINDOW_EXTRA = 90
    }
}
