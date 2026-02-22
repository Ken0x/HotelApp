package com.example.hotelapp.domain.model

data class Hotel(
    val id: String,
    val name: String,
    val city: String,
    val pricePerNight: Double? = null,
    val description: String? = null,
    val availableFromDay: Long? = null,
    val availableToDay: Long? = null,
    val isFavorite: Boolean = false
)
