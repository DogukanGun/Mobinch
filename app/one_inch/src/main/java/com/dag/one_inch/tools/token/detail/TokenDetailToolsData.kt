package com.dag.one_inch.tools.token.detail


data class SocialLink(
    val name: String,
    val url: String,
    val handle: String
)

data class DetailsResponse(
    val provider: String,
    val providerURL: String,
    val vol24: Double,
    val marketCap: Double,
    val circulatingSupply: Double,
    val totalSupply: Double
)

data class AssetsResponse(
    val name: String,
    val website: String,
    val sourceCode: String,
    val whitePaper: String,
    val description: String,
    val shortDescription: String,
    val research: String,
    val explorer: String,
    val social_links: List<SocialLink>,
    val details: DetailsResponse
)

data class InfoDataResponse(
    val assets: List<AssetsResponse>
)

data class ChartPointResponse(
    val t: Long,
    val v: Double,
    val p: String
)

data class ChartDataResponse(
    val d: List<ChartPointResponse>
)

data class TokenPriceChangeResponseDto(
    val inUSD: Double,
    val inPercent: Double
)

data class TokenListPriceChangeResponseDto(
    val tokenAddress: String,
    val inUSD: Double,
    val inPercent: Double
)

data class PriceChangeResponse(
    val inUSD: Double? = null,
    val inPercent: Double? = null,
    val items: List<TokenListPriceChangeResponseDto>? = null
)