package com.example.hotelapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.BuildConfig
import com.example.hotelapp.data.preferences.UserPreferences
import com.example.hotelapp.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences?> = userPreferencesRepository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val appVersion: String = BuildConfig.VERSION_NAME ?: ""

    private val _logoutTrigger = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    val logoutTrigger: SharedFlow<Unit> = _logoutTrigger.asSharedFlow()

    fun setLanguageTag(tag: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguageTag(tag)
        }
    }

    fun setCurrency(currencyCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.setCurrency(currencyCode.ifBlank { "EUR" })
        }
    }

    fun setUsername(username: String) {
        viewModelScope.launch {
            userPreferencesRepository.setUsername(username)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutTrigger.emit(Unit)
        }
    }
}
