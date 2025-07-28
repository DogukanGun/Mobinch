package com.dag.one_inch.tools.domains

data class ResponseV2Dto(
    val result: ProviderResponse
)

data class ProviderResponse(
    val protocol: String,
    val address: String,
    val checkUrl: String
)

data class ResponseReverseV2Dto(
    val result: ProviderReverseResponse
)

data class ProviderReverseResponse(
    val protocol: String,
    val domain: String,
    val checkUrl: String
)

data class AvatarsResponse(
    val result: ProviderResponseWithAvatar
)

data class ProviderResponseWithAvatar(
    val protocol: String,
    val domain: String,
    val address: String,
    val avatar: Any
)

typealias ResponseBatchV2ReturnTypeDto = Map<String, List<ProviderReverseResponse>>