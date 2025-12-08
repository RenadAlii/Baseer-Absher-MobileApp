package com.baseer.baseer.domain.service

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLPlacemark
import kotlin.coroutines.resume

class GeocoderServiceImpl : GeocoderService {

    override suspend fun getAddressFromCoordinates(
        latitude: Double,
        longitude: Double
    ): String? = suspendCancellableCoroutine { continuation ->
        val geocoder = CLGeocoder()
        val location = CLLocation(latitude, longitude)

        geocoder.reverseGeocodeLocation(location) { placemarks, error ->
            if (error != null) {
                continuation.resume(null)
                return@reverseGeocodeLocation
            }

            val placemark = placemarks?.firstOrNull() as? CLPlacemark
            val address = placemark?.let { formatAddress(it) }
            continuation.resume(address)
        }
    }

    private fun formatAddress(placemark: CLPlacemark): String {
        val streetNumber = placemark.subThoroughfare
        val streetName = placemark.thoroughfare
        val district = placemark.subLocality
        val city = placemark.locality

        val parts = listOfNotNull(
            streetNumber?.takeIf { it.isNotBlank() },
            streetName?.takeIf { it.isNotBlank() },
            district?.takeIf { it.isNotBlank() },
            city?.takeIf { it.isNotBlank() }
        )

        return parts.joinToString(separator = "، ")
    }
}