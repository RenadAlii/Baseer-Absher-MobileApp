package com.baseer.baseer.data.network.networking

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


private fun createHttpClient(httpClientEngine: HttpClientEngine): HttpClient {
    return HttpClient(httpClientEngine) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }
}

fun createEmergencyClient(
    httpClientEngineProvider: HttpClientEngineProvider
): HttpClient {
    return createHttpClient(httpClientEngineProvider.client).config {
        defaultRequest {
            url("https://emergency-api-app-eghdbbgphye5ctfe.centralus-01.azurewebsites.net/")
        }
    }
}