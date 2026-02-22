package com.example.hotelapp.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hotelapp.R
import com.example.hotelapp.ui.components.HotelAppBarTitle
import com.example.hotelapp.ui.theme.Spacing
import com.example.hotelapp.ui.theme.hotelAppOutlinedTextFieldColors
import com.example.hotelapp.util.CurrencyRates
import com.example.hotelapp.util.getSavedLocale

private val LANGUAGE_OPTIONS = listOf(
    "en" to "English",
    "bs" to "Bosanski",
    "de" to "Deutsch"
)

private val CURRENCY_OPTIONS: List<String> = CurrencyRates.supportedCurrencies()

@Composable
fun ProfileScreen(
    onLanguageChange: (languageTag: String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle(initialValue = null)
    val context = LocalContext.current
    val currentLang = getSavedLocale(context).ifBlank {
        LocalConfiguration.current.locales[0].language
    }
    val currentCurrency = userPreferences?.currency?.takeIf { it.isNotBlank() } ?: "EUR"
    val username = userPreferences?.username ?: ""
    val snackbarHostState = remember { SnackbarHostState() }
    val logoutMockMessage = stringResource(R.string.logout_mock_message)
    val textFieldColors = hotelAppOutlinedTextFieldColors()

    LaunchedEffect(Unit) {
        viewModel.logoutTrigger.collect {
            snackbarHostState.showSnackbar(logoutMockMessage)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { HotelAppBarTitle() })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.large)
        ) {
            Text(
                text = stringResource(R.string.nav_profile),
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.setUsername(it) },
                label = { Text(stringResource(R.string.profile_username)) },
                placeholder = { Text(stringResource(R.string.profile_username_placeholder)) },
                colors = textFieldColors,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.content_desc_username_field)
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            var languageExpanded by remember { mutableStateOf(false) }
            val currentLanguageLabel = LANGUAGE_OPTIONS.find { it.first == currentLang }?.second ?: LANGUAGE_OPTIONS.first().second
            @OptIn(ExperimentalMaterial3Api::class)
            ExposedDropdownMenuBox(
                expanded = languageExpanded,
                onExpandedChange = { languageExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = currentLanguageLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.profile_language)) },
                    colors = textFieldColors,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = stringResource(R.string.content_desc_language_field)
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                DropdownMenu(
                    expanded = languageExpanded,
                    onDismissRequest = { languageExpanded = false },
                    modifier = Modifier.widthIn(min = 200.dp)
                ) {
                    LANGUAGE_OPTIONS.forEach { (code, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                if (currentLang != code) {
                                    viewModel.setLanguageTag(code)
                                    onLanguageChange(code)
                                }
                                languageExpanded = false
                            }
                        )
                    }
                }
            }

            var currencyExpanded by remember { mutableStateOf(false) }
            @OptIn(ExperimentalMaterial3Api::class)
            ExposedDropdownMenuBox(
                expanded = currencyExpanded,
                onExpandedChange = { currencyExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = currentCurrency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.profile_currency)) },
                    colors = textFieldColors,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Paid,
                            contentDescription = stringResource(R.string.content_desc_currency_field)
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                DropdownMenu(
                    expanded = currencyExpanded,
                    onDismissRequest = { currencyExpanded = false },
                    modifier = Modifier.widthIn(min = 120.dp)
                ) {
                    CURRENCY_OPTIONS.forEach { code ->
                        DropdownMenuItem(
                            text = { Text(code) },
                            onClick = {
                                if (currentCurrency != code) {
                                    viewModel.setCurrency(code)
                                }
                                currencyExpanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.content_desc_app_version_info),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.profile_app_version),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = viewModel.appVersion,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            OutlinedButton(
                onClick = { viewModel.logout() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.logout))
            }
        }
    }
}
