package com.baseer.baseer.presentation.reportSelect.viewmodel

import androidx.lifecycle.ViewModel
import com.baseer.baseer.presentation.components.model.ReportType
import com.baseer.baseer.presentation.reportSelect.screen.ReportSelectionEvent
import com.baseer.baseer.presentation.reportSelect.screen.ReportSelectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ReportSelectionViewModel : ViewModel() {

    private val _state = MutableStateFlow(ReportSelectionState())
    val state: StateFlow<ReportSelectionState> = _state.asStateFlow()

    // Navigation events
    private var _navigationEvent: ((ReportSelectionNavigation) -> Unit)? = null

    fun setNavigationHandler(handler: (ReportSelectionNavigation) -> Unit) {
        _navigationEvent = handler
    }

    fun onEvent(event: ReportSelectionEvent) {
        when (event) {
            ReportSelectionEvent.OnBackClick -> navigateBack()
            ReportSelectionEvent.OnLocationEditClick -> navigateToLocationPicker()
            is ReportSelectionEvent.OnReportTypeClick -> onReportTypeClick(event.reportType)
            ReportSelectionEvent.OnCall911Click -> call911()
            ReportSelectionEvent.OnErrorShown -> clearError()
        }
    }

    private fun navigateBack() {
        _navigationEvent?.invoke(ReportSelectionNavigation.Back)
    }

    private fun navigateToLocationPicker() {
        _navigationEvent?.invoke(ReportSelectionNavigation.ToLocationPicker)
    }

    private fun onReportTypeClick(reportType: ReportType) {
        _navigationEvent?.invoke(ReportSelectionNavigation.ToReportDetails(reportType.id))
    }

    private fun call911() {
        _navigationEvent?.invoke(ReportSelectionNavigation.Call911)
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

sealed interface ReportSelectionNavigation {
    data object Back : ReportSelectionNavigation
    data object ToLocationPicker : ReportSelectionNavigation
    data class ToReportDetails(val reportTypeId: String) : ReportSelectionNavigation
    data object Call911 : ReportSelectionNavigation
}