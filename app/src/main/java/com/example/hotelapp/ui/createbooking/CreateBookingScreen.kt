package com.example.hotelapp.ui.createbooking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hotelapp.R
import com.example.hotelapp.ui.components.ErrorWithRetry
import com.example.hotelapp.ui.components.HotelAppBarTitle
import com.example.hotelapp.ui.theme.hotelAppOutlinedTextFieldColors
import com.example.hotelapp.ui.util.formatPrice
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBookingScreen(
    onNavigateBack: () -> Unit,
    onBookingCreated: (bookingId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateBookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCheckInPicker by remember { mutableStateOf(false) }
    var showCheckOutPicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val textFieldColors = hotelAppOutlinedTextFieldColors()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { HotelAppBarTitle() },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.hotel == null && uiState.error == null -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                uiState.error != null && uiState.hotel == null -> {
                    ErrorWithRetry(
                        message = uiState.error!!,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    val hotel = uiState.hotel!!
                    val locale = LocalConfiguration.current.locales[0]
                    val priceOnRequest = stringResource(R.string.price_on_request)
                    val perNightSuffix = stringResource(R.string.per_night_suffix)
                    val selectDateFromDesc = stringResource(R.string.content_desc_select_date_from)
                    val selectDateToDesc = stringResource(R.string.content_desc_select_date_to)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = hotel.name,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = hotel.city,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatPrice(hotel.pricePerNight, "EUR", locale, priceOnRequest, perNightSuffix),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = formatEpochDay(uiState.checkInDay),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.date_from)) },
                                colors = textFieldColors,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { showCheckInPicker = true }
                                    .semantics { contentDescription = selectDateFromDesc }
                            )
                        }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = formatEpochDay(uiState.checkOutDay),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.date_to)) },
                                colors = textFieldColors,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { showCheckOutPicker = true }
                                    .semantics { contentDescription = selectDateToDesc }
                            )
                        }

                        if (uiState.checkInDay != null && uiState.checkOutDay != null && uiState.checkOutDay!! > uiState.checkInDay!!) {
                            Text(
                                text = stringResource(R.string.create_booking_total, uiState.totalPrice),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onNavigateBack,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                            Button(
                                onClick = {
                                    scope.launch {
                                        viewModel.createBooking(onSuccess = { id -> onBookingCreated(id) })
                                    }
                                },
                                enabled = uiState.canConfirm && !uiState.isSaving,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.height(20.dp).padding(4.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(stringResource(R.string.create_booking_confirm))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCheckInPicker) {
        val todayMillis = epochDayToMillis(LocalDate.now(ZoneId.systemDefault()).toEpochDay())
        CreateBookingDatePickerDialog(
            initialMillis = uiState.checkInDay?.let { epochDayToMillis(it) } ?: todayMillis,
            minDateMillis = todayMillis,
            onDateSelected = { millis ->
                viewModel.setCheckInDate(millis)
                showCheckInPicker = false
            },
            onDismiss = { showCheckInPicker = false }
        )
    }
    if (showCheckOutPicker) {
        val checkInMillis = uiState.checkInDay?.let { epochDayToMillis(it) }
        CreateBookingDatePickerDialog(
            initialMillis = uiState.checkOutDay?.let { epochDayToMillis(it) }
                ?: checkInMillis,
            minDateMillis = checkInMillis,
            onDateSelected = { millis ->
                viewModel.setCheckOutDate(millis)
                showCheckOutPicker = false
            },
            onDismiss = { showCheckOutPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateBookingDatePickerDialog(
    initialMillis: Long?,
    minDateMillis: Long? = null,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val minMillis = minDateMillis
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        initialDisplayedMonthMillis = initialMillis ?: minDateMillis,
        selectableDates = object : androidx.compose.material3.SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                minMillis == null || utcTimeMillis >= minMillis

            override fun isSelectableYear(year: Int): Boolean = true
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val clamped = minDateMillis?.let { maxOf(millis, it) } ?: millis
                        onDateSelected(clamped)
                    }
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    ) {
        DatePicker(state = state)
    }
}

private fun formatEpochDay(epochDay: Long?): String = epochDay?.let {
    Instant.ofEpochMilli(epochDayToMillis(it)).atZone(ZoneId.systemDefault()).toLocalDate()
        .format(DateTimeFormatter.ofPattern("d.M.yyyy."))
} ?: ""

private fun epochDayToMillis(epochDay: Long): Long =
    java.time.LocalDate.ofEpochDay(epochDay).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
