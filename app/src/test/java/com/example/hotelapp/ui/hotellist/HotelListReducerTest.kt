package com.example.hotelapp.ui.hotellist

import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.ui.UiState
import org.junit.Assert.assertEquals
import org.junit.Test

class HotelListReducerTest {

    private fun defaultState() = HotelListState(
        uiState = UiState.Success(emptyList()),
        isRefreshing = false,
        sortByPriceAsc = true,
        minPrice = null,
        maxPrice = null,
        selectedForCompare = emptyList()
    )

    @Test
    fun reduce_SortByPriceAscChange_updatesSortOrder() {
        val state = defaultState()
        val result = HotelListReducer.reduce(state, HotelListUiEvent.SortByPriceAscChange(false))
        assertEquals(false, result.sortByPriceAsc)
        val resultAsc = HotelListReducer.reduce(result, HotelListUiEvent.SortByPriceAscChange(true))
        assertEquals(true, resultAsc.sortByPriceAsc)
    }

    @Test
    fun reduce_MinPriceChange_updatesMinPrice() {
        val state = defaultState()
        val result = HotelListReducer.reduce(state, HotelListUiEvent.MinPriceChange(50.0))
        assertEquals(50.0, result.minPrice)
        val resultNull = HotelListReducer.reduce(result, HotelListUiEvent.MinPriceChange(null))
        assertEquals(null, resultNull.minPrice)
    }

    @Test
    fun reduce_MaxPriceChange_updatesMaxPrice() {
        val state = defaultState()
        val result = HotelListReducer.reduce(state, HotelListUiEvent.MaxPriceChange(200.0))
        assertEquals(200.0, result.maxPrice)
    }

    @Test
    fun reduce_AddToCompare_addsHotelIdWhenUnderLimit() {
        val state = defaultState()
        val result = HotelListReducer.reduce(state, HotelListUiEvent.AddToCompare("hotel-1"))
        assertEquals(listOf("hotel-1"), result.selectedForCompare)
        val result2 = HotelListReducer.reduce(result, HotelListUiEvent.AddToCompare("hotel-2"))
        assertEquals(listOf("hotel-1", "hotel-2"), result2.selectedForCompare)
    }

    @Test
    fun reduce_AddToCompare_ignoresWhenAlreadyInList() {
        val state = defaultState().copy(selectedForCompare = listOf("hotel-1"))
        val result = HotelListReducer.reduce(state, HotelListUiEvent.AddToCompare("hotel-1"))
        assertEquals(listOf("hotel-1"), result.selectedForCompare)
    }

    @Test
    fun reduce_AddToCompare_ignoresWhenThreeAlreadySelected() {
        val state = defaultState().copy(selectedForCompare = listOf("a", "b", "c"))
        val result = HotelListReducer.reduce(state, HotelListUiEvent.AddToCompare("d"))
        assertEquals(listOf("a", "b", "c"), result.selectedForCompare)
    }

    @Test
    fun reduce_RemoveFromCompare_removesHotelId() {
        val state = defaultState().copy(selectedForCompare = listOf("hotel-1", "hotel-2"))
        val result = HotelListReducer.reduce(state, HotelListUiEvent.RemoveFromCompare("hotel-1"))
        assertEquals(listOf("hotel-2"), result.selectedForCompare)
    }

    @Test
    fun reduce_RemoveFromCompare_unchangedWhenIdNotInList() {
        val state = defaultState().copy(selectedForCompare = listOf("hotel-1", "hotel-2"))
        val result = HotelListReducer.reduce(state, HotelListUiEvent.RemoveFromCompare("hotel-3"))
        assertEquals(listOf("hotel-1", "hotel-2"), result.selectedForCompare)
    }

    @Test
    fun reduce_Refresh_returnsSameState() {
        val state = defaultState()
        val result = HotelListReducer.reduce(state, HotelListUiEvent.Refresh)
        assertEquals(state, result)
    }

    @Test
    fun reduce_DismissSnackbar_returnsSameState() {
        val state = defaultState()
        val result = HotelListReducer.reduce(state, HotelListUiEvent.DismissSnackbar)
        assertEquals(state, result)
    }
}
