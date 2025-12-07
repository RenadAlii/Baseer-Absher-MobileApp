package com.baseer.baseer.presentation.reportSelect.screen

import com.baseer.baseer.presentation.components.model.ReportType
import com.baseer.baseer.presentation.location.screen.LocationData


data class ReportSelectionState(
    val location: LocationData? = LocationData.Empty,
    val reportTypes: List<ReportType> = ReportType.all,
    val isLoading: Boolean = false,
    val error: String? = null
)