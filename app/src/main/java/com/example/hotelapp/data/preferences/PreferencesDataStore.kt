package com.example.hotelapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

private val Context.userPrefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

internal val KEY_CITY = stringPreferencesKey("search_city")
internal val KEY_DATE_FROM_MILLIS = longPreferencesKey("search_date_from_millis")
internal val KEY_DATE_TO_MILLIS = longPreferencesKey("search_date_to_millis")
internal val KEY_LANGUAGE_TAG = stringPreferencesKey("language_tag")
internal val KEY_CURRENCY = stringPreferencesKey("currency")
internal val KEY_USERNAME = stringPreferencesKey("username")

internal fun Context.getUserPreferencesDataStore(): DataStore<Preferences> = userPrefsDataStore
