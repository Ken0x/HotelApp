package com.example.hotelapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LauncherBlue80,
    onPrimary = Color(0xFF003258),
    primaryContainer = LauncherBlueDark,
    onPrimaryContainer = LauncherBlue80,
    secondary = LauncherBlueGrey80,
    secondaryContainer = LauncherBlueDark,
    onSecondaryContainer = LauncherBlue80,
    tertiary = LauncherBlue80,
    outline = LauncherBlue80.copy(alpha = 0.8f),
    outlineVariant = LauncherBlueDark.copy(alpha = 0.6f),
    surfaceVariant = LauncherBlueDark.copy(alpha = 0.3f)
)

private val LightColorScheme = lightColorScheme(
    primary = LauncherBlue,
    onPrimary = Color.White,
    primaryContainer = LauncherBlueContainer,
    onPrimaryContainer = LauncherBlueOnContainer,
    secondary = LauncherBlueDark,
    onSecondary = Color.White,
    secondaryContainer = LauncherBlueContainer,
    onSecondaryContainer = LauncherBlueOnContainer,
    tertiary = LauncherBlueLight,
    outline = InputOutline,
    outlineVariant = InputOutlineVariant,
    surfaceVariant = InputContainerTint
)

@Composable
fun HotelAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = HotelAppShapes,
        content = content
    )
}

@Composable
fun hotelAppOutlinedTextFieldColors(): TextFieldColors {
    val colorScheme = MaterialTheme.colorScheme
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colorScheme.primary,
        unfocusedBorderColor = colorScheme.outline,
        cursorColor = colorScheme.primary,
        focusedLabelColor = colorScheme.primary,
        unfocusedLabelColor = colorScheme.onSurfaceVariant,
        focusedContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.4f),
        unfocusedContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.25f),
        focusedLeadingIconColor = colorScheme.primary,
        unfocusedLeadingIconColor = colorScheme.onSurfaceVariant
    )
}
