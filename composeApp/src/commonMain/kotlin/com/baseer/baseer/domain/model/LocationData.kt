package com.baseer.baseer.domain.model

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
) {
    val isValid: Boolean
        get() = latitude != 0.0 && longitude != 0.0

    val displayText: String
        get() = address ?: "${roundToSixDecimals(latitude)}, ${roundToSixDecimals(longitude)}"

    private fun roundToSixDecimals(value: Double): String {
        val rounded = (value * 1000000).toLong() / 1000000.0
        return rounded.toString()
    }
}