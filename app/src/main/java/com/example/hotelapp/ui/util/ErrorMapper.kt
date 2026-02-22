package com.example.hotelapp.ui.util

import android.content.Context
import com.example.hotelapp.R
import com.example.hotelapp.domain.failure.AppFailure
import com.example.hotelapp.domain.failure.NetworkError
import com.example.hotelapp.domain.failure.UnknownError
import java.io.IOException

fun Throwable.toAppFailure(context: Context): AppFailure = when (this) {
    is IOException -> NetworkError(context.getString(R.string.error_offline_no_data))
    else -> UnknownError(message ?: context.getString(R.string.error_loading))
}
