package com.baseer.baseer.presentation.location.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LocationBox(
    currentLocation: LocationData?,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Icon(
//
//            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Current Location",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isLoading) {
                    Text(text = "Getting location...")
                } else if (currentLocation != null) {
                    Text(
                        text = currentLocation.address
                            ?: "${currentLocation.latitude}, ${currentLocation.longitude}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    Text(
                        text = "Tap to select location",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
//
//            Icon(
//                imageVector = Icons.Default.ChevronRight,
//                contentDescription = "Select"
//            )
        }
    }
}