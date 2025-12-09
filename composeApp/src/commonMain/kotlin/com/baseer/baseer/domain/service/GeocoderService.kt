package com.baseer.baseer.domain.service

import com.baseer.baseer.domain.model.LocationData

interface GeocoderService {
    suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): String?
    suspend fun searchLocations(query: String): List<LocationData>
}