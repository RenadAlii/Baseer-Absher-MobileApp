package com.baseer.baseer.data.network.networking

import io.ktor.client.engine.HttpClientEngine

expect class HttpClientEngineProvider(){
    val client: HttpClientEngine
}