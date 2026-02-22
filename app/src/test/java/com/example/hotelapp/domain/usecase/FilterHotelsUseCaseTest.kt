package com.example.hotelapp.domain.usecase

import com.example.hotelapp.domain.model.Hotel
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterHotelsUseCaseTest {

    private val useCase = FilterHotelsUseCase()

    @Test
    fun invoke_sortAsc_ordersByPriceLowToHigh() {
        val hotels = listOf(
            Hotel("1", "Expensive", "Paris", 200.0),
            Hotel("2", "Cheap", "Paris", 50.0),
            Hotel("3", "Mid", "Paris", 100.0)
        )
        val result = useCase(hotels, sortByPriceAsc = true, null, null)
        assertEquals(listOf(50.0, 100.0, 200.0), result.map { it.pricePerNight })
    }

    @Test
    fun invoke_sortDesc_ordersByPriceHighToLow() {
        val hotels = listOf(
            Hotel("1", "Cheap", "Paris", 50.0),
            Hotel("2", "Expensive", "Paris", 200.0)
        )
        val result = useCase(hotels, sortByPriceAsc = false, null, null)
        assertEquals(200.0, result[0].pricePerNight)
        assertEquals(50.0, result[1].pricePerNight)
    }

    @Test
    fun invoke_minPrice_filtersOutLowerPrices() {
        val hotels = listOf(
            Hotel("1", "A", "Paris", 30.0),
            Hotel("2", "B", "Paris", 80.0),
            Hotel("3", "C", "Paris", 120.0)
        )
        val result = useCase(hotels, sortByPriceAsc = true, minPrice = 50.0, null)
        assertEquals(2, result.size)
        assertEquals(80.0, result[0].pricePerNight)
        assertEquals(120.0, result[1].pricePerNight)
    }

    @Test
    fun invoke_maxPrice_filtersOutHigherPrices() {
        val hotels = listOf(
            Hotel("1", "A", "Paris", 80.0),
            Hotel("2", "B", "Paris", 150.0)
        )
        val result = useCase(hotels, sortByPriceAsc = true, null, maxPrice = 100.0)
        assertEquals(1, result.size)
        assertEquals(80.0, result[0].pricePerNight)
    }

    @Test
    fun invoke_nullPriceHotels_excludedWhenMinPriceSet() {
        val hotels = listOf(
            Hotel("1", "No price", "Paris", null),
            Hotel("2", "With price", "Paris", 100.0)
        )
        val result = useCase(hotels, sortByPriceAsc = true, minPrice = 50.0, null)
        assertEquals(1, result.size)
        assertEquals(100.0, result[0].pricePerNight)
    }
}
