package com.baseer.baseer.presentation.reportSelect.screen

import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.presentation.components.model.LocationBoxUiState
import com.baseer.baseer.presentation.components.model.ReportType
import dev.icerock.moko.permissions.PermissionState

data class ReportSelectionState(
    val location: LocationData? = null,
    val currentTrackingLocation: LocationData? = null,
    val locationPermissionState: PermissionState = PermissionState.NotDetermined,
    val reportTypes: List<ReportType> = ReportType.all,
    val isLoadingLocation: Boolean = false,
    val error: String? = null,
    val isLocationValid: Boolean = false,
    val locationBoxUiState: LocationBoxUiState = LocationBoxUiState.Loading,
)