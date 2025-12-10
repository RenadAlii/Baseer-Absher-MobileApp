package com.baseer.baseer.presentation.details.viewmodel

import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.presentation.components.fileupload.UploadFile

sealed class ReportDetailsEvent {
    data class LoadInitialData(val reportTypeId: String, val initialLocation: LocationData) : ReportDetailsEvent()
    data class PlateNumberChange(val value: String) : ReportDetailsEvent()
    data class DescriptionChange(val value: String) : ReportDetailsEvent()
    data class LocationUpdated(val location: LocationData) : ReportDetailsEvent()
    data object SendReportClick : ReportDetailsEvent()
    data class FilesSelected(val files: List<UploadFile>) : ReportDetailsEvent()
    data class RemoveFile(val fileId: String) : ReportDetailsEvent()
    data class RetryUploadFile(val fileId: String) : ReportDetailsEvent()
    data object UploadAllFiles : ReportDetailsEvent()
    data object DismissErrorDialog : ReportDetailsEvent()

}