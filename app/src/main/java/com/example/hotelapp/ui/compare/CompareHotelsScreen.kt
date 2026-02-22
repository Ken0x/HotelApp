package com.example.hotelapp.ui.compare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hotelapp.R
import com.example.hotelapp.ui.components.HotelAppBarTitle
import com.example.hotelapp.ui.theme.Spacing
import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.ui.util.formatPrice
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareHotelsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CompareHotelsViewModel = hiltViewModel()
) {
    val hotels by viewModel.hotels.collectAsStateWithLifecycle(initialValue = emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(initialValue = true)
    val error by viewModel.error.collectAsStateWithLifecycle(initialValue = null)
    val currency by viewModel.currency.collectAsStateWithLifecycle(initialValue = "EUR")

    val backDesc = stringResource(R.string.back)
    val priceOnRequest = stringResource(R.string.price_on_request)
    val perNightSuffix = stringResource(R.string.per_night_suffix)
    val availabilityLabel = stringResource(R.string.availability)
    val nameLabel = stringResource(R.string.hotel_name)
    val priceLabel = stringResource(R.string.compare_price)
    val locale = LocalConfiguration.current.locales[0]

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { HotelAppBarTitle() },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = backDesc)
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
                isLoading -> {
                    val loadingDesc = stringResource(R.string.content_desc_loading)
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.semantics { contentDescription = loadingDesc }
                        )
                        Text(
                            text = stringResource(R.string.loading_in_progress),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                error != null -> {
                    Text(
                        text = error!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    )
                }
                hotels.size < 2 -> {
                    Text(
                        text = stringResource(R.string.compare_select_at_least_two),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            hotels.forEach { hotel ->
                                CompareHotelCard(
                                    hotel = hotel,
                                    currency = currency,
                                    locale = locale,
                                    priceOnRequest = priceOnRequest,
                                    perNightSuffix = perNightSuffix,
                                    availabilityLabel = availabilityLabel,
                                    nameLabel = nameLabel,
                                    priceLabel = priceLabel,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompareHotelCard(
    hotel: Hotel,
    currency: String,
    locale: Locale,
    priceOnRequest: String,
    perNightSuffix: String,
    availabilityLabel: String,
    nameLabel: String,
    priceLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(Spacing.extraSmall),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.default),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = nameLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = hotel.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = hotel.city,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = priceLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatPrice(hotel.pricePerNight, currency, locale, priceOnRequest, perNightSuffix),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = availabilityLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatAvailability(hotel.availableFromDay, hotel.availableToDay),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun formatAvailability(fromDay: Long?, toDay: Long?): String {
    if (fromDay == null && toDay == null) return "—"
    val formatter = DateTimeFormatter.ofPattern("d.M.yyyy.")
    val fromStr = fromDay?.let { LocalDate.ofEpochDay(it).format(formatter) } ?: "—"
    val toStr = toDay?.let { LocalDate.ofEpochDay(it).format(formatter) } ?: "—"
    return "$fromStr – $toStr"
}
