package com.example.hotelapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hotelapp.R

private val bottomNavItems = listOf(
    BottomNavItem(
        route = BottomNavRoute.Search.route,
        labelResId = R.string.nav_search,
        icon = Icons.Filled.Search
    ),
    BottomNavItem(
        route = BottomNavRoute.Bookings.route,
        labelResId = R.string.nav_bookings,
        icon = Icons.Filled.CalendarMonth
    ),
    BottomNavItem(
        route = BottomNavRoute.Favorites.route,
        labelResId = R.string.nav_favorites,
        icon = Icons.Filled.Favorite
    ),
    BottomNavItem(
        route = BottomNavRoute.Profile.route,
        labelResId = R.string.nav_profile,
        icon = Icons.Filled.Person
    )
)

@Composable
fun AppBottomBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    NavigationBar(
        containerColor = colorScheme.primaryContainer,
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = selectedTab == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.labelResId)
                    )
                },
                label = { Text(stringResource(item.labelResId)) },
                selected = selected,
                onClick = { onTabSelected(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = colorScheme.primary,
                    selectedIconColor = colorScheme.onPrimary,
                    selectedTextColor = colorScheme.onPrimary,
                    unselectedIconColor = colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    unselectedTextColor = colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            )
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val labelResId: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
