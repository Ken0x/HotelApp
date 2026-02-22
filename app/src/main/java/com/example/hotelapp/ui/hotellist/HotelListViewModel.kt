package com.example.hotelapp.ui.hotellist

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.hotelapp.R
import com.example.hotelapp.analytics.AnalyticsTracker
import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.ui.UiState
import com.example.hotelapp.data.preferences.UserPreferencesRepository
import com.example.hotelapp.domain.usecase.GetHotelsPagedUseCase
import com.example.hotelapp.domain.usecase.RefreshHotelsUseCase
import com.example.hotelapp.work.SyncScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HotelListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getHotelsPagedUseCase: GetHotelsPagedUseCase,
    private val refreshHotelsUseCase: RefreshHotelsUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val syncScheduler: SyncScheduler,
    private val analyticsTracker: AnalyticsTracker,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(HotelListState())
    val state: StateFlow<HotelListState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HotelListUiEffect>(replay = 0, extraBufferCapacity = 1)
    val effects: Flow<HotelListUiEffect> = _effects.asSharedFlow()

    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)

    val currency: StateFlow<String> = userPreferencesRepository.userPreferences
        .map { it.currency.ifBlank { "EUR" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "EUR")

    val searchCity: String = savedStateHandle.get<String>("city").orEmpty()
    val searchCheckInDay: Long = savedStateHandle.get<Long>("checkInDay") ?: -1L
    val searchCheckOutDay: Long = savedStateHandle.get<Long>("checkOutDay") ?: -1L

    val hotelsPaged: Flow<PagingData<Hotel>> = state.flatMapLatest { s ->
        getHotelsPagedUseCase(
            searchCity,
            searchCheckInDay,
            searchCheckOutDay,
            s.minPrice,
            s.maxPrice,
            _refreshTrigger
        )
    }

    init {
        val city = savedStateHandle.get<String>("city")
        if (!city.isNullOrBlank()) {
            syncOnStart(city)
            viewModelScope.launch(Dispatchers.Default) {
                syncScheduler.scheduleSync(city)
            }
        } else {
            _state.update {
                it.copy(uiState = UiState.Error(context.getString(R.string.error_no_city)))
            }
        }
    }

    fun onEvent(event: HotelListUiEvent) {
        when (event) {
            is HotelListUiEvent.SortByPriceAscChange ->
                _state.update { reduce(it, event) }
            is HotelListUiEvent.MinPriceChange ->
                _state.update { reduce(it, event) }
            is HotelListUiEvent.MaxPriceChange ->
                _state.update { reduce(it, event) }
            is HotelListUiEvent.AddToCompare -> {
                val current = _state.value.selectedForCompare
                if (event.hotelId !in current && current.size < 3) {
                    analyticsTracker.trackFavorite(event.hotelId, hotelName = null)
                    _state.update { reduce(it, event) }
                }
            }
            is HotelListUiEvent.RemoveFromCompare ->
                _state.update { reduce(it, event) }
            HotelListUiEvent.DismissSnackbar -> { }
            HotelListUiEvent.Refresh, HotelListUiEvent.Retry ->
                performRefresh()
        }
    }

    private fun reduce(state: HotelListState, event: HotelListUiEvent): HotelListState =
        when (event) {
            is HotelListUiEvent.SortByPriceAscChange ->
                state.copy(sortByPriceAsc = event.ascending)
            is HotelListUiEvent.MinPriceChange ->
                state.copy(minPrice = event.price)
            is HotelListUiEvent.MaxPriceChange ->
                state.copy(maxPrice = event.price)
            is HotelListUiEvent.AddToCompare -> {
                val list = state.selectedForCompare
                if (event.hotelId in list || list.size >= 3) state
                else state.copy(selectedForCompare = list + event.hotelId)
            }
            is HotelListUiEvent.RemoveFromCompare ->
                state.copy(selectedForCompare = state.selectedForCompare - event.hotelId)
            HotelListUiEvent.Refresh, HotelListUiEvent.Retry,
            HotelListUiEvent.DismissSnackbar -> state
        }

    private fun performRefresh() {
        if (searchCity.isBlank()) return
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            try {
                refreshHotelsUseCase(searchCity)
                _refreshTrigger.emit(Unit)
            } catch (e: IOException) {
                _effects.emit(HotelListUiEffect.ShowSnackbar(context.getString(R.string.error_offline_no_data)))
            } catch (e: Exception) {
                _effects.emit(HotelListUiEffect.ShowSnackbar(e.message ?: context.getString(R.string.error_loading)))
            } finally {
                _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun syncOnStart(city: String) {
        viewModelScope.launch {
            try {
                refreshHotelsUseCase(city)
            } catch (e: IOException) {
                _effects.emit(HotelListUiEffect.ShowSnackbar(context.getString(R.string.error_offline_no_data)))
            } catch (e: Exception) {
                _effects.emit(HotelListUiEffect.ShowSnackbar(e.message ?: context.getString(R.string.error_loading)))
            }
        }
    }
}
