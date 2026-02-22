package com.example.hotelapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hotelapp.data.local.dao.BookingDao
import com.example.hotelapp.data.local.dao.HotelDao
import com.example.hotelapp.data.local.entity.BookingEntity
import com.example.hotelapp.data.local.entity.HotelEntity

/** Room baza: hotele (offline-first) i rezervacije (samo lokalno). */
@Database(
    entities = [HotelEntity::class, BookingEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hotelDao(): HotelDao
    abstract fun bookingDao(): BookingDao
}
