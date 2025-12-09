package com.baseer.baseer.presentation.reportSelect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.domain.service.GeocoderService
import com.baseer.baseer.presentation.reportSelect.screen.ReportSelectionEvent
import com.baseer.baseer.presentation.reportSelect.screen.ReportSelectionState
import com.baseer.baseer.presentation.utils.PhoneDialer
import dev.icerock.moko.geo.LocationTracker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReportSelectionViewModel(
    private val geocoderService: GeocoderService,
    private val phoneDialer: PhoneDialer
) : ViewModel() {

    private val _state = MutableStateFlow(ReportSelectionState())
    val state: StateFlow<ReportSelectionState> = _state.asStateFlow()

    private var locationTracker: LocationTracker? = null
    fun setLocationTracker(tracker: LocationTracker) {
        locationTracker = tracker
        loadLocation()
    }

    fun updateLocation(location: LocationData) {
        _state.update { it.copy(location = location) }
    }

    fun onEvent(event: ReportSelectionEvent) {
        when (event) {
            ReportSelectionEvent.LoadLocation -> loadLocation()
            ReportSelectionEvent.OnCall911Click -> call911()
            ReportSelectionEvent.OnErrorShown -> clearError()
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
                        currentTrackingLocation = trackedLocation
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