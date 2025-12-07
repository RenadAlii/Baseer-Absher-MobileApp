//package com.baseer.baseer.presentation.location.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import baseer.composeapp.generated.resources.Res
//import baseer.composeapp.generated.resources.*
//import com.baseer.baseer.presentation.reportSelect.componenets.ReportTypeUiModel
//import com.baseer.baseer.presentation.reportSelect.screen.ReportSelectionEvent
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//
//class ReportTypeViewModel : ViewModel() {
//
//    private val _state = MutableStateFlow(ReportTypeState())
//    val state: StateFlow<ReportTypeState> = _state.asStateFlow()
//
//    // List from shared resources — KMP Friendly
//    private val reportTypesList = listOf(
//        ReportTypeUiModel(
//            titleRes = Res.string.report_type_fire,
//            iconRes = Res.drawable.ic_fire
//        ),
//        ReportTypeUiModel(
//            titleRes = Res.string.report_type_conflict,
//            iconRes = Res.drawable.ic_humens
//        ),
//        ReportTypeUiModel(
//            titleRes = Res.string.report_type_broken_car,
//            iconRes = Res.drawable.ic_car
//        ),
//        ReportTypeUiModel(
//            titleRes = Res.string.report_type_reckless_driver,
//            iconRes = Res.drawable.ic_warning
//        ),
//        ReportTypeUiModel(
//            titleRes = Res.string.report_type_begging,
//            iconRes = Res.drawable.ic_slave
//        ),
//        ReportTypeUiModel(
//            titleRes = Res.string.report_type_suspected_drugs,
//            iconRes = Res.drawable.ic_drug
//        ),
//        ReportTypeUiModel(
//            titleRes = Res.string.report_type_harassment,
//            iconRes = Res.drawable.ic_shield
//        ),
//        ReportTypeUiModel(
//            titleRes = Res.string.report_type_theft,
//            iconRes = Res.drawable.ic_bag
//        )
//    )
//
//    init {
//        loadData()
//    }
//
//    private fun loadData() {
//        viewModelScope.launch {
//            _state.update {
//                it.copy(
//                    isLoading = false,
//                    location = "طريق الملك فهد، العليا، الرياض",
//                    reportTypes = reportTypesList
//                )
//            }
//        }
//    }
//
//    fun onEvent(event: ReportSelectionEvent) {
//        when (event) {
//            is ReportSelectionEvent.OnReportTypeClick -> {
//                // Handle selection (navigation handled in UI)
//            }
//
//            ReportSelectionEvent.OnBackClick -> {
//                // UI layer handles navigation
//            }
//
//            ReportSelectionEvent.OnChangeLocation -> {
//                // Later you can open location picker here
//            }
//
//            ReportSelectionEvent.OnCall911 -> {
//                // UI triggers phone Intent
//            }
//        }
//    }
//}