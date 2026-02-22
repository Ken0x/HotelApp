package com.example.hotelapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hotelapp.ui.theme.Spacing
import com.example.hotelapp.ui.util.ScaleAnimatedButton

@Composable
fun ErrorWithRetry(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    buttonText: String? = null
) {
    Column(
        modifier = modifier.padding(Spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        if (onRetry != null && buttonText != null) {
            ScaleAnimatedButton(
                onClick = onRetry,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(buttonText)
            }
        }
    }
}
