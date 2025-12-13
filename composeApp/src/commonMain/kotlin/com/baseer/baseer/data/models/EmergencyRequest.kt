package com.baseer.baseer.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EmergencyRequest(
    val description: String,
    val images: String,
    val emergencyType: Int,
    val licensePlate: String,
    val latitude: Double,
    val longitude: Double
)