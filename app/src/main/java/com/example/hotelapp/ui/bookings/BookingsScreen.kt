package com.example.hotelapp.ui.bookings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hotelapp.R
import com.example.hotelapp.domain.model.Booking
import com.example.hotelapp.ui.components.HotelAppBarTitle
import com.example.hotelapp.ui.theme.Spacing
import java.time.format.DateTimeFormatter

@Composable
fun BookingsScreen(
    modifier: Modifier = Modifier,
    viewModel: BookingsViewModel = hiltViewModel()
) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { HotelAppBarTitle() })
        }
    ) { paddingValues ->
        if (bookings.isEmpty()) {
            BookingsEmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(Spacing.default),
                verticalArrangement = Arrangement.spacedBy(Spacing.default)
            ) {
                items(
                    items = bookings,
                    key = { it.id }
                ) { booking ->
                    BookingItem(booking = booking)
                }
            }
        }
    }
}

@Composable
private fun BookingsEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(Spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.bookings_empty_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(R.string.bookings_placeholder),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BookingItem(
    booking: Booking,
    modifier: Modifier = Modifier
) {
    val checkInStr = epochDayToFormattedDate(booking.checkInDay)
    val checkOutStr = epochDayToFormattedDate(booking.checkOutDay)
    val datesStr = stringResource(R.string.bookings_dates_format, checkInStr, checkOutStr)
    val totalStr = stringResource(R.string.bookings_total, booking.totalPrice)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.default),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = booking.hotelName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = booking.city,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = datesStr,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = totalStr,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun epochDayToFormattedDate(epochDay: Long): String =
    java.time.LocalDate.ofEpochDay(epochDay)
        .format(DateTimeFormatter.ofPattern("d.M.yyyy."))
