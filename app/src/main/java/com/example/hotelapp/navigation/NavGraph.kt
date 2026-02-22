package com.example.hotelapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.hotelapp.ui.bookings.BookingsScreen
import com.example.hotelapp.ui.bookingsuccess.BookingSuccessScreen
import com.example.hotelapp.ui.compare.CompareHotelsScreen
import com.example.hotelapp.ui.createbooking.CreateBookingScreen
import com.example.hotelapp.ui.favorites.FavoritesScreen
import com.example.hotelapp.ui.search.HomeScreen
import com.example.hotelapp.ui.hoteldetails.HotelDetailsScreen
import com.example.hotelapp.ui.hotellist.HotelListScreen
import com.example.hotelapp.ui.profile.ProfileScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class BottomNavRoute(val route: String) {
    object Search : BottomNavRoute("search")
    object Bookings : BottomNavRoute("bookings")
    object Favorites : BottomNavRoute("favorites")
    object Profile : BottomNavRoute("profile")
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object HotelList : Screen("hotel_list/{city}/{checkInDay}/{checkOutDay}") {
        fun createRoute(
            city: String,
            checkInDay: Long = -1L,
            checkOutDay: Long = -1L
        ) = "hotel_list/${URLEncoder.encode(city, StandardCharsets.UTF_8.toString())}/$checkInDay/$checkOutDay"
    }
    object HotelDetails : Screen("hotel_details/{hotelId}") {
        fun createRoute(hotelId: String) = "hotel_details/$hotelId"
    }
    object CreateBooking : Screen("create_booking/{hotelId}") {
        fun createRoute(hotelId: String) = "create_booking/$hotelId"
    }
    object BookingSuccess : Screen("booking_success/{bookingId}") {
        fun createRoute(bookingId: String) = "booking_success/${URLEncoder.encode(bookingId, StandardCharsets.UTF_8.toString())}"
    }
    object Compare : Screen("compare/{ids}") {
        fun createRoute(ids: List<String>) = "compare/${ids.joinToString(",")}"
    }
}

@Composable
fun NavGraph(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    searchNavController: NavHostController,
    paddingValues: PaddingValues = PaddingValues(),
    onLanguageChange: (languageTag: String) -> Unit = {}
) {
    val tabRoutes = listOf(
        BottomNavRoute.Search.route,
        BottomNavRoute.Bookings.route,
        BottomNavRoute.Favorites.route,
        BottomNavRoute.Profile.route
    )
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        tabRoutes.sortedBy { if (it == selectedTab) 1 else 0 }.forEach { route ->
            val isSelected = selectedTab == route
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (isSelected) 1f else 0f)
            ) {
                when (route) {
                    BottomNavRoute.Search.route -> SearchGraph(
                        navController = searchNavController,
                        paddingValues = PaddingValues(),
                        onLanguageChange = onLanguageChange,
                        onNavigateToTab = onTabSelected
                    )
                    BottomNavRoute.Bookings.route -> BookingsScreen()
                    BottomNavRoute.Favorites.route -> FavoritesScreen(
                        onNavigateToDetails = { hotelId ->
                            onTabSelected(BottomNavRoute.Search.route)
                            searchNavController.navigate(Screen.HotelDetails.createRoute(hotelId))
                        }
                    )
                    BottomNavRoute.Profile.route -> ProfileScreen(onLanguageChange = onLanguageChange)
                }
            }
        }
    }
}

@Composable
private fun SearchGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onLanguageChange: (languageTag: String) -> Unit,
    onNavigateToTab: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        builder = {
            composable(Screen.Home.route) {
                HomeScreen(
                    onSearch = { city, checkInDay, checkOutDay ->
                        navController.navigate(
                            Screen.HotelList.createRoute(
                                city,
                                checkInDay ?: -1L,
                                checkOutDay ?: -1L
                            )
                        )
                    },
                    onLanguageChange = onLanguageChange
                )
            }
            composable(
                route = Screen.HotelList.route,
                arguments = listOf(
                    navArgument("city") { type = NavType.StringType },
                    navArgument("checkInDay") { type = NavType.LongType; defaultValue = -1L },
                    navArgument("checkOutDay") { type = NavType.LongType; defaultValue = -1L }
                )
            ) {
                HotelListScreen(
                    onNavigateToDetails = { hotelId ->
                        navController.navigate(Screen.HotelDetails.createRoute(hotelId))
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCompare = { ids ->
                        navController.navigate(Screen.Compare.createRoute(ids))
                    }
                )
            }
            composable(
                route = Screen.HotelDetails.route,
                arguments = listOf(
                    navArgument("hotelId") { type = NavType.StringType }
                ),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "https://hotelapp.example.com/hotel/{hotelId}" },
                    navDeepLink { uriPattern = "hotelapp://hotel/{hotelId}" }
                )
            ) {
                HotelDetailsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onBook = { hotelId ->
                        navController.navigate(Screen.CreateBooking.createRoute(hotelId))
                    }
                )
            }
            composable(
                route = Screen.CreateBooking.route,
                arguments = listOf(
                    navArgument("hotelId") { type = NavType.StringType }
                )
            ) {
                CreateBookingScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onBookingCreated = { bookingId ->
                        navController.navigate(Screen.BookingSuccess.createRoute(bookingId))
                    }
                )
            }
            composable(
                route = Screen.BookingSuccess.route,
                arguments = listOf(
                    navArgument("bookingId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getString("bookingId").orEmpty()
                BookingSuccessScreen(
                    bookingId = bookingId,
                    onNavigateToBookings = {
                        navController.popBackStack(Screen.Home.route, inclusive = false)
                        onNavigateToTab(BottomNavRoute.Bookings.route)
                    }
                )
            }
            composable(
                route = Screen.Compare.route,
                arguments = listOf(
                    navArgument("ids") { type = NavType.StringType }
                )
            ) {
                CompareHotelsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    )
}
