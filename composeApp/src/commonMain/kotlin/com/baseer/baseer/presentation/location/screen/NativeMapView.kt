package com.baseer.baseer.presentation.location.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.baseer.baseer.domain.model.LocationData

@Composable
expect fun NativeMapView(
    modifier: Modifier = Modifier,
    selectedLocation: LocationData?,
    onMapClick: (latitude: Double, longitude: Double) -> Unit
)