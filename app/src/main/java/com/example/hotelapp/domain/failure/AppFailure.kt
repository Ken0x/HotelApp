package com.example.hotelapp.domain.failure

sealed interface AppFailure {
    val userMessage: String
}

data class NetworkError(override val userMessage: String) : AppFailure

data class NotFoundError(override val userMessage: String) : AppFailure

data class ValidationError(override val userMessage: String) : AppFailure

data class UnknownError(override val userMessage: String) : AppFailure
