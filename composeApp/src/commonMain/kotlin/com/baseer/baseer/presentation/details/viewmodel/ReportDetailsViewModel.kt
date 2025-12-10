package com.baseer.baseer.presentation.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.presentation.components.fileupload.FileUploadErrorKeys
import com.baseer.baseer.presentation.components.fileupload.UploadFile
import com.baseer.baseer.presentation.components.fileupload.UploadState
import com.baseer.baseer.presentation.components.fileupload.isSupportedFileType
import com.baseer.baseer.presentation.components.model.LocationBoxUiState
import com.baseer.baseer.presentation.components.model.ReportType
import dev.icerock.moko.permissions.PermissionState
import kotlinx.coroutines.delay
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

    fun onEvent(event: ReportDetailsEvent) {
        when (event) {
            is ReportDetailsEvent.LoadInitialData -> loadInitialData(event.reportTypeId, event.initialLocation)
            is ReportDetailsEvent.PlateNumberChange -> updatePlateNumber(event.value)
            is ReportDetailsEvent.DescriptionChange -> updateDescription(event.value)
            is ReportDetailsEvent.LocationUpdated -> updateLocationAndCheckValidity(event.location)
            ReportDetailsEvent.SendReportClick -> sendReport()
            is ReportDetailsEvent.FilesSelected -> addFiles(event.files)
            is ReportDetailsEvent.RemoveFile -> removeFile(event.fileId)
            is ReportDetailsEvent.RetryUploadFile -> retryUploadFile(event.fileId)
            ReportDetailsEvent.UploadAllFiles -> uploadAllFiles()
            ReportDetailsEvent.DismissErrorDialog -> dismissErrorDialog()

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
            try {
                // TODO: Call API with state data
                delay(2000) // Simulate API call

                // Success - Navigate or show success
                _state.update { it.copy(isSending = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSending = false,
                        errorDialogMessage = e.message,
                    )
                }
            }
        }
    }

    private fun dismissErrorDialog() {
        _state.update { it.copy(errorDialogMessage = null) }
    }

    // ===== Get Ready Files for API =====
    fun getFilesForUpload(): List<UploadFile> {
        return _state.value.files.filter { it.state == UploadState.Success }
    }

    private fun addFiles(newFiles: List<UploadFile>) {
        val currentState = _state.value
        val remainingSlots = currentState.maxFiles - currentState.files.size

        if (remainingSlots <= 0) return

        val validatedFiles = newFiles.take(remainingSlots).map { file ->
            validateFile(file, currentState)
        }

        _state.update {
            it.copy(files = it.files + validatedFiles)
        }

        // Auto upload valid files
        validatedFiles
            .filter { it.state == UploadState.Pending }
            .forEach { uploadFile(it.id) }
    }

    private fun validateFile(file: UploadFile, state: ReportDetailsState): UploadFile {
        return when {
            !isSupportedFileType(file.mimeType) -> file.copy(
                state = UploadState.Error,
                errorKey = FileUploadErrorKeys.FILE_TYPE_NOT_SUPPORTED
            )

            file.size > state.maxFileSizeBytes -> file.copy(
                state = UploadState.Error,
                errorKey = FileUploadErrorKeys.FILE_TOO_LARGE
            )

            else -> file
        }
    }

    private fun removeFile(fileId: String) {
        _state.update {
            it.copy(files = it.files.filter { file -> file.id != fileId })
        }
    }

    private fun retryUploadFile(fileId: String) {
        _state.update {
            it.copy(
                files = it.files.map { file ->
                    if (file.id == fileId) file.copy(state = UploadState.Pending, errorKey = null, progress = 0f)
                    else file
                }
            )
        }
        uploadFile(fileId)
    }

    private fun uploadAllFiles() {
        _state.value.files
            .filter { it.state == UploadState.Pending }
            .forEach { uploadFile(it.id) }
    }

    private fun uploadFile(fileId: String) {
        viewModelScope.launch {
            // Update to uploading
            updateFileState(fileId, UploadState.Uploading)

            try {
                // Simulate upload progress
                for (i in 1..10) {
                    delay(150)
                    updateFileProgress(fileId, i / 10f)
                }

                // Success
                updateFileState(fileId, UploadState.Success)
            } catch (e: Exception) {
                updateFileError(fileId, e.message ?: "UPLOAD_FAILED")
            }
        }
    }

    private fun updateFileState(fileId: String, state: UploadState) {
        _state.update {
            it.copy(files = it.files.map { file ->
                if (file.id == fileId) file.copy(state = state) else file
            })
        }
    }

    private fun updateFileProgress(fileId: String, progress: Float) {
        _state.update {
            it.copy(files = it.files.map { file ->
                if (file.id == fileId) file.copy(progress = progress) else file
            })
        }
    }

    private fun updateFileError(fileId: String, error: String) {
        _state.update {
            it.copy(files = it.files.map { file ->
                if (file.id == fileId) file.copy(state = UploadState.Error, errorKey = error) else file
            })
        }
    }
}