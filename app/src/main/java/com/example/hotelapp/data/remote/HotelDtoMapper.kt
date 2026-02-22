package com.example.hotelapp.data.remote

import com.example.hotelapp.data.remote.dto.HotelDto
import com.example.hotelapp.domain.model.Hotel

fun HotelDto.toDomain(): Hotel = Hotel(
    id = id ?: hotelId ?: idAlt ?: "",
    name = hotelName ?: hotelNameSnake ?: name ?: title ?: "",
    city = city ?: location ?: destination ?: cityName ?: "",
    pricePerNight = pricePerNight ?: pricePerNightSnake ?: price ?: rate,
    description = description ?: descriptionShort ?: overview,
    availableFromDay = null,
    availableToDay = null
)

fun List<HotelDto>.toDomain(): List<Hotel> = map { it.toDomain() }
