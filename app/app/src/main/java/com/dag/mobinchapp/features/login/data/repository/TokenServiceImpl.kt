package com.dag.mobinchapp.features.login.data.repository

import com.dag.mobinchapp.features.login.data.datasource.TokenService
import com.dag.mobinchapp.features.login.domain.repository.ITokenService
import com.dag.mobinchapp.features.login.domain.model.TokenRequest
import com.dag.mobinchapp.features.login.domain.model.TokenResponse

class TokenServiceImpl(val tokenService: TokenService): ITokenService {
    override suspend fun requestToken(body: TokenRequest): TokenResponse? {
        return tokenService.requestToken(body)
    }
}