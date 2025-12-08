package com.baseer.baseer.presentation.reportSelect.screen

import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.presentation.components.model.ReportType

data class ReportSelectionState(
    val location: LocationData? = null,
    val reportTypes: List<ReportType> = ReportType.all,
    val isLoadingLocation: Boolean = false,
    val error: String? = null
)