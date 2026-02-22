package com.example.hotelapp.analytics

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementacija koja ne šalje događaje nigdje. Koristi se kao zadano u DI;
 * zamjena s Firebase/Mixpanel/… radi se kroz drugi modul koji pruža [AnalyticsTracker].
 */
@Singleton
class NoOpAnalyticsTracker @Inject constructor() : AnalyticsTracker {

    override fun trackSearch(city: String) {}

    override fun trackHotelClick(hotelId: String, hotelName: String?) {}

    override fun trackFavorite(hotelId: String, hotelName: String?) {}
}
