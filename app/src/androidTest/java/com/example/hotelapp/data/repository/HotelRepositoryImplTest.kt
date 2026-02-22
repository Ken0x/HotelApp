package com.example.hotelapp.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hotelapp.data.enrichment.HotelDataEnhancer
import com.example.hotelapp.data.local.AppDatabase
import com.example.hotelapp.data.local.LocalDataSourceImpl
import com.example.hotelapp.domain.model.Hotel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for [HotelRepositoryImpl].
 * Uses in-memory Room and [FakeRemoteDataSource]. Verifies offline-first behaviour:
 * cached data is emitted first, then updated after remote sync.
 */
@RunWith(AndroidJUnit4::class)
class HotelRepositoryImplTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var localDataSource: LocalDataSourceImpl
    private lateinit var fakeRemote: FakeRemoteDataSource
    private lateinit var hotelDataEnhancer: HotelDataEnhancer
    private lateinit var repository: HotelRepositoryImpl

    private val city = "Barcelona"

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        localDataSource = LocalDataSourceImpl(db.hotelDao())
        fakeRemote = FakeRemoteDataSource()
        hotelDataEnhancer = HotelDataEnhancer()
        repository = HotelRepositoryImpl(fakeRemote, localDataSource, hotelDataEnhancer)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun getHotels_emitsCachedDataFirst_thenUpdatedAfterSync() = runTest {
        val cachedHotels = listOf(
            Hotel("1", "Cached Hotel", city, 50.0),
            Hotel("2", "Old Second", city, 60.0)
        )
        val remoteHotels = listOf(
            Hotel("1", "Updated Hotel", city, 55.0),
            Hotel("2", "Old Second", city, 60.0),
            Hotel("3", "New Third", city, 70.0)
        )
        localDataSource.saveHotels(cachedHotels)
        fakeRemote.hotelsToReturn = remoteHotels
        fakeRemote.getHotelsDelayMs = 200

        val emissions = mutableListOf<List<Hotel>>()
        val job = launch {
            repository.getHotels(city, null, null).collect { emissions.add(it) }
        }
        delay(100)
        assertEquals(
            "First emission should be cached data",
            cachedHotels,
            emissions.getOrNull(0)
        )
        delay(250)
        val expectedAfterSync = remoteHotels.map { hotelDataEnhancer.enhance(it) }
        assertEquals(
            "Second emission should be enhanced remote data after sync",
            expectedAfterSync,
            emissions.getOrNull(1)
        )
        job.cancel()
    }

    @Test
    fun getHotels_emitsEmptyFirst_thenRemoteDataWhenCacheEmpty() = runTest {
        fakeRemote.hotelsToReturn = listOf(
            Hotel("1", "Hotel One", city, 100.0)
        )
        fakeRemote.getHotelsDelayMs = 100

        val emissions = mutableListOf<List<Hotel>>()
        val job = launch {
            repository.getHotels(city, null, null).collect { emissions.add(it) }
        }
        delay(50)
        assertEquals(
            "First emission should be empty (no cache)",
            emptyList<Hotel>(),
            emissions.getOrNull(0)
        )
        delay(150)
        val expectedAfterSync = fakeRemote.hotelsToReturn.map { hotelDataEnhancer.enhance(it) }
        assertEquals(
            "Second emission should be enhanced remote data after sync",
            expectedAfterSync,
            emissions.getOrNull(1)
        )
        job.cancel()
    }
}
