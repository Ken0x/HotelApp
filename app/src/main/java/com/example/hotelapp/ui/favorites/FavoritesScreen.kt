package com.example.hotelapp.ui.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hotelapp.R
import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.ui.components.HotelAppBarTitle
import com.example.hotelapp.ui.theme.Spacing
import com.example.hotelapp.ui.util.formatPrice

@Composable
fun FavoritesScreen(
    onNavigateToDetails: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle(initialValue = emptyList())
    val currency = "EUR"
    val locale = LocalConfiguration.current.locales[0]
    val priceOnRequest = stringResource(R.string.price_on_request)
    val perNightSuffix = stringResource(R.string.per_night_suffix)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { HotelAppBarTitle() })
        }
    ) { paddingValues ->
        if (favorites.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(Spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.favorites_empty_title),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(R.string.favorites_placeholder),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(Spacing.default),
                verticalArrangement = Arrangement.spacedBy(Spacing.default)
            ) {
                items(
                    items = favorites,
                    key = { it.id }
                ) { hotel ->
                    FavoriteHotelItem(
                        hotel = hotel,
                        currency = currency,
                        locale = locale,
                        priceOnRequest = priceOnRequest,
                        perNightSuffix = perNightSuffix,
                        onRemove = { viewModel.removeFromFavorites(hotel.id) },
                        onClick = { onNavigateToDetails(hotel.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteHotelItem(
    hotel: Hotel,
    currency: String,
    locale: java.util.Locale,
    priceOnRequest: String,
    perNightSuffix: String,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.default),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
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
                    text = formatPrice(hotel.pricePerNight, currency, locale, priceOnRequest, perNightSuffix),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.favorites_remove)
                )
            }
        }
    }
}
