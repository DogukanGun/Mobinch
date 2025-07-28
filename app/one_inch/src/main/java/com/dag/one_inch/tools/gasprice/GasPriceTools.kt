package com.dag.one_inch.tools.gasprice
import com.dag.one_inch.Registery
import com.dag.one_inch.tools.BaseTool
import dev.langchain4j.agent.tool.Tool

class GasPriceTool(private val oneInchKey: String) : BaseTool(oneInchKey) {

    companion object {
        private const val BASE_URL = "${Registery.BASE_URL}gas-price/v1.6"
    }

    @Tool("Get gas price for chain")
    suspend fun getGasPrice(chain: String): LegacyGasPriceResponse {
        return getDecodedResponse(
            "$BASE_URL/$chain"
        )
    }
}