package com.example.hotelapp.ui.hotellist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hotelapp.R
import com.example.hotelapp.domain.model.Hotel
import com.example.hotelapp.ui.components.HotelAppBarTitle
import com.example.hotelapp.ui.UiState
import com.example.hotelapp.ui.theme.HotelAppTheme
import com.example.hotelapp.ui.theme.Spacing
import com.example.hotelapp.ui.theme.hotelAppOutlinedTextFieldColors
import com.example.hotelapp.ui.util.formatPrice
import com.example.hotelapp.ui.util.ScaleAnimatedButton

@Composable
fun HotelListScreen(
    onNavigateToDetails: (String) -> Unit,
    onNavigateBack: () -> Unit = {},
    onNavigateToCompare: (List<String>) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HotelListViewModel = hiltViewModel()
) {
    val listState by viewModel.state.collectAsStateWithLifecycle(initialValue = HotelListState())
    val currency by viewModel.currency.collectAsStateWithLifecycle(initialValue = "EUR")
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HotelListUiEffect.ShowSnackbar ->
                    snackbarHostState.showSnackbar(effect.message, withDismissAction = true)
            }
        }
    }

    val onEvent = viewModel::onEvent

    val searchCheckInDay = viewModel.searchCheckInDay
    val searchCheckOutDay = viewModel.searchCheckOutDay
    if (viewModel.searchCity.isBlank()) {
        HotelListScreenContent(
            uiState = listState.uiState,
            searchCity = viewModel.searchCity,
            searchCheckInDay = searchCheckInDay,
            searchCheckOutDay = searchCheckOutDay,
            isRefreshing = listState.isRefreshing,
            sortByPriceAsc = listState.sortByPriceAsc,
            minPrice = listState.minPrice,
            maxPrice = listState.maxPrice,
            snackbarMessage = null,
            selectedForCompare = listState.selectedForCompare,
            currency = currency,
            onEvent = onEvent,
            onNavigateToDetails = onNavigateToDetails,
            onNavigateBack = onNavigateBack,
            onNavigateToCompare = onNavigateToCompare,
            modifier = modifier,
            hotelsPaged = null,
            snackbarHostState = snackbarHostState
        )
    } else {
        val lazyPagingItems = viewModel.hotelsPaged.collectAsLazyPagingItems()
        HotelListScreenContent(
            uiState = listState.uiState,
            searchCity = viewModel.searchCity,
            searchCheckInDay = searchCheckInDay,
            searchCheckOutDay = searchCheckOutDay,
            isRefreshing = listState.isRefreshing,
            sortByPriceAsc = listState.sortByPriceAsc,
            minPrice = listState.minPrice,
            maxPrice = listState.maxPrice,
            snackbarMessage = null,
            selectedForCompare = listState.selectedForCompare,
            currency = currency,
            onEvent = onEvent,
            onNavigateToDetails = onNavigateToDetails,
            onNavigateBack = onNavigateBack,
            onNavigateToCompare = onNavigateToCompare,
            modifier = modifier,
            hotelsPaged = lazyPagingItems,
            snackbarHostState = snackbarHostState
        )
    }
}

private fun formatEpochDay(epochDay: Long): String =
    LocalDate.ofEpochDay(epochDay).format(DateTimeFormatter.ofPattern("d.M.yyyy."))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HotelListScreenContent(
    uiState: UiState<List<Hotel>>,
    searchCity: String,
    searchCheckInDay: Long = -1L,
    searchCheckOutDay: Long = -1L,
    onNavigateToDetails: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToCompare: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    sortByPriceAsc: Boolean = true,
    minPrice: Double? = null,
    maxPrice: Double? = null,
    snackbarMessage: String? = null,
    selectedForCompare: List<String> = emptyList(),
    currency: String = "EUR",
    onEvent: (HotelListUiEvent) -> Unit = {},
    hotelsPaged: LazyPagingItems<Hotel>? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val backDesc = stringResource(R.string.back)

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(message = msg, withDismissAction = true)
            onEvent(HotelListUiEvent.DismissSnackbar)
        }
    }

    val compareLabel = stringResource(R.string.compare, selectedForCompare.size)
    val compareFabDesc = stringResource(R.string.content_desc_compare_fab, selectedForCompare.size)
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
        topBar = {
            TopAppBar(
                title = { HotelAppBarTitle() },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = backDesc)
                    }
                }
            )
        },
        floatingActionButton = {
            if (hotelsPaged != null && selectedForCompare.size >= 2) {
                FloatingActionButton(
                    onClick = { onNavigateToCompare(selectedForCompare) },
                    modifier = Modifier.semantics { contentDescription = compareFabDesc },
                    content = { Text(compareLabel) }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val resultsFor = stringResource(R.string.results_for)
            val priceOnRequest = stringResource(R.string.price_on_request)
            val perNightSuffix = stringResource(R.string.per_night_suffix)
            val locale = LocalConfiguration.current.locales[0]
            val filterAndSortLabel = stringResource(R.string.filter_and_sort)
            var showFilterSheet by remember { mutableStateOf(false) }
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            if (showFilterSheet) {
                FilterSortBottomSheet(
                    sheetState = sheetState,
                    sortByPriceAsc = sortByPriceAsc,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    onSortByPriceAscChange = { onEvent(HotelListUiEvent.SortByPriceAscChange(it)) },
                    onMinPriceChange = { onEvent(HotelListUiEvent.MinPriceChange(it)) },
                    onMaxPriceChange = { onEvent(HotelListUiEvent.MaxPriceChange(it)) },
                    onDismiss = { showFilterSheet = false }
                )
            }

            if (hotelsPaged != null) {
                val loadState = hotelsPaged.loadState.refresh
                when {
                    loadState is LoadState.Loading -> {
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

                    loadState is LoadState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Spacing.large),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = loadState.error.message
                                    ?: stringResource(R.string.error_loading),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            ScaleAnimatedButton(
                                onClick = { onEvent(HotelListUiEvent.Retry) },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }

                    hotelsPaged.itemCount == 0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Spacing.large),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_hotels_found),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    else -> {
                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = { onEvent(HotelListUiEvent.Refresh) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                if (searchCity.isNotBlank()) {
                                    Text(
                                        text = resultsFor.format(searchCity),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .padding(horizontal = Spacing.default, vertical = Spacing.small)
                                            .semantics { heading() }
                                    )
                                }
                                FilterChip(
                                    selected = false,
                                    onClick = { showFilterSheet = true },
                                    label = { Text(filterAndSortLabel) },
                                    modifier = Modifier
                                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                                        .padding(horizontal = Spacing.default, vertical = Spacing.extraSmall)
                                )
                                var listVisiblePaged by remember { mutableStateOf(false) }
                                LaunchedEffect(Unit) { listVisiblePaged = true }
                                val listAlphaPaged by animateFloatAsState(
                                    if (listVisiblePaged) 1f else 0f,
                                    animationSpec = tween(400),
                                    label = "listFade"
                                )
                                LazyColumn(
                                    contentPadding = PaddingValues(Spacing.default),
                                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                                    modifier = Modifier
                                        .padding(horizontal = Spacing.default)
                                        .graphicsLayer { alpha = listAlphaPaged }
                                ) {
                                    items(
                                        count = hotelsPaged.itemCount,
                                        key = { index ->
                                            hotelsPaged[index]?.id?.let { "hotel_$it" } ?: "paged_$index"
                                        }
                                    ) { index ->
                                        hotelsPaged[index]?.let { hotel ->
                                            HotelListItem(
                                                hotel = hotel,
                                                currency = currency,
                                                locale = locale,
                                                priceOnRequest = priceOnRequest,
                                                perNightSuffix = perNightSuffix,
                                                onNavigateToDetails = onNavigateToDetails,
                                                isInCompare = hotel.id in selectedForCompare,
                                                onEvent = onEvent,
                                                addToCompareLabel = stringResource(R.string.add_to_compare)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                when (val state = uiState) {
                    is UiState.Loading -> {
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

                    is UiState.Success -> {
                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = { onEvent(HotelListUiEvent.Refresh) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                if (searchCity.isNotBlank()) {
                                    Text(
                                        text = resultsFor.format(searchCity),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .padding(horizontal = Spacing.default, vertical = Spacing.small)
                                            .semantics { heading() }
                                    )
                                }
                                if (searchCheckInDay >= 0 && searchCheckOutDay >= 0) {
                                    val datesLabelSuccess = stringResource(R.string.available_dates_filter, formatEpochDay(searchCheckInDay), formatEpochDay(searchCheckOutDay))
                                    Text(
                                        text = datesLabelSuccess,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = Spacing.default, vertical = Spacing.extraSmall)
                                    )
                                }
                                FilterChip(
                                    selected = false,
                                    onClick = { showFilterSheet = true },
                                    label = { Text(filterAndSortLabel) },
                                    modifier = Modifier
                                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                                        .padding(horizontal = Spacing.default, vertical = Spacing.extraSmall)
                                )
                                var listVisibleSuccess by remember { mutableStateOf(false) }
                                LaunchedEffect(Unit) { listVisibleSuccess = true }
                                val listAlphaSuccess by animateFloatAsState(
                                    if (listVisibleSuccess) 1f else 0f,
                                    animationSpec = tween(400),
                                    label = "listFade"
                                )
                                LazyColumn(
                                    contentPadding = PaddingValues(Spacing.default),
                                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                                    modifier = Modifier
                                        .padding(horizontal = Spacing.default)
                                        .graphicsLayer { alpha = listAlphaSuccess }
                                ) {
                                    items(
                                        items = state.data,
                                        key = { it.id }
                                    ) { hotel ->
                                        HotelListItem(
                                            hotel = hotel,
                                            currency = currency,
                                            locale = locale,
                                            priceOnRequest = priceOnRequest,
                                            perNightSuffix = perNightSuffix,
                                            onNavigateToDetails = onNavigateToDetails,
                                            onEvent = null,
                                            addToCompareLabel = stringResource(R.string.add_to_compare)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is UiState.Empty -> {
                        val noHotelsFound = stringResource(R.string.no_hotels_found)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Spacing.large),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = noHotelsFound,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    is UiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Spacing.large),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            ScaleAnimatedButton(
                                onClick = { onEvent(HotelListUiEvent.Retry) },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelListScreenPreviewLoading() {
    HotelAppTheme {
        HotelListScreenContent(
            uiState = UiState.Loading,
            searchCity = "Barcelona",
            onNavigateToDetails = {},
            onNavigateBack = {},
            onNavigateToCompare = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSortBottomSheet(
    sheetState: SheetState,
    sortByPriceAsc: Boolean,
    minPrice: Double? = null,
    maxPrice: Double? = null,
    onSortByPriceAscChange: (Boolean) -> Unit,
    onMinPriceChange: (Double?) -> Unit = {},
    onMaxPriceChange: (Double?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetTitle = stringResource(R.string.filter_sort_sheet_title)
    val sortAscLabel = stringResource(R.string.sort_price_asc)
    val sortDescLabel = stringResource(R.string.sort_price_desc)
    val minPriceLabel = stringResource(R.string.min_price_filter)
    val minPriceHint = stringResource(R.string.min_price_filter_hint)
    val maxPriceLabel = stringResource(R.string.max_price_filter)
    val maxPriceHint = stringResource(R.string.max_price_filter_hint)
    var minPriceInput by remember(minPrice) { mutableStateOf(minPrice?.toString() ?: "") }
    var maxPriceInput by remember(maxPrice) { mutableStateOf(maxPrice?.toString() ?: "") }
    val textFieldColors = hotelAppOutlinedTextFieldColors()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        var sheetContentVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { sheetContentVisible = true }
        AnimatedVisibility(
            visible = sheetContentVisible,
            enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.large)
                    .padding(bottom = Spacing.extraLarge),
                verticalArrangement = Arrangement.spacedBy(Spacing.default)
            ) {
                Text(
                    text = sheetTitle,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = sortByPriceAsc,
                    onClick = { onSortByPriceAscChange(true) },
                    label = { Text(sortAscLabel) },
                    modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = stringResource(R.string.content_desc_sort_asc),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                FilterChip(
                    selected = !sortByPriceAsc,
                    onClick = { onSortByPriceAscChange(false) },
                    label = { Text(sortDescLabel) },
                    modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.content_desc_sort_desc),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
            OutlinedTextField(
                value = minPriceInput,
                onValueChange = {
                    minPriceInput = it
                    onMinPriceChange(it.toDoubleOrNull())
                },
                label = { Text(minPriceLabel) },
                placeholder = { Text(minPriceHint) },
                colors = textFieldColors,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = maxPriceInput,
                onValueChange = {
                    maxPriceInput = it
                    onMaxPriceChange(it.toDoubleOrNull())
                },
                label = { Text(maxPriceLabel) },
                placeholder = { Text(maxPriceHint) },
                colors = textFieldColors,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelListScreenPreviewSuccess() {
    HotelAppTheme {
        HotelListScreenContent(
            uiState = UiState.Success(
                listOf(
                    Hotel("1", "Hotel One", "Barcelona", 120.0),
                    Hotel("2", "Hotel Two", "Barcelona", null)
                )
            ),
            searchCity = "Barcelona",
            onNavigateToDetails = {},
            onNavigateBack = {},
            onNavigateToCompare = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelListScreenPreviewEmpty() {
    HotelAppTheme {
        HotelListScreenContent(
            uiState = UiState.Empty,
            searchCity = "Barcelona",
            onNavigateToDetails = {},
            onNavigateBack = {},
            onNavigateToCompare = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelListScreenPreviewOfflineNoData() {
    HotelAppTheme {
        HotelListScreenContent(
            uiState = UiState.Error("You're offline and there's no cached data."),
            searchCity = "Barcelona",
            onNavigateToDetails = {},
            onNavigateBack = {},
            onNavigateToCompare = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelListScreenPreviewError() {
    HotelAppTheme {
        HotelListScreenContent(
            uiState = UiState.Error("Something went wrong."),
            searchCity = "Barcelona",
            onNavigateToDetails = {},
            onNavigateBack = {},
            onNavigateToCompare = {}
        )
    }
}

@Composable
private fun HotelListItem(
    hotel: Hotel,
    currency: String,
    locale: Locale,
    priceOnRequest: String,
    perNightSuffix: String,
    onNavigateToDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    isInCompare: Boolean = false,
    onEvent: ((HotelListUiEvent) -> Unit)? = null,
    addToCompareLabel: String = ""
) {
    val priceText = formatPrice(hotel.pricePerNight, currency, locale, priceOnRequest, perNightSuffix)
    val cardDesc = stringResource(R.string.content_desc_hotel_card, hotel.name, hotel.city, priceText)
    val compareButtonDesc = stringResource(
        if (isInCompare) R.string.content_desc_remove_from_compare else R.string.content_desc_add_to_compare,
        hotel.name
    )
    val onClick = remember(hotel.id) { { onNavigateToDetails(hotel.id) } }
    val onCompareClick = onEvent?.let { event ->
        remember(hotel.id, isInCompare) {
            {
                if (isInCompare) event(HotelListUiEvent.RemoveFromCompare(hotel.id))
                else event(HotelListUiEvent.AddToCompare(hotel.id))
            }
        }
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                .clickable(onClick = onClick)
                .semantics(mergeDescendants = true) { contentDescription = cardDesc },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(Spacing.default),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (onCompareClick != null) {
                IconButton(
                    onClick = onCompareClick,
                    modifier = Modifier
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .padding(Spacing.small)
                ) {
                    Icon(
                        imageVector = if (isInCompare) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = compareButtonDesc,
                        tint = if (isInCompare) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
