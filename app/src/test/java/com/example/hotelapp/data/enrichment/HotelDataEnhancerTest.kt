package com.example.hotelapp.data.enrichment

import com.example.hotelapp.domain.model.Hotel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit testovi za determinističko generiranje cijene i dostupnosti.
 * Isti [Hotel.id] uvijek daje iste vrijednosti.
 */
class HotelDataEnhancerTest {

    private val enhancer = HotelDataEnhancer()

    @Test
    fun enhance_sameId_producesSamePriceAndAvailability() {
        val hotel = Hotel("hotel-1", "Test Hotel", "City", null, null, null)
        val a = enhancer.enhance(hotel)
        val b = enhancer.enhance(hotel)
        assertEquals(a.pricePerNight, b.pricePerNight)
        assertEquals(a.availableFromDay, b.availableFromDay)
        assertEquals(a.availableToDay, b.availableToDay)
    }

    @Test
    fun enhance_nullPrice_generatesPriceInRange() {
        val hotel = Hotel("id-42", "Hotel", "Sarajevo", null, null, null)
        val enhanced = enhancer.enhance(hotel)
        assertNotNull(enhanced.pricePerNight)
        assertTrue(
            "Price should be in [${HotelDataEnhancer.PRICE_MIN_KM}, ${HotelDataEnhancer.PRICE_MAX_KM}]",
            enhanced.pricePerNight!! in HotelDataEnhancer.PRICE_MIN_KM.toDouble()..HotelDataEnhancer.PRICE_MAX_KM.toDouble()
        )
    }

    @Test
    fun enhance_existingPrice_preserved() {
        val hotel = Hotel("id-99", "Hotel", "Mostar", 120.0, null, null)
        val enhanced = enhancer.enhance(hotel)
        assertEquals(120.0, enhanced.pricePerNight!!, 0.0)
    }

    @Test
    fun enhance_nullAvailability_generatesDeterministicRange() {
        val hotel = Hotel("deterministic-id", "Hotel", "City", 100.0, null, null)
        val enhanced = enhancer.enhance(hotel)
        assertNotNull(enhanced.availableFromDay)
        assertNotNull(enhanced.availableToDay)
        assertTrue(enhanced.availableToDay!! > enhanced.availableFromDay!!)
        // Isti id drugi put – isti raspon
        val enhanced2 = enhancer.enhance(hotel)
        assertEquals(enhanced.availableFromDay, enhanced2.availableFromDay)
        assertEquals(enhanced.availableToDay, enhanced2.availableToDay)
    }

    @Test
    fun enhance_existingAvailability_preserved() {
        val hotel = Hotel("id", "H", "C", 80.0, null, 19_700L, 19_800L)
        val enhanced = enhancer.enhance(hotel)
        assertEquals(19_700L, enhanced.availableFromDay)
        assertEquals(19_800L, enhanced.availableToDay)
    }
}
