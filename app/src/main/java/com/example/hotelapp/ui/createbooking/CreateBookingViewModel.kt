package com.example.hotelapp.ui.createbooking

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.domain.model.Booking
import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.ui.util.toAppFailure
import com.example.hotelapp.domain.usecase.CreateBookingUseCase
import com.example.hotelapp.domain.usecase.GetHotelByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class CreateBookingUiState(
    val hotel: Hotel? = null,
    val checkInDay: Long? = null,
    val checkOutDay: Long? = null,
    val isSaving: Boolean = false,
    val error: String? = null
) {
    val totalPrice: Double
        get() {
            val hotel = hotel ?: return 0.0
            val pricePerNight = hotel.pricePerNight ?: return 0.0
            val checkIn = checkInDay ?: return 0.0
            val checkOut = checkOutDay ?: return 0.0
            val nights = (checkOut - checkIn).coerceAtLeast(0)
            return pricePerNight * nights
        }

    val canConfirm: Boolean
        get() = hotel != null && checkInDay != null && checkOutDay != null && checkOutDay > checkInDay
}

@HiltViewModel
class CreateBookingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getHotelByIdUseCase: GetHotelByIdUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBookingUiState())
    val uiState: StateFlow<CreateBookingUiState> = _uiState.asStateFlow()

    init {
        val hotelId = savedStateHandle.get<String>("hotelId")
        if (!hotelId.isNullOrBlank()) {
            loadHotel(hotelId)
        }
    }

    private fun loadHotel(hotelId: String) {
        val todayEpochDay = LocalDate.now(ZoneId.systemDefault()).toEpochDay()
        getHotelByIdUseCase(hotelId)
            .onEach { hotel ->
                _uiState.update { state ->
                    state.copy(
                        hotel = hotel,
                        checkInDay = state.checkInDay ?: todayEpochDay
                    )
                }
            }
            .catch { _ ->
                _uiState.update { it.copy(error = "Hotel not found") }
            }
            .launchIn(viewModelScope)
    }

    fun setCheckInDate(millis: Long) {
        val newCheckInDay = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
        _uiState.update { state ->
            val checkOutDay = state.checkOutDay
            val adjustedCheckOut = if (checkOutDay != null && checkOutDay < newCheckInDay) newCheckInDay else checkOutDay
            state.copy(
                checkInDay = newCheckInDay,
                checkOutDay = adjustedCheckOut
            )
        }
    }

    fun setCheckOutDate(millis: Long) {
        _uiState.update {
            it.copy(
                checkOutDay = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
            )
        }
    }

    suspend fun createBooking(onSuccess: (String) -> Unit) {
        val state = _uiState.value
        val hotel = state.hotel ?: return
        val checkIn = state.checkInDay ?: return
        val checkOut = state.checkOutDay ?: return
        if (checkOut <= checkIn) return

        _uiState.update { it.copy(isSaving = true, error = null) }
        try {
            val bookingId = createBookingUseCase(
                Booking(
                    id = "",
                    hotelId = hotel.id,
                    hotelName = hotel.name,
                    city = hotel.city,
                    checkInDay = checkIn,
                    checkOutDay = checkOut,
                    totalPrice = state.totalPrice
                )
            )
            _uiState.update { it.copy(isSaving = false) }
            onSuccess(bookingId)
        } catch (e: Exception) {
            _uiState.update {
                it.copy(isSaving = false, error = e.toAppFailure(context).userMessage)
            }
        }
    }
}
