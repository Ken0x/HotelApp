package com.example.hotelapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room entity for hotels; offline-first persistence. */
@Entity(tableName = "hotels")
data class HotelEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val city: String,
    /** Cijena po noći; null ako nije dostupna. */
    val pricePerNight: Double? = null,
    /** Opis hotela; null ako nije dostupan. */
    val description: String? = null,
    /** Dostupno od (epoch day); null = nema ograničenja. */
    val availableFromDay: Long? = null,
    /** Dostupno do (epoch day); null = nema ograničenja. */
    val availableToDay: Long? = null,
    /** Korisnik je označio hotel kao favorit. */
    val isFavorite: Boolean = false
)
