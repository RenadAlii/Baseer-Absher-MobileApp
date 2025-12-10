package com.baseer.baseer.presentation.components.model

import com.baseer.baseer.domain.model.LocationData
import dev.icerock.moko.permissions.PermissionState

sealed class LocationBoxUiState {
    data object Loading : LocationBoxUiState()
    data class LocationAvailable(
        val location: LocationData,
        val locationDisplayText: String,
    ) : LocationBoxUiState()

    data class PermissionRequired(
        val permissionState: PermissionState,
    ) : LocationBoxUiState()
}