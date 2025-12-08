package com.baseer.baseer.domain.service

interface GeocoderService {
    suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): String?
}