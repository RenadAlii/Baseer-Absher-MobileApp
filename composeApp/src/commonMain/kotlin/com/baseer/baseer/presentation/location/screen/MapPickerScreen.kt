//package com.baseer.baseer.presentation.location.screen
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import org.maplibre.compose.MapLibreMap
//import org.maplibre.compose.Marker
//import org.maplibre.compose.camera.rememberCameraState
//import org.maplibre.geojson.Point
//
//@Composable
//fun MapPickerScreen(
//    initialLocation: LocationData?,
//    onLocationSelected: (LocationData) -> Unit,
//    onCurrentLocationClick: () -> Unit,
//    isLoadingCurrentLocation: Boolean = false,
//    onDismiss: () -> Unit
//) {
//    val defaultLocation = initialLocation ?: LocationData.Default
//
//    var selectedPoint by remember {
//        mutableStateOf(
//            Point.fromLngLat(defaultLocation.longitude, defaultLocation.latitude)
//        )
//    }
//
//    val cameraState = rememberCameraState {
//        setCenter(selectedPoint)
//        setZoom(14.0)
//    }
//
//    // Update camera when current location is fetched
//    LaunchedEffect(initialLocation) {
//        initialLocation?.let {
//            val newPoint = Point.fromLngLat(it.longitude, it.latitude)
//            selectedPoint = newPoint
//            cameraState.setCenter(newPoint)
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        // Map
//        MapLibreMap(
//            modifier = Modifier.fillMaxSize(),
//            cameraState = cameraState,
//            styleUri = "https://demotiles.maplibre.org/style.json",
//            onMapClick = { point ->
//                selectedPoint = point
//                true
//            }
//        ) {
//            // Marker at selected position
//            Marker(
//                point = selectedPoint
//            )
//        }
//
//        // Top Bar
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
//                .padding(horizontal = 8.dp, vertical = 12.dp)
//                .align(Alignment.TopCenter),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(onClick = onDismiss) {
//                Icon(
//                    imageVector = Icons.Default.Close,
//                    contentDescription = "Close"
//                )
//            }
//
//            Text(
//                text = "اختر الموقع",
//                style = MaterialTheme.typography.titleMedium
//            )
//
//            Spacer(modifier = Modifier.width(48.dp))
//        }
//
//        // Current Location FAB
//        FloatingActionButton(
//            onClick = onCurrentLocationClick,
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(end = 16.dp, bottom = 100.dp),
//            containerColor = MaterialTheme.colorScheme.surface
//        ) {
//            if (isLoadingCurrentLocation) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(24.dp),
//                    strokeWidth = 2.dp
//                )
//            } else {
//                Icon(
//                    imageVector = Icons.Default.MyLocation,
//                    contentDescription = "Current Location",
//                    tint = Color(0xFF2E7D32)
//                )
//            }
//        }
//
//        // Confirm Button
//        Button(
//            onClick = {
//                onLocationSelected(
//                    LocationData(
//                        latitude = selectedPoint.latitude(),
//                        longitude = selectedPoint.longitude()
//                    )
//                )
//            },
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .fillMaxWidth()
//                .padding(16.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color(0xFF2E7D32)
//            )
//        ) {
//            Icon(Icons.Default.Check, contentDescription = null)
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("تأكيد الموقع")
//        }
//    }
//}