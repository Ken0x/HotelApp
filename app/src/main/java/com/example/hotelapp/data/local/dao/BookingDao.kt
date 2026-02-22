package com.example.hotelapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hotelapp.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    /** Sve rezervacije, najnovije prvo (po check-in datumu). */
    @Query("SELECT * FROM bookings ORDER BY checkInDay DESC")
    fun getBookings(): Flow<List<BookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BookingEntity)
}
