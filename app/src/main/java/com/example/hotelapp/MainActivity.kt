package com.example.hotelapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.hotelapp.navigation.AppBottomBar
import com.example.hotelapp.navigation.BottomNavRoute
import com.example.hotelapp.navigation.NavGraph
import com.example.hotelapp.ui.theme.HotelAppTheme
import com.example.hotelapp.util.contextWithLocale
import com.example.hotelapp.util.setSavedLocale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        /** Intent extra: ako je true, pri pokretanju aktivnosti otvori tab „Rezervacije”. */
        const val EXTRA_OPEN_TAB_BOOKINGS = "open_tab_bookings"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(contextWithLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HotelAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var selectedTab by remember { mutableStateOf(BottomNavRoute.Search.route) }
                    val searchNavController = rememberNavController()
                    val context = LocalContext.current
                    val notificationPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { }

                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                        ) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                    LaunchedEffect(Unit) {
                        (context as? ComponentActivity)?.intent?.let { intent ->
                            searchNavController.handleDeepLink(intent)
                            if (intent.getBooleanExtra(EXTRA_OPEN_TAB_BOOKINGS, false)) {
                                selectedTab = BottomNavRoute.Bookings.route
                            }
                        }
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            AppBottomBar(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it }
                            )
                        }
                    ) { paddingValues ->
                        NavGraph(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            searchNavController = searchNavController,
                            paddingValues = paddingValues,
                            onLanguageChange = { languageTag ->
                                setSavedLocale(this, languageTag)
                                recreate()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
