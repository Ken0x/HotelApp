package com.example.hotelapp.ui.util

import java.util.Locale

fun formatPrice(
    pricePerNight: Double?,
    formatStr: String,
    onRequestStr: String
): String =
    if (pricePerNight != null && pricePerNight > 0)
        formatStr.format(pricePerNight)
    else
        onRequestStr

fun formatPrice(
    priceInEur: Double?,
    currencyCode: String,
    locale: Locale,
    onRequestStr: String,
    perNightSuffix: String?
): String = com.example.hotelapp.util.formatPriceWithCurrency(
    priceInEur = priceInEur,
    currencyCode = currencyCode,
    locale = locale,
    onRequestStr = onRequestStr,
    perNightLabel = perNightSuffix
)
