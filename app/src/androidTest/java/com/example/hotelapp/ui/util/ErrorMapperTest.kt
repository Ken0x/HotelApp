package com.example.hotelapp.ui.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.hotelapp.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ErrorMapperTest {

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun ioException_mapsToNetworkErrorWithOfflineMessage() {
        val failure = IOException().toAppFailure(context)
        val expectedMessage = context.getString(R.string.error_offline_no_data)
        assertEquals(expectedMessage, failure.userMessage)
    }

    @Test
    fun genericException_mapsToUnknownErrorWithFallbackMessage() {
        val failure = RuntimeException().toAppFailure(context)
        val expectedMessage = context.getString(R.string.error_loading)
        assertEquals(expectedMessage, failure.userMessage)
    }

    @Test
    fun exceptionWithMessage_mapsToUnknownErrorWithThatMessage() {
        val failure = RuntimeException("Custom error").toAppFailure(context)
        assertEquals("Custom error", failure.userMessage)
    }
}
