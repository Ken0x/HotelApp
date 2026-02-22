package com.example.hotelapp.data.local

import com.example.hotelapp.data.local.entity.HotelEntity
import com.example.hotelapp.domain.model.Hotel

/** Mapping logic lives in the data layer; domain has no dependency on Room. */

fun HotelEntity.toDomain(): Hotel = Hotel(
    id = id,
    name = name,
    city = city,
    pricePerNight = pricePerNight,
    description = description,
    availableFromDay = availableFromDay,
    availableToDay = availableToDay,
    isFavorite = isFavorite
)

fun List<HotelEntity>.toDomain(): List<Hotel> = map { it.toDomain() }

fun Hotel.toEntity(): HotelEntity = HotelEntity(
    id = id,
    name = name,
    city = city,
    pricePerNight = pricePerNight,
    description = description,
    availableFromDay = availableFromDay,
    availableToDay = availableToDay,
    isFavorite = isFavorite
)

fun List<Hotel>.toEntity(): List<HotelEntity> = map { it.toEntity() }
