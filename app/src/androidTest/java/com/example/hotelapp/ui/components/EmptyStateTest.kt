package com.example.hotelapp.ui.components

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hotelapp.MainActivity
import com.example.hotelapp.ui.theme.HotelAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmptyStateTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun emptyState_showsMessage() {
        composeTestRule.setContent {
            HotelAppTheme {
                EmptyState(message = "No hotels found")
            }
        }
        composeTestRule.onNodeWithText("No hotels found").assertExists()
    }
}
