package com.baseer.baseer.presentation.reportSelect.screen

sealed interface ReportSelectionEvent {
    data object LoadLocation : ReportSelectionEvent
    data object OnCall911Click : ReportSelectionEvent
    data object OnErrorShown : ReportSelectionEvent
}