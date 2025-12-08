package com.baseer.baseer.domain.service

import android.content.Context
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.resume

class GeocoderServiceImpl(
    private val context: Context
) : GeocoderService {

    override suspend fun getAddressFromCoordinates(
        latitude: Double,
        longitude: Double
    ): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        val address = addresses.firstOrNull()?.let { formatAddress(it) }
                        continuation.resume(address)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                addresses?.firstOrNull()?.let { formatAddress(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun formatAddress(address: android.location.Address): String {
        val streetNumber = address.subThoroughfare
        val streetName = address.thoroughfare
        val district = address.subLocality
        val city = address.locality

        val parts = listOfNotNull(
            streetNumber?.takeIf { it.isNotBlank() },
            streetName?.takeIf { it.isNotBlank() },
            district?.takeIf { it.isNotBlank() },
            city?.takeIf { it.isNotBlank() }
        )

        return parts.joinToString(separator = ", ").ifEmpty {
            address.getAddressLine(0) ?: ""
        }
    }
}