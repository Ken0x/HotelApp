package com.example.hotelapp.ui.hotellist

import androidx.compose.runtime.Stable
import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.ui.UiState

typealias HotelListUiState = UiState<List<Hotel>>

@Stable
data class HotelListState(
    val uiState: UiState<List<Hotel>> = UiState.Loading,
    val isRefreshing: Boolean = false,
    val sortByPriceAsc: Boolean = true,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val selectedForCompare: List<String> = emptyList()
)

sealed class HotelListUiEvent {
    data class SortByPriceAscChange(val ascending: Boolean) : HotelListUiEvent()
    data class MinPriceChange(val price: Double?) : HotelListUiEvent()
    data class MaxPriceChange(val price: Double?) : HotelListUiEvent()
    data class AddToCompare(val hotelId: String) : HotelListUiEvent()
    data class RemoveFromCompare(val hotelId: String) : HotelListUiEvent()
    data object Refresh : HotelListUiEvent()
    data object Retry : HotelListUiEvent()
    data object DismissSnackbar : HotelListUiEvent()
}

sealed class HotelListUiEffect {
    data class ShowSnackbar(val message: String) : HotelListUiEffect()
}
