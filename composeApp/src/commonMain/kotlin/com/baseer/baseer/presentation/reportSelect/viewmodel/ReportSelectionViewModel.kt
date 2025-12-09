package com.baseer.baseer.presentation.reportSelect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.domain.service.GeocoderService
import com.baseer.baseer.presentation.reportSelect.screen.ReportSelectionEvent
import com.baseer.baseer.presentation.reportSelect.screen.ReportSelectionState
import com.baseer.baseer.presentation.utils.PhoneDialer
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.location.LOCATION
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReportSelectionViewModel(
    private val geocoderService: GeocoderService,
    private val phoneDialer: PhoneDialer
) : ViewModel() {

    private val _state = MutableStateFlow(ReportSelectionState())
    val state: StateFlow<ReportSelectionState> = _state.asStateFlow()

    private var locationTracker: LocationTracker? = null
    private var permissionsController: PermissionsController? = null

    fun updateLocation(location: LocationData) {
        _state.update { it.copy(location = location) }
    }

    fun onEvent(event: ReportSelectionEvent) {
        when (event) {
            is ReportSelectionEvent.LoadLocationAndPermissions -> handlePermissionsAndLocation(event.permissionsController, event.locationTracker)
            ReportSelectionEvent.LoadLocation -> loadLocation()
            ReportSelectionEvent.OnCall911Click -> call911()
            ReportSelectionEvent.OnErrorShown -> clearError()
        }
    }

    private fun handlePermissionsAndLocation(controller: PermissionsController, tracker: LocationTracker) {
        permissionsController = controller
        locationTracker = tracker

        viewModelScope.launch {
            val initialPermissionState = controller.getPermissionState(Permission.LOCATION)
            _state.update { it.copy(locationPermissionState = initialPermissionState) }

            if (_state.value.location == null) {
                try {
                    controller.providePermission(Permission.LOCATION)

                    val currentState = controller.getPermissionState(Permission.LOCATION)
                    _state.update { it.copy(locationPermissionState = currentState) }

                    if (currentState == PermissionState.Granted) {
                        loadLocation()
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            locationPermissionState = controller.getPermissionState(Permission.LOCATION)
                        )
                    }
                }
            }
        }
    }

    private fun loadLocation() {
        val tracker = locationTracker ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingLocation = true, error = null) }

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
                    it.copy(
                        location = LocationData(
                            latitude = latLng.latitude,
                            longitude = latLng.longitude,
                            address = address
                        ),
                        isLoadingLocation = false,
                        currentTrackingLocation = trackedLocation,
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingLocation = false,
                        error = e.message ?: "فشل في تحديد الموقع"
                    )
                }
            }
        }
    }

    private fun call911() {
        phoneDialer.dial("911")
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}