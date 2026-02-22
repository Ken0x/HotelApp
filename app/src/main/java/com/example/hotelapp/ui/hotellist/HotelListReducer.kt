package com.example.hotelapp.ui.hotellist

object HotelListReducer {
    fun reduce(state: HotelListState, event: HotelListUiEvent): HotelListState =
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
}
