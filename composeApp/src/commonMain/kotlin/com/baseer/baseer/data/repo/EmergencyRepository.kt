package com.baseer.baseer.data.repo

import io.ktor.client.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*

class EmergencyRepository(
    private val httpClient: HttpClient,
) {

    suspend fun createEmergency(
        description: String,
        images: List<ByteArray>,
        imageNames: List<String>,
        emergencyType: Int,
        licensePlate: String,
        latitude: Double,
        longitude: Double
    ): Result<String> {
        return try {
            val response: HttpResponse = httpClient.submitFormWithBinaryData(
                url = "Emergency/create",
                formData = formData {
                    append("Description", description)
                    append("EmergencyType", emergencyType.toString())
                    append("LicensePlate", licensePlate)
                    append("Latitude", latitude.toString())
                    append("Longitude", longitude.toString())

                    images.forEachIndexed { index, imageBytes ->
                        append("Images", imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=\"${imageNames.getOrElse(index) { "image_$index.jpg" }}\""
                            )
                        })
                    }
                }
            )

            if (response.status.isSuccess()) {
                Result.success(response.bodyAsText())
            } else {
                Result.failure(Exception("Error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}