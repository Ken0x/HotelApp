package com.example.hotelapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HotelDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("hotelId") val hotelId: String? = null,
    @SerializedName("_id") val idAlt: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("hotelName") val hotelName: String? = null,
    @SerializedName("hotel_name") val hotelNameSnake: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("destination") val destination: String? = null,
    @SerializedName("cityName") val cityName: String? = null,
    @SerializedName("pricePerNight") val pricePerNight: Double? = null,
    @SerializedName("price_per_night") val pricePerNightSnake: Double? = null,
    @SerializedName("price") val price: Double? = null,
    @SerializedName("rate") val rate: Double? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("desc") val descriptionShort: String? = null,
    @SerializedName("overview") val overview: String? = null,
)
