package com.baseer.baseer.domain.service

import com.baseer.baseer.domain.model.LocationData
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
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

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun searchLocations(query: String): List<LocationData> =
        suspendCancellableCoroutine { continuation ->
            val geocoder = CLGeocoder()

            geocoder.geocodeAddressString(query) { placemarks, error ->
                if (error != null) {
                    continuation.resume(emptyList())
                    return@geocodeAddressString
                }

                val results = placemarks?.mapNotNull { item ->
                    val placemark = item as? CLPlacemark ?: return@mapNotNull null
                    val location = placemark.location ?: return@mapNotNull null

                    location.coordinate.useContents {
                        LocationData(
                            latitude = latitude,
                            longitude = longitude,
                            address = formatAddress(placemark)
                        )
                    }
                } ?: emptyList()
                continuation.resume(results)
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