package com.example.hotelapp.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.foundation.Image
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberDatePickerState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hotelapp.R
import com.example.hotelapp.ui.components.HotelAppBarTitle
import com.example.hotelapp.ui.theme.Spacing
import com.example.hotelapp.ui.theme.hotelAppOutlinedTextFieldColors
import com.example.hotelapp.util.CurrencyRates
import com.example.hotelapp.util.getSavedLocale
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val LANGUAGE_OPTIONS = listOf(
    Triple("en", "EN", R.drawable.ic_flag_en),
    Triple("bs", "BS", R.drawable.ic_flag_bs),
    Triple("de", "DE", R.drawable.ic_flag_de)
)

private val CURRENCY_OPTIONS: List<String> = CurrencyRates.supportedCurrencies()

@Composable
fun HomeScreen(
    onSearch: (city: String, checkInDay: Long?, checkOutDay: Long?) -> Unit,
    modifier: Modifier = Modifier,
    onLanguageChange: (languageTag: String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    var city by remember { mutableStateOf("") }
    val todayStartMillis = remember {
        LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    var dateFrom by remember { mutableStateOf<Long?>(null) }
    var dateTo by remember { mutableStateOf<Long?>(null) }
    var showDateFromPicker by remember { mutableStateOf(false) }
    var showDateToPicker by remember { mutableStateOf(false) }
    var didRestore by remember { mutableStateOf(false) }

    LaunchedEffect(userPreferences) {
        val prefs = userPreferences ?: return@LaunchedEffect
        if (!didRestore) {
            city = prefs.city
            dateFrom = prefs.dateFromMillis.takeIf { it >= 0 }
            dateTo = prefs.dateToMillis.takeIf { it >= 0 }
            didRestore = true
        }
    }

    fun formatDate(millis: Long?): String = millis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            .format(DateTimeFormatter.ofPattern("d.M.yyyy."))
    } ?: ""

    if (showDateFromPicker) {
        HomeDatePickerDialog(
            initialMillis = dateFrom ?: todayStartMillis,
            minDateMillis = todayStartMillis,
            onDateSelected = { millis ->
                dateFrom = millis
                val currentDateTo = dateTo
                if (currentDateTo != null && currentDateTo < millis) dateTo = millis
                showDateFromPicker = false
            },
            onDismiss = { showDateFromPicker = false }
        )
    }
    if (showDateToPicker) {
        HomeDatePickerDialog(
            initialMillis = dateTo ?: dateFrom ?: todayStartMillis,
            minDateMillis = dateFrom ?: todayStartMillis,
            onDateSelected = { dateTo = it; showDateToPicker = false },
            onDismiss = { showDateToPicker = false }
        )
    }

    val welcomeTitle = stringResource(R.string.welcome_title)
    val welcomeSubtitle = stringResource(R.string.welcome_subtitle)
    val cityLabel = stringResource(R.string.city)
    val cityPlaceholder = stringResource(R.string.city_placeholder)
    val dateFromLabel = stringResource(R.string.date_from)
    val dateFromPlaceholder = stringResource(R.string.date_from_placeholder)
    val dateToLabel = stringResource(R.string.date_to)
    val dateToPlaceholder = stringResource(R.string.date_to_placeholder)
    val searchHotels = stringResource(R.string.search_hotels)
    val languageLabel = stringResource(R.string.language)
    val currencyLabel = stringResource(R.string.currency)
    val selectDateFromDesc = stringResource(R.string.content_desc_select_date_from)
    val selectDateToDesc = stringResource(R.string.content_desc_select_date_to)
    val context = LocalContext.current
    val currentLang = getSavedLocale(context).ifBlank {
        LocalConfiguration.current.locales[0].language
    }
    val currentCurrency = userPreferences?.currency?.takeIf { it.isNotBlank() } ?: "EUR"
    val textFieldColors = hotelAppOutlinedTextFieldColors()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { HotelAppBarTitle() })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val languageExpanded = remember { mutableStateOf(false) }
            val currencyExpanded = remember { mutableStateOf(false) }
            val currentOption = LANGUAGE_OPTIONS.find { it.first == currentLang } ?: LANGUAGE_OPTIONS.first()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                @OptIn(ExperimentalMaterial3Api::class)
                ExposedDropdownMenuBox(
                    expanded = languageExpanded.value,
                    onExpandedChange = { languageExpanded.value = it },
                    modifier = Modifier.widthIn(min = 80.dp, max = 140.dp)
                ) {
                    OutlinedTextField(
                        value = currentOption.second,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(languageLabel) },
                        colors = textFieldColors,
                        leadingIcon = {
                            Image(
                                painter = painterResource(currentOption.third),
                                contentDescription = stringResource(R.string.content_desc_language, currentOption.second),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded.value) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    DropdownMenu(
                        expanded = languageExpanded.value,
                        onDismissRequest = { languageExpanded.value = false },
                        modifier = Modifier.widthIn(min = 120.dp, max = 160.dp)
                    ) {
                        LANGUAGE_OPTIONS.forEach { (code, label, iconResId) ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(iconResId),
                                            contentDescription = label,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(label)
                                    }
                                },
                                onClick = {
                                    if (currentLang != code) onLanguageChange(code)
                                    languageExpanded.value = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(12.dp))
                @OptIn(ExperimentalMaterial3Api::class)
                ExposedDropdownMenuBox(
                    expanded = currencyExpanded.value,
                    onExpandedChange = { currencyExpanded.value = it },
                    modifier = Modifier.widthIn(min = 88.dp, max = 140.dp)
                ) {
                    OutlinedTextField(
                        value = currentCurrency,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(currencyLabel) },
                        colors = textFieldColors,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded.value) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    DropdownMenu(
                        expanded = currencyExpanded.value,
                        onDismissRequest = { currencyExpanded.value = false },
                        modifier = Modifier.widthIn(min = 100.dp, max = 140.dp)
                    ) {
                        CURRENCY_OPTIONS.forEach { code ->
                            DropdownMenuItem(
                                text = { Text(code) },
                                onClick = {
                                    if (currentCurrency != code) viewModel.setCurrency(code)
                                    currencyExpanded.value = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = welcomeTitle,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = welcomeSubtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text(cityLabel) },
                placeholder = { Text(cityPlaceholder) },
                colors = textFieldColors,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = formatDate(dateFrom),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(dateFromLabel) },
                        placeholder = { Text(dateFromPlaceholder) },
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
                            ) { showDateFromPicker = true }
                            .semantics { contentDescription = selectDateFromDesc }
                    )
                }
                if (dateFrom != null) {
                    IconButton(
                        onClick = {
                            dateFrom = null
                            dateTo = null
                        }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear_date),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = formatDate(dateTo),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(dateToLabel) },
                        placeholder = { Text(dateToPlaceholder) },
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
                            ) { showDateToPicker = true }
                            .semantics { contentDescription = selectDateToDesc }
                    )
                }
                if (dateTo != null) {
                    IconButton(
                        onClick = { dateTo = null }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear_date),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (city.isNotBlank()) {
                        val fromMillis = dateFrom ?: -1L
                        val toMillis = dateTo ?: -1L
                        viewModel.saveSearchParams(city.trim(), fromMillis, toMillis)
                        val checkIn = dateFrom?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
                        }
                        val checkOut = dateTo?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
                        }
                        onSearch(city.trim(), checkIn, checkOut)
                    }
                },
                enabled = city.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(searchHotels)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeDatePickerDialog(
    initialMillis: Long?,
    minDateMillis: Long? = null,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val ok = stringResource(R.string.ok)
    val cancel = stringResource(R.string.cancel)
    val minMillis = minDateMillis
    val datePickerState = rememberDatePickerState(
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
                    datePickerState.selectedDateMillis?.let { millis ->
                        val clamped = minDateMillis?.let { maxOf(millis, it) } ?: millis
                        onDateSelected(clamped)
                    }
                }
            ) {
                Text(ok)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(cancel)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
