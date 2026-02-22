package com.example.hotelapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room entity za rezervacije; samo lokalna pohrana. */
@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey
    val id: String,
    val hotelId: String,
    val hotelName: String,
    val city: String,
    val checkInDay: Long,
    val checkOutDay: Long,
    val totalPrice: Double
)
