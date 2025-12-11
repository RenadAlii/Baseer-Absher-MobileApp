package com.baseer.baseer.data.network.networking

import com.google.maps.android.ktx.BuildConfig

actual class ApiKeyProvider {
    actual val apiKey: String
        get() = BuildConfig.A_K
}