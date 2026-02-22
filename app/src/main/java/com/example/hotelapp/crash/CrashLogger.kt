package com.example.hotelapp.crash

/**
 * Apstrakcija za prijavu crash-eva. Implementacija može slati izvještaje u Firebase Crashlytics,
 * Sentry, ili samo logirati u logcat. Zamjena se radi kroz DI.
 */
interface CrashLogger {

    /**
     * Prijavi neuhvaćeni iznimku (npr. iz default uncaught exception handlera).
     * @param throwable Iznimka
     * @param message Opcionalna poruka za kontekst
     */
    fun log(throwable: Throwable, message: String? = null)

    /**
     * Označi korisnika za identifikaciju u izvještajima (npr. anonymizirani ID).
     * Poziva se nakon prijave; implementacija može ignorirati.
     */
    fun setUserId(userId: String?) {}
}
