package com.example.hotelapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Wrapper za odgovor api.hotels-api.com.
 * Podržava različite formate: "hotels", "data" ili direktan niz.
 */
data class HotelsSearchResponse(
    @SerializedName("hotels") val hotels: List<HotelDto>? = null,
    @SerializedName("data") val data: List<HotelDto>? = null,
)
