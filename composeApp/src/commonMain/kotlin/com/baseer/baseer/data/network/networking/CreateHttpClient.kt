package com.baseer.baseer.data.network.networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
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
        install(Logging) { logger = Logger.SIMPLE }
    }
}

fun createNYSClient(
    httpClientEngineProvider: HttpClientEngineProvider,
    apiKeyProvider: ApiKeyProvider
): HttpClient {
    return createHttpClient(httpClientEngineProvider.client).config {
        defaultRequest {
            url("https://api.nytimes.com/svc/")
        }
        install(ApiKeyPlugin) {
            apiKey = apiKeyProvider.apiKey
        }
    }
}

