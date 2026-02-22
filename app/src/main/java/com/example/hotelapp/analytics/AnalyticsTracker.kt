package com.example.hotelapp.analytics

/**
 * Apstrakcija za praćenje analitike. Implementacija nije vezana za konkretnog providera
 * (Firebase, Mixpanel, itd.) – zamjena se radi kroz DI.
 */
interface AnalyticsTracker {

    /**
     * Zabilježi pretragu hotela (npr. grad + datum).
     * @param city Grad u kojem se pretražuje; može biti prazan ako nije unesen.
     */
    fun trackSearch(city: String)

    /**
     * Zabilježi otvaranje detalja hotela (klik na hotel u listi).
     * @param hotelId ID hotela
     * @param hotelName Naziv hotela (opcionalno, za kontekst u izvještajima)
     */
    fun trackHotelClick(hotelId: String, hotelName: String? = null)

    /**
     * Zabilježi dodavanje hotela u favorite / usporedbu.
     * @param hotelId ID hotela
     * @param hotelName Naziv hotela (opcionalno)
     */
    fun trackFavorite(hotelId: String, hotelName: String? = null)
}
