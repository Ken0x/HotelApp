package com.example.hotelapp.ui.bookingsuccess

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotelapp.R
import com.example.hotelapp.ui.components.HotelAppBarTitle
import com.example.hotelapp.ui.theme.HotelAppTheme
import com.example.hotelapp.ui.theme.Spacing

@Composable
fun BookingSuccessScreen(
    bookingId: String,
    onNavigateToBookings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { HotelAppBarTitle() })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val successDesc = stringResource(R.string.content_desc_booking_success)
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = successDesc,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(Spacing.default))
            Text(
                text = stringResource(R.string.booking_success_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(Spacing.small))
            Text(
                text = stringResource(R.string.booking_success_id_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = bookingId,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.padding(Spacing.extraLarge))
            Button(
                onClick = onNavigateToBookings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.booking_success_view_bookings))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookingSuccessScreenPreview() {
    HotelAppTheme {
        BookingSuccessScreen(
            bookingId = "abc-123-def",
            onNavigateToBookings = {}
        )
    }
}
