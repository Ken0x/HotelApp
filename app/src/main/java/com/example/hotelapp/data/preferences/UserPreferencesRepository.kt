package com.example.hotelapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore: DataStore<Preferences> = context.getUserPreferencesDataStore()

    val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            city = prefs[KEY_CITY] ?: "",
            dateFromMillis = prefs[KEY_DATE_FROM_MILLIS] ?: -1L,
            dateToMillis = prefs[KEY_DATE_TO_MILLIS] ?: -1L,
            languageTag = prefs[KEY_LANGUAGE_TAG] ?: "",
            currency = prefs[KEY_CURRENCY] ?: "EUR",
            username = prefs[KEY_USERNAME] ?: ""
        )
    }

    suspend fun setSearchParams(city: String, dateFromMillis: Long, dateToMillis: Long) {
        dataStore.edit { prefs ->
            prefs[KEY_CITY] = city
            prefs[KEY_DATE_FROM_MILLIS] = dateFromMillis
            prefs[KEY_DATE_TO_MILLIS] = dateToMillis
        }
    }

    suspend fun setLanguageTag(tag: String) {
        dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE_TAG] = tag
        }
    }

    suspend fun setCurrency(currencyCode: String) {
        dataStore.edit { prefs ->
            prefs[KEY_CURRENCY] = currencyCode.ifBlank { "EUR" }
        }
    }

    suspend fun setUsername(username: String) {
        dataStore.edit { prefs ->
            prefs[KEY_USERNAME] = username
        }
    }

    /** Sync read za [attachBaseContext]; koristi runBlocking. */
    fun getLanguageTagBlocking(): String = runBlocking {
        dataStore.data.map { prefs -> prefs[KEY_LANGUAGE_TAG] ?: "" }.first()
    }

    suspend fun getLanguageTag(): String =
        dataStore.data.map { prefs -> prefs[KEY_LANGUAGE_TAG] ?: "" }.first()
}
