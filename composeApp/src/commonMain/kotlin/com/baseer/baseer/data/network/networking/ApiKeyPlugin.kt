package com.baseer.baseer.data.network.networking

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.util.AttributeKey

class ApiKeyPlugin(private val apiKey: String) {

    companion object Plugin : HttpClientPlugin<Config, ApiKeyPlugin> {
        override val key = AttributeKey<ApiKeyPlugin>("ApiKeyPlugin")

        override fun prepare(block: Config.() -> Unit): ApiKeyPlugin {
            val config = Config().apply(block)
            return ApiKeyPlugin(config.apiKey)
        }

        override fun install(plugin: ApiKeyPlugin, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                context.url.parameters.append("api-key", plugin.apiKey)
            }
        }
    }

    class Config {
        lateinit var apiKey: String
    }
}