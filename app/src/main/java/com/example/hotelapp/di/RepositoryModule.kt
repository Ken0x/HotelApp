package com.example.hotelapp.di

import com.example.hotelapp.data.repository.BookingRepositoryImpl
import com.example.hotelapp.data.repository.HotelRepositoryImpl
import com.example.hotelapp.domain.repository.BookingRepository
import com.example.hotelapp.domain.repository.HotelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHotelRepository(
        hotelRepositoryImpl: HotelRepositoryImpl
    ): HotelRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        bookingRepositoryImpl: BookingRepositoryImpl
    ): BookingRepository
}
