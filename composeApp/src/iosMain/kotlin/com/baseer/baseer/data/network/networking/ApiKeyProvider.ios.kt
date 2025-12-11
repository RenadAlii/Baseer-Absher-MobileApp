package com.baseer.baseer.data.network.networking

actual class ApiKeyProvider {
    actual val apiKey: String
        get() = "NSBundle.mainBundle.objectForInfoDictionaryKey()"

}