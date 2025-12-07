package com.baseer.baseer.presentation.reportSelect.screen

import com.baseer.baseer.presentation.components.model.ReportType


sealed interface ReportSelectionEvent {
    data object OnBackClick : ReportSelectionEvent
    data object OnLocationEditClick : ReportSelectionEvent
    data class OnReportTypeClick(val reportType: ReportType) : ReportSelectionEvent
    data object OnCall911Click : ReportSelectionEvent
    data object OnErrorShown : ReportSelectionEvent
}