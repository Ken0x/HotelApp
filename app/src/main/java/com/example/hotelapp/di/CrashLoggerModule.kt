package com.example.hotelapp.di

import com.example.hotelapp.crash.CrashLogger
import com.example.hotelapp.crash.NoOpCrashLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CrashLoggerModule {

    @Binds
    @Singleton
    abstract fun bindCrashLogger(impl: NoOpCrashLogger): CrashLogger
}
