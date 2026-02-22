package com.example.hotelapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.analytics.AnalyticsTracker
import com.example.hotelapp.data.preferences.UserPreferences
import com.example.hotelapp.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    private val _userPreferences = MutableStateFlow<UserPreferences?>(null)
    /** Null dok DataStore nije učitan; nakon toga uvijek ne-null (restore nakon app restart / process death). */
    val userPreferences: StateFlow<UserPreferences?> = _userPreferences.asStateFlow()

    init {
        userPreferencesRepository.userPreferences
            .onEach { prefs -> _userPreferences.value = prefs }
            .launchIn(viewModelScope)
    }

    fun saveSearchParams(city: String, dateFromMillis: Long, dateToMillis: Long) {
        viewModelScope.launch {
            userPreferencesRepository.setSearchParams(city, dateFromMillis, dateToMillis)
            analyticsTracker.trackSearch(city)
        }
    }

    fun setCurrency(currencyCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.setCurrency(currencyCode.ifBlank { "EUR" })
        }
    }
}
