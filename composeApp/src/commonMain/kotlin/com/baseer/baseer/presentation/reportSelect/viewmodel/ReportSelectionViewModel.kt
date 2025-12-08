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

    fun onEvent(event: ReportSelectionEvent) {
        when (event) {
            ReportSelectionEvent.LoadLocation -> loadLocation()
            ReportSelectionEvent.OnLocationEditClick -> onLocationEditClick()
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

                // Get address from coordinates
                val address = geocoderService.getAddressFromCoordinates(
                    latitude = latLng.latitude,
                    longitude = latLng.longitude
                )

                _state.update {
                    it.copy(
                        location = LocationData(
                            latitude = latLng.latitude,
                            longitude = latLng.longitude,
                            address = address
                        ),
                        isLoadingLocation = false
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

    private fun onLocationEditClick() {
        // TODO: Navigate to map picker
    }

    private fun call911() {
        phoneDialer.dial("911")
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}