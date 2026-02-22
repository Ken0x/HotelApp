package com.example.hotelapp.data.local

import com.example.hotelapp.data.local.entity.HotelEntity
import com.example.hotelapp.data.remote.dto.HotelDto

/** Maps DTO → Entity in the data layer; no domain types. Keeps DTOs isolated from domain. */

fun HotelDto.toEntity(isFavorite: Boolean = false): HotelEntity = HotelEntity(
    id = id ?: hotelId ?: idAlt ?: "",
    name = hotelName ?: hotelNameSnake ?: name ?: title ?: "",
    city = city ?: location ?: destination ?: cityName ?: "",
    pricePerNight = pricePerNight ?: pricePerNightSnake ?: price ?: rate,
    description = description ?: descriptionShort ?: overview,
    availableFromDay = null,
    availableToDay = null,
    isFavorite = isFavorite
)

fun List<HotelDto>.toEntity(): List<HotelEntity> = map { it.toEntity() }
