package com.baseer.baseer.presentation.location.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.baseer.baseer.domain.model.LocationData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
actual fun NativeMapView(
    modifier: Modifier,
    selectedLocation: LocationData?,
    onMapClick: (latitude: Double, longitude: Double) -> Unit
) {
    val defaultLocation = LatLng(
        selectedLocation?.latitude ?: 30.0,
        selectedLocation?.longitude ?: 30.0
    )
    val defaultZoom = 14f

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, defaultZoom)
    }

    val markerState = remember { MarkerState(position = defaultLocation) }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { location ->
            val newLatLng = LatLng(location.latitude, location.longitude)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(newLatLng, defaultZoom),
                durationMs = 500
            )
            markerState.position = newLatLng
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false),
        properties = MapProperties(isMyLocationEnabled = true),

        onMapClick = { latLng ->
            onMapClick(latLng.latitude, latLng.longitude)
        },
        onMapLongClick = { latLng ->
            onMapClick(latLng.latitude, latLng.longitude)
        }
    ) {
        Marker(
            state = markerState
        )
    }
}