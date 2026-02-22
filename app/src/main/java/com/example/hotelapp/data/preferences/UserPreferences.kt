package com.example.hotelapp.data.preferences

/**
 * Zadnji parametri pretrage, jezik, valuta i korisničko ime; perzistirani u DataStore.
 * [dateFromMillis] / [dateToMillis]: -1L = nije postavljeno.
 * [currency]: ISO 4217 kod (npr. EUR, BAM, USD); prazno = EUR.
 */
data class UserPreferences(
    val city: String = "",
    val dateFromMillis: Long = -1L,
    val dateToMillis: Long = -1L,
    val languageTag: String = "",
    val currency: String = "EUR",
    val username: String = ""
)
