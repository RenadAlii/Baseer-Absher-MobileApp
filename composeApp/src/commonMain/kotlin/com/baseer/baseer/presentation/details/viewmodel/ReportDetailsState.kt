package com.baseer.baseer.presentation.details.viewmodel

import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.presentation.components.model.LocationBoxUiState
import com.baseer.baseer.presentation.components.model.ReportType
import dev.icerock.moko.permissions.PermissionState

data class ReportDetailsState(
    val reportType: ReportType? = null,
    val location: LocationData = LocationData(0.0, 0.0, null),
    val isLoadingLocation: Boolean = false,
    val locationPermissionState: PermissionState = PermissionState.Granted,
    val isLocationValid: Boolean = false,
    val plateNumber: String = "",
    val description: String = "",
    val isSending: Boolean = false,
    val errorDialogMessage: String? = null,
    val locationBoxUiState: LocationBoxUiState = LocationBoxUiState.Loading,
) {
    val showPlateNumberField = reportType?.requiresPlateNumber ?: false
    val isDescriptionRequired = reportType?.requiresDescription ?: false

    private val isPlateOk = !(reportType?.requiresPlateNumber ?: true) || plateNumber.isNotBlank()
    private val isDescOk = !(reportType?.requiresDescription ?: true) || description.isNotBlank()

    val isSendEnabled = isLocationValid && isPlateOk && isDescOk && !isSending
}