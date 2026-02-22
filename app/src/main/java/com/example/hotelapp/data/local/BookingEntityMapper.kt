package com.example.hotelapp.data.local

import com.example.hotelapp.data.local.entity.BookingEntity
import com.example.hotelapp.domain.model.Booking

fun BookingEntity.toDomain(): Booking = Booking(
    id = id,
    hotelId = hotelId,
    hotelName = hotelName,
    city = city,
    checkInDay = checkInDay,
    checkOutDay = checkOutDay,
    totalPrice = totalPrice
)

fun List<BookingEntity>.toDomain(): List<Booking> = map { it.toDomain() }

fun Booking.toEntity(): BookingEntity = BookingEntity(
    id = id,
    hotelId = hotelId,
    hotelName = hotelName,
    city = city,
    checkInDay = checkInDay,
    checkOutDay = checkOutDay,
    totalPrice = totalPrice
)
