package com.baseer.baseer.presentation.reportSelect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.domain.service.GeocoderService
import com.baseer.baseer.presentation.components.model.LocationBoxUiState
import com.baseer.baseer.presentation.components.topSnackbar.SnackbarController.sendSnackbarEvent
import com.baseer.baseer.presentation.components.topSnackbar.SnackbarEvent
import com.baseer.baseer.presentation.reportSelect.screen.ReportSelectionEvent
import com.baseer.baseer.presentation.reportSelect.screen.ReportSelectionState
import com.baseer.baseer.presentation.utils.PhoneDialer
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.location.LOCATION
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class ReportSelectionViewModel(
    private val geocoderService: GeocoderService,
    private val phoneDialer: PhoneDialer
) : ViewModel() {

    private val _state = MutableStateFlow(ReportSelectionState())
    val state: StateFlow<ReportSelectionState> = _state.asStateFlow()

    private var locationTracker: LocationTracker? = null
    private var permissionsController: PermissionsController? = null

    fun updateLocation(location: LocationData) {
        _state.update {
            val newState = it.copy(location = location)
            newState.copy(locationBoxUiState = calculateLocationBoxUiState(newState))
        }
    }

    fun onEvent(event: ReportSelectionEvent) {
        when (event) {
            is ReportSelectionEvent.LoadLocationAndPermissions -> handlePermissionsAndLocation(event.permissionsController, event.locationTracker)
            ReportSelectionEvent.LoadLocation -> loadLocation()
            ReportSelectionEvent.OnCall911Click -> call911()
            ReportSelectionEvent.OnErrorShown -> clearError()
            is ReportSelectionEvent.OnShowSnackbarError -> showErrorSnackbar(event.msg)
        }
    }

    private fun showErrorSnackbar(msg: StringResource) {
        viewModelScope.launch {
            sendSnackbarEvent(
                event = SnackbarEvent.Show.Error(
                    message = msg
                )
            )
        }
    }

    private fun handlePermissionsAndLocation(controller: PermissionsController, tracker: LocationTracker) {
        permissionsController = controller
        locationTracker = tracker

        viewModelScope.launch {
            val initialPermissionState = controller.getPermissionState(Permission.LOCATION)

            _state.update {
                val newState = it.copy(locationPermissionState = initialPermissionState)
                newState.copy(locationBoxUiState = calculateLocationBoxUiState(newState))
            }

            if (_state.value.location == null) {
                try {
                    controller.providePermission(Permission.LOCATION)

                    val currentState = controller.getPermissionState(Permission.LOCATION)

                    _state.update {
                        val newState = it.copy(locationPermissionState = currentState)
                        newState.copy(locationBoxUiState = calculateLocationBoxUiState(newState))
                    }

                    if (currentState == PermissionState.Granted) {
                        loadLocation()
                    }
                } catch (e: Exception) {
                    _state.update {
                        val newState = it.copy(
                            locationPermissionState = controller.getPermissionState(Permission.LOCATION)
                        )
                        newState.copy(locationBoxUiState = calculateLocationBoxUiState(newState))
                    }
                }
            }
        }
    }

    private fun loadLocation() {
        val tracker = locationTracker ?: return

        viewModelScope.launch {
            _state.update {
                val newState = it.copy(isLoadingLocation = true, error = null)
                newState.copy(locationBoxUiState = calculateLocationBoxUiState(newState))
            }

            try {
                tracker.startTracking()
                val latLng = tracker.getLocationsFlow().first()
                tracker.stopTracking()

                val address = geocoderService.getAddressFromCoordinates(
                    latitude = latLng.latitude,
                    longitude = latLng.longitude
                )
                val trackedLocation = LocationData(
                    latitude = latLng.latitude,
                    longitude = latLng.longitude,
                    address = address
                )


                _state.update {
                    val newState = it.copy(
                        location = trackedLocation,
                        isLoadingLocation = false,
                        currentTrackingLocation = trackedLocation,
                    )
                    newState.copy(locationBoxUiState = calculateLocationBoxUiState(newState))
                }
            } catch (e: Exception) {
                _state.update {
                    val newState = it.copy(
                        isLoadingLocation = false,
                        error = e.message ?: "فشل في تحديد الموقع"
                    )
                    newState.copy(locationBoxUiState = calculateLocationBoxUiState(newState))
                }
            }
        }
    }

    private fun calculateLocationBoxUiState(state: ReportSelectionState): LocationBoxUiState {
        return when {
            state.isLoadingLocation -> LocationBoxUiState.Loading
            state.location != null -> LocationBoxUiState.LocationAvailable(
                location = state.location,
                locationDisplayText = state.location.address
                    ?: "${state.location.latitude}, ${state.location.longitude}"
            )

            else -> LocationBoxUiState.PermissionRequired(state.locationPermissionState)
        }
    }

    private fun call911() {
        phoneDialer.dial("911")
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}