package com.dag.mobinchapp.features.login.domain.usecase

import com.dag.mobinchapp.base.BaseUseCase
import com.dag.mobinchapp.base.extensions.ifNull
import com.dag.mobinchapp.features.login.domain.model.TokenRequest
import com.dag.mobinchapp.features.login.domain.model.TokenResponse
import com.dag.mobinchapp.features.login.domain.repository.ITokenService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GenerateTokenUseCase @Inject constructor(val tokenService: ITokenService):
    BaseUseCase<TokenRequest, TokenResponse> {
    override fun execute(params: TokenRequest?): Flow<TokenResponse> = flow {
        ifNull(
            params,
            notNullClosure = {
                tokenService.requestToken(it)?.let {
                    emit(it)
                }
            }
        )
    }
}