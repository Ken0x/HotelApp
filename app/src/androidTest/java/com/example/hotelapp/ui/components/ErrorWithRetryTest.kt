package com.example.hotelapp.ui.components

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hotelapp.MainActivity
import com.example.hotelapp.ui.theme.HotelAppTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorWithRetryTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun errorWithRetry_showsMessageAndRetryButton() {
        var retryClicked = false
        composeTestRule.setContent {
            HotelAppTheme {
                ErrorWithRetry(
                    message = "Something went wrong",
                    onRetry = { retryClicked = true },
                    buttonText = "Retry"
                )
            }
        }
        composeTestRule.onNodeWithText("Something went wrong").assertExists()
        composeTestRule.onNodeWithText("Retry").assertExists().performClick()
        assertTrue(retryClicked)
    }
}
