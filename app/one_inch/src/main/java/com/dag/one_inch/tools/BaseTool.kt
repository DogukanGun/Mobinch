package com.dag.one_inch.tools

import com.dag.one_inch.Agent
import com.dag.one_inch.Agent.Companion.json
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

open class BaseTool(
    val agent: Agent
) {
    internal suspend inline fun <reified T> getDecodedResponse(
        url: String,
        params: Map<String, Any>? = null
    ): T {
        val response = agent.getAgentClient().get(url) {
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
        val response = agent.getAgentClient().post(url) {
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