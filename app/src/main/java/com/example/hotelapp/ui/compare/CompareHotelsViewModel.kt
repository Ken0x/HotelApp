package com.example.hotelapp.ui.compare

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.data.preferences.UserPreferencesRepository
import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.domain.usecase.GetHotelsByIdsUseCase
import com.example.hotelapp.ui.util.toAppFailure
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
import javax.inject.Inject

@HiltViewModel
class CompareHotelsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    userPreferencesRepository: UserPreferencesRepository,
    getHotelsByIdsUseCase: GetHotelsByIdsUseCase
) : ViewModel() {

    val currency: StateFlow<String> = userPreferencesRepository.userPreferences
        .map { it.currency.ifBlank { "EUR" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "EUR")

    private val ids: List<String> = savedStateHandle.get<String>("ids")
        ?.split(",")
        ?.map { it.trim() }
        ?.filter { it.isNotBlank() }
        ?.take(3)
        ?: emptyList()

    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        if (ids.size < 2) {
            _isLoading.value = false
            _hotels.value = emptyList()
        } else {
            getHotelsByIdsUseCase(ids)
                .onEach { list ->
                    _hotels.value = list
                    _isLoading.value = false
                }
                .catch { e ->
                    _error.value = e.toAppFailure(context).userMessage
                    _isLoading.value = false
                }
                .launchIn(viewModelScope)
        }
    }
}
