//package com.baseer.baseer.presentation.location.screen
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.baseer.baseer.presentation.location.viewmodel.LocationPickerViewModel
//import dev.icerock.moko.permissions.compose.*
//import dev.icerock.moko.geo.compose.*
//import dev.icerock.moko.permissions.DeniedAlwaysException
//import dev.icerock.moko.permissions.DeniedException
//import dev.icerock.moko.permissions.Permission
//import dev.icerock.moko.permissions.location.LOCATION
//
//@Composable
//fun LocationScreen(
//    viewModel: LocationPickerViewModel
//) {
//    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
//    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
//    var showMapPicker by remember { mutableStateOf(false) }
//
//    // Permission handling
//    val permissionsController = rememberPermissionsControllerFactory().createPermissionsController()
//    val locationTracker = rememberLocationTrackerFactory(
//        accuracy = LocationTrackerAccuracy.Best
//    ).createLocationTracker(permissionsController)
//
//    BindLocationTrackerEffect(locationTracker)
//
//    // Request permission on first launch
//    LaunchedEffect(Unit) {
//        try {
//            permissionsController.providePermission(Permission.LOCATION)
//            viewModel.getCurrentLocation()
//        } catch (e: DeniedException) {
//            // Handle denied
//        } catch (e: DeniedAlwaysException) {
//            // Handle denied always - open settings
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            Text(
//                text = "Delivery Address",
//                style = MaterialTheme.typography.headlineSmall
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            LocationBox(
//                currentLocation = currentLocation,
//                isLoading = isLoading,
//                onClick = { showMapPicker = true }
//            )
//
//            // Rest of your screen content...
//        }
//
//        // Map Picker as full-screen overlay
//        if (showMapPicker) {
//            MapPickerScreen(
//                initialLocation = currentLocation,
//                onLocationSelected = { location ->
//                    viewModel.setSelectedLocation(location)
//                    showMapPicker = false
//                },
//                onDismiss = { showMapPicker = false }
//            )
//        }
//    }
//}