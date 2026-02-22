package com.example.hotelapp.util

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.datastore.preferences.core.edit
import com.example.hotelapp.data.preferences.KEY_LANGUAGE_TAG
import com.example.hotelapp.data.preferences.getUserPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

/** Supported app language tags: "en", "bs", "de". Empty = system default. Persisted in DataStore. */
fun getSavedLocale(context: Context): String = runBlocking {
    context.getUserPreferencesDataStore().data.first()[KEY_LANGUAGE_TAG] ?: ""
}

fun setSavedLocale(context: Context, languageTag: String) {
    runBlocking {
        context.getUserPreferencesDataStore().edit { prefs ->
            prefs[KEY_LANGUAGE_TAG] = languageTag
        }
    }
}

/**
 * Wraps the context so that resources use the saved (or given) locale.
 * Call from Activity.attachBaseContext.
 */
fun contextWithLocale(context: Context, languageTag: String? = null): Context {
    val tag = languageTag?.takeIf { it.isNotBlank() } ?: getSavedLocale(context)
    if (tag.isBlank()) return context
    val locale = when (tag) {
        "bs" -> Locale("bs")
        "de" -> Locale.GERMAN
        else -> Locale.ENGLISH
    }
    val config = Configuration(context.resources.configuration).apply {
        setLocale(locale)
        setLocales(LocaleList(locale))
    }
    return context.createConfigurationContext(config)
}
