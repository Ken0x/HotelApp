package com.example.hotelapp.ui.hoteldetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hotelapp.R
import com.example.hotelapp.ui.components.ErrorWithRetry
import com.example.hotelapp.ui.components.HotelAppBarTitle
import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.ui.UiState
import com.example.hotelapp.ui.theme.HotelAppTheme
import com.example.hotelapp.ui.theme.Spacing
import com.example.hotelapp.ui.util.ScaleAnimatedButton
import com.example.hotelapp.ui.util.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(
    onNavigateBack: () -> Unit,
    onBook: (hotelId: String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HotelDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(initialValue = UiState.Loading)
    val currency by viewModel.currency.collectAsStateWithLifecycle(initialValue = "EUR")
    HotelDetailsScreenContent(
        uiState = uiState,
        currency = currency,
        onNavigateBack = onNavigateBack,
        onBook = onBook,
        onToggleFavorite = { viewModel.toggleFavorite() },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HotelDetailsScreenContent(
    uiState: UiState<Hotel>,
    currency: String = "EUR",
    onNavigateBack: () -> Unit,
    onBook: (hotelId: String) -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val backDesc = stringResource(R.string.back)
    val reserveLabel = stringResource(R.string.reserve)
    val priceOnRequest = stringResource(R.string.price_on_request)
    val perNightSuffix = stringResource(R.string.per_night_suffix)
    val locale = LocalConfiguration.current.locales[0]
    val backButton = stringResource(R.string.back)
    val loadingDesc = stringResource(R.string.content_desc_loading)
    val descriptionLabel = stringResource(R.string.hotel_description)
    val descriptionPlaceholder = stringResource(R.string.hotel_description_placeholder)
    val addToFavoritesDesc = stringResource(R.string.favorites_add)
    val removeFromFavoritesDesc = stringResource(R.string.favorites_remove)
    val isFavorite = (uiState as? UiState.Success)?.data?.isFavorite == true

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { HotelAppBarTitle() },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = backDesc)
                    }
                },
                actions = {
                    if (uiState is UiState.Success) {
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isFavorite) removeFromFavoritesDesc else addToFavoritesDesc
                            )
                        }
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
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .semantics { contentDescription = loadingDesc }
                    )
                }
                is UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Spacing.default)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = state.data.name,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = state.data.city,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatPrice(state.data.pricePerNight, currency, locale, priceOnRequest, perNightSuffix),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = descriptionLabel,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = state.data.description?.ifBlank { null } ?: descriptionPlaceholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.padding(vertical = 8.dp))
                        ScaleAnimatedButton(
                            onClick = { onBook(state.data.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(reserveLabel)
                        }
                    }
                }
                is UiState.Error -> {
                    ErrorWithRetry(
                        message = state.message,
                        onRetry = onNavigateBack,
                        modifier = Modifier.align(Alignment.Center),
                        buttonText = backButton
                    )
                }
                is UiState.Empty -> {
                    ErrorWithRetry(
                        message = stringResource(R.string.error_unknown_hotel),
                        onRetry = onNavigateBack,
                        modifier = Modifier.align(Alignment.Center),
                        buttonText = backButton
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelDetailsScreenPreviewLoading() {
    HotelAppTheme {
        HotelDetailsScreenContent(
            uiState = UiState.Loading,
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelDetailsScreenPreviewSuccess() {
    HotelAppTheme {
        HotelDetailsScreenContent(
            uiState = UiState.Success(
                Hotel("1", "Grand Plaza", "Barcelona", 150.0, description = "A luxury hotel in the heart of the city.")
            ),
            onNavigateBack = {},
            onBook = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelDetailsScreenPreviewError() {
    HotelAppTheme {
        HotelDetailsScreenContent(
            uiState = UiState.Error("Error loading hotel."),
            onNavigateBack = {}
        )
    }
}
