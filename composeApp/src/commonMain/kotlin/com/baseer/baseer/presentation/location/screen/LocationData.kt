package com.baseer.baseer.presentation.location.screen

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
) {
    companion object {
        val Empty = LocationData(0.0, 0.0, null)

        // Default location (Riyadh)
        val Default = LocationData(24.7136, 46.6753, null)
    }
}