package com.example.hotelapp.ui.hoteldetails

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.R
import com.example.hotelapp.analytics.AnalyticsTracker
import com.example.hotelapp.data.preferences.UserPreferencesRepository
import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.domain.usecase.GetHotelByIdUseCase
import com.example.hotelapp.ui.UiState
import com.example.hotelapp.ui.util.toAppFailure
import com.example.hotelapp.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getHotelByIdUseCase: GetHotelByIdUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val analyticsTracker: AnalyticsTracker,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun toggleFavorite() {
        val hotelId = (_uiState.value as? UiState.Success)?.data?.id ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(hotelId)
        }
    }

    private var hotelClickTracked = false

    private val _uiState = MutableStateFlow<UiState<Hotel>>(UiState.Loading)
    val uiState: StateFlow<UiState<Hotel>> = _uiState.asStateFlow()

    val currency: StateFlow<String> = userPreferencesRepository.userPreferences
        .map { it.currency.ifBlank { "EUR" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "EUR")

    init {
        val hotelId = savedStateHandle.get<String>("hotelId")
        if (!hotelId.isNullOrBlank()) {
            loadHotel(hotelId)
        } else {
            _uiState.update { UiState.Error(context.getString(R.string.error_unknown_hotel)) }
        }
    }

    private fun loadHotel(hotelId: String) {
        getHotelByIdUseCase(hotelId)
            .onEach { hotel: Hotel ->
                if (!hotelClickTracked) {
                    analyticsTracker.trackHotelClick(hotel.id, hotel.name)
                    hotelClickTracked = true
                }
                _uiState.update { UiState.Success(hotel) }
            }
            .catch { e ->
                _uiState.update {
                    UiState.Error(e.toAppFailure(context).userMessage)
                }
            }
            .launchIn(viewModelScope)
    }
}
