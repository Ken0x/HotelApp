package com.example.hotelapp.util

import java.util.Currency
import java.util.Locale

/**
 * API cijene su u EUR. Lokalna konverzija u odabranu valutu za prikaz.
 * Tečajevi su pojednostavljeni (fiksni); u produkciji bi došli s API-ja ili konfiguracije.
 */
object CurrencyRates {
    private const val EUR = "EUR"
    private const val BAM = "BAM"
    private const val USD = "USD"

    /** Tečajevi prema EUR (1 EUR = X u target valuti). */
    private val rateFromEur: Map<String, Double> = mapOf(
        EUR to 1.0,
        BAM to 1.95,
        USD to 1.08
    )

    fun convertFromEur(amountEur: Double, targetCurrencyCode: String): Double {
        val rate = rateFromEur[targetCurrencyCode] ?: rateFromEur[EUR]!!
        return amountEur * rate
    }

    fun supportedCurrencies(): List<String> = listOf(EUR, BAM, USD)
}

/**
 * Formatira cijenu u odabranoj valuti s locale-aware formatiranjem.
 * [priceInEur] – cijena u EUR (API standard); null ili ≤ 0 → [onRequestStr].
 * [perNightLabel] – opciono " / night" ili lokalizirani ekvivalent; null = bez sufiksa.
 */
fun formatPriceWithCurrency(
    priceInEur: Double?,
    currencyCode: String,
    locale: Locale,
    onRequestStr: String,
    perNightLabel: String? = null
): String {
    if (priceInEur == null || priceInEur <= 0) return onRequestStr
    val amount = CurrencyRates.convertFromEur(priceInEur, currencyCode.ifBlank { "EUR" })
    val currency = try {
        Currency.getInstance(currencyCode.ifBlank { "EUR" })
    } catch (_: Exception) {
        Currency.getInstance("EUR")
    }
    val formatter = java.text.NumberFormat.getCurrencyInstance(locale).apply {
        this.currency = currency
    }
    val formatted = formatter.format(amount)
    return if (perNightLabel != null) "$formatted$perNightLabel" else formatted
}
