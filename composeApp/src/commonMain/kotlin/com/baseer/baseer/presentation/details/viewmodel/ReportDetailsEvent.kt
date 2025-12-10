package com.baseer.baseer.presentation.details.viewmodel

import com.baseer.baseer.domain.model.LocationData

sealed class ReportDetailsEvent {
    data class LoadInitialData(val reportTypeId: String, val initialLocation: LocationData) : ReportDetailsEvent()
    data class PlateNumberChange(val value: String) : ReportDetailsEvent()
    data class DescriptionChange(val value: String) : ReportDetailsEvent()
    data class LocationUpdated(val location: LocationData) : ReportDetailsEvent()
    data object SendReportClick : ReportDetailsEvent()
}