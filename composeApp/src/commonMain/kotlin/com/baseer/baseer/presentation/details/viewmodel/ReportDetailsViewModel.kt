package com.baseer.baseer.presentation.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.presentation.components.model.LocationBoxUiState
import com.baseer.baseer.presentation.components.model.ReportType
import dev.icerock.moko.permissions.PermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class ReportDetailsViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        ReportDetailsState(
            locationBoxUiState = LocationBoxUiState.PermissionRequired(PermissionState.DeniedAlways)
        )
    )
    val state: StateFlow<ReportDetailsState> = _state

    fun processEvent(event: ReportDetailsEvent) {
        when (event) {
            is ReportDetailsEvent.LoadInitialData -> loadInitialData(event.reportTypeId, event.initialLocation)
            is ReportDetailsEvent.PlateNumberChange -> updatePlateNumber(event.value)
            is ReportDetailsEvent.DescriptionChange -> updateDescription(event.value)
            is ReportDetailsEvent.LocationUpdated -> updateLocationAndCheckValidity(event.location)
            ReportDetailsEvent.SendReportClick -> sendReport()
        }
    }

    private fun loadInitialData(reportTypeId: String, initialLocation: LocationData) {
        viewModelScope.launch {
            val type = ReportType.fromId(reportTypeId)
            val isLocationValid = checkLocationValidity(initialLocation)

            _state.update {
                val newState = it.copy(
                    reportType = type,
                    location = initialLocation,
                    locationPermissionState = if (isLocationValid) PermissionState.Granted else PermissionState.DeniedAlways,
                    isLocationValid = isLocationValid,
                    isLoadingLocation = false
                )
                val finalState = newState.copy(
                    locationBoxUiState = calculateLocationBoxUiState(newState)
                )
                finalState
            }
        }
    }

    private fun updatePlateNumber(value: String) {
        _state.update {
            it.copy(plateNumber = value)
        }
    }

    private fun updateDescription(value: String) {
        _state.update {
            it.copy(description = value)
        }
    }

    private fun updateLocationAndCheckValidity(location: LocationData) {
        val isValid = checkLocationValidity(location)
        _state.update {
            val newState = it.copy(
                location = location,
                isLocationValid = isValid,
                locationPermissionState = if (isValid) PermissionState.Granted else PermissionState.DeniedAlways
            )
            val finalState = newState.copy(
                locationBoxUiState = calculateLocationBoxUiState(newState)
            )
            finalState
        }
    }

    private fun checkLocationValidity(location: LocationData): Boolean {
        return location.latitude.absoluteValue > 0.0001 && location.longitude.absoluteValue > 0.0001
    }


    private fun calculateLocationBoxUiState(state: ReportDetailsState): LocationBoxUiState {
        return when {
            state.isLoadingLocation -> LocationBoxUiState.Loading

            !state.isLocationValid -> LocationBoxUiState.PermissionRequired(state.locationPermissionState)

            state.location.address != null -> LocationBoxUiState.LocationAvailable(
                location = state.location,
                locationDisplayText = state.location.address
            )

            else -> LocationBoxUiState.LocationAvailable(
                location = state.location,
                locationDisplayText = "${state.location.latitude}, ${state.location.longitude}"
            )
        }
    }


    private fun sendReport() {
        _state.update { it.copy(isSending = true) }

        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            _state.update { it.copy(isSending = false) }
        }
    }
}