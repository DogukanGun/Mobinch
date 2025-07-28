package com.dag.one_inch.tools.token.detail

import com.dag.one_inch.Registery
import com.dag.one_inch.tools.BaseTool
import dev.langchain4j.agent.tool.Tool

class TokenDetailTools(private val oneInchKey: String) : BaseTool(oneInchKey) {

    companion object {
        private const val BASE_URL = "${Registery.BASE_URL}token-details/v1.0"
    }

    @Tool("Get token info for chain")
    suspend fun getChainTokenInfo(chain: String): InfoDataResponse {
        return getDecodedResponse<InfoDataResponse>(
            "$BASE_URL/details/$chain"
        )
    }

    @Tool("Get token info for specific contract")
    suspend fun getTokenInfo(chain: String, contractAddress: String): InfoDataResponse {
        return getDecodedResponse<InfoDataResponse>(
            "$BASE_URL/details/$chain/$contractAddress"
        )
    }

    @Tool("Get chart data (range) all tokens on chain")
    suspend fun getRangeCharts(chain: String): ChartDataResponse {
        return getDecodedResponse<ChartDataResponse>(
            "$BASE_URL/charts/range/$chain"
        )
    }

    @Tool("Get range chart data for specific token")
    suspend fun getTokenRangeCharts(chain: String, tokenAddress: String): ChartDataResponse {
        return getDecodedResponse<ChartDataResponse>(
            "$BASE_URL/charts/range/$chain/$tokenAddress"
        )
    }

    @Tool("Get chart data (interval) all tokens on chain")
    suspend fun getIntervalCharts(chain: String): ChartDataResponse {
        return getDecodedResponse<ChartDataResponse>(
            "$BASE_URL/charts/interval/$chain"
        )
    }

    @Tool("Get interval chart data for specific token")
    suspend fun getTokenIntervalCharts(chain: String, tokenAddress: String): ChartDataResponse {
        return getDecodedResponse<ChartDataResponse>(
            "$BASE_URL/charts/interval/$chain/$tokenAddress"
        )
    }

    @Tool("Get price change for all tokens on chain (GET)")
    suspend fun getPriceChange(chain: String): PriceChangeResponse {
        return getDecodedResponse<PriceChangeResponse>(
            "$BASE_URL/prices/change/$chain"
        )
    }

    @Tool("Post price change request for all tokens on chain (POST)")
    suspend fun postPriceChange(
        chain: String,
        body: Any /* replace with your PriceChangeRequest data class */
    ): PriceChangeResponse {
        return postDecodedResponse<PriceChangeResponse, Any>(
            "$BASE_URL/prices/change/$chain",
            body
        )
    }

    @Tool("Get price change for specific token (GET)")
    suspend fun getTokenPriceChange(
        chain: String,
        tokenAddress: String
    ): PriceChangeResponse {
        return getDecodedResponse<PriceChangeResponse>(
            "$BASE_URL/prices/change/$chain/$tokenAddress"
        )
    }
}