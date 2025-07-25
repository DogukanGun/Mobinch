package com.dag.mobinchapp.features.login.data.datasource

import com.dag.mobinchapp.base.Logger
import com.dag.mobinchapp.base.extensions.getResponseData
import com.dag.mobinchapp.features.login.domain.model.TokenRequest
import com.dag.mobinchapp.features.login.domain.model.TokenResponse
import com.dag.mobinchapp.features.login.domain.repository.ITokenService
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TokenService @Inject constructor(
    val ktor: HttpClient,
    private val logger: Logger
): ITokenService {
    override suspend fun requestToken(body: TokenRequest): TokenResponse? {
        val res = ktor.post("/auth/token") {
            setBody(body)
        }
        
        return res.getResponseData(logger)
    }
}