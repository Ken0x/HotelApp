package com.example.hotelapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

                    LaunchedEffect(Unit) {
                        (context as? ComponentActivity)?.intent?.let { intent ->
                            searchNavController.handleDeepLink(intent)
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
