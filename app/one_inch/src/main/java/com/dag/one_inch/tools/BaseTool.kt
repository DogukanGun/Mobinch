package com.dag.one_inch.tools

import com.dag.one_inch.Agent.Companion.json
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

open class BaseTool(
    val oneinchKey: String
) {
    val client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.INFO
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10000L
            connectTimeoutMillis = 10000L
            socketTimeoutMillis = 10000L
        }
        defaultRequest {
            header("Authorization", "Bearer $oneinchKey")
            accept(ContentType.Application.Json)
        }
    }
    internal suspend inline fun <reified T> getDecodedResponse(
        url: String,
        params: Map<String, Any>? = null
    ): T {
        val response = client.get(url) {
            params?.forEach { (key, value) ->
                parameter(key, value)
            }
        }

        if (!response.status.isSuccess()) {
            throw RuntimeException("API returned HTTP ${response.status.value}")
        }

        val bodyText = response.bodyAsText()
        return json.decodeFromString(bodyText)
    }

    internal suspend inline fun <reified Resp, reified Req> postDecodedResponse(
        url: String,
        requestBody: Req
    ): Resp {
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        if (!response.status.isSuccess()) {
            throw RuntimeException("POST $url failed: HTTP ${response.status.value} / ${response.bodyAsText()}")
        }

        val bodyText = response.bodyAsText()
        return json.decodeFromString(bodyText)
    }
}