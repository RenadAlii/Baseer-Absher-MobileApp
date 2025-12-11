package com.baseer.baseer.data.network.networking

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual class HttpClientEngineProvider  {
    actual val client: HttpClientEngine
        get() = Darwin.create()
}