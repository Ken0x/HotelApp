package com.example.hotelapp.domain.model

data class Booking(
    val id: String,
    val hotelId: String,
    val hotelName: String,
    val city: String,
    val checkInDay: Long,
    val checkOutDay: Long,
    val totalPrice: Double
)
