package com.dag.mobinchapp.features.login.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String
)
