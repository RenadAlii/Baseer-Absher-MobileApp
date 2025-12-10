package com.baseer.baseer.presentation.reportSelect.screen


import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import org.jetbrains.compose.resources.StringResource

sealed interface ReportSelectionEvent {
    object LoadLocation : ReportSelectionEvent
    object OnCall911Click : ReportSelectionEvent
    object OnErrorShown : ReportSelectionEvent

    data class OnShowSnackbarError(val msg: StringResource) : ReportSelectionEvent

    data class LoadLocationAndPermissions(
        val permissionsController: PermissionsController,
        val locationTracker: LocationTracker
    ) : ReportSelectionEvent
}