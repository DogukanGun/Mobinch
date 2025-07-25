package com.dag.mobinchapp.features.login.domain.repository

import com.dag.mobinchapp.features.login.domain.model.TokenRequest
import com.dag.mobinchapp.features.login.domain.model.TokenResponse

interface ITokenService {
    suspend fun requestToken(body: TokenRequest): TokenResponse?
}