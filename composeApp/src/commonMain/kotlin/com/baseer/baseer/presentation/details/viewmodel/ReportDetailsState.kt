package com.baseer.baseer.presentation.details.viewmodel

import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.presentation.components.fileupload.UploadFile
import com.baseer.baseer.presentation.components.fileupload.UploadState
import com.baseer.baseer.presentation.components.model.LocationBoxUiState
import com.baseer.baseer.presentation.components.model.ReportType
import com.baseer.baseer.presentation.utils.SingleEvent
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
    val files: List<UploadFile> = emptyList(),
    val maxFiles: Int = 3,
    val navigateToHome: SingleEvent<Unit>? = null,
    val maxFileSizeMb: Int = 10,
) {
    val showPlateNumberField = reportType?.requiresPlateNumber ?: false
    val isDescriptionRequired = reportType?.requiresDescription ?: false

    private val isPlateOk = !(reportType?.requiresPlateNumber ?: true) || plateNumber.isNotBlank()
    private val isDescOk = !(reportType?.requiresDescription ?: true) || description.isNotBlank()

    val isSendEnabled = isLocationValid && isPlateOk && isDescOk && !isSending

    // File helpers
    val canAddMoreFiles: Boolean get() = files.size < maxFiles
    val hasFiles: Boolean get() = files.isNotEmpty()
    val uploadingFilesCount: Int get() = files.count { it.state == UploadState.Uploading }
    val successFilesCount: Int get() = files.count { it.state == UploadState.Success }
    val errorFilesCount: Int get() = files.count { it.state == UploadState.Error }
    val pendingFilesCount: Int get() = files.count { it.state == UploadState.Pending }
    val maxFileSizeBytes: Long get() = maxFileSizeMb * 1024L * 1024L
}