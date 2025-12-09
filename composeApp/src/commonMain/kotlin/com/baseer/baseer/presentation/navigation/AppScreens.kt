package com.baseer.baseer.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppScreens {

    @Serializable
    data object Home : AppScreens

    @Serializable
    data object ReportSelection : AppScreens

    @Serializable
    data class ReportDetails(val reportTypeId: String) : AppScreens

    @Serializable
    data class LocationPicker(
        val selectedLatitude: Double,
        val selectedLongitude: Double,
        val selectedAddress: String?,
        val currentLocatingLatitude: Double?,
        val currentLocatingLongitude: Double?,
        val currentLocatingAddress: String?,
    ) : AppScreens

}