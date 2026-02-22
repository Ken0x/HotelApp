package com.example.hotelapp.di

import android.content.Context
import androidx.room.Room
import com.example.hotelapp.data.local.AppDatabase
import com.example.hotelapp.data.local.dao.BookingDao
import com.example.hotelapp.data.local.dao.HotelDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /** Trajna baza na disku (podaci prežive restart uređaja). Gube se samo pri deinstalaciji ili „Clear data“. */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "hotel_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideHotelDao(db: AppDatabase): HotelDao = db.hotelDao()

    @Provides
    @Singleton
    fun provideBookingDao(db: AppDatabase): BookingDao = db.bookingDao()
}
