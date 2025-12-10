package com.baseer.baseer.presentation.reportSelect.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import baseer.composeapp.generated.resources.*
import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.presentation.components.PrimaryButton
import com.baseer.baseer.presentation.components.TopMainAppBar
import com.baseer.baseer.presentation.components.textField.LocationBox
import com.baseer.baseer.presentation.navigation.AppScreens
import com.baseer.baseer.presentation.reportSelect.componenets.ReportTypeItem
import com.baseer.baseer.presentation.reportSelect.viewmodel.ReportSelectionViewModel
import com.baseer.baseer.presentation.utils.OnAppResumed
import dev.icerock.moko.geo.compose.BindLocationTrackerEffect
import dev.icerock.moko.geo.compose.LocationTrackerAccuracy
import dev.icerock.moko.geo.compose.rememberLocationTrackerFactory
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
            val selectedLocation = state.location
            if (selectedLocation != null) {
                navController.navigate(
                    AppScreens.ReportDetails(
                        reportTypeId = reportTypeId,
                        initialLatitude = selectedLocation.latitude,
                        initialLongitude = selectedLocation.longitude,
                        initialAddress = selectedLocation.address
                    )
                )
            } else {
                viewModel.onEvent(ReportSelectionEvent.OnShowSnackbarError(Res.string.location_permission_settings_message))
            }
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

            item {
                LocationBox(
                    uiState = state.locationBoxUiState,
                    onEditClick = { location ->
                        onNavigateToLocationPicker(location)
                    },
                    onRetryClick = { onEvent(ReportSelectionEvent.LoadLocation) }
                )
            }

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