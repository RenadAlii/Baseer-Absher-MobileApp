package com.baseer.baseer.presentation.reportSelect.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import baseer.composeapp.generated.resources.*
import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.presentation.components.EditableInfoBox
import com.baseer.baseer.presentation.components.PrimaryButton
import com.baseer.baseer.presentation.components.TopMainAppBar
import com.baseer.baseer.presentation.navigation.AppScreens
import com.baseer.baseer.presentation.reportSelect.componenets.ReportTypeItem
import com.baseer.baseer.presentation.reportSelect.viewmodel.ReportSelectionViewModel
import com.baseer.baseer.presentation.utils.OnAppResumed
import dev.icerock.moko.geo.compose.BindLocationTrackerEffect
import dev.icerock.moko.geo.compose.LocationTrackerAccuracy
import dev.icerock.moko.geo.compose.rememberLocationTrackerFactory
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReportSelectionScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ReportSelectionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val permissionsFactory = rememberPermissionsControllerFactory()
    val permissionsController = remember(permissionsFactory) {
        permissionsFactory.createPermissionsController()
    }
    BindEffect(permissionsController)

    val locationTrackerFactory = rememberLocationTrackerFactory(LocationTrackerAccuracy.Best)
    val locationTracker = remember(locationTrackerFactory, permissionsController) {
        locationTrackerFactory.createLocationTracker(permissionsController)
    }
    BindLocationTrackerEffect(locationTracker)

    LaunchedEffect(Unit) {
        viewModel.onEvent(ReportSelectionEvent.LoadLocationAndPermissions(permissionsController, locationTracker))
    }

    OnAppResumed {
        viewModel.onEvent(ReportSelectionEvent.LoadLocationAndPermissions(permissionsController, locationTracker))
    }

    // Listen for location updates from LocationPicker
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow<LocationData?>("selected_location", null)
            ?.collect { location ->
                location?.let {
                    viewModel.updateLocation(it)
                    savedStateHandle.remove<LocationData>("selected_location")
                }
            }
    }

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(ReportSelectionEvent.OnErrorShown)
        }
    }

    ReportSelectionContent(
        state = state,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onEvent = viewModel::onEvent,
        locationPermissionState = state.locationPermissionState,
        onNavigateToLocationPicker = { location ->
            navController.navigate(
                AppScreens.LocationPicker(
                    selectedLatitude = location.latitude,
                    selectedLongitude = location.longitude,
                    selectedAddress = location.address,
                    currentLocatingLatitude = state.currentTrackingLocation?.latitude,
                    currentLocatingLongitude = state.currentTrackingLocation?.longitude,
                    currentLocatingAddress = state.currentTrackingLocation?.address
                )
            )
        },
        onNavigateToReportDetails = { reportTypeId ->
            navController.navigate(AppScreens.ReportDetails(reportTypeId))
        },
        onBackClick = { navController.popBackStack() }
    )
}

@Composable
private fun ReportSelectionContent(
    state: ReportSelectionState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onEvent: (ReportSelectionEvent) -> Unit,
    locationPermissionState: PermissionState,
    onNavigateToLocationPicker: (LocationData) -> Unit,
    onNavigateToReportDetails: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopMainAppBar(
                modifier = modifier,
                title = stringResource(Res.string.report_screen_title),
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = stringResource(Res.string.report_call_911)
            ) {
                onEvent(ReportSelectionEvent.OnCall911Click)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Location Section
            item {
                LocationSection(
                    state = state,
                    locationPermissionState = locationPermissionState,
                    onEditClick = {
                        state.location?.let { onNavigateToLocationPicker(it) }
                    },
                    onRetryClick = { onEvent(ReportSelectionEvent.LoadLocation) }
                )
            }

            // Report Type Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.report_type_header),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(
                items = state.reportTypes,
                key = { it.id }
            ) { reportType ->
                ReportTypeItem(
                    title = stringResource(reportType.titleRes),
                    icon = painterResource(reportType.icon),
                    onClick = { onNavigateToReportDetails(reportType.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun LocationSection(
    state: ReportSelectionState,
    locationPermissionState: PermissionState,
    onEditClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    when {
        state.isLoadingLocation -> {
            EditableInfoBox(
                title = stringResource(Res.string.report_location_label),
                value = stringResource(Res.string.location_loading),
                buttonText = stringResource(Res.string.report_change_location),
                icon = painterResource(Res.drawable.ic_pin_location),
                onEditClick = { }
            )
        }

        state.location != null -> {
            EditableInfoBox(
                title = stringResource(Res.string.report_location_label),
                value = state.location.displayText,
                buttonText = stringResource(Res.string.report_change_location),
                icon = painterResource(Res.drawable.ic_pin_location),
                onEditClick = onEditClick
            )
        }

        else -> {
            LocationPermissionCard(
                onRetryClick = onRetryClick,
                permissionState = locationPermissionState
            )
        }
    }
}

@Composable
private fun LocationPermissionCard(
    onRetryClick: () -> Unit,
    permissionState: PermissionState
) {
    val permissionsController = rememberPermissionsControllerFactory().createPermissionsController()

    val action: () -> Unit = when (permissionState) {
        PermissionState.DeniedAlways -> {
            { permissionsController.openAppSettings() }
        }
        else -> {
            onRetryClick
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (permissionState == PermissionState.DeniedAlways) {
                    stringResource(Res.string.permission_denied_always)
                } else {
                    stringResource(Res.string.permission_required)
                },
                fontSize = 14.sp,
                color = Color(0xFFE65100),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = action,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (permissionState == PermissionState.DeniedAlways) {
                        stringResource(Res.string.button_open_settings)
                    } else {
                        stringResource(Res.string.button_locate)
                    },
                )
            }
        }
    }
}
